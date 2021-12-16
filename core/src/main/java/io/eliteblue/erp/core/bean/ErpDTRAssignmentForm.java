package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.ErpDTRAssignmentService;
import io.eliteblue.erp.core.service.ErpDTRService;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpDTRAssignmentForm implements Serializable {

    @Autowired
    private ErpDTRAssignmentService erpDTRAssignmentService;

    @Autowired
    private ErpDTRService erpDTRService;

    @Autowired
    private ErpEmployeeService employeeService;

    private Long id;
    private Long erpDTRId;
    private ErpDTR erpDTR;
    private ErpDTRAssignment erpDTRAssignment;

    private ErpEmployee employee;
    private Map<String, Long> employees;

    private List<ErpDTRSchedule> filteredErpDTRSchedule;
    private List<ErpDTRSchedule> erpDTRSchedules;
    private ErpDTRSchedule selectedErpDTRSchedule;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpDTRAssignment = erpDTRAssignmentService.findById(Long.valueOf(id));
            erpDTR = erpDTRAssignment.getErpDTR();
            erpDTRId = erpDTR.getId();
            employee = erpDTRAssignment.getEmployeeAssigned();
            erpDTRSchedules = new ArrayList<>(erpDTRAssignment.getSchedules());
        } else {
            erpDTRAssignment = new ErpDTRAssignment();
            employee = new ErpEmployee();
            if(has(erpDTRId)) {
                erpDTR = erpDTRService.findById(erpDTRId);
                erpDTRAssignment.setErpDTR(erpDTR);
            }
        }
        employees = new HashMap<>();
        for(ErpEmployee emp: employeeService.getAllFiltered()) {
            employees.put(emp.getFirstname()+" "+emp.getLastname(), emp.getId());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getErpDTRId() {
        return erpDTRId;
    }

    public void setErpDTRId(Long erpDTRId) {
        this.erpDTRId = erpDTRId;
    }

    public ErpDTR getErpDTR() {
        return erpDTR;
    }

    public void setErpDTR(ErpDTR erpDTR) {
        this.erpDTR = erpDTR;
    }

    public ErpDTRAssignment getErpDTRAssignment() {
        return erpDTRAssignment;
    }

    public void setErpDTRAssignment(ErpDTRAssignment erpDTRAssignment) {
        this.erpDTRAssignment = erpDTRAssignment;
    }

    public ErpEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(ErpEmployee employee) {
        this.employee = employee;
    }

    public Map<String, Long> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, Long> employees) {
        this.employees = employees;
    }

    public List<ErpDTRSchedule> getFilteredErpDTRSchedule() {
        return filteredErpDTRSchedule;
    }

    public void setFilteredErpDTRSchedule(List<ErpDTRSchedule> filteredErpDTRSchedule) {
        this.filteredErpDTRSchedule = filteredErpDTRSchedule;
    }

    public List<ErpDTRSchedule> getErpDTRSchedules() {
        return erpDTRSchedules;
    }

    public void setErpDTRSchedules(List<ErpDTRSchedule> erpDTRSchedules) {
        this.erpDTRSchedules = erpDTRSchedules;
    }

    public ErpDTRSchedule getSelectedErpDTRSchedule() {
        return selectedErpDTRSchedule;
    }

    public void setSelectedErpDTRSchedule(ErpDTRSchedule selectedErpDTRSchedule) {
        this.selectedErpDTRSchedule = selectedErpDTRSchedule;
    }

    public void onRowSelect(SelectEvent<ErpDTRSchedule> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-schedule-form.xhtml?id="+ selectedErpDTRSchedule.getId()+"&erpDTRAssignmentId="+id);
    }

    public void onRowUnselect(UnselectEvent<ErpDTRSchedule> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-schedule-form.xhtml?id="+ selectedErpDTRSchedule.getId()+"&erpDTRAssignmentId="+id);
    }

    public String newSchedulePressed() {
        return "dtr-schedule-form?erpDTRAssignmentId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String backBtnPressed() { return "dtr-form?id="+ erpDTRId +"faces-redirect=true&includeViewParams=true"; }

    public void clear() {
        erpDTRAssignment = new ErpDTRAssignment();
        id = null;
    }

    public boolean isNew() {
        return erpDTRAssignment == null || erpDTRAssignment.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpDTRAssignment) && has(erpDTRAssignment.getId())) {
            erpDTRAssignmentService.delete(erpDTRAssignment);
            addDetailMessage("DTR ASSIGNMENT DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-form.xhtml?id="+ erpDTR.getId());
        }
    }

    public void save() throws Exception {
        if(erpDTRAssignment != null) {
            if(has(employee.getId())) {
                employee = employeeService.findById(employee.getId());
                erpDTRAssignment.setEmployeeAssigned(employee);
            }
            erpDTRAssignmentService.save(erpDTRAssignment);
            addDetailMessage("DTR ASSIGNMENT SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-form.xhtml?id="+ erpDTR.getId());
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
