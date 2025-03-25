package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.constants.GovernmentLoanType;
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
public class PayrollGovernmentDeduction implements Serializable {

    @Autowired
    private EmployeePayrollService employeePayrollService;

    @Autowired
    private BasicSalaryService basicSalaryService;

    @Autowired
    private SalaryDeductionsService salaryDeductionsService;

    @Autowired
    private GovernmentLoanService governmentLoanService;

    @Autowired
    private GovernmentDeductionService governmentDeductionService;

    @Autowired
    private GovernmentLoanHistoryService governmentLoanHistoryService;

    @Autowired
    private PayrollUtil payrollUtil;

    private Long id;
    private Long pId;
    private EmployeePayroll employeePayroll;
    private List<GovernmentLoan> governmentLoans;
    private Map<String, GovernmentLoanType> governmentLoanMap;
    private GovernmentLoan selectedGovernmentLoan;
    private GovernmentLoanType selectedLoan;
    private Double loanAmount;
    private String selLoan;
    private Map<String, String> loanMap;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            employeePayroll = employeePayrollService.findById(Long.valueOf(id));
            governmentLoans = governmentLoanService.getLoansByEmployee(employeePayroll.getEmployeePayroll());
            governmentLoanMap = new HashMap<>();
            loanMap = new HashMap<>();
            for(GovernmentLoan sl: governmentLoans) {
                if(sl.getBalanceAmount() <= 0) {
                    continue;
                }
                governmentLoanMap.put(sl.getLoanType().name().replaceAll("_", " ") + " [₱" + sl.getBalanceAmount() + "]", sl.getLoanType());
                loanMap.put(sl.getLoanType().name().replaceAll("_", " "), sl.getDeductionAmount().toString());
            }
        } else {
            employeePayroll = new EmployeePayroll();
            governmentLoans = new ArrayList<>();
        }
        selectedGovernmentLoan = new GovernmentLoan();
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

    public List<GovernmentLoan> getGovernmentLoans() {
        return governmentLoans;
    }

    public void setGovernmentLoans(List<GovernmentLoan> governmentLoans) {
        this.governmentLoans = governmentLoans;
    }

    public Map<String, GovernmentLoanType> getGovernmentLoanMap() {
        return governmentLoanMap;
    }

    public void setGovernmentLoanMap(Map<String, GovernmentLoanType> governmentLoanMap) {
        this.governmentLoanMap = governmentLoanMap;
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

    public GovernmentLoan getSelectedGovernmentLoan() {
        return selectedGovernmentLoan;
    }

    public void setSelectedGovernmentLoan(GovernmentLoan selectedGovernmentLoan) {
        this.selectedGovernmentLoan = selectedGovernmentLoan;
    }

    public GovernmentLoanType getSelectedLoan() {
        return selectedLoan;
    }

    public void setSelectedLoan(GovernmentLoanType selectedLoan) {
        this.selectedLoan = selectedLoan;
    }

    public boolean isNew() {
        return employeePayroll == null || employeePayroll.getId() == null;
    }

    public String editPressed(Long employeePayrollId) {
        return "payroll-employee-form?payrollDetachmentId="+id+"&id="+employeePayrollId+"&faces-redirect=true&includeViewParams=true";
    }

    public String backBtnPressed() { return "payroll-detachment-form.xhtml?id="+ pId +"faces-redirect=true&includeViewParams=true"; }

    public void getDeductionAmount(GovernmentLoanType selectedLoan) {
        //System.out.println("LOAN: "+selectedLoan);
        if(has(selectedLoan)) {
            int x = 0;
            for(Map.Entry<String, String> entry: loanMap.entrySet()) {
                //System.out.println("ENTRY: "+entry.getKey()+" SELECTED:"+selectedLoan.name());
                if(selectedLoan.name().replaceAll("_"," ").equals(entry.getKey())) {
                    selLoan = entry.getValue();
                    selectedGovernmentLoan = filterLoanByType(selectedLoan);
                    if(selectedGovernmentLoan == null) {
                        selectedGovernmentLoan = governmentLoans.get(x);
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

    public GovernmentLoan filterLoanByType(GovernmentLoanType selectedLoan) {
        for(GovernmentLoan gl: governmentLoans) {
            if(gl.getLoanType().equals(selectedLoan)) {
                return gl;
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
            GovernmentDeductions hod = new GovernmentDeductions();
            GovernmentLoanHistory histBalance = new GovernmentLoanHistory();
            GovernmentLoanHistory histPaid = new GovernmentLoanHistory();
            hod.setDeductionAmount(Double.parseDouble(selLoan));
            hod.setDeductionDescription(selectedLoan.name());
            hod.setSalaryDeductions(deductions);
            hod.setGovernmentLoan(selectedGovernmentLoan);
            histBalance.setGovernmentLoan(selectedGovernmentLoan);
            histBalance.setReason("System Generated from Payroll");
            histPaid.setGovernmentLoan(selectedGovernmentLoan);
            histPaid.setReason("System Generated from Payroll");
            Double computedBalance = selectedGovernmentLoan.getBalanceAmount() - Double.parseDouble(selLoan);
            Double computedPaid = selectedGovernmentLoan.getPaidAmount() + Double.parseDouble(selLoan);
            histBalance.setChanges("Balance changed from "+ selectedGovernmentLoan.getBalanceAmount()+" to "+computedBalance);
            histBalance.setPrevBalanceAmount(selectedGovernmentLoan.getBalanceAmount());
            histBalance.setUpdatedBalanceAmount(computedBalance);
            histPaid.setChanges("Paid Amount changed from "+(selectedGovernmentLoan.getPaidAmount())+" to "+computedPaid);
            histPaid.setPrevPaidAmount(selectedGovernmentLoan.getPaidAmount());
            histPaid.setUpdatedPaidAmount(computedPaid);
            histPaid.setUpdatedBalanceAmount(computedPaid);
            selectedGovernmentLoan.setBalanceAmount(computedBalance);
            selectedGovernmentLoan.setPaidAmount(computedPaid);
            governmentLoanHistoryService.save(histBalance);
            governmentLoanHistoryService.save(histPaid);
            governmentLoanService.save(selectedGovernmentLoan);
            governmentDeductionService.save(hod);
            deductions.getGovernmentDeductions().add(hod);
            //System.out.println("SALARY DEDUCTIONS: "+deductions);
            /*if(deductions.getHeadOfficeDeductions() == null) {
                deductions.setHeadOfficeDeductions(new HashSet<>());
                System.out.println("HO DEDUCTIONS: "+deductions.getHeadOfficeDeductions());
            }
            deductions.getHeadOfficeDeductions().add(hod);*/
            employeePayroll = payrollUtil.calculateEmployeePayroll(employeePayroll);
            //basicSalaryService.save(employeePayroll.getBasicSalary());
            salaryDeductionsService.save(employeePayroll.getSalaryDeductions());
            //System.out.println("Salary Deductions Total: "+employeePayroll.getSalaryDeductions().getTotalDeductions());
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
