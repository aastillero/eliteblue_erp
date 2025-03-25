package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.HolidayType;
import io.eliteblue.erp.core.lazy.LazyErpDetachmentModel;
import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpHoliday;
import io.eliteblue.erp.core.service.ErpHolidayService;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
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
public class ErpHolidayForm implements Serializable {

    @Autowired
    private ErpHolidayService erpHolidayService;

    private Long id;
    private ErpHoliday erpHoliday;
    private Map<String, HolidayType> holidayTypes;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpHoliday = erpHolidayService.findById(Long.valueOf(id));
        } else {
            erpHoliday = new ErpHoliday();
        }
        holidayTypes = new HashMap<>();
        for(HolidayType ht: HolidayType.values()) {
            holidayTypes.put(ht.name(), ht);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpHoliday getErpHoliday() {
        return erpHoliday;
    }

    public void setErpHoliday(ErpHoliday erpHoliday) {
        this.erpHoliday = erpHoliday;
    }

    public Map<String, HolidayType> getHolidayTypes() {
        return holidayTypes;
    }

    public void setHolidayTypes(Map<String, HolidayType> holidayTypes) {
        this.holidayTypes = holidayTypes;
    }

    public void clear() {
        erpHoliday = new ErpHoliday();
        id = null;
    }

    public boolean isNew() {
        return erpHoliday == null || erpHoliday.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpHoliday) && has(erpHoliday.getId())) {
            String name = erpHoliday.getName();
            erpHolidayService.delete(erpHoliday);
            addDetailMessage("HOLIDAY DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("holidays.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpHoliday != null) {
            erpHolidayService.save(erpHoliday);
            addDetailMessage("HOLIDAY SAVED", erpHoliday.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("holidays.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
