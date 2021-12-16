package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.model.HeadOfficeLoan;
import io.eliteblue.erp.core.model.HeadOfficeLoanHistory;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.HeadOfficeLoanHistoryService;
import io.eliteblue.erp.core.service.HeadOfficeLoanService;
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
public class HeadOfficeLoanForm implements Serializable {

    private final String pattern = "MMMM dd,yyyy";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Autowired
    private HeadOfficeLoanService headOfficeLoanService;

    @Autowired
    private HeadOfficeLoanHistoryService headOfficeLoanHistoryService;

    @Autowired
    private ErpEmployeeService employeeService;

    private Long id;
    private String componentStr;
    private String reason;
    private HeadOfficeLoan headOfficeLoan;
    private List<HeadOfficeLoanHistory> headOfficeLoanHistory;
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
            headOfficeLoan = headOfficeLoanService.findById(Long.valueOf(id));
            headOfficeLoanHistory = new ArrayList<>(headOfficeLoan.getLoanHistory());
            headOfficeLoanHistory.sort(Comparator.comparing((HeadOfficeLoanHistory hs) -> hs.getLastUpdate()).reversed());
            employee = headOfficeLoan.getEmployeeBorrower();
        } else {
            headOfficeLoan = new HeadOfficeLoan();
            headOfficeLoanHistory = new ArrayList<>();
            employee = new ErpEmployee();
        }
        if(has(componentStr) && has(id)) {
            if(componentStr.equals("loanAmount")) {
                initialValue = headOfficeLoan.getLoanAmount();
            } else if(componentStr.equals("deductionAmount")) {
                initialValue = headOfficeLoan.getDeductionAmount();
            } else if(componentStr.equals("loanDescription")) {
                initialDesc = headOfficeLoan.getLoanDescription();
            } else {
                initialDate = headOfficeLoan.getDateStarted();
            }
        } else {
            initialDate = new Date();
            initialValue = 0.0;
        }
        employeeLoanValues = new HashMap<>();
        for(EmployeeLoanType t: EmployeeLoanType.values()) {
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

    public String getInitialDesc() {
        return initialDesc;
    }

    public void setInitialDesc(String initialDesc) {
        this.initialDesc = initialDesc;
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

    public List<HeadOfficeLoanHistory> getHeadOfficeLoanHistory() {
        return headOfficeLoanHistory;
    }

    public void setHeadOfficeLoanHistory(List<HeadOfficeLoanHistory> headOfficeLoanHistory) {
        this.headOfficeLoanHistory = headOfficeLoanHistory;
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

    public HeadOfficeLoan getHeadOfficeLoan() {
        return headOfficeLoan;
    }

    public void setHeadOfficeLoan(HeadOfficeLoan headOfficeLoan) {
        this.headOfficeLoan = headOfficeLoan;
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

    public boolean isNew() {
        return headOfficeLoan == null || headOfficeLoan.getId() == null;
    }

    public boolean isEdit() {
        return componentStr == null || componentStr.isEmpty();
    }

    public String backBtnPressed() { return "head-office-loans?faces-redirect=true&includeViewParams=true"; }

    public String editPressed(String component) {
        //System.out.println("COMPONENT: "+component);
        return "head-office-loan-edit?id="+id+"&componentStr="+component+"&faces-redirect=true&includeViewParams=true";
    }

    public void clear() {
        headOfficeLoan = new HeadOfficeLoan();
        id = null;
    }

    public void remove() throws Exception {
        if(has(headOfficeLoan) && has(headOfficeLoan.getId())) {
            headOfficeLoanService.delete(headOfficeLoan);
            addDetailMessage("HEAD OFFICE LOAN DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("head-office-loans.xhtml");
        }
    }

    public void save() throws Exception {
        if(has(componentStr)) {
            if(has(headOfficeLoan) && has(employee.getId())) {
                HeadOfficeLoanHistory hist = new HeadOfficeLoanHistory();
                hist.setOfficeLoan(headOfficeLoan);
                hist.setReason(reason);
                switch (componentStr) {
                    case "loanAmount":
                        hist.setChanges("Loan Amount changed from "+initialValue+" to "+headOfficeLoan.getLoanAmount());
                        hist.setPreviousLoanAmount(initialValue);
                        hist.setUpdatedLoanAmount(headOfficeLoan.getLoanAmount());
                        break;
                    case "deductionAmount":
                        hist.setChanges("Deduction Amount changed from "+initialValue+" to "+headOfficeLoan.getDeductionAmount());
                        hist.setPrevDeductionAmount(initialValue);
                        hist.setUpdatedDeductionAmount(headOfficeLoan.getDeductionAmount());
                        break;
                    case "loanDescription":
                        hist.setChanges("Loan Description changed from "+initialDesc+" to "+headOfficeLoan.getLoanDescription());
                        hist.setPreviousLoanDescription(initialDesc);
                        hist.setUpdatedLoanDescription(headOfficeLoan.getLoanDescription());
                        break;
                    case "balanceAmount":
                        hist.setChanges("Balance changed from "+initialValue+" to "+headOfficeLoan.getBalanceAmount());
                        hist.setPrevBalanceAmount(initialValue);
                        hist.setUpdatedBalanceAmount(headOfficeLoan.getBalanceAmount());
                        break;
                    case "paidAmount":
                        hist.setChanges("Paid Amount changed from "+initialValue+" to "+headOfficeLoan.getPaidAmount());
                        hist.setPrevPaidAmount(initialValue);
                        hist.setUpdatedBalanceAmount(headOfficeLoan.getPaidAmount());
                        break;
                    default: // date started
                        String changesLog = "Date Started changed from "+simpleDateFormat.format(initialDate)+" to "+simpleDateFormat.format(headOfficeLoan.getDateStarted());
                        hist.setChanges(changesLog);
                        hist.setPreviousStartDate(initialDate);
                        hist.setUpdatedStartDate(headOfficeLoan.getDateStarted());
                }
                headOfficeLoanHistoryService.save(hist);
                headOfficeLoanService.save(headOfficeLoan);
                addDetailMessage("HEAD OFFICE LOAN SAVED", "", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("head-office-loans.xhtml");
            }
        }
        else {
            if (has(headOfficeLoan)) {
                if (has(employee.getId())) {
                    employee = employeeService.findById(employee.getId());
                    headOfficeLoan.setEmployeeBorrower(employee);
                }
                headOfficeLoan.setBalanceAmount(headOfficeLoan.getLoanAmount());
                headOfficeLoan.setPaidAmount(0.0);
                headOfficeLoanService.save(headOfficeLoan);
                addDetailMessage("HEAD OFFICE LOAN SAVED", "", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("head-office-loans.xhtml");
            }
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
