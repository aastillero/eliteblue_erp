package io.eliteblue.erp.core.util;

import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.ScoutLoan;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.ScoutLoanService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Component
public class ScoutCAUtil {

    private final DateFormat dateFormat = new SimpleDateFormat("M/d/yy");

    @Autowired
    private ErpEmployeeService employeeService;

    @Autowired
    private ScoutLoanService scoutLoanService;

    public void startProcess() throws Exception {
        ExcelUtils.initializeWithFilename("CA_SCOUT.xlsx", "Cash Advance 2021");
        Integer rows = ExcelUtils.getRowCount();
        for(int x=51; x < rows; x++) {
            String deployedRaw = (String) ExcelUtils.getCellData(x, 1);
            String dateStr = (String) ExcelUtils.getCellData(x, 3);
            String loanAmtStr = (String) ExcelUtils.getCellData(x, 4);
            //System.out.println("DEDUCT AMT: "+ExcelUtils.evaluateCellFormula(x, 6).getNumberValue());
            //String deductAmtStr = (String) ExcelUtils.evaluateCellFormula(x, 6).getStringValue();
            //String paidAmtStr = (String) ExcelUtils.evaluateCellFormula(x, 8).getStringValue();
            //String balanceAmtStr = (String) ExcelUtils.evaluateCellFormula(x, 9).getStringValue();
            loanAmtStr = loanAmtStr.replaceAll(",", "");
            CellValue paidAmtVal = ExcelUtils.evaluateCellFormula(x, 8);
            CellValue deductAmtVal = ExcelUtils.evaluateCellFormula(x, 6);
            CellValue balanceAmtVal = ExcelUtils.evaluateCellFormula(x, 9);
            //paidAmtStr = paidAmtStr.replaceAll(",", "");
            //deductAmtStr = deductAmtStr.replaceAll(",", "");
            //balanceAmtStr = balanceAmtStr.replaceAll(",", "");
            Double loanAmount = has(loanAmtStr) ? Double.parseDouble(loanAmtStr) : 0.0;
            Double paidAmount = has(paidAmtVal) ? paidAmtVal.getNumberValue() : 0.0;
            Double deductAmount = has(deductAmtVal) ? deductAmtVal.getNumberValue() : 0.0;
            Double balanceAmount = has(balanceAmtVal) ? balanceAmtVal.getNumberValue() : 0.0;
            dateStr = dateStr.replaceAll("//", "/");
            Date startDate = has(dateStr) ? dateFormat.parse(dateStr) : new Date();
            if(has(deployedRaw) && deployedRaw.contains(",")) {
                String firstName = deployedRaw.split(",")[1].trim();
                String lastName = deployedRaw.split(",")[0].trim();
                List<ErpEmployee> emps = employeeService.findByFirstnameAndLastname(firstName, lastName);
                if(emps.size() > 0) {
                    System.out.println("FOUND EMPLOYEE (" + emps.size() + ")");
                    System.out.println("LOAN AMOUNT: "+loanAmount);
                    if(has(startDate)) {
                        System.out.println("START DATE: "+dateFormat.format(startDate));
                    }
                    for(ErpEmployee e: emps) {
                        System.out.println("NAME: " + e.getFirstname() + " " + e.getLastname());
                        ScoutLoan scoutLoan = new ScoutLoan();
                        scoutLoan.setPaidAmount(paidAmount);
                        scoutLoan.setBalanceAmount(balanceAmount);
                        scoutLoan.setDateStarted(startDate);
                        scoutLoan.setDeductionAmount(deductAmount);
                        scoutLoan.setEmployeeBorrower(e);
                        scoutLoan.setLoanAmount(loanAmount);
                        scoutLoan.setLoanType(EmployeeLoanType.CASH_ADVANCE);

                        scoutLoanService.save(scoutLoan);
                        break;
                    }
                }
            }
        }
        ExcelUtils.workbook.close();
    }
}
