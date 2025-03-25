package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import io.eliteblue.erp.core.service.EquipmentSubCategoryService;
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
public class EquipmentSubCategoryForm implements Serializable {

    @Autowired
    private EquipmentSubCategoryService equipmentSubCategoryService;

    private Long id;

    private EquipmentSubCategory equipmentSubCategory;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            equipmentSubCategory = equipmentSubCategoryService.findById(Long.valueOf(id));
        } else {
            equipmentSubCategory = new EquipmentSubCategory();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipmentSubCategory getEquipmentSubCategory() {
        return equipmentSubCategory;
    }

    public void setEquipmentSubCategory(EquipmentSubCategory equipmentSubCategory) {
        this.equipmentSubCategory = equipmentSubCategory;
    }

    public void clear() {
        equipmentSubCategory = new EquipmentSubCategory();
        id = null;
    }

    public boolean isNew() {
        return equipmentSubCategory == null || equipmentSubCategory.getId() == null;
    }

    public void remove() throws Exception {
        if(has(equipmentSubCategory) && has(equipmentSubCategory.getId())) {
            String name = equipmentSubCategory.getName();
            equipmentSubCategoryService.delete(equipmentSubCategory);
            addDetailMessage("EQUIPMENT SUB CATEGORY DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-sub-categories.xhtml");
        }
    }

    public void save() throws Exception {
        if(equipmentSubCategory != null) {
            equipmentSubCategoryService.save(equipmentSubCategory);
            addDetailMessage("EQUIPMENT SUB CATEGORY SAVED", equipmentSubCategory.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-sub-categories.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
