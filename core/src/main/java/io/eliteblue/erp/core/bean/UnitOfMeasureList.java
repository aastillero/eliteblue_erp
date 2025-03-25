package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyUnitOfMeasureModel;
import io.eliteblue.erp.core.model.UnitOfMeasure;
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
public class UnitOfMeasureList implements Serializable {

    @Autowired
    private UnitOfMeasureService unitOfMeasureService;

    private LazyDataModel<UnitOfMeasure> lazyUnitOfMeasures;

    private List<UnitOfMeasure> filteredUnitOfMeasures;

    private List<UnitOfMeasure> unitOfMeasures;

    private UnitOfMeasure selectedUnitOfMeasure;

    @PostConstruct
    public void init() {
        unitOfMeasures = unitOfMeasureService.getAll();
        lazyUnitOfMeasures = new LazyUnitOfMeasureModel(unitOfMeasures);
        lazyUnitOfMeasures.setRowCount(10);
    }

    public LazyDataModel<UnitOfMeasure> getLazyUnitOfMeasures() {
        return lazyUnitOfMeasures;
    }

    public void setLazyUnitOfMeasures(LazyDataModel<UnitOfMeasure> lazyUnitOfMeasures) {
        this.lazyUnitOfMeasures = lazyUnitOfMeasures;
    }

    public List<UnitOfMeasure> getFilteredUnitOfMeasures() {
        return filteredUnitOfMeasures;
    }

    public void setFilteredUnitOfMeasures(List<UnitOfMeasure> filteredUnitOfMeasures) {
        this.filteredUnitOfMeasures = filteredUnitOfMeasures;
    }

    public List<UnitOfMeasure> getUnitOfMeasures() {
        return unitOfMeasures;
    }

    public void setUnitOfMeasures(List<UnitOfMeasure> unitOfMeasures) {
        this.unitOfMeasures = unitOfMeasures;
    }

    public UnitOfMeasure getSelectedUnitOfMeasure() {
        return selectedUnitOfMeasure;
    }

    public void setSelectedUnitOfMeasure(UnitOfMeasure selectedUnitOfMeasure) {
        this.selectedUnitOfMeasure = selectedUnitOfMeasure;
    }

    public void onRowSelect(SelectEvent<UnitOfMeasure> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("unit-form.xhtml?id="+selectedUnitOfMeasure.getId());
    }

    public void onRowUnselect(UnselectEvent<UnitOfMeasure> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("unit-form.xhtml?id="+selectedUnitOfMeasure.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        UnitOfMeasure unitOfMeasure = (UnitOfMeasure) value;
        return unitOfMeasure.getMeasure().toLowerCase().contains(filterText);
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
