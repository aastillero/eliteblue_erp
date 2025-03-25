package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.lazy.LazyErpItemRequestModel;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpMaterialRequestForm implements Serializable {

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Autowired
    private ErpMaterialRequestService erpMaterialRequestService;

    @Autowired
    private ErpUserService erpUserService;

    @Autowired
    private ErpItemService erpItemService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private RequisitionApprovalService requisitionApprovalService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private Long id;

    private ErpMaterialRequest erpMaterialRequest;

    private Map<String, Long> detachments;

    private ErpDetachment detachment;

    private String requestedBy;

    private String dateRequested;

    private LazyDataModel<ErpItemRequest> lazyItemRequest;

    private List<ErpItemRequest> filteredErpItemRequest;

    private ErpItemRequest selectedItemRequest;

    private List<RequestDelivery> deliveries;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpMaterialRequest = erpMaterialRequestService.findById(Long.valueOf(id));
            detachment = erpMaterialRequest.getDetachment();
            List<ErpItemRequest> itemRequests = new ArrayList<>(erpMaterialRequest.getItemRequested());
            lazyItemRequest = new LazyErpItemRequestModel(itemRequests);
            //erpItem = erpMaterialRequest.getItemRequested();
            dateRequested = format.format(erpMaterialRequest.getDateCreated());
            deliveries = new ArrayList<>(erpMaterialRequest.getRequestDeliveries());
            deliveries.sort(Comparator.comparing(CoreEntity::getDateCreated).reversed());
        } else {
            erpMaterialRequest = new ErpMaterialRequest();
            if(!has(detachment)) {
                detachment = new ErpDetachment();
            }
            dateRequested = format.format(new Date());
        }

        detachments = new HashMap<>();
        List<ErpDetachment> erpDetachments;
        if(CurrentUserUtil.isAdmin()) {
            erpDetachments = erpDetachmentService.getAll();
        } else {
            erpDetachments = erpDetachmentService.filteredLocationOrCurrent();
        }
        for(ErpDetachment edp: erpDetachments) {
            detachments.put(edp.getName(), edp.getId());
        }
        requestedBy = CurrentUserUtil.getFullName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public ErpMaterialRequest getErpMaterialRequest() {
        return erpMaterialRequest;
    }

    public void setErpMaterialRequest(ErpMaterialRequest erpMaterialRequest) {
        this.erpMaterialRequest = erpMaterialRequest;
    }

    public LazyDataModel<ErpItemRequest> getLazyItemRequest() {
        return lazyItemRequest;
    }

    public void setLazyItemRequest(LazyDataModel<ErpItemRequest> lazyItemRequest) {
        this.lazyItemRequest = lazyItemRequest;
    }

    public List<ErpItemRequest> getFilteredErpItemRequest() {
        return filteredErpItemRequest;
    }

    public void setFilteredErpItemRequest(List<ErpItemRequest> filteredErpItemRequest) {
        this.filteredErpItemRequest = filteredErpItemRequest;
    }

    public ErpItemRequest getSelectedItemRequest() {
        return selectedItemRequest;
    }

    public void setSelectedItemRequest(ErpItemRequest selectedItemRequest) {
        this.selectedItemRequest = selectedItemRequest;
    }

    public List<RequestDelivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<RequestDelivery> deliveries) {
        this.deliveries = deliveries;
    }

    public void clear() {
        erpMaterialRequest = new ErpMaterialRequest();
        id = null;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public String newItemRequestPressed() {
        return "item-request-form.xhtml?materialId="+erpMaterialRequest.getId()+"faces-redirect=true&includeViewParams=true";
    }

    public String newDeliveryRequestPressed() {
        return "delivery-request-form.xhtml?materialId="+erpMaterialRequest.getId()+"faces-redirect=true&includeViewParams=true";
    }

    public String navDeliveryRequest(RequestDelivery del) {
        return "delivery-request-form.xhtml?id="+del.getId()+"faces-redirect=true&includeViewParams=true";
    }

    public void onRowSelect(SelectEvent<ErpItemRequest> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("item-request-form.xhtml?id="+ selectedItemRequest.getId()+"&materialId="+erpMaterialRequest.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpItemRequest> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("item-request-form.xhtml?id="+ selectedItemRequest.getId()+"&materialId="+erpMaterialRequest.getId());
    }

    public boolean isNew() {
        return erpMaterialRequest == null || erpMaterialRequest.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpMaterialRequest) && has(erpMaterialRequest.getId())) {
            String name = erpMaterialRequest.getDetachment().getName();
            erpMaterialRequestService.delete(erpMaterialRequest);
            addDetailMessage("MATERIAL REQUEST DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("material-requests.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpMaterialRequest != null) {
            ErpUser usr = erpUserService.findByUsername(CurrentUserUtil.getUsername());
            erpMaterialRequest.setRequester(usr);
            //erpMaterialRequest.setItemRequested(itm);
            erpMaterialRequest.setDetachment(detachment);
            erpMaterialRequest.setStatus(ApprovalStatus.PENDING);
            erpMaterialRequestService.save(erpMaterialRequest);
            addDetailMessage("MATERIAL REQUEST SAVED", erpMaterialRequest.getDetachment().getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("material-requests.xhtml");
        }
    }

    public void changeApproval(String role) {
        if(role.equals("ROLE_AREA_SEC_COM")) {
            //System.out.println("SETTING ASEC....");
            erpMaterialRequest.getApproval().setAsecApproved(true);
        } else if(role.equals("ROLE_HEAD_OPERATIONS")) {
            //System.out.println("SETTING HEAD OP....");
            erpMaterialRequest.getApproval().setHeadOperationsApproved(true);
        } else if(role.equals("ROLE_CEO")) {
            //System.out.println("SETTING CEO....");
            erpMaterialRequest.getApproval().setCeoApproved(true);
        } else if(role.equals("ROLE_SCOUT_HEAD")) {
            //System.out.println("SETTING SCOUT HEAD....");
            erpMaterialRequest.getApproval().setScoutApproved(true);
        }
    }

    public String approve() {
        List<String> roles = new ArrayList<>(Arrays.asList("ROLE_AREA_SEC_COM", "ROLE_HEAD_OPERATIONS", "ROLE_CEO", "ROLE_SCOUT_HEAD"));
        if(erpMaterialRequest != null && CurrentUserUtil.inAuthorities(roles)) {
            //erpMaterialRequest.setStatus(ApprovalStatus.APPROVED);
            for(String r: roles) {
                if(CurrentUserUtil.isAuthorityName(r)) {
                    RequisitionApproval requisitionApproval = new RequisitionApproval();
                    requisitionApproval.setHeadOperationsApproved(false);
                    requisitionApproval.setAsecApproved(false);
                    requisitionApproval.setCeoApproved(false);
                    requisitionApproval.setScoutApproved(false);

                    if(erpMaterialRequest.getApproval() != null) {
                        //System.out.println("ROLE: "+r+" APPROVAL not null");
                        changeApproval(r);
                        requisitionApprovalService.save(erpMaterialRequest.getApproval());
                    } else {
                        //System.out.println("ROLE: "+r+" APPROVAL null");
                        erpMaterialRequest.setApproval(requisitionApproval);
                        changeApproval(r);
                        requisitionApprovalService.save(requisitionApproval);
                    }
                }
            }
            if(erpMaterialRequest.getApproval() != null
                    && (erpMaterialRequest.getApproval().getAsecApproved() != null && erpMaterialRequest.getApproval().getAsecApproved())
                    && (erpMaterialRequest.getApproval().getHeadOperationsApproved() != null && erpMaterialRequest.getApproval().getHeadOperationsApproved())
                    && (erpMaterialRequest.getApproval().getCeoApproved() != null && erpMaterialRequest.getApproval().getCeoApproved())
                    && (erpMaterialRequest.getApproval().getScoutApproved() != null && erpMaterialRequest.getApproval().getScoutApproved())) {
                erpMaterialRequest.setStatus(ApprovalStatus.APPROVED);
            }
            /*System.out.println("ASEC: "+erpMaterialRequest.getApproval().getAsecApproved());
            System.out.println("HO: "+erpMaterialRequest.getApproval().getHeadOperationsApproved());
            System.out.println("CEO: "+erpMaterialRequest.getApproval().getCeoApproved());
            System.out.println("SCT: "+erpMaterialRequest.getApproval().getScoutApproved());*/
            erpMaterialRequestService.save(erpMaterialRequest);
        }
        return "material-requests?faces-redirect=true&includeViewParams=true";
    }

    public String reject() {
        if(erpMaterialRequest != null) {
            erpMaterialRequest.setStatus(ApprovalStatus.REJECTED);
            erpMaterialRequestService.save(erpMaterialRequest);
        }
        return "material-requests?faces-redirect=true&includeViewParams=true";
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
