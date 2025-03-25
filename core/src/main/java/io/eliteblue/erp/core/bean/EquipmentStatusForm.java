package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.EquipmentStatus;
import io.eliteblue.erp.core.model.EquipmentType;
import io.eliteblue.erp.core.service.EquipmentStatusService;
import io.eliteblue.erp.core.service.EquipmentTypeService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class EquipmentStatusForm implements Serializable {

    @Autowired
    private EquipmentStatusService equipmentStatusService;

    private Long id;
    private EquipmentStatus equipmentStatus;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            equipmentStatus = equipmentStatusService.findById(Long.valueOf(id));
        } else {
            equipmentStatus = new EquipmentStatus();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipmentStatus getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(EquipmentStatus equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public void clear() {
        equipmentStatus = new EquipmentStatus();
        id = null;
    }

    public boolean isNew() {
        return equipmentStatus == null || equipmentStatus.getId() == null;
    }

    public void remove() throws Exception {
        if(has(equipmentStatus) && has(equipmentStatus.getId())) {
            String name = equipmentStatus.getName();
            equipmentStatusService.delete(equipmentStatus);
            addDetailMessage("EQUIPMENT STATUS DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-status.xhtml");
        }
    }

    public void save() throws Exception {
        if(equipmentStatus != null) {
            equipmentStatusService.save(equipmentStatus);
            addDetailMessage("EQUIPMENT STATUS SAVED", equipmentStatus.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-status.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
