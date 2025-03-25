package io.eliteblue.erp.core.bean;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class PayrollEmployeeForm implements Serializable {

    @Autowired
    private EmployeePayrollService employeePayrollService;

    @Autowired
    private BasicSalaryService basicSalaryService;

    @Autowired
    private SalaryDeductionsService salaryDeductionsService;

    @Autowired
    private HeadOfficeDeductionsService headOfficeDeductionsService;

    @Autowired
    private HeadOfficeLoanService headOfficeLoanService;

    @Autowired
    private ScoutDeductionService scoutDeductionService;

    @Autowired
    private ScoutLoanService scoutLoanService;

    @Autowired
    private GovernmentDeductionService governmentDeductionService;

    @Autowired
    private GovernmentLoanService governmentLoanService;

    @Autowired
    private PayrollUtil payrollUtil;

    private Long id;
    private Long pId;
    private EmployeePayroll employeePayroll;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            employeePayroll = employeePayrollService.findById(Long.valueOf(id));
        } else {
            employeePayroll = new EmployeePayroll();
        }
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

    public void clear() {
        employeePayroll = new EmployeePayroll();
        id = null;
    }

    public boolean isNew() {
        return employeePayroll == null || employeePayroll.getId() == null;
    }

    public String editPressed() {
        return "payroll-employee-form?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String governmentDeductions() {
        return "payroll-government-deductions?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String headOfficeDeductions() {
        return "payroll-headoffice-deductions?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String newScoutDeductions() {
        return "payroll-scout-deductions?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String removeGovernmentDeduction(Long deductId) {
        GovernmentDeductions gd = governmentDeductionService.findById(deductId);
        // return the amount deducted
        GovernmentLoan gl = gd.getGovernmentLoan();
        Double deductedAmount = gd.getDeductionAmount();
        Double balanceAmount = gl.getBalanceAmount();
        Double paidAmount = gl.getPaidAmount();
        gl.setBalanceAmount(balanceAmount + deductedAmount);
        gl.setPaidAmount(paidAmount - deductedAmount);
        Collection<GovernmentDeductions> col = employeePayroll.getSalaryDeductions().getGovernmentDeductions();
        col.removeIf(s->(s.getId().equals(gd.getId())));

        employeePayroll = payrollUtil.calculateEmployeePayroll(employeePayroll);
        salaryDeductionsService.save(employeePayroll.getSalaryDeductions());
        employeePayrollService.save(employeePayroll);
        governmentDeductionService.delete(gd);
        governmentLoanService.save(gl);
        return "payroll-employee-form?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String removeHeadOfficeDeduction(Long deductId) {
        HeadOfficeDeductions hd = headOfficeDeductionsService.findById(deductId);
        // return the amount deducted
        HeadOfficeLoan ho = hd.getHeadOfficeLoan();
        Double deductedAmount = hd.getDeductionAmount();
        Double balanceAmount = ho.getBalanceAmount();
        Double paidAmount = ho.getPaidAmount();
        ho.setBalanceAmount(balanceAmount + deductedAmount);
        ho.setPaidAmount(paidAmount - deductedAmount);
        Collection<HeadOfficeDeductions> col = employeePayroll.getSalaryDeductions().getHeadOfficeDeductions();
        col.removeIf(s->(s.getId().equals(hd.getId())));

        employeePayroll = payrollUtil.calculateEmployeePayroll(employeePayroll);
        salaryDeductionsService.save(employeePayroll.getSalaryDeductions());
        employeePayrollService.save(employeePayroll);
        headOfficeDeductionsService.delete(hd);
        headOfficeLoanService.save(ho);
        return "payroll-employee-form?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String removeScoutDeduction(Long deductId) {
        ScoutDeductions sd = scoutDeductionService.findById(deductId);
        // return the amount deducted
        Double deductedAmount = sd.getDeductionAmount();
        Double balanceAmount = sd.getScoutLoan().getBalanceAmount();
        Double paidAmount = sd.getScoutLoan().getPaidAmount();
        ScoutLoan sc = sd.getScoutLoan();
        sc.setBalanceAmount(balanceAmount + deductedAmount);
        sc.setPaidAmount(paidAmount - deductedAmount);
        Collection<ScoutDeductions> col = employeePayroll.getSalaryDeductions().getScoutDeductions();
        col.removeIf(s->(s.getId().equals(sd.getId())));

        employeePayroll = payrollUtil.calculateEmployeePayroll(employeePayroll);
        salaryDeductionsService.save(employeePayroll.getSalaryDeductions());
        employeePayrollService.save(employeePayroll);
        scoutDeductionService.delete(sd);
        scoutLoanService.save(sc);
        return "payroll-employee-form?payrollDetachmentId="+pId+"&id="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public String backBtnPressed() { return "payroll-detachment-form.xhtml?id="+ pId +"faces-redirect=true&includeViewParams=true"; }

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
        if(employeePayroll != null) {
            employeePayroll = payrollUtil.calculateEmployeePayroll(employeePayroll);
            //employeePayroll = employeePayrollService.computePayroll(employeePayroll);
            basicSalaryService.save(employeePayroll.getBasicSalary());
            salaryDeductionsService.save(employeePayroll.getSalaryDeductions());
            employeePayrollService.save(employeePayroll);
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
