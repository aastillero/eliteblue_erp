package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyEquipmentSubCategoryModel;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import io.eliteblue.erp.core.service.EquipmentSubCategoryService;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Named
@ViewScoped
public class EquipmentSubCategoryList implements Serializable {

    @Autowired
    private EquipmentSubCategoryService equipmentSubCategoryService;

    private LazyDataModel<EquipmentSubCategory> lazyEquipmentSubCategories;

    private List<EquipmentSubCategory> filteredEquipmentSubCategories;

    private List<EquipmentSubCategory> equipmentSubCategories;

    private EquipmentSubCategory selectedEquipmentSubCategory;

    @PostConstruct
    public void init() {
        equipmentSubCategories = equipmentSubCategoryService.getAll();
        lazyEquipmentSubCategories = new LazyEquipmentSubCategoryModel(equipmentSubCategories);
        lazyEquipmentSubCategories.setRowCount(10);
    }

    public LazyDataModel<EquipmentSubCategory> getLazyEquipmentSubCategories() {
        return lazyEquipmentSubCategories;
    }

    public void setLazyEquipmentSubCategories(LazyDataModel<EquipmentSubCategory> lazyEquipmentSubCategories) {
        this.lazyEquipmentSubCategories = lazyEquipmentSubCategories;
    }

    public List<EquipmentSubCategory> getFilteredEquipmentSubCategories() {
        return filteredEquipmentSubCategories;
    }

    public void setFilteredEquipmentSubCategories(List<EquipmentSubCategory> filteredEquipmentSubCategories) {
        this.filteredEquipmentSubCategories = filteredEquipmentSubCategories;
    }

    public List<EquipmentSubCategory> getEquipmentSubCategories() {
        return equipmentSubCategories;
    }

    public void setEquipmentSubCategories(List<EquipmentSubCategory> equipmentSubCategories) {
        this.equipmentSubCategories = equipmentSubCategories;
    }

    public EquipmentSubCategory getSelectedEquipmentSubCategory() {
        return selectedEquipmentSubCategory;
    }

    public void setSelectedEquipmentSubCategory(EquipmentSubCategory selectedEquipmentSubCategory) {
        this.selectedEquipmentSubCategory = selectedEquipmentSubCategory;
    }

    public void onRowSelect(SelectEvent<EquipmentSubCategory> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-subcat-form.xhtml?id="+selectedEquipmentSubCategory.getId());
    }

    public void onRowUnselect(UnselectEvent<EquipmentSubCategory> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-subcat-form.xhtml?id="+selectedEquipmentSubCategory.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        EquipmentSubCategory equipmentSubCategory = (EquipmentSubCategory) value;
        return equipmentSubCategory.getName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }
}
