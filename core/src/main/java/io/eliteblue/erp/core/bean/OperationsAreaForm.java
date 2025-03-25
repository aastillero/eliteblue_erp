package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.Archipelago;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpHoliday;
import io.eliteblue.erp.core.model.ErpLocalHoliday;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.service.OperationsAreaService;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class OperationsAreaForm implements Serializable {

    @Autowired
    private OperationsAreaService operationsAreaService;

    private Long id;
    private OperationsArea area;

    private List<ErpLocalHoliday> filteredLocalHolidays;
    private List<ErpLocalHoliday> localHolidays;
    private ErpLocalHoliday selectedLocalHoliday;

    private Map<String, Archipelago> archipelagoValues;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            area = operationsAreaService.findById(Long.valueOf(id));
            localHolidays = new ArrayList<>(area.getLocalHolidays());
        } else {
            area = new OperationsArea();
        }
        archipelagoValues = new HashMap<>();
        for(Archipelago a: Archipelago.values()) {
            archipelagoValues.put(a.name(), a);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationsArea getArea() {
        return area;
    }

    public void setArea(OperationsArea area) {
        this.area = area;
    }

    public List<ErpLocalHoliday> getFilteredLocalHolidays() {
        return filteredLocalHolidays;
    }

    public void setFilteredLocalHolidays(List<ErpLocalHoliday> filteredLocalHolidays) {
        this.filteredLocalHolidays = filteredLocalHolidays;
    }

    public List<ErpLocalHoliday> getLocalHolidays() {
        return localHolidays;
    }

    public void setLocalHolidays(List<ErpLocalHoliday> localHolidays) {
        this.localHolidays = localHolidays;
    }

    public ErpLocalHoliday getSelectedLocalHoliday() {
        return selectedLocalHoliday;
    }

    public void setSelectedLocalHoliday(ErpLocalHoliday selectedLocalHoliday) {
        this.selectedLocalHoliday = selectedLocalHoliday;
    }

    public Map<String, Archipelago> getArchipelagoValues() {
        return archipelagoValues;
    }

    public void setArchipelagoValues(Map<String, Archipelago> archipelagoValues) {
        this.archipelagoValues = archipelagoValues;
    }

    public void clear() {
        area = new OperationsArea();
        id = null;
    }

    public boolean isNew() {
        return area == null || area.getId() == null;
    }

    public String newHolidayPressed() {
        return "local-holiday-form?operationId="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public void onRowSelect(SelectEvent<ErpHoliday> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("local-holiday-form.xhtml?id="+selectedLocalHoliday.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpHoliday> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("local-holiday-form.xhtml?id="+selectedLocalHoliday.getId());
    }

    public void remove() throws Exception {
        if(has(area) && has(area.getId())) {
            String locationName = area.getLocation();
            operationsAreaService.delete(area);
            addDetailMessage("LOCATION DELETED", locationName, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("areas.xhtml");
        }
    }

    public void save() throws Exception {
        if(area != null) {
            operationsAreaService.save(area);
            addDetailMessage("LOCATION SAVED", area.getLocation(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("areas.xhtml");
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpLocalHoliday erpLocalHoliday = (ErpLocalHoliday) value;
        return erpLocalHoliday.getName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
