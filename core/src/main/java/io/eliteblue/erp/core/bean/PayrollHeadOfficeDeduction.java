package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.PayrollUtil;
import org.omnifaces.util.Faces;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class PayrollHeadOfficeDeduction implements Serializable {

    @Autowired
    private EmployeePayrollService employeePayrollService;

    @Autowired
    private BasicSalaryService basicSalaryService;

    @Autowired
    private SalaryDeductionsService salaryDeductionsService;

    @Autowired
    private HeadOfficeLoanService headOfficeLoanService;

    @Autowired
    private HeadOfficeLoanHistoryService headOfficeLoanHistoryService;

    @Autowired
    private HeadOfficeDeductionsService headOfficeDeductionsService;

    @Autowired
    private PayrollUtil payrollUtil;

    private Long id;
    private Long pId;
    private EmployeePayroll employeePayroll;
    private List<HeadOfficeLoan> headOfficeLoans;
    private Map<String, EmployeeLoanType> headOfficeLoanMap;
    private HeadOfficeLoan selectedOfficeLoan;
    private EmployeeLoanType selectedLoan;
    private Double loanAmount;
    private String selLoan;
    private Map<String, String> loanMap;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            employeePayroll = employeePayrollService.findById(Long.valueOf(id));
            headOfficeLoans = headOfficeLoanService.getLoansByEmployee(employeePayroll.getEmployeePayroll());
            headOfficeLoanMap = new HashMap<>();
            loanMap = new HashMap<>();
            for(HeadOfficeLoan hol: headOfficeLoans) {
                if(hol.getBalanceAmount() <= 0) {
                    continue;
                }

                if(hol.getLoanType().equals(EmployeeLoanType.PARAPHERNALIA)) {
                    headOfficeLoanMap.put(hol.getLoanType().name().replaceAll("_", " ") + " - "+hol.getLoanDescription()+" [₱" + hol.getBalanceAmount() + "]", hol.getLoanType());
                } else {
                    headOfficeLoanMap.put(hol.getLoanType().name().replaceAll("_", " ") + " [₱" + hol.getBalanceAmount() + "]", hol.getLoanType());
                }
                loanMap.put(hol.getLoanType().name().replaceAll("_", " "), hol.getDeductionAmount().toString());
            }
        } else {
            employeePayroll = new EmployeePayroll();
            headOfficeLoans = new ArrayList<>();
        }
        selectedOfficeLoan = new HeadOfficeLoan();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeePayroll getEmployeePayroll() {
        return employeePayroll;
    }

    public void setEmployeePayroll(EmployeePayroll employeePayroll) {
        this.employeePayroll = employeePayroll;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public List<HeadOfficeLoan> getHeadOfficeLoans() {
        return headOfficeLoans;
    }

    public void setHeadOfficeLoans(List<HeadOfficeLoan> headOfficeLoans) {
        this.headOfficeLoans = headOfficeLoans;
    }

    public Map<String, EmployeeLoanType> getHeadOfficeLoanMap() {
        return headOfficeLoanMap;
    }

    public void setHeadOfficeLoanMap(Map<String, EmployeeLoanType> headOfficeLoanMap) {
        this.headOfficeLoanMap = headOfficeLoanMap;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getSelLoan() {
        return selLoan;
    }

    public void setSelLoan(String selLoan) {
        this.selLoan = selLoan;
    }

    public Map<String, String> getLoanMap() {
        return loanMap;
    }

    public void setLoanMap(Map<String, String> loanMap) {
        this.loanMap = loanMap;
    }

    public void clear() {
        employeePayroll = new EmployeePayroll();
        id = null;
    }

    public HeadOfficeLoan getSelectedOfficeLoan() {
        return selectedOfficeLoan;
    }

    public void setSelectedOfficeLoan(HeadOfficeLoan selectedOfficeLoan) {
        this.selectedOfficeLoan = selectedOfficeLoan;
    }

    public EmployeeLoanType getSelectedLoan() {
        return selectedLoan;
    }

    public void setSelectedLoan(EmployeeLoanType selectedLoan) {
        this.selectedLoan = selectedLoan;
    }

    public boolean isNew() {
        return employeePayroll == null || employeePayroll.getId() == null;
    }

    public String editPressed(Long employeePayrollId) {
        return "payroll-employee-form?payrollDetachmentId="+id+"&id="+employeePayrollId+"&faces-redirect=true&includeViewParams=true";
    }

    public String backBtnPressed() { return "payroll-detachment-form.xhtml?id="+ pId +"faces-redirect=true&includeViewParams=true"; }

    public void getDeductionAmount(EmployeeLoanType selectedLoan) {
        //System.out.println("LOAN: "+selectedLoan);
        if(has(selectedLoan)) {
            int x = 0;
            for(Map.Entry<String, String> entry: loanMap.entrySet()) {
                //System.out.println("ENTRY: "+entry.getKey()+" SELECTED:"+selectedLoan.name());
                if(selectedLoan.name().replaceAll("_"," ").equals(entry.getKey())) {
                    selLoan = entry.getValue();
                    selectedOfficeLoan = filterLoanByType(selectedLoan);
                    if(selectedLoan == null) {
                        selectedOfficeLoan = headOfficeLoans.get(x);
                    }
                    //System.out.println("selLoan: "+selLoan);
                    break;
                    //return entry.getValue();
                }
                x++;
            }
        }
        //return "0.00";
    }

    public HeadOfficeLoan filterLoanByType(EmployeeLoanType selectedLoan) {
        for(HeadOfficeLoan ho: headOfficeLoans) {
            if(ho.getLoanType().equals(selectedLoan)) {
                return ho;
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

    public void remove() throws Exception {
        if(has(employeePayroll) && has(employeePayroll.getId())) {
            //String name = detachmentPayroll.getName();
            employeePayrollService.delete(employeePayroll);
            addDetailMessage("PAYROLL EMPLOYEE DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-detachment-form.xhtml?id="+pId);
        }
    }

    public void save() throws Exception {
        if(employeePayroll != null && has(selectedLoan)) {
            SalaryDeductions deductions = employeePayroll.getSalaryDeductions();
            HeadOfficeDeductions hod = new HeadOfficeDeductions();
            HeadOfficeLoanHistory histBalance = new HeadOfficeLoanHistory();
            HeadOfficeLoanHistory histPaid = new HeadOfficeLoanHistory();
            hod.setDeductionAmount(Double.parseDouble(selLoan));
            if(selectedLoan.equals(EmployeeLoanType.PARAPHERNALIA)) {
                hod.setDeductionDescription(selectedLoan.name()+" - "+selectedOfficeLoan.getLoanDescription());
            } else {
                hod.setDeductionDescription(selectedLoan.name());
            }
            hod.setSalaryDeductions(deductions);
            hod.setHeadOfficeLoan(selectedOfficeLoan);
            histBalance.setOfficeLoan(selectedOfficeLoan);
            histBalance.setReason("System Generated from Payroll");
            histPaid.setOfficeLoan(selectedOfficeLoan);
            histPaid.setReason("System Generated from Payroll");
            Double computedBalance = selectedOfficeLoan.getBalanceAmount() - Double.parseDouble(selLoan);
            Double computedPaid = selectedOfficeLoan.getPaidAmount() + Double.parseDouble(selLoan);
            histBalance.setChanges("Balance changed from "+selectedOfficeLoan.getBalanceAmount()+" to "+computedBalance);
            histBalance.setPrevBalanceAmount(selectedOfficeLoan.getBalanceAmount());
            histBalance.setUpdatedBalanceAmount(computedBalance);
            histPaid.setChanges("Paid Amount changed from "+(selectedOfficeLoan.getPaidAmount())+" to "+computedPaid);
            histPaid.setPrevPaidAmount(selectedOfficeLoan.getPaidAmount());
            histPaid.setUpdatedPaidAmount(computedPaid);
            histPaid.setUpdatedBalanceAmount(computedPaid);
            selectedOfficeLoan.setBalanceAmount(computedBalance);
            selectedOfficeLoan.setPaidAmount(computedPaid);
            headOfficeLoanHistoryService.save(histBalance);
            headOfficeLoanHistoryService.save(histPaid);
            headOfficeLoanService.save(selectedOfficeLoan);
            headOfficeDeductionsService.save(hod);
            deductions.getHeadOfficeDeductions().add(hod);
            //System.out.println("SALARY DEDUCTIONS: "+deductions);
            /*if(deductions.getHeadOfficeDeductions() == null) {
                deductions.setHeadOfficeDeductions(new HashSet<>());
                System.out.println("HO DEDUCTIONS: "+deductions.getHeadOfficeDeductions());
            }
            deductions.getHeadOfficeDeductions().add(hod);*/
            employeePayroll = payrollUtil.calculateEmployeePayroll(employeePayroll);
            //System.out.println("Salary Deductions Total: "+employeePayroll.getSalaryDeductions().getTotalDeductions());
            //basicSalaryService.save(employeePayroll.getBasicSalary());
            salaryDeductionsService.save(employeePayroll.getSalaryDeductions());
            employeePayrollService.save(employeePayroll);

            //System.out.println("SELECTED: "+selectedLoan.name());
            //System.out.println("selLoan: "+selLoan);
            addDetailMessage("PAYROLL EMPLOYEE SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-detachment-form.xhtml?id="+pId);
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
