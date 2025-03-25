package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import io.eliteblue.erp.core.model.EquipmentType;
import io.eliteblue.erp.core.model.UnitOfMeasure;
import io.eliteblue.erp.core.service.EquipmentCategoryService;
import io.eliteblue.erp.core.service.EquipmentSubCategoryService;
import io.eliteblue.erp.core.service.EquipmentTypeService;
import io.eliteblue.erp.core.service.UnitOfMeasureService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class EquipmentTypeForm implements Serializable {

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    @Autowired
    private EquipmentSubCategoryService equipmentSubCategoryService;

    private Long id;
    private EquipmentType equipmentType;

    private List<SelectItem> categories;

    private List<SelectItem> subCategories;

    private String categoryValue;

    private String subCategoryValue;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            equipmentType = equipmentTypeService.findById(Long.valueOf(id));
            if(equipmentType.getCategory() != null) {
                categoryValue = equipmentType.getCategory().getName();
            }
            if(equipmentType.getSubCategory() != null) {
                subCategoryValue = equipmentType.getSubCategory().getName();
            }
        } else {
            equipmentType = new EquipmentType();
        }
        List<EquipmentCategory> equipmentCategories = equipmentCategoryService.getAll();
        categories = new ArrayList<>();
        for(EquipmentCategory ec: equipmentCategories) {
            SelectItem itm = new SelectItem(ec.getName(), ec.getName());
            categories.add(itm);
        }
        List<EquipmentSubCategory> equipmentSubCategories = equipmentSubCategoryService.getAll();
        subCategories = new ArrayList<>();
        for(EquipmentSubCategory esc: equipmentSubCategories) {
            SelectItem itm = new SelectItem(esc.getName(), esc.getName());
            subCategories.add(itm);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public List<SelectItem> getCategories() {
        return categories;
    }

    public void setCategories(List<SelectItem> categories) {
        this.categories = categories;
    }

    public List<SelectItem> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SelectItem> subCategories) {
        this.subCategories = subCategories;
    }

    public String getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    public String getSubCategoryValue() {
        return subCategoryValue;
    }

    public void setSubCategoryValue(String subCategoryValue) {
        this.subCategoryValue = subCategoryValue;
    }

    public void clear() {
        equipmentType = new EquipmentType();
        id = null;
    }

    public boolean isNew() {
        return equipmentType == null || equipmentType.getId() == null;
    }

    public void remove() throws Exception {
        if(has(equipmentType) && has(equipmentType.getId())) {
            String name = equipmentType.getName();
            equipmentTypeService.delete(equipmentType);
            addDetailMessage("EQUIPMENT TYPE DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-types.xhtml");
        }
    }

    public void save() throws Exception {
        if(equipmentType != null) {
            if(categoryValue != null && !categoryValue.isEmpty()) {
                equipmentType.setCategory(equipmentCategoryService.findByName(categoryValue));
            }
            if(subCategoryValue != null && !subCategoryValue.isEmpty()) {
                equipmentType.setSubCategory(equipmentSubCategoryService.findByName(subCategoryValue));
            }
            equipmentTypeService.save(equipmentType);
            addDetailMessage("EQUIPMENT TYPE SAVED", equipmentType.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("equip-types.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
