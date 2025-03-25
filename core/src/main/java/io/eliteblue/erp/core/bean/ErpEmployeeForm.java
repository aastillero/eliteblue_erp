package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.*;
import io.eliteblue.erp.core.model.CompanyPosition;
import io.eliteblue.erp.core.model.EmploymentHistory;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.service.CompanyPositionService;
import io.eliteblue.erp.core.service.EmploymentHistoryService;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.util.DateTimeUtil;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpEmployeeForm implements Serializable {

    @Autowired
    private ErpEmployeeService employeeService;

    @Autowired
    private CompanyPositionService companyPositionService;

    @Autowired
    private EmploymentHistoryService employmentHistoryService;

    private final String pattern = "dd MMM yyyy";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    private Long id;
    private ErpEmployee erpEmployee;
    private Map<String, Gender> genderValues;
    private Map<String, EmploymentType> employmentValues;
    private Map<String, CivilStatus> maritalValues;
    private Map<String, EmployeeStatus> empStatusValues;
    private Map<String, BloodType> bloodValues;
    private Map<String, SalaryType> salaryTypes;
    private Map<String, String> bankTypes;
    private Map<String, ContributionType> contributionTypes;
    private EmployeeStatus initialStatus;
    private String joinedDate;
    private String lastUpdate;
    private List<EmploymentHistory> employmentHistory;

    private List<SelectItem> companyPositions;

    private String positionValue;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpEmployee = employeeService.findById(Long.valueOf(id));
            employmentHistory = new ArrayList<>(erpEmployee.getEmploymentHistory());
            initialStatus = erpEmployee.getStatus();
            if(has(erpEmployee.getCompanyPosition())) {
                positionValue = erpEmployee.getCompanyPosition().getName();
            }
            if(erpEmployee.getJoinedDate() != null) {
                joinedDate = "Joined " +simpleDateFormat.format(erpEmployee.getJoinedDate());
            }
            lastUpdate = DateTimeUtil.timeAgo(new Date(), erpEmployee.getLastUpdate());
        } else {
            erpEmployee = new ErpEmployee();
            employmentHistory = new ArrayList<>();
            initialStatus = EmployeeStatus.CREATED;
            joinedDate = "Joined 09 Dec 2017";
        }
        genderValues = new HashMap<>();
        for(Gender g: Gender.values()) {
            genderValues.put(g.name(), g);
        }
        employmentValues = new HashMap<>();
        for(EmploymentType et: EmploymentType.values()) {
            employmentValues.put(et.name(), et);
        }
        maritalValues = new HashMap<>();
        for(CivilStatus c: CivilStatus.values()) {
            maritalValues.put(c.name(), c);
        }
        empStatusValues = new HashMap<>();
        for(EmployeeStatus e: EmployeeStatus.values()) {
            empStatusValues.put(e.name(), e);
        }
        bloodValues = new HashMap<>();
        for(BloodType b: BloodType.values()) {
            bloodValues.put(b.toString(), b);
        }
        salaryTypes = new HashMap<>();
        for(SalaryType st: SalaryType.values()) {
            salaryTypes.put(st.name(), st);
        }
        bankTypes = new HashMap<>();
        for(BankType bt: BankType.values()) {
            bankTypes.put(bt.name().replaceAll("_", " "), bt.name().replaceAll("_", " "));
        }
        contributionTypes = new HashMap<>();
        for(ContributionType ct: ContributionType.values()) {
            contributionTypes.put(ct.name(), ct);
        }
        List<CompanyPosition> positionsList = companyPositionService.getAll();
        companyPositions = new ArrayList<SelectItem>();
        for (CompanyPosition cp: positionsList) {
            SelectItem itm = new SelectItem(cp.getName(), cp.getName());
            companyPositions.add(itm);
        }
        /*if(erpEmployee.getBirthDate() != null) {
            System.out.println("BIRTHDATE: "+simpleDateFormat.format(erpEmployee.getBirthDate()));
        }*/
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ErpEmployee getErpEmployee() {
        return erpEmployee;
    }

    public Map<String, Gender> getGenderValues() {
        return genderValues;
    }

    public void setGenderValues(Map<String, Gender> genderValues) {
        this.genderValues = genderValues;
    }

    public Map<String, EmploymentType> getEmploymentValues() {
        return employmentValues;
    }

    public void setEmploymentValues(Map<String, EmploymentType> employmentValues) {
        this.employmentValues = employmentValues;
    }

    public Map<String, CivilStatus> getMaritalValues() {
        return maritalValues;
    }

    public void setMaritalValues(Map<String, CivilStatus> maritalValues) {
        this.maritalValues = maritalValues;
    }

    public Map<String, EmployeeStatus> getEmpStatusValues() {
        return empStatusValues;
    }

    public void setEmpStatusValues(Map<String, EmployeeStatus> empStatusValues) {
        this.empStatusValues = empStatusValues;
    }

    public Map<String, BloodType> getBloodValues() {
        return bloodValues;
    }

    public void setBloodValues(Map<String, BloodType> bloodValues) {
        this.bloodValues = bloodValues;
    }

    public void setErpEmployee(ErpEmployee erpEmployee) {
        this.erpEmployee = erpEmployee;
    }

    public Map<String, SalaryType> getSalaryTypes() {
        return salaryTypes;
    }

    public void setSalaryTypes(Map<String, SalaryType> salaryTypes) {
        this.salaryTypes = salaryTypes;
    }

    public Map<String, String> getBankTypes() {
        return bankTypes;
    }

    public void setBankTypes(Map<String, String> bankTypes) {
        this.bankTypes = bankTypes;
    }

    public EmployeeStatus getInitialStatus() {
        return initialStatus;
    }

    public void setInitialStatus(EmployeeStatus initialStatus) {
        this.initialStatus = initialStatus;
    }

    public Map<String, ContributionType> getContributionTypes() {
        return contributionTypes;
    }

    public void setContributionTypes(Map<String, ContributionType> contributionTypes) {
        this.contributionTypes = contributionTypes;
    }

    public List<SelectItem> getCompanyPositions() {
        return companyPositions;
    }

    public void setCompanyPositions(List<SelectItem> companyPositions) {
        this.companyPositions = companyPositions;
    }

    public String getPositionValue() {
        return positionValue;
    }

    public void setPositionValue(String positionValue) {
        this.positionValue = positionValue;
    }

    public List<EmploymentHistory> getEmploymentHistory() {
        return employmentHistory;
    }

    public void setEmploymentHistory(List<EmploymentHistory> employmentHistory) {
        this.employmentHistory = employmentHistory;
    }

    public String newIDPressed() {
        return "employee-id-form?employeeId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String newAddressPressed() {
        return "address-form?employeeId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String editAddressPressed(Long addressId) {
        return "address-form?employeeId="+id+"&id="+addressId+"&faces-redirect=true&includeViewParams=true";
    }

    public String newContactPressed() {
        return "contact-info-form?employeeId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String editContactPressed(Long addressId) {
        return "contact-info-form?employeeId="+id+"&id="+addressId+"&faces-redirect=true&includeViewParams=true";
    }

    public String editIDPressed(Long erpEmployeeID) {
        return "employee-id-form?employeeId="+id+"&id="+erpEmployeeID+"&faces-redirect=true&includeViewParams=true";
    }

    public boolean isNew() {
        return erpEmployee == null || erpEmployee.getId() == null;
    }

    public void clear() {
        erpEmployee = new ErpEmployee();
        id = null;
    }

    public void remove() throws Exception {
        if(has(erpEmployee) && has(erpEmployee.getId())) {
            String userFullName = erpEmployee.getFirstname()+ " " +erpEmployee.getLastname();
            employeeService.delete(erpEmployee);
            addDetailMessage("EMPLOYEE DELETED", userFullName+".", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("employees.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpEmployee != null) {
            List<ErpEmployee> results = employeeService.findByFirstnameAndLastname(erpEmployee.getFirstname().toUpperCase(), erpEmployee.getLastname().toUpperCase());
            EmploymentHistory hist = new EmploymentHistory();
            boolean hasEmploymentHistory = false;
            if(erpEmployee.getWeightPound() != null) {
                erpEmployee.setWeightKilo(erpEmployee.getWeightPound() * 0.454);
            }
            if(has(positionValue)) {
                CompanyPosition pos = companyPositionService.findByName(positionValue);
                erpEmployee.setCompanyPosition(pos);
            }
            if(!initialStatus.equals(erpEmployee.getStatus())) {
                hist.setEmployee(erpEmployee);
                hist.setChanges("Employment Status changed from "+(initialStatus != null ? initialStatus.name() : "empty")+" to "+erpEmployee.getStatus().name());
                if(erpEmployee.getEmploymentHistory() == null) {
                    erpEmployee.setEmploymentHistory(new HashSet<>());
                }
                if(erpEmployee.getStatus().equals(EmployeeStatus.HIRED) && erpEmployee.getJoinedDate() == null) {
                    erpEmployee.setJoinedDate(new Date());
                }
                if(erpEmployee.getStatus().equals(EmployeeStatus.RESIGNED) && erpEmployee.getResignedDate() == null) {
                    erpEmployee.setResignedDate(new Date());
                }
                erpEmployee.getEmploymentHistory().add(hist);
                if(erpEmployee.getId() != null) {
                    employmentHistoryService.save(hist);
                } else {
                    hasEmploymentHistory = true;
                }
            }
            // camelize names: firstname, lastname, middlename
            if(erpEmployee.getId() == null && (results != null && results.size() > 0)) {
                String userFullName = erpEmployee.getFirstname() + " " + erpEmployee.getLastname();
                addDetailMessage("An Employee named "+userFullName+ " already exist","cannot add employee.", FacesMessage.SEVERITY_ERROR);
                //FacesContext.getCurrentInstance().getExternalContext().redirect("employees.xhtml");
            }
            else {
                employeeService.save(erpEmployee);
                if(hasEmploymentHistory) {
                    employmentHistoryService.save(hist);
                }
                String userFullName = erpEmployee.getFirstname() + " " + erpEmployee.getLastname();
                addDetailMessage("EMPLOYEE SAVED", userFullName + ".", FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("employees.xhtml");
            }
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
