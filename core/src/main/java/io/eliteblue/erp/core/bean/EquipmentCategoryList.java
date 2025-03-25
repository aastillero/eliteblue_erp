package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyEquipmentCategoryModel;
import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.service.EquipmentCategoryService;
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
public class EquipmentCategoryList implements Serializable {

    @Autowired
    private EquipmentCategoryService equipmentCategoryService;

    private LazyDataModel<EquipmentCategory> lazyEquipmentCategories;

    private List<EquipmentCategory> filteredEquipmentCategories;

    private List<EquipmentCategory> equipmentCategories;

    private EquipmentCategory selectedEquipmentCategory;

    @PostConstruct
    public void init() {
        equipmentCategories = equipmentCategoryService.getAll();
        lazyEquipmentCategories = new LazyEquipmentCategoryModel(equipmentCategories);
        lazyEquipmentCategories.setRowCount(10);
    }

    public LazyDataModel<EquipmentCategory> getLazyEquipmentCategories() {
        return lazyEquipmentCategories;
    }

    public void setLazyEquipmentCategories(LazyDataModel<EquipmentCategory> lazyEquipmentCategories) {
        this.lazyEquipmentCategories = lazyEquipmentCategories;
    }

    public List<EquipmentCategory> getFilteredEquipmentCategories() {
        return filteredEquipmentCategories;
    }

    public void setFilteredEquipmentCategories(List<EquipmentCategory> filteredEquipmentCategories) {
        this.filteredEquipmentCategories = filteredEquipmentCategories;
    }

    public List<EquipmentCategory> getEquipmentCategories() {
        return equipmentCategories;
    }

    public void setEquipmentCategories(List<EquipmentCategory> equipmentCategories) {
        this.equipmentCategories = equipmentCategories;
    }

    public EquipmentCategory getSelectedEquipmentCategory() {
        return selectedEquipmentCategory;
    }

    public void setSelectedEquipmentCategory(EquipmentCategory selectedEquipmentCategory) {
        this.selectedEquipmentCategory = selectedEquipmentCategory;
    }

    public void onRowSelect(SelectEvent<EquipmentCategory> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-cat-form.xhtml?id="+selectedEquipmentCategory.getId());
    }

    public void onRowUnselect(UnselectEvent<EquipmentCategory> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-cat-form.xhtml?id="+selectedEquipmentCategory.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        EquipmentCategory equipmentCategory = (EquipmentCategory) value;
        return equipmentCategory.getName().toLowerCase().contains(filterText);
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
