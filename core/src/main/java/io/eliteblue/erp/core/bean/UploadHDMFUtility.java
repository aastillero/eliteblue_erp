package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.model.upload_util.SSSUpload;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.GovernmentLoanService;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.omnifaces.util.Faces;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Named
@ViewScoped
public class UploadHDMFUtility implements Serializable {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private final String STOP_DEDUCTION_COLOR = "FFFF0000";

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private GovernmentLoanService governmentLoanService;

    @Autowired
    private ErpEmployeeService employeeService;

    private UploadedFile file;

    private List<SSSUpload> sssUpload;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        sssUpload = new ArrayList<>();
        file = null;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<SSSUpload> getSssUpload() {
        return sssUpload;
    }

    public void setSssUpload(List<SSSUpload> sssUpload) {
        this.sssUpload = sssUpload;
    }

    public void fileUpload() throws Exception {
        System.out.println("FILE: "+file);
        if (file != null && file.getSize() > 0) {
            ExcelUtils.initializeUploadUtility(file.getInputStream());
            for(int i = 0; i<ExcelUtils.workbook.getNumberOfSheets(); i++) {
                ExcelUtils.evaluateAllCells();
                //ExcelUtils.setWorksheet("NEGROS SALARY LOANS");
                int lastCellNum = ExcelUtils.getLastCellNum(0);
                int lastRowNum = ExcelUtils.getLastRowNum();
                int nameCol = -1;
                int postCol = -1;
                int remarksCol = -1;
                int loanAmtCol = -1;
                int amortCol = -1;
                int loanDateCol = -1;
                int balanceCol = -1;
                int totalDeductedCol = -1;
                for(int y=0; y <= lastRowNum; y++) {
                    for (int x = 0; x <= lastCellNum; x++) {
                        String colName = (String) ExcelUtils.getCellData(y, x);
                        colName = colName.trim();
                        CellStyle colStyle = ExcelUtils.getCellStyle(y, x);
                        boolean isRedCol = false;
                        if (colStyle != null) {
                            Color bgColor = colStyle.getFillForegroundColorColor();
                            if (bgColor != null) {
                                XSSFColor xssfColor = (XSSFColor) bgColor;
                                if (xssfColor.getARGBHex() != null && xssfColor.getARGBHex().equals(STOP_DEDUCTION_COLOR)) {
                                    isRedCol = true;
                                }
                            }
                        }
                        //System.out.println(colName);
                        if (colName != null && colName.toLowerCase().equals("name")) {
                            nameCol = x;
                        }
                        if (colName != null && colName.toLowerCase().equals("post")) {
                            postCol = x;
                        }
                        if (colName != null && colName.toLowerCase().equals("remarks") && !isRedCol) {
                            remarksCol = x;
                            //System.out.println("["+isRedCol+"] remarks: "+remarksCol);
                        }
                        if (colName != null && colName.toLowerCase().equals("loan amount")) {
                            loanAmtCol = x;
                        }
                        if (colName != null && colName.toLowerCase().equals("amortization")) {
                            amortCol = x;
                        }
                        if (colName != null && colName.toLowerCase().equals("loan date")) {
                            loanDateCol = x;
                        }
                        if (colName != null && colName.toLowerCase().equals("balance")) {
                            balanceCol = x;
                        }
                        if (colName != null && colName.toLowerCase().equals("total deducted")) {
                            totalDeductedCol = x;
                        }
                    }

                    if(nameCol != -1) {
                        break;
                    }
                }
                int stopped = 0;
                if (lastRowNum == -1) {
                    // Sheet is empty
                    //System.out.println("Sheet is empty");
                } else {
                    for (int x = 2; x <= lastRowNum; x++) {
                        //System.out.println("Row X: "+(x+1));
                        String empName = nameCol != -1 ? (String) ExcelUtils.getCellData(x, nameCol) : null;
                        if(empName != null) {
                            empName = empName.trim();
                        }
                        String postName = postCol != -1 ? (String) ExcelUtils.getCellData(x, postCol) : null;
                        if(postName != null) {
                            postName = postName.trim();
                        }
                        String remarks = remarksCol != -1 ? (String) ExcelUtils.getCellData(x, remarksCol) : null;
                        if(remarks != null) {
                            remarks = remarks.trim();
                        }
                        String loanAmount = loanAmtCol != -1 ? (String) ExcelUtils.getCellData(x, loanAmtCol) : null;
                        String amortization = amortCol != -1 ? (String) ExcelUtils.getCellData(x, amortCol) : null;
                        String loanDate = loanDateCol != -1 ? ExcelUtils.getCellDate(x, loanDateCol) : null;
                        String balance = balanceCol != -1 ? ExcelUtils.getEvaluatedValue(x, balanceCol) : null;
                        String totalDeducted = totalDeductedCol != -1 ? ExcelUtils.getEvaluatedValue(x, totalDeductedCol) : null;
                        CellStyle nameStyle = nameCol != -1 ? ExcelUtils.getCellStyle(x, nameCol) : null;
                        boolean skip = false;
                        Color nameColor = null;
                        XSSFColor fontColor = null;
                        if(empName == null || empName.isEmpty()) {
                            skip = true;
                        }
                        else if(empName.equalsIgnoreCase("name") || loanAmount.equalsIgnoreCase("loan amount")) {
                            skip = true;
                        }
                        if(nameStyle != null) {
                            nameColor = nameStyle.getFillForegroundColorColor();
                            Font nameFont = ExcelUtils.workbook.getFontAt(nameStyle.getFontIndex());
                            XSSFFont xssfFont = (XSSFFont) nameFont;
                            fontColor = xssfFont.getXSSFColor();
                            if(fontColor != null && fontColor.getARGBHex().equals(STOP_DEDUCTION_COLOR)) {
                                skip = true;
                            }
                            if(nameColor != null) {
                                XSSFColor xssfColor = (XSSFColor) nameColor;
                                if(xssfColor.getARGBHex() != null && xssfColor.getARGBHex().equals(STOP_DEDUCTION_COLOR)) {
                                    skip = true;
                                }
                            }
                        } else {
                            skip = true;
                        }
                        /*else {
                            if(nameStyle != null) {
                                if(nameColor != null) {
                                    if(((XSSFColor) nameColor).getARGBHex().equals(STOP_DEDUCTION_COLOR)) {
                                        System.out.println("STOP DEDUCTION: true");
                                        skip = true;
                                    }
                                    System.out.println("nameStyle Color: " + ((XSSFColor) nameColor).getARGBHex());
                                }
                                if(nameFont != null) {
                                    XSSFFont xssfFont = (XSSFFont) nameFont;
                                    XSSFColor fontColor = xssfFont.getXSSFColor();
                                    if(fontColor != null) {
                                        String fontColorHex = fontColor.getARGBHex();
                                        System.out.println("XSSF Font Color: " + fontColorHex);
                                    }
                                }
                            }
                        }*/
                        if(!skip) {
                            //System.out.println("INSIDE IF----");
                            if(nameColor != null) {
                                XSSFColor xssfColor = (XSSFColor) nameColor;
                                if(xssfColor.getARGBHex() != null) {
                                    //System.out.println("BG Color: " + xssfColor.getARGBHex());
                                }
                            }
                            if(fontColor != null) {
                                //System.out.println("Font Color: " + fontColor.getARGBHex());
                            }

                            //System.out.println("EMPLOYEE: [" + empName+"]");
                            //System.out.println("LOAN AMOUNT: " + loanAmount);
                            //System.out.println("AMORTIZATION: " + amortization);
                            //System.out.println("LOAN DATE: " + loanDate);
                            //System.out.println("BALANCE: " + balance);
                            //System.out.println("TOTAL DEDUCTED: " + totalDeducted);
                            String _stripedEmpName = stripMiddleName(empName);
                            Date parsedLoanDate = getParsedDate(loanDate);
                            //System.out.println("Stripped Middle name: "+_stripedEmpName);
                            List<ErpEmployee> _empMatched = employeeService.findByFullName(_stripedEmpName);
                            List<GovernmentLoan> _loansMatched = null;
                            if(parsedLoanDate != null && _empMatched != null && !_empMatched.isEmpty()) {
                                _loansMatched = governmentLoanService.filterLoansByDateEmployeeSSS(parsedLoanDate, _empMatched.get(0));
                            }
                            //if(_empMatched != null)
                                //System.out.println("Matched employees: "+_empMatched.size());
                            //System.out.println("--------------------------------");
                            SSSUpload _temp = new SSSUpload();
                            _temp.setEmployeeName(empName);
                            _temp.setPost(postName);
                            _temp.setRemarks(remarks);
                            _temp.setSheetName(ExcelUtils.worksheet.getSheetName());
                            _temp.setLoanAmount(getNumString(loanAmount));
                            _temp.setAmortization(getNumString(amortization));
                            _temp.setBalance(getNumString(balance));
                            _temp.setTotalDeducted(getNumString(totalDeducted));
                            _temp.setLoanDate(parsedLoanDate);
                            String[] fullName = stripName(empName);
                            _temp.setFirstName(fullName[0]);
                            _temp.setLastName(fullName[1]);
                            _temp.setEmployeeMatched((_empMatched != null && !_empMatched.isEmpty()));
                            if(_empMatched != null && !_empMatched.isEmpty()) {
                                _temp.setErpEmployee(_empMatched.get(0));
                                _temp.setSelectedRecord(true);
                            } else {
                                _temp.setSelectedRecord(false);
                            }
                            if(_loansMatched != null && !_loansMatched.isEmpty()) {
                                _temp.setSelectedRecord(false);
                                _temp.setExistingLoan(true);
                            }
                            sssUpload.add(_temp);
                            stopped = x + 1;
                        }
                        //System.out.println("Stopped: "+stopped);
                    }
                }
                //System.out.println("["+ExcelUtils.worksheet.getSheetName()+"] STOPPED AT ROW [" + (stopped) + "]");
                ExcelUtils.nextSheetUploadUtility();
            }
        } else {
            addDetailMessage("FAILED UPLOAD", file.getFileName() + " is Empty.", FacesMessage.SEVERITY_ERROR);
        }
    }

    public void save() throws Exception {
        List<GovernmentLoan> loanList = new ArrayList<>();
        List<GovernmentLoan> deleteList = new ArrayList<>();
        for(SSSUpload upl: sssUpload) {
            if(!upl.getSelectedRecord()) {
                continue;
            }

            System.out.println("checked["+upl.getSelectedRecord()+"] NAME: "+upl.getEmployeeName());
            List<GovernmentLoan> _loansMatched = null;
            if(upl.getLoanDate() != null && upl.getErpEmployee() != null) {
                _loansMatched = governmentLoanService.filterLoansByDateEmployeeSSS(upl.getLoanDate(), upl.getErpEmployee());
                if(_loansMatched != null && !_loansMatched.isEmpty()) {
                    // delete the records permanently
                    continue;
                }
            }
            // new Government Loan
            GovernmentLoan loan = new GovernmentLoan();
            loan.setBalanceAmount(upl.getBalance());
            loan.setLoanAmount(upl.getLoanAmount());
            loan.setDateStarted(upl.getLoanDate());
            loan.setDeductionAmount(upl.getAmortization());
            loan.setEmployeeBorrower(upl.getErpEmployee());
            loan.setLoanType(GovernmentLoanType.SSS_SALARY_LOAN);
            loanList.add(loan);
        }
        // save loans
        // governmentLoanService.deleteAllGovernmentLoans(deleteList);
        governmentLoanService.saveAll(loanList);
        // reset sssUpload object
        sssUpload = null;
        sssUpload = new ArrayList<>();
    }

    public void cancel() throws Exception {
        sssUpload = null;
        sssUpload = new ArrayList<>();
    }

    public void checkboxTicked(SSSUpload upl) {
        upl.setSelectedRecord(!upl.getSelectedRecord());
        //System.out.println("Value changed: "+upl.getSelectedRecord());
    }

    public static String stripMiddleName(String fullName) {
        String strippedName = fullName.replaceAll("\\b(?i)(Jr|Sr)\\b", "");
        strippedName = strippedName.replaceAll("\\b\\w\\s?\\.", "");
        //String strippedName = fullName.replaceAll("\\b\\w\\s?(?=\\.)", "");
        strippedName = strippedName.replaceAll("\\s+", " ").trim();

        String[] parts = strippedName.split("\\s+"); // split by whitespace

        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (part.length() > 1) {
                // if part has more than one character or starts with uppercase
                builder.append(part).append(" ");
            }
        }

        return builder.toString().trim();
    }

    private String[] stripName(String name) {
        String[] retVal = new String[]{null,null};
        String[] nameParts;
        String firstName = null;
        String lastName = null;

        /*if(name == null || name.isEmpty()) {
            return retVal;
        }*/

        if (name.contains(",")) {
            // Last name first with comma separator
            nameParts = name.split(",");
            if (nameParts.length >= 2) { // Ensure there are at least two parts
                lastName = nameParts[0].trim();
                firstName = nameParts[1].trim();
            } else {
                // Handle invalid format gracefully
                //System.out.println("Invalid format: " + name);
            }
        } else {
            // Regular case, split by whitespace
            name = name.replaceAll(",", "");
            nameParts = name.split("\\s+");
            lastName = nameParts[nameParts.length - 1];
            firstName = name.substring(0, name.length() - lastName.length()).trim();
        }
        retVal[0] = firstName;
        retVal[1] = lastName;
        return retVal;
    }

    private Double getNumString(String str) {
        if(str == null || str.isEmpty()) {
            return null;
        } else {
            str = str.trim();
        }

        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            Number number = format.parse(str);
            return number.doubleValue();
        } catch (Exception e) {
            //System.out.println("Parsed ["+str+"] error format: " + e.getMessage());
            return null;
        }
    }

    private Date getParsedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
