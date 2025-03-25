package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.model.identifiers.ErpEquipmentId;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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
public class ErpEquipmentForm implements Serializable {

    @Autowired
    private ErpEquipmentService erpEquipmentService;

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    @Autowired
    private EquipmentStatusService equipmentStatusService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private OperationsAreaService operationsAreaService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private Long id;
    private String refNum;
    private ErpEquipment erpEquipment;

    private List<SelectItem> types;
    private String typeValue;

    private List<SelectItem> statuses;
    private String statusValue;

    private Map<String, Long> detachments;
    private ErpDetachment detachment;

    private Map<String, Long> locations;
    private OperationsArea location;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpEquipment = erpEquipmentService.findById(new ErpEquipmentId(Long.valueOf(id), refNum));
            statusValue = erpEquipment.getEquipmentStatus().getName();
            detachment = (has(erpEquipment.getDetachment())) ? erpEquipment.getDetachment() : new ErpDetachment();
            location = (has(erpEquipment.getLocation())) ? erpEquipment.getLocation() : new OperationsArea();
            if(has(erpEquipment) && has(erpEquipment.getEquipmentType())) {
                typeValue = erpEquipment.getEquipmentType().getName();
            }
        } else {
            erpEquipment = new ErpEquipment();
            //detachment = erpDetachmentService.getAllCurrentDetachment();
            if(!has(detachment)) {
                detachment = new ErpDetachment();
            }
            location = new OperationsArea();
        }

        detachments = new HashMap<>();
        List<ErpDetachment> erpDetachments;
        if(CurrentUserUtil.isAdmin()) {
            erpDetachments = erpDetachmentService.getAll();
        } else {
            erpDetachments = erpDetachmentService.filteredLocationOrCurrent();
        }
        for(ErpDetachment edp: erpDetachments) {
            detachments.put(edp.getName(), edp.getId());
        }
        locations = new HashMap<>();
        for(OperationsArea op: operationsAreaService.getAll()) {
            locations.put(op.getLocation(), op.getId());
        }
        List<EquipmentType> equipmentTypes = equipmentTypeService.getAll();
        types = new ArrayList<SelectItem>();
        for(EquipmentType m: equipmentTypes) {
            SelectItem itm = new SelectItem(m.getName(), m.getName());
            types.add(itm);
        }
        List<EquipmentStatus> equipmentStatuses = equipmentStatusService.getAll();
        statuses = new ArrayList<SelectItem>();
        for(EquipmentStatus m: equipmentStatuses) {
            SelectItem itm = new SelectItem(m.getName(), m.getName());
            statuses.add(itm);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public ErpEquipment getErpEquipment() {
        return erpEquipment;
    }

    public void setErpEquipment(ErpEquipment erpEquipment) {
        this.erpEquipment = erpEquipment;
    }

    public List<SelectItem> getTypes() {
        return types;
    }

    public void setTypes(List<SelectItem> types) {
        this.types = types;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public Map<String, Long> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, Long> locations) {
        this.locations = locations;
    }

    public OperationsArea getLocation() {
        return location;
    }

    public void setLocation(OperationsArea location) {
        this.location = location;
    }

    public List<SelectItem> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SelectItem> statuses) {
        this.statuses = statuses;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public void clear() {
        erpEquipment = new ErpEquipment();
        id = null;
    }

    public boolean isNew() {
        return erpEquipment == null || erpEquipment.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpEquipment) && has(erpEquipment.getId())) {
            String name = erpEquipment.getName();
            erpEquipmentService.delete(erpEquipment);
            addDetailMessage("EQUIPMENT DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equipments.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpEquipment != null) {
            if(has(typeValue)) {
                erpEquipment.setEquipmentType(equipmentTypeService.findByName(typeValue));
            }
            if(has(statusValue)) {
                erpEquipment.setEquipmentStatus(equipmentStatusService.findByName(statusValue));
            }
            if(has(detachment.getId())) {
                detachment = erpDetachmentService.findById(detachment.getId());
                erpEquipment.setDetachment(detachment);
            }
            if(has(location.getId())) {
                location = operationsAreaService.findById(location.getId());
                erpEquipment.setLocation(location);
            }
            erpEquipmentService.save(erpEquipment);
            addDetailMessage("EQUIPMENT SAVED", erpEquipment.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equipments.xhtml");
        }
    }

    public boolean isDisabled() {
        if(!CurrentUserUtil.isAdmin()) {
            return true;
        }
        return false;
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
