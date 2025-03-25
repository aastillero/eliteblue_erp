package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyEquipmentTypeModel;
import io.eliteblue.erp.core.lazy.LazyUnitOfMeasureModel;
import io.eliteblue.erp.core.model.EquipmentType;
import io.eliteblue.erp.core.model.UnitOfMeasure;
import io.eliteblue.erp.core.service.EquipmentTypeService;
import io.eliteblue.erp.core.service.UnitOfMeasureService;
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
public class EquipmentTypeList implements Serializable {

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    private LazyDataModel<EquipmentType> lazyEquipmentTypes;

    private List<EquipmentType> filteredEquipmentTypes;

    private List<EquipmentType> equipmentTypes;

    private EquipmentType selectedEquipmentType;

    @PostConstruct
    public void init() {
        equipmentTypes = equipmentTypeService.getAll();
        lazyEquipmentTypes = new LazyEquipmentTypeModel(equipmentTypes);
        lazyEquipmentTypes.setRowCount(10);
    }

    public LazyDataModel<EquipmentType> getLazyEquipmentTypes() {
        return lazyEquipmentTypes;
    }

    public void setLazyEquipmentTypes(LazyDataModel<EquipmentType> lazyEquipmentTypes) {
        this.lazyEquipmentTypes = lazyEquipmentTypes;
    }

    public List<EquipmentType> getFilteredEquipmentTypes() {
        return filteredEquipmentTypes;
    }

    public void setFilteredEquipmentTypes(List<EquipmentType> filteredEquipmentTypes) {
        this.filteredEquipmentTypes = filteredEquipmentTypes;
    }

    public List<EquipmentType> getEquipmentTypes() {
        return equipmentTypes;
    }

    public void setEquipmentTypes(List<EquipmentType> equipmentTypes) {
        this.equipmentTypes = equipmentTypes;
    }

    public EquipmentType getSelectedEquipmentType() {
        return selectedEquipmentType;
    }

    public void setSelectedEquipmentType(EquipmentType selectedEquipmentType) {
        this.selectedEquipmentType = selectedEquipmentType;
    }

    public void onRowSelect(SelectEvent<EquipmentType> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-type-form.xhtml?id="+selectedEquipmentType.getId());
    }

    public void onRowUnselect(UnselectEvent<EquipmentType> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-type-form.xhtml?id="+selectedEquipmentType.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        EquipmentType equipmentType = (EquipmentType) value;
        return equipmentType.getName().toLowerCase().contains(filterText);
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
