package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.model.GovernmentLoanHistory;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.GovernmentLoanHistoryService;
import io.eliteblue.erp.core.service.GovernmentLoanService;
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
public class GovernmentLoanForm implements Serializable {

    private final String pattern = "MMMM dd,yyyy";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Autowired
    private GovernmentLoanService governmentLoanService;

    @Autowired
    private GovernmentLoanHistoryService governmentLoanHistoryService;

    @Autowired
    private ErpEmployeeService employeeService;

    private Long id;
    private String componentStr;
    private String reason;
    private GovernmentLoan governmentLoan;
    private List<GovernmentLoanHistory> governmentLoanHistory;
    private Map<String, GovernmentLoanType> governmentLoanValues;
    private Map<String, Long> employees;
    private ErpEmployee employee;
    private Double initialValue;
    private Date initialDate;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            governmentLoan = governmentLoanService.findById(Long.valueOf(id));
            governmentLoanHistory = new ArrayList<>(governmentLoan.getLoanHistory());
            governmentLoanHistory.sort(Comparator.comparing((GovernmentLoanHistory hs) -> hs.getLastUpdate()).reversed());
            employee = governmentLoan.getEmployeeBorrower();
        } else {
            governmentLoan = new GovernmentLoan();
            governmentLoanHistory = new ArrayList<>();
            employee = new ErpEmployee();
        }
        if(has(componentStr) && has(id)) {
            if(componentStr.equals("loanAmount")) {
                initialValue = governmentLoan.getLoanAmount();
            } else if(componentStr.equals("deductionAmount")) {
                initialValue = governmentLoan.getDeductionAmount();
            } else {
                initialDate = governmentLoan.getDateStarted();
            }
        } else {
            initialDate = new Date();
            initialValue = 0.0;
        }
        governmentLoanValues = new HashMap<>();
        for(GovernmentLoanType t: GovernmentLoanType.values()) {
            governmentLoanValues.put(t.name(), t);
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

    public GovernmentLoan getGovernmentLoan() {
        return governmentLoan;
    }

    public void setGovernmentLoan(GovernmentLoan governmentLoan) {
        this.governmentLoan = governmentLoan;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public List<GovernmentLoanHistory> getGovernmentLoanHistory() {
        return governmentLoanHistory;
    }

    public void setGovernmentLoanHistory(List<GovernmentLoanHistory> governmentLoanHistory) {
        this.governmentLoanHistory = governmentLoanHistory;
    }

    public Map<String, GovernmentLoanType> getGovernmentLoanValues() {
        return governmentLoanValues;
    }

    public void setGovernmentLoanValues(Map<String, GovernmentLoanType> governmentLoanValues) {
        this.governmentLoanValues = governmentLoanValues;
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

    public String getComponentStr() {
        return componentStr;
    }

    public void setComponentStr(String componentStr) {
        this.componentStr = componentStr;
    }

    public String backBtnPressed() { return "government-loans?faces-redirect=true&includeViewParams=true"; }

    public String editPressed(String component) {
        //System.out.println("COMPONENT: "+component);
        return "government-loan-edit?id="+id+"&componentStr="+component+"&faces-redirect=true&includeViewParams=true";
    }

    public boolean isNew() {
        return governmentLoan == null || governmentLoan.getId() == null;
    }

    public boolean isEdit() {
        return componentStr == null || componentStr.isEmpty();
    }

    public void clear() {
        governmentLoan = new GovernmentLoan();
        id = null;
    }

    public void remove() throws Exception {
        if(has(governmentLoan) && has(governmentLoan.getId())) {
            governmentLoanService.delete(governmentLoan);
            addDetailMessage("GOVERNMENT LOAN DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("government-loans.xhtml");
        }
    }

    public void save() throws Exception {
        if(has(componentStr)) {
            if(has(governmentLoan) && has(employee.getId())) {
                GovernmentLoanHistory hist = new GovernmentLoanHistory();
                hist.setGovernmentLoan(governmentLoan);
                hist.setReason(reason);
                switch (componentStr) {
                    case "loanAmount":
                        hist.setChanges("Loan Amount changed from "+initialValue+" to "+governmentLoan.getLoanAmount());
                        hist.setPreviousLoanAmount(initialValue);
                        hist.setUpdatedLoanAmount(governmentLoan.getLoanAmount());
                        break;
                    case "deductionAmount":
                        hist.setChanges("Deduction Amount changed from "+initialValue+" to "+governmentLoan.getDeductionAmount());
                        hist.setPrevDeductionAmount(initialValue);
                        hist.setUpdatedDeductionAmount(governmentLoan.getDeductionAmount());
                        break;
                    case "balanceAmount":
                        hist.setChanges("Balance changed from "+initialValue+" to "+governmentLoan.getBalanceAmount());
                        hist.setPrevBalanceAmount(initialValue);
                        hist.setUpdatedBalanceAmount(governmentLoan.getBalanceAmount());
                        break;
                    case "paidAmount":
                        hist.setChanges("Paid Amount changed from "+initialValue+" to "+governmentLoan.getPaidAmount());
                        hist.setPrevPaidAmount(initialValue);
                        hist.setUpdatedBalanceAmount(governmentLoan.getPaidAmount());
                        break;
                    default: // date started
                        String changesLog = "Date Started changed from "+simpleDateFormat.format(initialDate)+" to "+simpleDateFormat.format(governmentLoan.getDateStarted());
                        hist.setChanges(changesLog);
                        hist.setPreviousStartDate(initialDate);
                        hist.setUpdatedStartDate(governmentLoan.getDateStarted());
                }
                governmentLoanHistoryService.save(hist);
                governmentLoanService.save(governmentLoan);
                addDetailMessage("GOVERNMENT LOAN SAVED", "", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("government-loans.xhtml");
            }
        }
        else {
            if (has(governmentLoan)) {
                if (has(employee.getId())) {
                    employee = employeeService.findById(employee.getId());
                    governmentLoan.setEmployeeBorrower(employee);
                }
                governmentLoan.setBalanceAmount(governmentLoan.getLoanAmount());
                governmentLoan.setPaidAmount(0.0);
                governmentLoanService.save(governmentLoan);
                addDetailMessage("GOVERNMENT LOAN SAVED", "", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("government-loans.xhtml");
            }
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
