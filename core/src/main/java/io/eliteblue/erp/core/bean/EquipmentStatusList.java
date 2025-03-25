package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyEquipmentStatusModel;
import io.eliteblue.erp.core.model.EquipmentStatus;
import io.eliteblue.erp.core.service.EquipmentStatusService;
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
public class EquipmentStatusList implements Serializable {

    @Autowired
    private EquipmentStatusService equipmentStatusService;

    private LazyDataModel<EquipmentStatus> lazyEquipmentStatus;

    private List<EquipmentStatus> filteredEquipmentStatus;

    private List<EquipmentStatus> equipmentStatuses;

    private EquipmentStatus selectedEquipmentStatus;

    @PostConstruct
    public void init() {
        equipmentStatuses = equipmentStatusService.getAll();
        lazyEquipmentStatus = new LazyEquipmentStatusModel(equipmentStatuses);
        lazyEquipmentStatus.setRowCount(10);
    }

    public LazyDataModel<EquipmentStatus> getLazyEquipmentStatus() {
        return lazyEquipmentStatus;
    }

    public void setLazyEquipmentStatus(LazyDataModel<EquipmentStatus> lazyEquipmentStatus) {
        this.lazyEquipmentStatus = lazyEquipmentStatus;
    }

    public List<EquipmentStatus> getFilteredEquipmentStatus() {
        return filteredEquipmentStatus;
    }

    public void setFilteredEquipmentStatus(List<EquipmentStatus> filteredEquipmentStatus) {
        this.filteredEquipmentStatus = filteredEquipmentStatus;
    }

    public List<EquipmentStatus> getEquipmentStatuses() {
        return equipmentStatuses;
    }

    public void setEquipmentStatuses(List<EquipmentStatus> equipmentStatuses) {
        this.equipmentStatuses = equipmentStatuses;
    }

    public EquipmentStatus getSelectedEquipmentStatus() {
        return selectedEquipmentStatus;
    }

    public void setSelectedEquipmentStatus(EquipmentStatus selectedEquipmentStatus) {
        this.selectedEquipmentStatus = selectedEquipmentStatus;
    }

    public void onRowSelect(SelectEvent<EquipmentStatus> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-stat-form.xhtml?id="+selectedEquipmentStatus.getId());
    }

    public void onRowUnselect(UnselectEvent<EquipmentStatus> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("equip-stat-form.xhtml?id="+selectedEquipmentStatus.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        EquipmentStatus equipmentStatus = (EquipmentStatus) value;
        return equipmentStatus.getName().toLowerCase().contains(filterText);
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
