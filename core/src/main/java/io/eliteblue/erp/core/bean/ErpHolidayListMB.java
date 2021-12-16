package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpHolidayModel;
import io.eliteblue.erp.core.model.ErpHoliday;
import io.eliteblue.erp.core.service.ErpHolidayService;
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
public class ErpHolidayListMB implements Serializable {

    @Autowired
    private ErpHolidayService erpHolidayService;

    private LazyDataModel<ErpHoliday> lazyErpHolidays;

    private List<ErpHoliday> filteredErpHolidays;

    private List<ErpHoliday> holidays;

    private ErpHoliday selectedHoliday;

    @PostConstruct
    public void init() {
        holidays = erpHolidayService.getAll();
        lazyErpHolidays = new LazyErpHolidayModel(holidays);
        lazyErpHolidays.setRowCount(10);
    }

    public LazyDataModel<ErpHoliday> getLazyErpHolidays() {
        return lazyErpHolidays;
    }

    public void setLazyErpHolidays(LazyDataModel<ErpHoliday> lazyErpHolidays) {
        this.lazyErpHolidays = lazyErpHolidays;
    }

    public List<ErpHoliday> getFilteredErpHolidays() {
        return filteredErpHolidays;
    }

    public void setFilteredErpHolidays(List<ErpHoliday> filteredErpHolidays) {
        this.filteredErpHolidays = filteredErpHolidays;
    }

    public List<ErpHoliday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<ErpHoliday> holidays) {
        this.holidays = holidays;
    }

    public ErpHoliday getSelectedHoliday() {
        return selectedHoliday;
    }

    public void setSelectedHoliday(ErpHoliday selectedHoliday) {
        this.selectedHoliday = selectedHoliday;
    }

    public void onRowSelect(SelectEvent<ErpHoliday> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("holiday-form.xhtml?id="+ selectedHoliday.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpHoliday> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("holiday-form.xhtml?id="+ selectedHoliday.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        return true;
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