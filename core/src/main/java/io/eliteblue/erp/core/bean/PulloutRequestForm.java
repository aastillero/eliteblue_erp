package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.InventoryStatus;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEquipment;
import io.eliteblue.erp.core.model.PulloutRequest;
import io.eliteblue.erp.core.model.RequisitionApproval;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class PulloutRequestForm implements Serializable {

    @Autowired
    private PulloutRequestService pulloutRequestService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private RequisitionApprovalService requisitionApprovalService;

    @Autowired
    private ErpEquipmentService erpEquipmentService;

    @Autowired
    private ErpUserService erpUserService;

    private Long id;
    private PulloutRequest pulloutRequest;

    private Map<String, Long> detachments;
    private ErpDetachment detachment;

    private Map<String, String> equipments;
    private ErpEquipment equipment;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            pulloutRequest = pulloutRequestService.findById(Long.valueOf(id));
            detachment = pulloutRequest.getDetachment();
            equipment = pulloutRequest.getEquipment();
        } else {
            pulloutRequest = new PulloutRequest();
            List<ErpDetachment> dets = erpDetachmentService.getAllCurrentDetachment();
            if(has(dets) && dets.size() > 0) {
                detachment = erpDetachmentService.getAllCurrentDetachment().get(0);
            }
            else {
                detachment = new ErpDetachment();
            }
            equipment = new ErpEquipment();
        }

        equipments = new HashMap<>();
        for(ErpEquipment eq: erpEquipmentService.getAllFilteredByDetachment(detachment)) {
            equipments.put(eq.getName()+" "+eq.getEquipmentType().getName()+" "+eq.getSerialNo(), eq.getRefNum());
        }
        detachments = new HashMap<>();
        for(ErpDetachment d: erpDetachmentService.getAllFilteredDetachment()) {
            detachments.put(d.getName(), d.getId());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PulloutRequest getPulloutRequest() {
        return pulloutRequest;
    }

    public void setPulloutRequest(PulloutRequest pulloutRequest) {
        this.pulloutRequest = pulloutRequest;
    }

    public void clear() {
        pulloutRequest = new PulloutRequest();
        id = null;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public Map<String, String> getEquipments() {
        return equipments;
    }

    public void setEquipments(Map<String, String> equipments) {
        this.equipments = equipments;
    }

    public ErpEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(ErpEquipment equipment) {
        this.equipment = equipment;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public boolean isNew() {
        return pulloutRequest == null || pulloutRequest.getId() == null;
    }

    public void remove() throws Exception {
        if(has(pulloutRequest) && has(pulloutRequest.getId())) {
            String name = pulloutRequest.getEquipment().getName();
            pulloutRequestService.delete(pulloutRequest);
            addDetailMessage("PULL-OUT REQUEST DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("pullout-reqs.xhtml");
        }
    }

    public void save() throws Exception {
        if(pulloutRequest != null) {
            if(!has(pulloutRequest.getId())) {
                if(has(equipment.getRefNum())) {
                    ErpEquipment erpEquipment = erpEquipmentService.findByCodenum(equipment.getRefNum());
                    pulloutRequest.setEquipment(erpEquipment);
                }
                ErpUser usr = erpUserService.findByUsername(CurrentUserUtil.getUsername());
                pulloutRequest.setStatus(InventoryStatus.PENDING_ASC);
                RequisitionApproval requisitionApproval = new RequisitionApproval();
                requisitionApproval.setAsecApproved(false);
                requisitionApproval.setHeadOperationsApproved(false);
                requisitionApproval.setCeoApproved(false);
                requisitionApproval.setScoutApproved(false);
                requisitionApprovalService.save(requisitionApproval);

                pulloutRequest.setApproval(requisitionApproval);
                pulloutRequest.setRequester(usr);
                pulloutRequest.setDetachment(detachment);
                pulloutRequest.setRequestDate(new Date());
            }
            pulloutRequestService.save(pulloutRequest);
            addDetailMessage("PULL-OUT REQUEST SAVED", pulloutRequest.getEquipment().getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("pullout-reqs.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
