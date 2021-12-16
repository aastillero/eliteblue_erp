package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.AttendanceType;
import io.eliteblue.erp.core.model.ErpDTRAssignment;
import io.eliteblue.erp.core.model.ErpDTRSchedule;
import io.eliteblue.erp.core.service.ErpDTRAssignmentService;
import io.eliteblue.erp.core.service.ErpDTRScheduleService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpDTRScheduleForm implements Serializable {

    @Autowired
    private ErpDTRScheduleService erpDTRScheduleService;

    @Autowired
    private ErpDTRAssignmentService erpDTRAssignmentService;

    private Long id;
    private Long erpDTRAssignmentId;
    private ErpDTRAssignment erpDTRAssignment;
    private ErpDTRSchedule erpDTRSchedule;
    private Map<String, AttendanceType> workSchedValues;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpDTRSchedule = erpDTRScheduleService.findById(Long.valueOf(id));
            erpDTRAssignment = erpDTRSchedule.getErpDTRAssignment();
            erpDTRAssignmentId = erpDTRAssignment.getId();
        } else {
            erpDTRSchedule = new ErpDTRSchedule();
            if(has(erpDTRAssignmentId)) {
                erpDTRAssignment = erpDTRAssignmentService.findById(erpDTRAssignmentId);
                erpDTRSchedule.setErpDTRAssignment(erpDTRAssignment);
            }
        }
        workSchedValues = new HashMap<>();
        for(AttendanceType l: AttendanceType.values()) {
            workSchedValues.put(l.name(), l);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getErpDTRAssignmentId() {
        return erpDTRAssignmentId;
    }

    public void setErpDTRAssignmentId(Long erpDTRAssignmentId) {
        this.erpDTRAssignmentId = erpDTRAssignmentId;
    }

    public ErpDTRAssignment getErpDTRAssignment() {
        return erpDTRAssignment;
    }

    public void setErpDTRAssignment(ErpDTRAssignment erpDTRAssignment) {
        this.erpDTRAssignment = erpDTRAssignment;
    }

    public ErpDTRSchedule getErpDTRSchedule() {
        return erpDTRSchedule;
    }

    public void setErpDTRSchedule(ErpDTRSchedule erpDTRSchedule) {
        this.erpDTRSchedule = erpDTRSchedule;
    }

    public Map<String, AttendanceType> getWorkSchedValues() {
        return workSchedValues;
    }

    public void setWorkSchedValues(Map<String, AttendanceType> workSchedValues) {
        this.workSchedValues = workSchedValues;
    }

    public String backBtnPressed() { return "dtr-assn-form?id="+ erpDTRAssignmentId +"faces-redirect=true&includeViewParams=true"; }

    public void clear() {
        erpDTRSchedule = new ErpDTRSchedule();
        id = null;
    }

    public boolean isNew() {
        return erpDTRSchedule == null || erpDTRSchedule.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpDTRSchedule) && has(erpDTRSchedule.getId())) {
            erpDTRScheduleService.delete(erpDTRSchedule);
            addDetailMessage("DTR SCHEDULE DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-assn-form.xhtml?id="+ erpDTRAssignment.getId());
        }
    }

    public void save() throws Exception {
        if(erpDTRSchedule != null) {
            erpDTRScheduleService.save(erpDTRSchedule);
            addDetailMessage("DTR SCHEDULE SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-assn-form.xhtml?id="+ erpDTRAssignment.getId());
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
