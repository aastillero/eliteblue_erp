package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpHoliday;
import io.eliteblue.erp.core.model.ErpLocalHoliday;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.service.ErpLocalHolidayService;
import io.eliteblue.erp.core.service.OperationsAreaService;
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
public class LocalHolidayForm implements Serializable {

    @Autowired
    private ErpLocalHolidayService erpLocalHolidayService;

    @Autowired
    private OperationsAreaService operationsAreaService;

    private Long id;
    private Long operationId;
    private ErpLocalHoliday erpLocalHoliday;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpLocalHoliday = erpLocalHolidayService.findById(Long.valueOf(id));
        } else {
            erpLocalHoliday = new ErpLocalHoliday();
            if(has(operationId)) {
                OperationsArea area = operationsAreaService.findById(operationId);
                erpLocalHoliday.setOperationsArea(area);
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpLocalHoliday getErpLocalHoliday() {
        return erpLocalHoliday;
    }

    public void setErpLocalHoliday(ErpLocalHoliday erpLocalHoliday) {
        this.erpLocalHoliday = erpLocalHoliday;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public void clear() {
        erpLocalHoliday = new ErpLocalHoliday();
        id = null;
    }

    public boolean isNew() {
        return erpLocalHoliday == null || erpLocalHoliday.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpLocalHoliday) && has(erpLocalHoliday.getId())) {
            String name = erpLocalHoliday.getName();
            erpLocalHolidayService.delete(erpLocalHoliday);
            addDetailMessage("HOLIDAY DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("areas.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpLocalHoliday != null) {
            erpLocalHolidayService.save(erpLocalHoliday);
            addDetailMessage("HOLIDAY SAVED", erpLocalHoliday.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("areas.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
