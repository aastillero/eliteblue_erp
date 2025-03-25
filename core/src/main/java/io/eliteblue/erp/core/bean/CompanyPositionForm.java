package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.model.CompanyPosition;
import io.eliteblue.erp.core.service.CompanyPositionService;
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
public class CompanyPositionForm implements Serializable {

    @Autowired
    private CompanyPositionService companyPositionService;

    private Long id;

    private CompanyPosition companyPosition;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            companyPosition = companyPositionService.findById(Long.valueOf(id));
        } else {
            companyPosition = new CompanyPosition();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanyPosition getCompanyPosition() {
        return companyPosition;
    }

    public void setCompanyPosition(CompanyPosition companyPosition) {
        this.companyPosition = companyPosition;
    }

    public void clear() {
        companyPosition = new CompanyPosition();
        id = null;
    }

    public boolean isNew() {
        return companyPosition == null || companyPosition.getId() == null;
    }

    public void remove() throws Exception {
        if(has(companyPosition) && has(companyPosition.getId())) {
            String name = companyPosition.getName();
            companyPositionService.delete(companyPosition);
            addDetailMessage("COMPANY POSITION DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("comp-positions.xhtml");
        }
    }

    public void save() throws Exception {
        if(companyPosition != null) {
            companyPositionService.save(companyPosition);
            addDetailMessage("COMPANY POSITION SAVED", companyPosition.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("comp-positions.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
