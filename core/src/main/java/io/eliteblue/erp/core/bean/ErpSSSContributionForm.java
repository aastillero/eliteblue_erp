package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpSSSContribution;
import io.eliteblue.erp.core.service.ErpSSSContributionService;
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
public class ErpSSSContributionForm implements Serializable {

    @Autowired
    private ErpSSSContributionService erpSSSContributionService;

    private Long id;
    private ErpSSSContribution erpSSSContribution;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpSSSContribution = erpSSSContributionService.findById(Long.valueOf(id));
        } else {
            erpSSSContribution = new ErpSSSContribution();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpSSSContribution getErpSSSContribution() {
        return erpSSSContribution;
    }

    public void setErpSSSContribution(ErpSSSContribution erpSSSContribution) {
        this.erpSSSContribution = erpSSSContribution;
    }

    public void clear() {
        erpSSSContribution = new ErpSSSContribution();
        id = null;
    }

    public boolean isNew() {
        return erpSSSContribution == null || erpSSSContribution.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpSSSContribution) && has(erpSSSContribution.getId())) {
            erpSSSContributionService.delete(erpSSSContribution);
            addDetailMessage("CONTRIBUTION DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("sss-contributions.xhtml");
        }
    }

    public void save() throws Exception {
        if(has(erpSSSContribution)) {
            erpSSSContributionService.save(erpSSSContribution);
            addDetailMessage("CONTRIBUTION SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("sss-contributions.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
