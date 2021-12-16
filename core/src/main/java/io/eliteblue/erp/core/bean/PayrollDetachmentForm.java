package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpDetachmentModel;
import io.eliteblue.erp.core.model.DetachmentPayroll;
import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.service.DetachmentPayrollService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class PayrollDetachmentForm implements Serializable {

    @Autowired
    private DetachmentPayrollService detachmentPayrollService;

    private Long id;
    private DetachmentPayroll detachmentPayroll;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            detachmentPayroll = detachmentPayrollService.findById(Long.valueOf(id));
        } else {
            detachmentPayroll = new DetachmentPayroll();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetachmentPayroll getDetachmentPayroll() {
        return detachmentPayroll;
    }

    public void setDetachmentPayroll(DetachmentPayroll detachmentPayroll) {
        this.detachmentPayroll = detachmentPayroll;
    }

    public void clear() {
        detachmentPayroll = new DetachmentPayroll();
        id = null;
    }

    public boolean isNew() {
        return detachmentPayroll == null || detachmentPayroll.getId() == null;
    }

    /*public void onRowSelect(SelectEvent<ErpDetachment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+selectedDetachment.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpDetachment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+selectedDetachment.getId());
    }*/

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpDetachment erpDetachment = (ErpDetachment) value;
        return erpDetachment.getName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public void remove() throws Exception {
        if(has(detachmentPayroll) && has(detachmentPayroll.getId())) {
            //String name = detachmentPayroll.getName();
            detachmentPayrollService.delete(detachmentPayroll);
            addDetailMessage("PAYROLL DETACHMENT DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("clients.xhtml");
        }
    }

    public void save() throws Exception {
        if(detachmentPayroll != null) {
            detachmentPayrollService.save(detachmentPayroll);
            addDetailMessage("PAYROLL DETACHMENT SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("clients.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
