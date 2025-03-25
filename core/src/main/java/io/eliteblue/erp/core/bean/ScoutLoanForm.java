package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.model.ScoutLoan;
import io.eliteblue.erp.core.model.ScoutLoanHistory;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.ScoutLoanHistoryService;
import io.eliteblue.erp.core.service.ScoutLoanService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ScoutLoanForm implements Serializable {

    private final String pattern = "MMMM dd,yyyy";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Autowired
    private ScoutLoanService scoutLoanService;

    @Autowired
    private ScoutLoanHistoryService scoutLoanHistoryService;

    @Autowired
    private ErpEmployeeService employeeService;

    private Long id;
    private String componentStr;
    private String reason;
    private ScoutLoan scoutLoan;
    private List<ScoutLoanHistory> scoutLoanHistory;
    private Map<String, EmployeeLoanType> employeeLoanValues;
    private Map<String, Long> employees;
    private ErpEmployee employee;
    private Double initialValue;
    private Date initialDate;
    private String initialDesc;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            scoutLoan = scoutLoanService.findById(Long.valueOf(id));
            scoutLoanHistory = new ArrayList<>(scoutLoan.getLoanHistory());
            scoutLoanHistory.sort(Comparator.comparing((ScoutLoanHistory hs) -> hs.getLastUpdate()).reversed());
            employee = scoutLoan.getEmployeeBorrower();
        } else {
            scoutLoan = new ScoutLoan();
            scoutLoanHistory = new ArrayList<>();
            employee = new ErpEmployee();
        }
        if(has(componentStr) && has(id)) {
            if(componentStr.equals("loanAmount")) {
                initialValue = scoutLoan.getLoanAmount();
            } else if(componentStr.equals("payableAmount")) {
                initialValue = scoutLoan.getPayableAmount();
            } else if(componentStr.equals("deductionAmount")) {
                initialValue = scoutLoan.getDeductionAmount();
            } else if(componentStr.equals("loanDescription")) {
                initialDesc = scoutLoan.getLoanDescription();
            } else {
                initialDate = scoutLoan.getDateStarted();
            }
        } else {
            initialDate = new Date();
            initialValue = 0.0;
        }
        employeeLoanValues = new HashMap<>();
        for(EmployeeLoanType t: EmployeeLoanType.values()) {
            if(t.equals(EmployeeLoanType.CASH_ADVANCE) || t.equals(EmployeeLoanType.PARAPHERNALIA) || t.equals(EmployeeLoanType.SECURITY_LICENSE))
                employeeLoanValues.put(t.name(), t);
        }
        employees = new HashMap<>();
        for(ErpEmployee emp: employeeService.getAll()) {
            employees.put(emp.getFirstname()+" "+emp.getLastname(), emp.getId());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScoutLoan getScoutLoan() {
        return scoutLoan;
    }

    public void setScoutLoan(ScoutLoan scoutLoan) {
        this.scoutLoan = scoutLoan;
    }

    public String getComponentStr() {
        return componentStr;
    }

    public void setComponentStr(String componentStr) {
        this.componentStr = componentStr;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ScoutLoanHistory> getScoutLoanHistory() {
        return scoutLoanHistory;
    }

    public void setScoutLoanHistory(List<ScoutLoanHistory> scoutLoanHistory) {
        this.scoutLoanHistory = scoutLoanHistory;
    }

    public Double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Double initialValue) {
        this.initialValue = initialValue;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Map<String, EmployeeLoanType> getEmployeeLoanValues() {
        return employeeLoanValues;
    }

    public void setEmployeeLoanValues(Map<String, EmployeeLoanType> employeeLoanValues) {
        this.employeeLoanValues = employeeLoanValues;
    }

    public Map<String, Long> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, Long> employees) {
        this.employees = employees;
    }

    public ErpEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(ErpEmployee employee) {
        this.employee = employee;
    }

    public String backBtnPressed() { return "scout-loans?faces-redirect=true&includeViewParams=true"; }

    public String editPressed(String component) {
        //System.out.println("COMPONENT: "+component);
        return "scout-loan-edit?id="+id+"&componentStr="+component+"&faces-redirect=true&includeViewParams=true";
    }

    public boolean isNew() {
        return scoutLoan == null || scoutLoan.getId() == null;
    }

    public boolean isEdit() {
        return componentStr == null || componentStr.isEmpty();
    }

    public void clear() {
        scoutLoan = new ScoutLoan();
        id = null;
    }

    public void remove() throws Exception {
        if(has(scoutLoan) && has(scoutLoan.getId())) {
            scoutLoanService.delete(scoutLoan);
            addDetailMessage("SCOUT LOAN DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("scout-loans.xhtml");
        }
    }

    public void save() throws Exception {
        if(has(componentStr)) {
            if(has(scoutLoan) && has(employee.getId())) {
                ScoutLoanHistory hist = new ScoutLoanHistory();
                hist.setScoutLoan(scoutLoan);
                hist.setReason(reason);
                switch (componentStr) {
                    case "loanAmount":
                        hist.setChanges("Loan Amount changed from "+(initialValue != null ? initialValue : "empty")+" to "+scoutLoan.getLoanAmount());
                        hist.setPreviousLoanAmount(initialValue);
                        hist.setUpdatedLoanAmount(scoutLoan.getLoanAmount());
                        break;
                    case "payableAmount":
                        hist.setChanges("Payable Amount changed from "+(initialValue != null ? initialValue : "empty")+" to "+scoutLoan.getPayableAmount());
                        hist.setPreviousPayableAmount(initialValue);
                        hist.setUpdatedPayableAmount(scoutLoan.getPayableAmount());
                        break;
                    case "deductionAmount":
                        hist.setChanges("Deduction Amount changed from "+(initialValue != null ? initialValue : "empty")+" to "+scoutLoan.getDeductionAmount());
                        hist.setPrevDeductionAmount(initialValue);
                        hist.setUpdatedDeductionAmount(scoutLoan.getDeductionAmount());
                        break;
                    case "loanDescription":
                        hist.setChanges("Loan Description changed from "+(initialDesc != null ? initialDesc : "empty")+" to "+scoutLoan.getLoanDescription());
                        hist.setPreviousLoanDescription(initialDesc);
                        hist.setUpdatedLoanDescription(scoutLoan.getLoanDescription());
                        break;
                    case "balanceAmount":
                        hist.setChanges("Balance changed from "+(initialValue != null ? initialValue : "empty")+" to "+scoutLoan.getBalanceAmount());
                        hist.setPrevBalanceAmount(initialValue);
                        hist.setUpdatedBalanceAmount(scoutLoan.getBalanceAmount());
                        break;
                    case "paidAmount":
                        hist.setChanges("Paid Amount changed from "+(initialValue != null ? initialValue : "empty")+" to "+scoutLoan.getPaidAmount());
                        hist.setPrevPaidAmount(initialValue);
                        hist.setUpdatedBalanceAmount(scoutLoan.getPaidAmount());
                        break;
                    default: // date started
                        String changesLog = "Date Started changed from "+(initialDate != null ? simpleDateFormat.format(initialDate) : "empty")+" to "+simpleDateFormat.format(scoutLoan.getDateStarted());
                        hist.setChanges(changesLog);
                        hist.setPreviousStartDate(initialDate);
                        hist.setUpdatedStartDate(scoutLoan.getDateStarted());
                }
                scoutLoanHistoryService.save(hist);
                scoutLoanService.save(scoutLoan);
                addDetailMessage("SCOUT LOAN SAVED", "", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("scout-loans.xhtml");
            }
        }
        else {
            if (has(scoutLoan)) {
                if (has(employee.getId())) {
                    employee = employeeService.findById(employee.getId());
                    scoutLoan.setEmployeeBorrower(employee);
                }
                scoutLoan.setBalanceAmount(scoutLoan.getPayableAmount());
                scoutLoan.setPaidAmount(0.0);
                scoutLoanService.save(scoutLoan);
                addDetailMessage("SCOUT LOAN SAVED", "", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("scout-loans.xhtml");
            }
        }
    }

    public void computeByTerms(Double loanAmount) {
        //System.out.println("LOAN AMOUNT: "+loanAmount);
        if(has(loanAmount)){
            Integer payTermsDiv = scoutLoan.getPaymentTerms().intValue() * 2;
            Double percentage = (scoutLoan.getPaymentTerms() * 5) / 100;
            scoutLoan.setPayableAmount((loanAmount * percentage)+loanAmount); // 10%
            scoutLoan.setDeductionAmount(scoutLoan.getPayableAmount() / payTermsDiv);
            //System.out.println("PAYABLE: "+scoutLoan.getPayableAmount()+" DEDUCT: "+scoutLoan.getDeductionAmount());
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
