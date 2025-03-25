package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.constants.SalaryType;
import io.eliteblue.erp.core.lazy.LazyErpDetachmentModel;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.DetachmentPayrollService;
import io.eliteblue.erp.core.service.EmployeePayrollService;
import io.eliteblue.erp.core.service.ErpSSSContributionService;
import io.eliteblue.erp.core.util.ExcelUtils;
import io.eliteblue.erp.core.util.PDFUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class PayrollDetachmentForm implements Serializable {

    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private final String[] columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};

    @Autowired
    private DetachmentPayrollService detachmentPayrollService;

    @Autowired
    private EmployeePayrollService employeePayrollService;

    @Autowired
    private ErpSSSContributionService erpSSSContributionService;

    private Long id;
    private DetachmentPayroll detachmentPayroll;
    private List<EmployeePayroll> employeePayrolls;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            detachmentPayroll = detachmentPayrollService.findById(Long.valueOf(id));
            employeePayrolls = new ArrayList<>(detachmentPayroll.getEmployeePayrolls());
            Collections.sort(employeePayrolls, (o1, o2) -> (o1.getEmployeePayroll().getLastname().compareTo(o2.getEmployeePayroll().getLastname())));
        } else {
            detachmentPayroll = new DetachmentPayroll();
            employeePayrolls = new ArrayList<>();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetachmentPayroll getDetachmentPayroll() {
        return detachmentPayroll;
    }

    public void setDetachmentPayroll(DetachmentPayroll detachmentPayroll) {
        this.detachmentPayroll = detachmentPayroll;
    }

    public List<EmployeePayroll> getEmployeePayrolls() {
        return employeePayrolls;
    }

    public void setEmployeePayrolls(List<EmployeePayroll> employeePayrolls) {
        this.employeePayrolls = employeePayrolls;
    }

    public void clear() {
        detachmentPayroll = new DetachmentPayroll();
        id = null;
    }

    public boolean isNew() {
        return detachmentPayroll == null || detachmentPayroll.getId() == null;
    }

    public String editPressed(Long employeePayrollId) {
        return "payroll-employee-form?payrollDetachmentId="+id+"&id="+employeePayrollId+"&faces-redirect=true&includeViewParams=true";
    }

    public Double getHeadOfficeDeductionAmount(String loanType, Object[] deductions) {
        //System.out.println("["+deductions.size()+"] LOAN TYPE: "+loanType);
        for(Object o: deductions) {
            //System.out.println("HEAD OFFICE LOAN TYPE: "+ho.getHeadOfficeLoan().getLoanType());
            //if(ho.getHeadOfficeLoan().getLoanType().equals(EmployeeLoanType.valueOf(loanType))) {
            HeadOfficeDeductions ho = (HeadOfficeDeductions) o;
            if(ho.getDeductionDescription().toLowerCase().contains(loanType.toLowerCase())) {
                return ho.getDeductionAmount();
            }
        }
        return null;
    }

    public Double getScoutDeductionAmount(String loanType, Object[] deductions) {
        for(Object o: deductions) {
            //if(sd.getScoutLoan().getLoanType().equals(EmployeeLoanType.valueOf(loanType))) {
            ScoutDeductions sd = (ScoutDeductions) o;
            if(sd.getDeductionDescription().toLowerCase().contains(loanType.toLowerCase())) {
                return sd.getDeductionAmount();
            }
        }
        return null;
    }

    public String getScoutDeductionParaphernalia(Object[] deductions) {
        for(Object o: deductions) {
            //if(sd.getScoutLoan().getLoanType().equals(EmployeeLoanType.valueOf(loanType))) {
            ScoutDeductions sd = (ScoutDeductions) o;
            if(sd.getDeductionDescription().toLowerCase().contains("paraphernalia")) {
                return sd.getDeductionDescription().replaceAll("PARAPHERNALIA - ", "");
            }
        }
        return null;
    }

    public Double getGovernmentDeductionAmount(String loanType, Object[] deductions) {
        //System.out.println("["+deductions.length+"] LOAN TYPE: "+loanType);
        for(Object o: deductions) {
            GovernmentDeductions gd = (GovernmentDeductions) o;
            //System.out.println("GOVERNMENT LOAN TYPE: "+gd.getDeductionDescription());
            if(gd.getDeductionDescription().equals(loanType)) {
                return gd.getDeductionAmount();
            }
        }
        return null;
    }

    /*public void onRowSelect(SelectEvent<ErpDetachment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+selectedDetachment.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpDetachment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+selectedDetachment.getId());
    }*/

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpDetachment erpDetachment = (ErpDetachment) value;
        return erpDetachment.getName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public void remove() throws Exception {
        if(has(detachmentPayroll) && has(detachmentPayroll.getId())) {
            //String name = detachmentPayroll.getName();
            detachmentPayrollService.delete(detachmentPayroll);
            addDetailMessage("PAYROLL DETACHMENT DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("clients.xhtml");
        }
    }

    public void save() throws Exception {
        if(detachmentPayroll != null) {
            detachmentPayrollService.save(detachmentPayroll);
            addDetailMessage("PAYROLL DETACHMENT SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("clients.xhtml");
        }
    }

    public ErpSSSContribution getContribution(Date coverStart, ErpEmployee emp, Double grossSal) throws Exception {
        ErpSSSContribution retVal = null;
        LocalDate today = convertToLocalDateViaInstant(coverStart);
        LocalDate cutOffStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
        LocalDate cutOffEnd = LocalDate.of(today.getYear(), today.getMonth(), 15);
        // get the previous Employee Payroll
        Date _start = convertToDateViaInstant(cutOffStart);
        Date _end = convertToDateViaInstant(cutOffEnd);
        //System.out.println("DATE START: "+_start+"DATE END: "+_end);
        List<EmployeePayroll> ep = employeePayrollService.findByEmployeeCutoff(emp, _start, _end);
        //System.out.println("EMPLOYEE PAYROLL ep LENGTH: "+ep.size()+" GROSS SAL: "+grossSal);
        if(ep.size() > 0 && has(grossSal)) {
            Double prevGross = ep.get(0).getGrossSalary();
            Double totalGross = prevGross + grossSal;
            //System.out.println("totalGross: "+totalGross);
            List<ErpSSSContribution> contributions = erpSSSContributionService.getAllFromRange(totalGross);
            //System.out.println("CONTRIBUTIONS length: "+contributions.size());
            if(contributions.size() > 0) {
                retVal = contributions.get(0);
            }
        } else {
            // no previous records
            //System.out.println("HAS NO PREVIOUS RECORDS.");
        }
        return retVal;
    }

    public void downloadSSStemplate() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate coverStart = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodStart());
        LocalDate coverEnd = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodEnd());
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("sss_sub_template.xlsx", "Sheet 1");
            System.out.println("Excel Initialization DONE...");
            String coverPeriod = "PAYROLL CUTOFF: " + coverStart.getMonth().name() + " " + coverStart.getDayOfMonth() + "-" + coverEnd.format(DateTimeFormatter.ofPattern("dd,yyyy"));
            System.out.println(coverPeriod);
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            ExcelUtils.setCell(0, 5, "SSS FOR PAYMENT "+coverStart.getMonth().name().toUpperCase());

            int x = 5;
            for (EmployeePayroll ep : employeePayrolls) {
                String fullName = ep.getEmployeePayroll().getLastname().toUpperCase() + ", " + ep.getEmployeePayroll().getFirstname().toUpperCase();
                ExcelUtils.setCell(x, 5, fullName);
                ErpSSSContribution contri = getContribution(convertToDateViaInstant(coverStart), ep.getEmployeePayroll(), ep.getGrossSalary());
                if(has(contri)) {
                    ExcelUtils.setCell(x, 6, contri.getEe());
                    ExcelUtils.setCell(x, 7, contri.getEr());
                    ExcelUtils.setCell(x, 8, (contri.getEe()+contri.getEr()));
                    ExcelUtils.setCell(x, 9, contri.getEc());
                } else {
                    System.out.println(fullName+" has no Contri");
                    ExcelUtils.setCell(x, 6, 0);
                    ExcelUtils.setCell(x, 7, 0);
                    ExcelUtils.setCell(x, 8, 0);
                    ExcelUtils.setCell(x, 9, 0);
                }
                x++;
            }

            // set totals
            CellStyle oldStyle = ExcelUtils.getCellStyle(3, 5);
            CellStyle currStyle = ExcelUtils.getCellStyle(5, 6);
            XSSFCellStyle s = ExcelUtils.getWorkBook().createCellStyle();
            XSSFCellStyle s2 = ExcelUtils.getWorkBook().createCellStyle();
            s.cloneStyleFrom(oldStyle);
            s2.cloneStyleFrom(currStyle);
            s.setDataFormat(s2.getDataFormat());
            s.setAlignment(HorizontalAlignment.GENERAL);
            ExcelUtils.setCellFormula(x, 6, "SUM(G6:G"+x+")", s);
            ExcelUtils.setCellFormula(x, 7, "SUM(H6:H"+x+")", s);
            ExcelUtils.setCellFormula(x, 8, "SUM(I6:I"+x+")", s);
            ExcelUtils.setCellFormula(x, 9, "SUM(J6:J"+x+")", s);

            ExcelUtils.evaluateAllCells();

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "SSS_PAYMENT_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadPayslip() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(detachmentPayroll)) {
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");
            PDFUtils.initializePDFDocumentWithFileName("payslip_template.pdf");
            System.out.println("Excel Initialization DONE...");

            int[] name_x = {152, 546, 152, 546};
            int[] name_y = {70, 70, 376, 376};
            int[] totDays_x = {92, 490, 92, 490};
            int[] totDays_y = {103, 103, 409, 409};
            int[] basic_y = {113, 113, 419, 419};
            int[] otpay_y = {123, 123, 429, 429};
            int[] ndpay_y = {134, 134, 440, 440};
            //int[] rdpay_y = {136, 136, 442, 442};
            //int[] rdpay12_y = {146, 146, 452, 452};
            int[] rhpay12_y = {144, 144, 450, 450};
            int[] rhpay_y = {182, 182, 488, 488};
            int[] rhot_y = {155, 155, 461, 461};
            int[] rhnd_y = {167, 167, 473, 473};
            //int[] shpay12_y = {271, 271, 576, 576};
            int[] shpay_y = {178, 178, 484, 484};
            int[] shot_y = {188, 188, 494, 494};
            int[] shnd_y = {196, 196, 502, 502};
            int[] adj_y = {206, 206, 512, 512};
            int[] sil_y = {216, 216, 522, 522};
            int[] allow_y = {226, 226, 532, 532};
            int[] paternity_y = {237, 237, 543, 543};
            int[] gross_y = {249, 249, 555, 555};
            int[] netpay_y = {260, 260, 566, 566};
            int[] recieved_y = {297, 297, 602, 602};
            int[] received_x = {232, 626, 232, 626};
            int[] license_x = {232, 626, 232, 626};
            int[] ca_x = {352, 746, 352, 746};
            int[] deduct_x = {322, 716, 322, 716};
            int labelDeduct = 80;
            int idx = 0;
            NumberFormat nf = NumberFormat.getNumberInstance();
            for (EmployeePayroll ep : employeePayrolls) {
                //System.out.println("IDX ["+idx+"]");
                String fullName = StringUtils.capitalize(ep.getEmployeePayroll().getLastname().toLowerCase()) + ", " + StringUtils.capitalize(ep.getEmployeePayroll().getFirstname().toLowerCase());
                PDFUtils.setFont(PDType1Font.HELVETICA_BOLD, 9);
                PDFUtils.setFontColor(1f,0.137f,0.141f);
                PDFUtils.appendText(name_x[idx], name_y[idx], fullName);
                PDFUtils.setFont(PDType1Font.HELVETICA, 6);
                PDFUtils.setFontColor(0f,0f,0f);
                PDFUtils.appendText(totDays_x[idx], totDays_y[idx], nf.format(ep.getBasicSalary().getTotalDaysWorked()));
                Double basicSalary = ep.getBasicSalary().getBasicSalary();
                PDFUtils.appendText(totDays_x[idx], basic_y[idx], (basicSalary != null) ? nf.format(basicSalary) : "-");
                Double otPay = ep.getBasicSalary().getOt_pay();
                PDFUtils.appendText(totDays_x[idx], otpay_y[idx], (otPay != null) ? nf.format(otPay) : "-");
                Double ndPay = ep.getBasicSalary().getNd_pay();
                PDFUtils.appendText(totDays_x[idx], ndpay_y[idx], (ndPay != null) ? nf.format(ndPay) : "-");
                Double allowance = ep.getAllowance();
                PDFUtils.appendText(totDays_x[idx], allow_y[idx], (allowance != null) ? nf.format(allowance) : "-");
                Double rdPay = null;
                //PDFUtils.appendText(totDays_x[idx], rdpay_y[idx], (rdPay != null) ? nf.format(rdPay) : "-");
                Double rdPay12 = null;
                //PDFUtils.appendText(totDays_x[idx], rdpay12_y[idx], (rdPay12 != null) ? nf.format(rdPay12) : "-");
                Double rhPay12 = ep.getBasicSalary().getRh_pay();
                PDFUtils.appendText(totDays_x[idx], rhpay12_y[idx], (rhPay12 != null && rhPay12 > 0) ? nf.format(rhPay12) : "-");
                Double rhPay = null;
                //PDFUtils.appendText(totDays_x[idx], rhpay_y[idx], (rhPay != null) ? nf.format(rhPay) : "-");
                Double rhOt = ep.getBasicSalary().getRh_ot_pay();
                PDFUtils.appendText(totDays_x[idx], rhot_y[idx], (rhOt != null && rhOt > 0) ? nf.format(rhOt) : "-");
                Double rhNd = ep.getBasicSalary().getRh_nightdiff_pay();
                PDFUtils.appendText(totDays_x[idx], rhnd_y[idx], (rhNd != null && rhNd > 0) ? nf.format(rhNd) : "-");
                Double paternity = ep.getPaternityLeave();
                PDFUtils.appendText(totDays_x[idx], paternity_y[idx], (paternity != null) ? nf.format(paternity) : "-");
                Double shPay12 = ep.getBasicSalary().getSh_pay();
                //PDFUtils.appendText(totDays_x[idx], shpay12_y[idx], (shPay12 != null && shPay12 > 0) ? nf.format(shPay12) : "-");
                Double shPay = null;
                PDFUtils.appendText(totDays_x[idx], shpay_y[idx], (shPay != null) ? nf.format(shPay) : "-");
                Double shOt = ep.getBasicSalary().getSh_ot_pay();
                PDFUtils.appendText(totDays_x[idx], shot_y[idx], (shOt != null && shOt > 0) ? nf.format(shOt) : "-");
                Double shNd = ep.getBasicSalary().getSh_nightdiff_pay();
                PDFUtils.appendText(totDays_x[idx], shnd_y[idx], (shNd != null && shNd > 0) ? nf.format(shNd) : "-");
                Double sil = ep.getSil();
                PDFUtils.appendText(totDays_x[idx], sil_y[idx], (sil != null) ? nf.format(sil) : "-");
                Double adj = ep.getAdjustments();
                PDFUtils.appendText(totDays_x[idx], adj_y[idx], (adj != null) ? nf.format(adj) : "-");
                Double gross = ep.getGrossSalary();
                PDFUtils.appendText(totDays_x[idx], gross_y[idx], (gross != null) ? nf.format(gross) : "-");
                PDFUtils.setFont(PDType1Font.HELVETICA_BOLD, 8);
                PDFUtils.setFontColor(1f,0.137f,0.141f);
                Double netPay = ep.getTotalNetPay();
                PDFUtils.appendText(totDays_x[idx], netpay_y[idx], (netPay != null) ? nf.format(netPay) : "-");

                PDFUtils.setFont(PDType1Font.HELVETICA, 6);
                PDFUtils.setFontColor(0f,0f,0f);
                Double license = getHeadOfficeDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, totDays_y[idx], "License Renewal");
                PDFUtils.appendText(license_x[idx], totDays_y[idx], (license != null) ? nf.format(license) : "-");
                Double firearms = getHeadOfficeDeductionAmount("FIRE_ARMS_BOND", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, basic_y[idx], "Firearms Bond");
                PDFUtils.appendText(license_x[idx], basic_y[idx], (firearms != null) ? nf.format(firearms) : "-");
                Double sss = ep.getSalaryDeductions().getSssPremium();
                PDFUtils.appendText(license_x[idx]-labelDeduct, otpay_y[idx], "SSS");
                PDFUtils.appendText(license_x[idx], otpay_y[idx], (sss != null) ? nf.format(sss) : "-");
                Double sssLoan = getGovernmentDeductionAmount("SSS_SALARY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, ndpay_y[idx], "SSS Loan");
                PDFUtils.appendText(license_x[idx], ndpay_y[idx], (sssLoan != null) ? nf.format(sssLoan) : "-");
                Double sssDel = null;
                //PDFUtils.appendText(license_x[idx], allow_y[idx], (sssDel != null) ? nf.format(sssDel) : "-delinquence");
                Double sssCalamity = getGovernmentDeductionAmount("SSS_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, rhpay12_y[idx], "SSS Calamity Loan");
                PDFUtils.appendText(license_x[idx], rhpay12_y[idx], (sssCalamity != null) ? nf.format(sssCalamity) : "-");
                Double hdmf = ep.getSalaryDeductions().getHdmfPremium();
                PDFUtils.appendText(license_x[idx]-labelDeduct, rhot_y[idx], "HDMF");
                PDFUtils.appendText(license_x[idx], rhot_y[idx], (hdmf != null) ? nf.format(hdmf) : "-");
                Double hdmfLoan = getGovernmentDeductionAmount("PAGIBIG_SHORT_TERM_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, rhnd_y[idx], "HDMF Loan");
                PDFUtils.appendText(license_x[idx], rhnd_y[idx], (hdmfLoan != null) ? nf.format(hdmfLoan) : "-");
                Double hdmfCalamity = getGovernmentDeductionAmount("PAGIBIG_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, shpay_y[idx], "HDMF Calamity Loan");
                PDFUtils.appendText(license_x[idx], shpay_y[idx], (hdmfCalamity != null) ? nf.format(hdmfCalamity) : "-");
                Double phic = ep.getSalaryDeductions().getPhicPremium();
                PDFUtils.appendText(license_x[idx]-labelDeduct, shot_y[idx], "PHIC");
                PDFUtils.appendText(license_x[idx], shot_y[idx], (phic != null) ? nf.format(phic) : "-");
                PDFUtils.setFont(PDType1Font.HELVETICA_BOLD, 6);
                PDFUtils.appendText(license_x[idx]-labelDeduct, shnd_y[idx], "HEAD OFFICE DEDUCTIONS");
                PDFUtils.setFont(PDType1Font.HELVETICA, 6);
                Double ca1 = getHeadOfficeDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, adj_y[idx], "CASH ADVANCE");
                PDFUtils.appendText(license_x[idx], adj_y[idx], (ca1 != null) ? ca1.toString() : "-");
                Double training1 = getHeadOfficeDeductionAmount("TRAINING_FEE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, sil_y[idx], "TRAINING");
                PDFUtils.appendText(license_x[idx], sil_y[idx], (training1 != null) ? training1.toString() : "-");
                Double para1 = getHeadOfficeDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, allow_y[idx], "PARAPHERNALIA");
                PDFUtils.appendText(license_x[idx], allow_y[idx], (para1 != null) ? para1.toString() : "-");
                Double others = getHeadOfficeDeductionAmount("OTHERS", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                PDFUtils.appendText(license_x[idx]-labelDeduct, paternity_y[idx], "OTHERS");
                PDFUtils.appendText(license_x[idx], paternity_y[idx], (para1 != null) ? para1.toString() : "-");

                Double overSil = null;
                //PDFUtils.appendText(license_x[idx], paternity_y[idx], (overSil != null) ? nf.format(overSil) : "-over SIL");

                //Double ca1 = getHeadOfficeDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray());
                //PDFUtils.appendText(ca_x[idx], totDays_y[idx], (ca1 != null) ? ca1.toString() : "-CA 1");
                Double ca2 = getScoutDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getScoutDeductions().toArray());
                PDFUtils.setFont(PDType1Font.HELVETICA_BOLD, 6);
                PDFUtils.appendText(ca_x[idx]-labelDeduct, totDays_y[idx], "SCT DEDUCTIONS");
                PDFUtils.setFont(PDType1Font.HELVETICA, 6);
                PDFUtils.appendText(ca_x[idx]-labelDeduct, basic_y[idx], "CASH ADVANCE");
                PDFUtils.appendText(ca_x[idx], basic_y[idx], (ca2 != null) ? nf.format(ca2) : "-");
                Double sct_license = getScoutDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getScoutDeductions().toArray());
                PDFUtils.appendText(ca_x[idx]-labelDeduct, otpay_y[idx], "LICENSE");
                PDFUtils.appendText(ca_x[idx], otpay_y[idx], (sct_license != null) ? nf.format(sct_license) : "-");
                Double para2 = getScoutDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getScoutDeductions().toArray());
                PDFUtils.appendText(ca_x[idx]-labelDeduct, ndpay_y[idx], "PARAPHERNALIA");
                PDFUtils.appendText(ca_x[idx], ndpay_y[idx], (para2 != null) ? nf.format(para2) : "-");

                PDFUtils.setFont(PDType1Font.HELVETICA_BOLD, 8);
                PDFUtils.setFontColor(1f,0.137f,0.141f);
                Double deduct = ep.getSalaryDeductions().getTotalDeductions();
                PDFUtils.appendText(deduct_x[idx], netpay_y[idx], (deduct != null) ? nf.format(deduct) : "-");
                PDFUtils.appendText(received_x[idx], recieved_y[idx], fullName);
                //System.out.println("Appended: "+basicSalary);
                if(idx == 3) {
                    PDFUtils.addPageTemplate();
                    idx = 0;
                } else {
                    idx++;
                }
            }

            //PDFUtils.endText();
            PDFUtils.close();
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generatePDFFile(PDFUtils.clonedDoc, "PAYSLIP_" + detachmentName + today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            PDFUtils.document.close();
            PDFUtils.clonedDoc.close();
        }
    }

    public void downloadPayrollMasterListTemplate() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate coverStart = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodStart());
        LocalDate coverEnd = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodEnd());
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("payroll_master_list.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String coverPeriod = "PAYROLL CUTOFF: " + coverStart.getMonth().name() + " " + coverStart.getDayOfMonth() + "-" + coverEnd.format(DateTimeFormatter.ofPattern("dd,yyyy"));
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");

            int cnt = 1;
            int x = 25;
            Double totalScoutDeductions = 0.0;
            for (EmployeePayroll ep : employeePayrolls) {
                String fullName = ep.getEmployeePayroll().getLastname().toUpperCase() + ", " + ep.getEmployeePayroll().getFirstname().toUpperCase();
                int dateDiff = (ep.getEmployeePayroll().getJoinedDate() != null) ? getDiffYears(ep.getEmployeePayroll().getJoinedDate(), new Date()) : 0;
                String empStatus = ep.getEmployeePayroll().getStatus().name();
                if(empStatus.equals("HIRED")) {
                    empStatus = "ACTIVE";
                }
                ExcelUtils.setCell(x, 0, cnt);
                ExcelUtils.setCell(x, 1, empStatus);
                ExcelUtils.setCell(x, 2, ep.getDetachmentPayroll().getDetachment().getName());
                ExcelUtils.setCell(x, 3, fullName);
                ExcelUtils.setCell(x, 4, ep.getEmployeePayroll().getPosition());
                ExcelUtils.setCell(x, 5, (ep.getEmployeePayroll().getJoinedDate() != null) ? format.format(ep.getEmployeePayroll().getJoinedDate()) : "");
                ExcelUtils.setCell(x, 6, (dateDiff < 1) ? "" : dateDiff);
                ExcelUtils.setCell(x, 7, ep.getBasicSalary().getDailyWage());
                ExcelUtils.setCell(x, 8, ep.getBasicSalary().getHourlyWage());
                ExcelUtils.setCell(x, 9, ep.getBasicSalary().getTotalDaysWorked());
                ExcelUtils.setCell(x, 10, ep.getBasicSalary().getBasicHoursWorked());
                ExcelUtils.setCell(x, 11, ep.getBasicSalary().getTotalHoursWorked());
                ExcelUtils.setCell(x, 12, ep.getBasicSalary().getTotalDaysOT());
                ExcelUtils.setCell(x, 13, ep.getBasicSalary().getTotalDaysND());
                ExcelUtils.setCell(x, 14, ep.getBasicSalary().getBasicSalary());
                ExcelUtils.setCell(x, 15, ep.getBasicSalary().getOt_no_days());
                ExcelUtils.setCell(x, 18, ep.getBasicSalary().getOt_total_hours());
                ExcelUtils.setCell(x, 19, ep.getBasicSalary().getOt_rate());
                ExcelUtils.setCell(x, 20, ep.getBasicSalary().getOt_pay());
                ExcelUtils.setCell(x, 21, ep.getBasicSalary().getNd_no_days());
                ExcelUtils.setCell(x, 22, ep.getBasicSalary().getNd_hourly_rate());
                ExcelUtils.setCell(x, 23, ep.getBasicSalary().getNd_pay());
                Double rh_no_hours = ep.getBasicSalary().getRh_no_days();
                if(rh_no_hours != null && rh_no_hours > 0) {
                    rh_no_hours = rh_no_hours * 12;
                }
                ExcelUtils.setCell(x, 24, rh_no_hours);
                ExcelUtils.setCell(x, 26, ep.getBasicSalary().getRh_excess_ot_hrs());
                ExcelUtils.setCell(x, 28, ep.getBasicSalary().getRh_pay());
                ExcelUtils.setCell(x, 30, ep.getBasicSalary().getRh_ot_rate());
                ExcelUtils.setCell(x, 31, ep.getBasicSalary().getRh_ot_pay());
                ExcelUtils.setCell(x, 32, ep.getBasicSalary().getRh_nightdiff_days());
                ExcelUtils.setCell(x, 33, ep.getBasicSalary().getRh_nightdiff_rate());
                ExcelUtils.setCell(x, 34, ep.getBasicSalary().getRh_nightdiff_pay());
                Double sh_no_hours = ep.getBasicSalary().getSh_no_days();
                if(sh_no_hours != null && sh_no_hours > 0) {
                    sh_no_hours = sh_no_hours * 12;
                }
                ExcelUtils.setCell(x, 35, sh_no_hours);
                ExcelUtils.setCell(x, 37, ep.getBasicSalary().getSh_excess_ot_hrs());
                ExcelUtils.setCell(x, 39, ep.getBasicSalary().getSh_pay());
                ExcelUtils.setCell(x, 41, ep.getBasicSalary().getSh_ot_rate());
                ExcelUtils.setCell(x, 42, ep.getBasicSalary().getSh_ot_pay());
                ExcelUtils.setCell(x, 43, ep.getBasicSalary().getSh_nightdiff_days());
                ExcelUtils.setCell(x, 44, ep.getBasicSalary().getSh_nightdiff_rate());
                ExcelUtils.setCell(x, 45, ep.getBasicSalary().getSh_nightdiff_pay());
                ExcelUtils.setCell(x, 46, ep.getAllowance());
                ExcelUtils.setCell(x, 47, ep.getSil());
                ExcelUtils.setCell(x, 48, ep.getPaternityLeave());
                ExcelUtils.setCell(x, 49, ep.getThirteenthMonth());
                ExcelUtils.setCell(x, 50, ep.getAdjustments());
                ExcelUtils.setCell(x, 51, ep.getGrossSalary());
                ExcelUtils.setCell(x, 52, ep.getSalaryDeductions().getSssPremium());
                Double sssLoan = getGovernmentDeductionAmount("SSS_SALARY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,54, sssLoan);
                Double sssCalamity = getGovernmentDeductionAmount("SSS_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,55, sssCalamity);

                ExcelUtils.setCell(x,57, ep.getSalaryDeductions().getHdmfPremium());
                Double hdmfLoan = getGovernmentDeductionAmount("PAGIBIG_SHORT_TERM_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,58, hdmfLoan);
                Double hdmfCalamity = getGovernmentDeductionAmount("PAGIBIG_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,59, hdmfCalamity);

                ExcelUtils.setCell(x,61, ep.getSalaryDeductions().getPhicPremium());

                ExcelUtils.setCell(x,62, getHeadOfficeDeductionAmount("FIRE_ARMS_BOND", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,63, getHeadOfficeDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,69, getHeadOfficeDeductionAmount("TRAINING_FEE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                //ExcelUtils.setCell(x,54, getHeadOfficeDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                //ExcelUtils.setCell(x,55, getHeadOfficeDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                //ExcelUtils.setCell(x,56, getHeadOfficeDeductionAmount("OTHERS", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));

                ExcelUtils.setCell(x,70, getScoutDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getScoutDeductions().toArray()));
                ExcelUtils.setCell(x,71, getScoutDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getScoutDeductions().toArray()));
                //ExcelUtils.setCell(x,59, getScoutDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getScoutDeductions().toArray()));

                ExcelUtils.setCell(x,84, ep.getSalaryDeductions().getTotalDeductions());
                ExcelUtils.setCell(x,85, ep.getTotalNetPay());

                //compute scout deductions
                for(ScoutDeductions sd: ep.getSalaryDeductions().getScoutDeductions()) {
                    totalScoutDeductions += sd.getDeductionAmount();
                }
                x++;
                cnt++;
            }
            // evaluate total formula
            /*for (int z=7; x < 79; z++) {
                ExcelUtils.evaluateCellFormula(22, z);
            }*/
            // set totals
            CellStyle oldStyle = ExcelUtils.getCellStyle(x, 1);
            XSSFCellStyle s = ExcelUtils.getWorkBook().createCellStyle();
            s.cloneStyleFrom(oldStyle);
            XSSFFont font = ExcelUtils.getWorkBook().createFont();
            DataFormat fmt = ExcelUtils.workbook.createDataFormat();
            font.setBold(true);
            font.setFamily(s.getFont().getFamily());
            font.setItalic(s.getFont().getItalic());
            font.setFontHeightInPoints(s.getFont().getFontHeightInPoints());
            font.setColor(XSSFFont.COLOR_RED);
            s.setFont(font);
            s.setBorderBottom(BorderStyle.MEDIUM);
            s.setDataFormat(fmt.getFormat("#,###.00"));

            ExcelUtils.setCell(14, 2, totalScoutDeductions, s);
            ExcelUtils.setCell(15, 2, employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getTotalDaysWorked() == null ? 0.0 : (item.getBasicSalary().getTotalDaysWorked() * 12)).sum(), s);
            ExcelUtils.setCell(16, 2, employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getTotalDaysWorked() == null ? 0.0 : item.getBasicSalary().getTotalDaysWorked()).sum(), s);
            ExcelUtils.setCell(17, 2, employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getBasicSalary() == null ? 0.0 : item.getBasicSalary().getBasicSalary()).sum(), s);
            ExcelUtils.setCell(18, 2, employeePayrolls.stream().mapToDouble(item->item.getGrossSalary() == null ? 0.0 : item.getGrossSalary()).sum(), s);
            ExcelUtils.setCell(19, 2, employeePayrolls.stream().mapToDouble(item->item.getTotalNetPay() == null ? 0.0 : item.getTotalNetPay()).sum(), s);

            ExcelUtils.evaluateAllCells();

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "PAYROLL_MASTER_LIST_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadRCBCMyWalletTemplate() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("rcbc_savings_template.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");

            int x = 1;
            for (EmployeePayroll ep : employeePayrolls) {
                //if(ep.getEmployeePayroll().getBankName().toUpperCase().equals("RCBC")) {
                    String fullName = ep.getEmployeePayroll().getLastname().toUpperCase() + ", " + ep.getEmployeePayroll().getFirstname().toUpperCase();
                    String empAccNumber = ep.getEmployeePayroll().getBankAccountNumber();
                    Double netPay = ep.getTotalNetPay();
                    if (ep.getEmployeePayroll().getMiddlename() != null && !ep.getEmployeePayroll().getMiddlename().isEmpty()) {
                        fullName = fullName + " " + ep.getEmployeePayroll().getMiddlename().charAt(0) + ".";
                    }
                    ExcelUtils.setCell(x, 2, fullName);
                    ExcelUtils.setCell(x, 0, empAccNumber);
                    ExcelUtils.setCell(x, 1, netPay);
                    x++;
                //}
            }

            CellStyle oldStyle = ExcelUtils.getCellStyle(x, 1);
            XSSFCellStyle s = ExcelUtils.getWorkBook().createCellStyle();
            s.cloneStyleFrom(oldStyle);
            XSSFFont font = ExcelUtils.getWorkBook().createFont();
            font.setBold(true);
            font.setFamily(s.getFont().getFamily());
            font.setItalic(s.getFont().getItalic());
            font.setFontHeightInPoints(s.getFont().getFontHeightInPoints());
            font.setColor(XSSFFont.COLOR_RED);
            s.setFont(font);
            s.setBorderTop(BorderStyle.MEDIUM);

            // Total
            ExcelUtils.setCellFormula(x, 1, "SUM(B2:B"+x+")", s);

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "RCBC_MYWALLET_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadRCBCSavingsTemplate() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("rcbc_savings_template.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");

            int x = 1;
            for (EmployeePayroll ep : employeePayrolls) {
                if(ep.getEmployeePayroll().getBankName().toUpperCase().equals("RCBC")) {
                    String fullName = ep.getEmployeePayroll().getLastname().toUpperCase() + ", " + ep.getEmployeePayroll().getFirstname().toUpperCase();
                    String empAccNumber = ep.getEmployeePayroll().getBankAccountNumber();
                    Double netPay = ep.getTotalNetPay();
                    if (ep.getEmployeePayroll().getMiddlename() != null && !ep.getEmployeePayroll().getMiddlename().isEmpty()) {
                        fullName = fullName + " " + ep.getEmployeePayroll().getMiddlename().charAt(0) + ".";
                    }
                    ExcelUtils.setCell(x, 2, fullName);
                    ExcelUtils.setCell(x, 0, empAccNumber);
                    ExcelUtils.setCell(x, 1, netPay);
                    x++;
                }
            }

            CellStyle oldStyle = ExcelUtils.getCellStyle(x, 1);
            XSSFCellStyle s = ExcelUtils.getWorkBook().createCellStyle();
            s.cloneStyleFrom(oldStyle);
            XSSFFont font = ExcelUtils.getWorkBook().createFont();
            font.setBold(true);
            font.setFamily(s.getFont().getFamily());
            font.setItalic(s.getFont().getItalic());
            font.setFontHeightInPoints(s.getFont().getFontHeightInPoints());
            font.setColor(XSSFFont.COLOR_RED);
            s.setFont(font);
            s.setBorderTop(BorderStyle.MEDIUM);

            // Total
            ExcelUtils.setCellFormula(x, 1, "SUM(B2:B"+x+")", s);

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "RCBC_SAVINGS_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadMetrobankTemplate() throws Exception {
        LocalDate today = LocalDate.now();
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("metrobank_upload_template.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");

            int x = 1;
            for(EmployeePayroll ep: employeePayrolls) {
                if(ep.getEmployeePayroll().getBankName().toUpperCase().contains("MBTC") || ep.getEmployeePayroll().getBankName().toUpperCase().contains("METROBANK")) {
                    String fullName = StringUtils.capitalize(ep.getEmployeePayroll().getLastname().toLowerCase()) + ", " + StringUtils.capitalize(ep.getEmployeePayroll().getFirstname().toLowerCase());
                    String empAccNumber = ep.getEmployeePayroll().getBankAccountNumber();
                    Double netPay = ep.getTotalNetPay();
                    if (ep.getEmployeePayroll().getMiddlename() != null && !ep.getEmployeePayroll().getMiddlename().isEmpty()) {
                        fullName = fullName + " " + ep.getEmployeePayroll().getMiddlename().charAt(0) + ".";
                    }
                    ExcelUtils.setCell(x, 0, fullName);
                    ExcelUtils.setCell(x, 3, empAccNumber);
                    ExcelUtils.setCell(x, 4, netPay);
                    x++;
                }
            }

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "METROBANK_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadHRContriLoansReport() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate coverStart = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodStart());
        LocalDate coverEnd = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodEnd());
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("report_summary_hr.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String coverPeriod = "PAYROLL CUTOFF: "+coverStart.getMonth().name()+" "+coverStart.getDayOfMonth()+"-"+coverEnd.format(DateTimeFormatter.ofPattern("dd,yyyy"));
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");
            ExcelUtils.setCell(2, 1, coverPeriod);
            ExcelUtils.setCell(2, 7, coverPeriod);
            ExcelUtils.setCell(31, 1, coverPeriod);
            ExcelUtils.setCell(31, 7, coverPeriod);
            ExcelUtils.setCell(60, 1, coverPeriod);
            ExcelUtils.setCell(60, 7, coverPeriod);
            ExcelUtils.setCell(89, 1, coverPeriod);
            ExcelUtils.setCell(3, 2, detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(3, 8, detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(32, 2, detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(32, 8, detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(61, 2, detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(61, 8, detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(90, 2, detachmentName.replaceAll("_", " "));

            int x_sss_p = 5;
            int x_sss_sl = 5;
            int x_sss_cl = 34;
            int x_hdmf_stl = 34;
            int x_hdmf_cl = 63;
            int x_hdmf_p = 63;
            int x_phic_p = 92;
            for(EmployeePayroll ep: employeePayrolls) {
                Double sssPremium = ep.getSalaryDeductions().getSssPremium();
                Double sssLoan = getGovernmentDeductionAmount("SSS_SALARY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                Double sssCalamity = getGovernmentDeductionAmount("SSS_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                Double hdmfPremium = ep.getSalaryDeductions().getHdmfPremium();
                Double hdmfLoan = getGovernmentDeductionAmount("PAGIBIG_SHORT_TERM_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                Double hdmfCalamity = getGovernmentDeductionAmount("PAGIBIG_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                Double phicPremium = ep.getSalaryDeductions().getPhicPremium();
                if(sssPremium != null) {
                    ExcelUtils.setCell(x_sss_p, 1, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_sss_p, 3, sssPremium);
                    ExcelUtils.evaluateCell(27, 3);
                    x_sss_p++;
                }
                //System.out.println(ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname()+" SSS LOAN: "+sssLoan);
                if(sssLoan != null) {
                    ExcelUtils.setCell(x_sss_sl, 7, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_sss_sl, 9, sssLoan);
                    ExcelUtils.evaluateCell(27, 9);
                    x_sss_sl++;
                }
                if(sssCalamity != null) {
                    ExcelUtils.setCell(x_sss_cl, 1, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_sss_cl, 3, sssCalamity);
                    ExcelUtils.evaluateCell(56, 3);
                    x_sss_cl++;
                }
                if(hdmfLoan != null) {
                    ExcelUtils.setCell(x_hdmf_stl, 7, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_hdmf_stl, 9, hdmfLoan);
                    ExcelUtils.evaluateCell(56, 9);
                    x_hdmf_stl++;
                }
                if(hdmfCalamity != null) {
                    ExcelUtils.setCell(x_hdmf_cl, 1, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_hdmf_cl, 3, hdmfCalamity);
                    ExcelUtils.evaluateCell(85, 3);
                    x_hdmf_cl++;
                }
                if(hdmfPremium != null) {
                    ExcelUtils.setCell(x_hdmf_p, 7, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_hdmf_p, 9, hdmfPremium);
                    ExcelUtils.evaluateCell(85, 9);
                    x_hdmf_p++;
                }
                if(phicPremium != null) {
                    ExcelUtils.setCell(x_phic_p, 1, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(x_phic_p, 3, phicPremium);
                    ExcelUtils.evaluateCell(114, 3);
                    x_phic_p++;
                }
            }

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "SUMMARY_REPORT_HR_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadScoutLoansReport() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate coverStart = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodStart());
        LocalDate coverEnd = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodEnd());
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("report_sct_loans.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String coverPeriod = "Payroll cut-off: "+coverStart.getMonth().name()+" "+coverStart.getDayOfMonth()+"-"+coverEnd.format(DateTimeFormatter.ofPattern("dd,yyyy"));
            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");
            ExcelUtils.setCell(2, 0, "Detachment: "+detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(2, 3, "Detachment: "+detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(2, 6, "Detachment: "+detachmentName.replaceAll("_", " "));
            ExcelUtils.setCell(3, 0, coverPeriod);
            ExcelUtils.setCell(3, 3, coverPeriod);
            ExcelUtils.setCell(3, 6, coverPeriod);

            int xca = 5;
            int xsl = 5;
            int xpa = 5;
            int ca = 0;
            int sl = 3;
            int para = 6;
            for(EmployeePayroll ep: employeePayrolls) {
                Double cash_adv = getScoutDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getScoutDeductions().toArray());
                Double sec_license = getScoutDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getScoutDeductions().toArray());
                Double paraphernalia = getScoutDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getScoutDeductions().toArray());
                if(cash_adv != null) {
                    ExcelUtils.setCell(xca, ca, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(xca,ca+1, cash_adv);
                    ExcelUtils.evaluateCell(29, ca+1);
                    xca++;
                }
                if(sec_license != null) {
                    ExcelUtils.setCell(xsl, sl, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(xsl,sl+1, sec_license);
                    ExcelUtils.evaluateCell(29, sl+1);
                    xsl++;
                }
                if(paraphernalia != null) {
                    ExcelUtils.setCell(xpa, para, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                    ExcelUtils.setCell(xpa,para+1, paraphernalia);
                    ExcelUtils.evaluateCell(29, para+1);
                    ExcelUtils.setCell(xpa, para+2, getScoutDeductionParaphernalia(ep.getSalaryDeductions().getScoutDeductions().toArray()));
                    xpa++;
                }
            }

            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "SUMMARY_REPORT_SCT_LOANS_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadFile() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate coverStart = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodStart());
        LocalDate coverEnd = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodEnd());
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("payroll_list_template.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String coverPeriod = "Covering Period "+coverStart.getMonth().name()+" "+coverStart.getDayOfMonth()+"-"+coverEnd.format(DateTimeFormatter.ofPattern("dd,yyyy"));
            ExcelUtils.setCell(2,9, "Payroll of Security Guards Assigned in "+detachmentPayroll.getDetachment().getName());
            ExcelUtils.setCell(3,8, coverPeriod);

            int x = 10;
            int a = 11;
            DataFormat dataFormat = ExcelUtils.workbook.createDataFormat();
            CellStyle decimalStyle = ExcelUtils.getCellStyle(x, 1);
            XSSFCellStyle ds = ExcelUtils.getWorkBook().createCellStyle();
            ds.cloneStyleFrom(decimalStyle);
            ds.setDataFormat(dataFormat.getFormat("0.00"));
            Collections.sort(employeePayrolls, (o1, o2) -> (o1.getEmployeePayroll().getLastname().compareTo(o2.getEmployeePayroll().getLastname())));
            for(EmployeePayroll ep: employeePayrolls) {
                ExcelUtils.setCell(x,0, ep.getEmployeePayroll().getLastname()+", "+ep.getEmployeePayroll().getFirstname());
                ExcelUtils.setCell(x,1, ep.getBasicSalary().getDailyWage(), ds);
                ExcelUtils.setCell(x,2, ep.getBasicSalary().getHourlyWage(), ds);
                ExcelUtils.setCell(x,3, ep.getBasicSalary().getTotalDaysWorked(), ds);
                ExcelUtils.setCell(x,4, ep.getBasicSalary().getBasicHoursWorked(), ds);
                ExcelUtils.setCell(x,5, ep.getBasicSalary().getTotalHoursWorked(), ds);
                ExcelUtils.setCell(x,6, ep.getBasicSalary().getTotalDaysOT(), ds);
                ExcelUtils.setCell(x,7, ep.getBasicSalary().getTotalDaysND(), ds);
                //ExcelUtils.setCell(x,8, round(ep.getBasicSalary().getBasicSalary(), 2));
                int xf = x+1;
                String basicSalaryFormula = "B"+xf+"*D"+xf;
                if(ep.getEmployeePayroll().getSalaryType().equals(SalaryType.FIXED)) {
                    ExcelUtils.setCell(x,8, ep.getBasicSalary().getBasicSalary(), ds);
                } else {
                    ExcelUtils.setCellFormula(x, 8, basicSalaryFormula);
                }

                ExcelUtils.setCell(x,9, ep.getBasicSalary().getOt_no_days(), ds);
                ExcelUtils.setCell(x,10, ep.getBasicSalary().getOt_total_hours(), ds);
                ExcelUtils.setCell(x,11, ep.getBasicSalary().getOt_rate(), ds);
                //ExcelUtils.setCell(x,12, round(ep.getBasicSalary().getOt_pay(), 2));
                String otPayFormula = "K"+xf+"*L"+xf;
                ExcelUtils.setCellFormula(x,12, otPayFormula);

                ExcelUtils.setCell(x,13, ep.getBasicSalary().getNd_no_days(), ds);
                ExcelUtils.setCell(x,14, ep.getBasicSalary().getNd_total_hours(), ds);
                ExcelUtils.setCell(x,15, ep.getBasicSalary().getNd_hourly_rate(), ds);
                //ExcelUtils.setCell(x,16, round(ep.getBasicSalary().getNd_pay(), 2));
                String ndPayFormula = "O"+xf+"*P"+xf;
                ExcelUtils.setCellFormula(x,16, ndPayFormula);

                ExcelUtils.setCell(x,17, ep.getBasicSalary().getRh_no_days(), ds);
                ExcelUtils.setCell(x,18, ep.getBasicSalary().getRh_excess_ot_hrs(), ds);
                ExcelUtils.setCell(x,19, ep.getBasicSalary().getRh_ot_hrs(), ds);
                //ExcelUtils.setCell(x,20, round(ep.getBasicSalary().getRh_pay(), 2));
                if(ep.getBasicSalary().getRh_no_days() != null && ep.getBasicSalary().getRh_no_days() > 0) {
                    String rhPayFormula = "B"+xf+"*R"+xf;
                    ExcelUtils.setCellFormula(x, 20, rhPayFormula);
                }
                ExcelUtils.setCell(x,21, ep.getBasicSalary().getRh_ot_rate(), ds);
                if(ep.getBasicSalary().getRh_ot_pay() != null && ep.getBasicSalary().getRh_ot_pay() > 0) {
                    //ExcelUtils.setCell(x, 22, round(ep.getBasicSalary().getRh_ot_pay(), 2));
                    String rhOTPayFormula = "T"+xf+"*V"+xf;
                    if(ep.getBasicSalary().getRh_excess_ot_hrs() != null && ep.getBasicSalary().getRh_excess_ot_hrs() > 0) {
                        rhOTPayFormula = "S"+xf+"*V"+xf;
                    }
                    ExcelUtils.setCellFormula(x, 22, rhOTPayFormula);
                }
                ExcelUtils.setCell(x,23, ep.getBasicSalary().getRh_nightdiff_days(), ds);
                ExcelUtils.setCell(x,24, ep.getBasicSalary().getRh_nightdiff_hrs(), ds);
                ExcelUtils.setCell(x,25, ep.getBasicSalary().getRh_nightdiff_rate(), ds);
                if(ep.getBasicSalary().getRh_nightdiff_pay() != null && ep.getBasicSalary().getRh_nightdiff_pay() > 0) {
                    //ExcelUtils.setCell(x, 26, round(ep.getBasicSalary().getRh_nightdiff_pay(), 2));
                    String rhNdiffPayFormula = "Y"+xf+"*Z"+xf;
                    ExcelUtils.setCellFormula(x, 26, rhNdiffPayFormula);
                }

                ExcelUtils.setCell(x,27, ep.getBasicSalary().getSh_no_days(), ds);
                ExcelUtils.setCell(x,28, ep.getBasicSalary().getSh_excess_ot_hrs(), ds);
                ExcelUtils.setCell(x,29, ep.getBasicSalary().getSh_ot_hrs(), ds);
                //ExcelUtils.setCell(x,30, round(ep.getBasicSalary().getSh_pay(), 2));
                if(ep.getBasicSalary().getSh_pay() != null && ep.getBasicSalary().getSh_pay() > 0) {
                    String shPayFormula = "B"+xf+"*AB"+xf+"*0.3";
                    ExcelUtils.setCellFormula(x, 30, shPayFormula);
                }
                ExcelUtils.setCell(x,31, ep.getBasicSalary().getSh_ot_rate(), ds);
                if(ep.getBasicSalary().getSh_ot_pay() != null && ep.getBasicSalary().getSh_ot_pay() > 0) {
                    //ExcelUtils.setCell(x, 32, round(ep.getBasicSalary().getSh_ot_pay(), 2));
                    String shOTPayFormula = "AD"+xf+"*AF"+xf;
                    if(ep.getBasicSalary().getSh_excess_ot_hrs() != null && ep.getBasicSalary().getSh_excess_ot_hrs() > 0) {
                        shOTPayFormula = "AC"+xf+"*AF"+xf;
                    }
                    ExcelUtils.setCellFormula(x, 32, shOTPayFormula);
                }
                ExcelUtils.setCell(x,33, ep.getBasicSalary().getSh_nightdiff_days(), ds);
                ExcelUtils.setCell(x,34, ep.getBasicSalary().getSh_nightdiff_hrs(), ds);
                ExcelUtils.setCell(x,35, ep.getBasicSalary().getSh_nightdiff_rate(), ds);
                //ExcelUtils.setCell(x,36, round(ep.getBasicSalary().getSh_nightdiff_pay(), 2));
                if(ep.getBasicSalary().getSh_nightdiff_pay() != null && ep.getBasicSalary().getSh_nightdiff_pay() > 0) {
                    String shNdiffPayFormula = "AI"+xf+"*AJ"+xf;
                    ExcelUtils.setCellFormula(x,36, shNdiffPayFormula);
                }

                ExcelUtils.setCell(x,37, ep.getEcola(), ds);
                ExcelUtils.setCell(x,38, ep.getAllowance(), ds);
                ExcelUtils.setCell(x,39, ep.getSil(), ds);
                ExcelUtils.setCell(x,40, ep.getPaternityLeave(), ds);
                ExcelUtils.setCell(x,41, ep.getThirteenthMonth(), ds);
                ExcelUtils.setCell(x,42, ep.getAdjustments(), ds);
                ExcelUtils.setCell(x,43, ep.getBasicSalary().getRd_no_days(), ds);
                ExcelUtils.setCell(x,44, ep.getBasicSalary().getExtra_pay(), ds);
                //ExcelUtils.setCell(x,44, round(ep.getGrossSalary(), 2));
                String grossFormula = "I"+xf+"+M"+xf+"+Q"+xf+"+U"+xf+"+W"+xf+"+AA"+xf+"+AE"+xf+"+AG"+xf+"+AK"+xf+"+AL"+xf+"+AM"+xf+"+AN"+xf+"+AO"+xf+"+AP"+xf+"+AQ"+xf+"+AS"+xf;
                ExcelUtils.setCellFormula(x,45, grossFormula);

                ExcelUtils.setCell(x,46, ep.getSalaryDeductions().getSssPremium(), ds);
                Double sssLoan = getGovernmentDeductionAmount("SSS_SALARY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,47, sssLoan);
                Double sssCalamity = getGovernmentDeductionAmount("SSS_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,48, sssCalamity);

                ExcelUtils.setCell(x,49, ep.getSalaryDeductions().getHdmfPremium(), ds);
                Double hdmfLoan = getGovernmentDeductionAmount("PAGIBIG_SHORT_TERM_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,50, hdmfLoan);
                Double hdmfCalamity = getGovernmentDeductionAmount("PAGIBIG_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,51, hdmfCalamity);

                ExcelUtils.setCell(x,52, ep.getSalaryDeductions().getPhicPremium(), ds);

                ExcelUtils.setCell(x,53, getHeadOfficeDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,54, getHeadOfficeDeductionAmount("FIRE_ARMS_BOND", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,55, getHeadOfficeDeductionAmount("TRAINING_FEE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,56, getHeadOfficeDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,57, getHeadOfficeDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,58, getHeadOfficeDeductionAmount("OTHERS", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));

                ExcelUtils.setCell(x,59, getScoutDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getScoutDeductions().toArray()));
                ExcelUtils.setCell(x,60, getScoutDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getScoutDeductions().toArray()));
                ExcelUtils.setCell(x,61, getScoutDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getScoutDeductions().toArray()));

                //ExcelUtils.setCell(x,61, round(ep.getSalaryDeductions().getTotalDeductions(), 2));
                String deductionsFormula = "SUM(AU"+xf+":BJ"+xf+")";
                ExcelUtils.setCellFormula(x,62, deductionsFormula);
                String netPayFormula = "AT"+xf+"-BK"+xf;
                ExcelUtils.setCellFormula(x,63, netPayFormula);

                x++;
            }
            int b = x;
            CellStyle oldStyle = ExcelUtils.getCellStyle(x, 1);
            XSSFCellStyle s = ExcelUtils.getWorkBook().createCellStyle();
            s.cloneStyleFrom(oldStyle);
            XSSFFont font = ExcelUtils.getWorkBook().createFont();
            font.setBold(true);
            font.setFamily(s.getFont().getFamily());
            font.setItalic(s.getFont().getItalic());
            font.setFontHeightInPoints(s.getFont().getFontHeightInPoints());
            font.setColor(XSSFFont.COLOR_RED);
            s.setFont(font);
            //s.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            //s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //ExcelUtils.setCell(x,1, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getDailyWage() == null ? 0.0 : item.getBasicSalary().getDailyWage()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 1, "SUM(B"+a+":B"+b+")", s);
            //ExcelUtils.setCell(x,2, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getHourlyWage() == null ? 0.0 : item.getBasicSalary().getHourlyWage()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 2, "SUM(C"+a+":C"+b+")", s);
            //ExcelUtils.setCell(x,3, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getTotalDaysWorked() == null ? 0.0 : item.getBasicSalary().getTotalDaysWorked()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 3, "SUM(D"+a+":D"+b+")", s);
            //ExcelUtils.setCell(x,4, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getBasicHoursWorked() == null ? 0.0 : item.getBasicSalary().getBasicHoursWorked()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 4, "SUM(E"+a+":E"+b+")", s);
            //ExcelUtils.setCell(x,5, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getTotalHoursWorked() == null ? 0.0 : item.getBasicSalary().getTotalHoursWorked()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 5, "SUM(F"+a+":F"+b+")", s);
            //ExcelUtils.setCell(x,6, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getTotalDaysOT() == null ? 0.0 : item.getBasicSalary().getTotalDaysOT()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 6, "SUM(G"+a+":G"+b+")", s);
            //ExcelUtils.setCell(x,7, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getTotalDaysND() == null ? 0.0 : item.getBasicSalary().getTotalDaysND()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 7, "SUM(H"+a+":H"+b+")", s);
            //ExcelUtils.setCell(x,8, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getBasicSalary() == null ? 0.0 : item.getBasicSalary().getBasicSalary()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 8, "SUM(I"+a+":I"+b+")", s);

            //ExcelUtils.setCell(x,9, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getOt_no_days() == null ? 0.0 : item.getBasicSalary().getOt_no_days()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 9, "SUM(J"+a+":J"+b+")", s);
            //ExcelUtils.setCell(x,10, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getOt_total_hours() == null ? 0.0 : item.getBasicSalary().getOt_total_hours()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 10, "SUM(K"+a+":K"+b+")", s);
            //ExcelUtils.setCell(x,11, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getOt_rate() == null ? 0.0 : item.getBasicSalary().getOt_rate()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 11, "SUM(L"+a+":L"+b+")", s);
            //ExcelUtils.setCell(x,12, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getOt_pay() == null ? 0.0 : item.getBasicSalary().getOt_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 12, "SUM(M"+a+":M"+b+")", s);

            //ExcelUtils.setCell(x,13, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getNd_no_days() == null ? 0.0 : item.getBasicSalary().getNd_no_days()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 13, "SUM(N"+a+":N"+b+")", s);
            //ExcelUtils.setCell(x,14, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getNd_total_hours() == null ? 0.0 : item.getBasicSalary().getNd_total_hours()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 14, "SUM(O"+a+":O"+b+")", s);
            //ExcelUtils.setCell(x,15, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getNd_hourly_rate() == null ? 0.0 : item.getBasicSalary().getNd_hourly_rate()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 15, "SUM(P"+a+":P"+b+")", s);
            //ExcelUtils.setCell(x,16, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getNd_pay() == null ? 0.0 : item.getBasicSalary().getNd_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 16, "SUM(Q"+a+":Q"+b+")", s);

            //ExcelUtils.setCell(x,17, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_no_days() == null ? 0.0 : item.getBasicSalary().getRh_no_days()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 17, "SUM(R"+a+":R"+b+")", s);
            //ExcelUtils.setCell(x,18, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_excess_ot_hrs() == null ? 0.0 : item.getBasicSalary().getRh_excess_ot_hrs()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 18, "SUM(S"+a+":S"+b+")", s);
            //ExcelUtils.setCell(x,19, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_ot_hrs() == null ? 0.0 : item.getBasicSalary().getRh_ot_hrs()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 19, "SUM(T"+a+":T"+b+")", s);
            //ExcelUtils.setCell(x,20, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_pay() == null ? 0.0 : item.getBasicSalary().getRh_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 20, "SUM(U"+a+":U"+b+")", s);
            //ExcelUtils.setCell(x,21, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_ot_rate() == null ? 0.0 : item.getBasicSalary().getRh_ot_rate()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 21, "SUM(V"+a+":V"+b+")", s);
            //ExcelUtils.setCell(x,22, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_ot_pay() == null ? 0.0 : item.getBasicSalary().getRh_ot_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 22, "SUM(W"+a+":W"+b+")", s);
            //ExcelUtils.setCell(x,23, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_nightdiff_days() == null ? 0.0 : item.getBasicSalary().getRh_nightdiff_days()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 23, "SUM(X"+a+":X"+b+")", s);
            //ExcelUtils.setCell(x,24, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_nightdiff_hrs() == null ? 0.0 : item.getBasicSalary().getRh_nightdiff_hrs()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 24, "SUM(Y"+a+":Y"+b+")", s);
            //ExcelUtils.setCell(x,25, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_nightdiff_rate() == null ? 0.0 : item.getBasicSalary().getRh_nightdiff_rate()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 25, "SUM(Z"+a+":Z"+b+")", s);
            //ExcelUtils.setCell(x,26, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getRh_nightdiff_pay() == null ? 0.0 : item.getBasicSalary().getRh_nightdiff_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 26, "SUM(AA"+a+":AA"+b+")", s);

            //ExcelUtils.setCell(x,27, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_no_days() == null ? 0.0 : item.getBasicSalary().getSh_no_days()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 27, "SUM(AB"+a+":AB"+b+")", s);
            //ExcelUtils.setCell(x,28, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_excess_ot_hrs() == null ? 0.0 : item.getBasicSalary().getSh_excess_ot_hrs()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 28, "SUM(AC"+a+":AC"+b+")", s);
            //ExcelUtils.setCell(x,29, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_ot_hrs() == null ? 0.0 : item.getBasicSalary().getSh_ot_hrs()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 29, "SUM(AD"+a+":AD"+b+")", s);
            //ExcelUtils.setCell(x,30, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_pay() == null ? 0.0 : item.getBasicSalary().getSh_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 30, "SUM(AE"+a+":AE"+b+")", s);
            //ExcelUtils.setCell(x,31, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_ot_rate() == null ? 0.0 : item.getBasicSalary().getSh_ot_rate()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 31, "SUM(AF"+a+":AF"+b+")", s);
            //ExcelUtils.setCell(x,32, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_ot_pay() == null ? 0.0 : item.getBasicSalary().getSh_ot_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 32, "SUM(AG"+a+":AG"+b+")", s);
            //ExcelUtils.setCell(x,33, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_nightdiff_days() == null ? 0.0 : item.getBasicSalary().getSh_nightdiff_days()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 33, "SUM(AH"+a+":AH"+b+")", s);
            //ExcelUtils.setCell(x,34, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_nightdiff_hrs() == null ? 0.0 : item.getBasicSalary().getSh_nightdiff_hrs()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 34, "SUM(AI"+a+":AI"+b+")", s);
            //ExcelUtils.setCell(x,35, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_nightdiff_rate() == null ? 0.0 : item.getBasicSalary().getSh_nightdiff_rate()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 35, "SUM(AJ"+a+":AJ"+b+")", s);
            //ExcelUtils.setCell(x,36, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getSh_nightdiff_pay() == null ? 0.0 : item.getBasicSalary().getSh_nightdiff_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 36, "SUM(AK"+a+":AK"+b+")", s);

            //ExcelUtils.setCell(x,37, round(employeePayrolls.stream().mapToDouble(item->item.getEcola() == null ? 0.0 : item.getEcola()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 37, "SUM(AL"+a+":AL"+b+")", s);
            //ExcelUtils.setCell(x,38, round(employeePayrolls.stream().mapToDouble(item->item.getAllowance() == null ? 0.0 : item.getAllowance()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 38, "SUM(AM"+a+":AM"+b+")", s);
            //ExcelUtils.setCell(x,39, round(employeePayrolls.stream().mapToDouble(item->item.getSil() == null ? 0.0 : item.getSil()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 39, "SUM(AN"+a+":AN"+b+")", s);
            //ExcelUtils.setCell(x,40, round(employeePayrolls.stream().mapToDouble(item->item.getPaternityLeave() == null ? 0.0 : item.getPaternityLeave()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 40, "SUM(AO"+a+":AO"+b+")", s);
            //ExcelUtils.setCell(x,41, round(employeePayrolls.stream().mapToDouble(item->item.getThirteenthMonth() == null ? 0.0 : item.getThirteenthMonth()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 41, "SUM(AP"+a+":AP"+b+")", s);
            //ExcelUtils.setCell(x,42, round(employeePayrolls.stream().mapToDouble(item->item.getAdjustments() == null ? 0.0 : item.getAdjustments()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 42, "SUM(AQ"+a+":AQ"+b+")", s);
            //ExcelUtils.setCell(x,43, round(employeePayrolls.stream().mapToDouble(item->item.getBasicSalary().getExtra_pay() == null ? 0.0 : item.getBasicSalary().getExtra_pay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 43, "SUM(AR"+a+":AR"+b+")", s);
            //ExcelUtils.setCell(x,44, round(employeePayrolls.stream().mapToDouble(item->item.getGrossSalary() == null ? 0.0 : item.getGrossSalary()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 44, "SUM(AS"+a+":AS"+b+")", s);

            //ExcelUtils.setCell(x,45, round(employeePayrolls.stream().mapToDouble(item->item.getSalaryDeductions().getSssPremium() == null ? 0.0 : item.getSalaryDeductions().getSssPremium()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 45, "SUM(AT"+a+":AT"+b+")", s);
            //ExcelUtils.setCell(x,46, round(employeePayrolls.stream().mapToDouble(item->item.getSalaryDeductions().getHdmfPremium() == null ? 0.0 : item.getSalaryDeductions().getHdmfPremium()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 46, "SUM(AU"+a+":AU"+b+")", s);
            ExcelUtils.setCellFormula(x, 47, "SUM(AV"+a+":AV"+b+")", s);
            ExcelUtils.setCellFormula(x, 48, "SUM(AW"+a+":AW"+b+")", s);
            ExcelUtils.setCellFormula(x, 49, "SUM(AX"+a+":AX"+b+")", s);
            ExcelUtils.setCellFormula(x, 50, "SUM(AY"+a+":AY"+b+")", s);
            //ExcelUtils.setCell(x,51, round(employeePayrolls.stream().mapToDouble(item->item.getSalaryDeductions().getPhicPremium() == null ? 0.0 : item.getSalaryDeductions().getPhicPremium()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 51, "SUM(AZ"+a+":AZ"+b+")", s);
            ExcelUtils.setCellFormula(x, 52, "SUM(BA"+a+":BA"+b+")", s);
            ExcelUtils.setCellFormula(x, 53, "SUM(BB"+a+":BB"+b+")", s);
            ExcelUtils.setCellFormula(x, 54, "SUM(BC"+a+":BC"+b+")", s);
            ExcelUtils.setCellFormula(x, 55, "SUM(BD"+a+":BD"+b+")", s);
            ExcelUtils.setCellFormula(x, 56, "SUM(BE"+a+":BE"+b+")", s);
            ExcelUtils.setCellFormula(x, 57, "SUM(BF"+a+":BF"+b+")", s);
            ExcelUtils.setCellFormula(x, 58, "SUM(BG"+a+":BG"+b+")", s);
            ExcelUtils.setCellFormula(x, 59, "SUM(BH"+a+":BH"+b+")", s);
            ExcelUtils.setCellFormula(x, 60, "SUM(BI"+a+":BI"+b+")", s);

            //ExcelUtils.setCell(x,61, round(employeePayrolls.stream().mapToDouble(item->item.getSalaryDeductions().getTotalDeductions() == null ? 0.0 : item.getSalaryDeductions().getTotalDeductions()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 61, "SUM(BJ"+a+":BJ"+b+")", s);
            //ExcelUtils.setCell(x,62, round(employeePayrolls.stream().mapToDouble(item->item.getTotalNetPay() == null ? 0.0 : item.getTotalNetPay()).sum(), 2), s);
            ExcelUtils.setCellFormula(x, 62, "SUM(BK"+a+":BK"+b+")", s);
            ExcelUtils.setCellFormula(x, 63, "SUM(BL"+a+":BL"+b+")", s);

            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "PAYROLL_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-list.xhtml");
        }
    }

    public void downloadPayrollListForClient() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate coverStart = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodStart());
        LocalDate coverEnd = convertToLocalDateViaInstant(detachmentPayroll.getErpPayroll().getCoverPeriodEnd());
        if(has(detachmentPayroll)) {
            ExcelUtils.initializeWithFilename("payroll_list_client.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String coverPeriod = coverStart.getMonth().name()+" "+coverStart.getDayOfMonth()+"-"+coverEnd.format(DateTimeFormatter.ofPattern("dd,yyyy"));
            ExcelUtils.setCell(2,3, capitalizeString(detachmentPayroll.getDetachment().getName().toLowerCase()));
            ExcelUtils.setCell(3,3, StringUtils.capitalize(coverPeriod.toLowerCase()));

            int x = 9;
            int cnt = 1;
            DataFormat dataFormat = ExcelUtils.workbook.createDataFormat();
            CellStyle decimalStyle = ExcelUtils.getCellStyle(x, 1);
            XSSFCellStyle ds = ExcelUtils.getWorkBook().createCellStyle();
            ds.cloneStyleFrom(decimalStyle);
            ds.setDataFormat(dataFormat.getFormat("0.00"));
            Collections.sort(employeePayrolls, (o1, o2) -> (o1.getEmployeePayroll().getLastname().compareTo(o2.getEmployeePayroll().getLastname())));
            for(EmployeePayroll ep: employeePayrolls) {
                String fullName = StringUtils.capitalize(ep.getEmployeePayroll().getLastname().toLowerCase()) + ", " + StringUtils.capitalize(ep.getEmployeePayroll().getFirstname().toLowerCase());
                if (ep.getEmployeePayroll().getMiddlename() != null && !ep.getEmployeePayroll().getMiddlename().isEmpty()) {
                    fullName = fullName + " " + ep.getEmployeePayroll().getMiddlename().charAt(0) + ".";
                }
                ExcelUtils.setCell(x, 0, cnt);
                ExcelUtils.setCell(x,1, fullName);
                ExcelUtils.setCell(x,2, ep.getBasicSalary().getDailyWage(),ds);
                ExcelUtils.setCell(x,3, ep.getBasicSalary().getHourlyWage(),ds);
                ExcelUtils.setCell(x,4, ep.getBasicSalary().getTotalDaysWorked(), ds);
                ExcelUtils.setCell(x,5, ep.getBasicSalary().getBasicHoursWorked(), ds);
                ExcelUtils.setCell(x,6, ep.getBasicSalary().getTotalHoursWorked(), ds);
                ExcelUtils.setCell(x,7, ep.getBasicSalary().getTotalDaysOT(), ds);
                ExcelUtils.setCell(x,8, ep.getBasicSalary().getTotalDaysND(), ds);
                ExcelUtils.setCell(x,9, ep.getBasicSalary().getBasicSalary(), ds);

                ExcelUtils.setCell(x,10, ep.getBasicSalary().getOt_no_days(), ds);
                ExcelUtils.setCell(x,13, ep.getBasicSalary().getOt_total_hours(), ds);
                ExcelUtils.setCell(x,14, ep.getBasicSalary().getOt_rate(), ds);
                ExcelUtils.setCell(x,15, ep.getBasicSalary().getOt_pay(), ds);

                ExcelUtils.setCell(x,16, ep.getBasicSalary().getNd_no_days(), ds);
                //ExcelUtils.setCell(x,15, round(ep.getBasicSalary().getNd_total_hours(), 2));
                ExcelUtils.setCell(x,17, ep.getBasicSalary().getNd_hourly_rate(), ds);
                ExcelUtils.setCell(x,18, ep.getBasicSalary().getNd_pay(), ds);
                ExcelUtils.setCell(x,19, ep.getEcola(), ds);

                ExcelUtils.setCell(x,20, ep.getBasicSalary().getRh_no_days(), ds);
                ExcelUtils.setCell(x,22, ep.getBasicSalary().getRh_excess_ot_hrs(), ds);
                //ExcelUtils.setCell(x,20, round(ep.getBasicSalary().getRh_ot_hrs(), 2));
                ExcelUtils.setCell(x,24, ep.getBasicSalary().getRh_pay(), ds);
                ExcelUtils.setCell(x,26, ep.getBasicSalary().getRh_ot_rate(), ds);
                ExcelUtils.setCell(x,27, ep.getBasicSalary().getRh_ot_pay(), ds);
                ExcelUtils.setCell(x,28, ep.getBasicSalary().getRh_nightdiff_days(), ds);
                //ExcelUtils.setCell(x,25, round(ep.getBasicSalary().getRh_nightdiff_hrs(), 2));
                ExcelUtils.setCell(x,29, ep.getBasicSalary().getRh_nightdiff_rate(), ds);
                ExcelUtils.setCell(x,30, ep.getBasicSalary().getRh_nightdiff_pay(), ds);

                ExcelUtils.setCell(x,31, ep.getBasicSalary().getSh_no_days(), ds);
                ExcelUtils.setCell(x,33, ep.getBasicSalary().getSh_excess_ot_hrs(), ds);
                //ExcelUtils.setCell(x,30, round(ep.getBasicSalary().getSh_ot_hrs(), 2));
                ExcelUtils.setCell(x,35, ep.getBasicSalary().getSh_pay(), ds);
                ExcelUtils.setCell(x,37, ep.getBasicSalary().getSh_ot_rate(), ds);
                ExcelUtils.setCell(x,38, ep.getBasicSalary().getSh_ot_pay(), ds);
                ExcelUtils.setCell(x,39, ep.getBasicSalary().getSh_nightdiff_days(), ds);
                //ExcelUtils.setCell(x,35, round(ep.getBasicSalary().getSh_nightdiff_hrs(), 2));
                ExcelUtils.setCell(x,40, ep.getBasicSalary().getSh_nightdiff_rate(), ds);
                ExcelUtils.setCell(x,41, ep.getBasicSalary().getSh_nightdiff_pay(), ds);

                ExcelUtils.setCell(x,49, ep.getAllowance(), ds);
                ExcelUtils.setCell(x,50, ep.getSil(), ds);
                ExcelUtils.setCell(x,51, ep.getPaternityLeave(), ds);
                ExcelUtils.setCell(x,52, ep.getThirteenthMonth(), ds);
                ExcelUtils.setCell(x,53, ep.getAdjustments(), ds);
                ExcelUtils.setCell(x,54, ep.getGrossSalary(), ds);

                ExcelUtils.setCell(x,55, ep.getSalaryDeductions().getSssPremium(), ds);
                Double sssLoan = getGovernmentDeductionAmount("SSS_SALARY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,57, sssLoan);
                Double sssCalamity = getGovernmentDeductionAmount("SSS_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,58, sssCalamity);

                ExcelUtils.setCell(x,60, ep.getSalaryDeductions().getHdmfPremium(), ds);
                Double hdmfLoan = getGovernmentDeductionAmount("PAGIBIG_SHORT_TERM_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,61, hdmfLoan);
                Double hdmfCalamity = getGovernmentDeductionAmount("PAGIBIG_CALAMITY_LOAN", ep.getSalaryDeductions().getGovernmentDeductions().toArray());
                ExcelUtils.setCell(x,62, hdmfCalamity);

                ExcelUtils.setCell(x,64, ep.getSalaryDeductions().getPhicPremium(), ds);

                ExcelUtils.setCell(x,65, getHeadOfficeDeductionAmount("FIRE_ARMS_BOND", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,66, getHeadOfficeDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,68, getHeadOfficeDeductionAmount("TRAINING_FEE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                //ExcelUtils.setCell(x,55, getHeadOfficeDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                //ExcelUtils.setCell(x,56, getHeadOfficeDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));
                ExcelUtils.setCell(x,72, getHeadOfficeDeductionAmount("OTHERS", ep.getSalaryDeductions().getHeadOfficeDeductions().toArray()));

                ExcelUtils.setCell(x,71, getScoutDeductionAmount("CASH_ADVANCE", ep.getSalaryDeductions().getScoutDeductions().toArray()));
                ExcelUtils.setCell(x,74, getScoutDeductionAmount("SECURITY_LICENSE", ep.getSalaryDeductions().getScoutDeductions().toArray()));
                //ExcelUtils.setCell(x,60, getScoutDeductionAmount("PARAPHERNALIA", ep.getSalaryDeductions().getScoutDeductions().toArray()));

                ExcelUtils.setCell(x,90, ep.getSalaryDeductions().getTotalDeductions(), ds);
                ExcelUtils.setCell(x,91, ep.getTotalNetPay(), ds);

                x++;
                cnt++;
            }

            String detachmentName = detachmentPayroll.getDetachment().getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "PAYROLL_LIST_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-list.xhtml");
        }
    }

    public void generateFile(Workbook workbook, String fileName) throws IOException {
        final FacesContext fc = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = fc.getExternalContext();

        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        final HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        final ServletOutputStream out = response.getOutputStream();

        workbook.write(out);
        workbook.close();
        out.flush();
        fc.responseComplete();
    }

    public void generatePDFFile(PDDocument document, String fileName) throws IOException {
        final FacesContext fc = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = fc.getExternalContext();

        externalContext.responseReset();
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename="+fileName+".pdf");
        final HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        final ServletOutputStream out = response.getOutputStream();

        document.save(out);
        document.close();
        out.flush();
        fc.responseComplete();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date
                .from(dateToConvert.atStartOfDay(ZoneId.systemDefault())
                        .toInstant());
    }

    public static Double round(Double value, int places) {
        if(value == null) {
            return null;
        }

        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
