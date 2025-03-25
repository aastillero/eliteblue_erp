package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.service.EquipmentCategoryService;
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
public class EquipmentCategoryForm implements Serializable {

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    private Long id;

    private EquipmentCategory equipmentCategory;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            equipmentCategory = equipmentCategoryService.findById(Long.valueOf(id));
        } else {
            equipmentCategory = new EquipmentCategory();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipmentCategory getEquipmentCategory() {
        return equipmentCategory;
    }

    public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public void clear() {
        equipmentCategory = new EquipmentCategory();
        id = null;
    }

    public boolean isNew() {
        return equipmentCategory == null || equipmentCategory.getId() == null;
    }

    public void remove() throws Exception {
        if(has(equipmentCategory) && has(equipmentCategory.getId())) {
            String name = equipmentCategory.getName();
            equipmentCategoryService.delete(equipmentCategory);
            addDetailMessage("EQUIPMENT CATEGORY DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-categories.xhtml");
        }
    }

    public void save() throws Exception {
        if(equipmentCategory != null) {
            equipmentCategoryService.save(equipmentCategory);
            addDetailMessage("EQUIPMENT CATEGORY SAVED", equipmentCategory.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-categories.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
