package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ContractedManHours;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.service.ContractedManHoursService;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ContractedManHoursForm implements Serializable {

    @Autowired
    private ContractedManHoursService contractedManHoursService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    private Long id;
    private Long detachmentId;
    private ErpDetachment detachment;
    private ContractedManHours contractedManHours;

    private Map<String, Long> detachments;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            contractedManHours = contractedManHoursService.findById(Long.valueOf(id));
            detachment = contractedManHours.getErpDetachment();
            detachmentId = detachment.getId();
        } else {
            contractedManHours = new ContractedManHours();
            if(has(detachmentId)) {
                detachment = erpDetachmentService.findById(detachmentId);
                contractedManHours.setErpDetachment(detachment);
            }
            else {
                detachment = new ErpDetachment();
            }
        }
        detachments = new HashMap<>();
        for(ErpDetachment edp: erpDetachmentService.getAll()) {
            detachments.put(edp.getName(), edp.getId());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDetachmentId() {
        return detachmentId;
    }

    public void setDetachmentId(Long detachmentId) {
        this.detachmentId = detachmentId;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public ContractedManHours getContractedManHours() {
        return contractedManHours;
    }

    public void setContractedManHours(ContractedManHours contractedManHours) {
        this.contractedManHours = contractedManHours;
    }

    public void clear() {
        contractedManHours = new ContractedManHours();
        id = null;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public boolean isNew() {
        return contractedManHours == null || contractedManHours.getId() == null;
    }

    public void remove() throws Exception {
        if(has(contractedManHours) && has(contractedManHours.getId())) {
            String name = contractedManHours.getErpDetachment().getName();
            contractedManHoursService.delete(contractedManHours);
            addDetailMessage("CONTRACTED MAN HOURS DELETED FOR DETACHMENT", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("contracted-list.xhtml");
        }
    }

    public void save() throws Exception {
        if(contractedManHours != null) {
            if(has(detachment.getId())) {
                detachment = erpDetachmentService.findById(detachment.getId());
                contractedManHours.setErpDetachment(detachment);
            }

            contractedManHoursService.save(contractedManHours);
            addDetailMessage("CONTRACTED MAN HOURS FOR DETACHMENT SAVED", contractedManHours.getErpDetachment().getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("contracted-list.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
