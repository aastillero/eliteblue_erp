package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.DeliveryStatus;
import io.eliteblue.erp.core.model.ErpMaterialRequest;
import io.eliteblue.erp.core.model.RequestDelivery;
import io.eliteblue.erp.core.service.ErpMaterialRequestService;
import io.eliteblue.erp.core.service.RequestDeliveryService;
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
public class ErpDeliveryRequestForm implements Serializable {

    @Autowired
    private RequestDeliveryService requestDeliveryService;

    @Autowired
    private ErpMaterialRequestService erpMaterialRequestService;

    private Long id;

    private Long materialId;

    private ErpMaterialRequest materialRequest;

    private RequestDelivery requestDelivery;

    private Map<String, DeliveryStatus> statusValues;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        if (has(id)) {
            requestDelivery = requestDeliveryService.findById(Long.valueOf(id));
            materialRequest = requestDelivery.getErpMaterialRequest();
            materialId = materialRequest.getId();
        } else {
            requestDelivery = new RequestDelivery();
            if(has(materialId)) {
                materialRequest = erpMaterialRequestService.findById(Long.valueOf(materialId));
                requestDelivery.setErpMaterialRequest(materialRequest);
            }
        }

        statusValues = new HashMap<>();
        for(DeliveryStatus d: DeliveryStatus.values()) {
            statusValues.put(d.name(), d);
        }
    }

    public RequestDeliveryService getRequestDeliveryService() {
        return requestDeliveryService;
    }

    public void setRequestDeliveryService(RequestDeliveryService requestDeliveryService) {
        this.requestDeliveryService = requestDeliveryService;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestDelivery getRequestDelivery() {
        return requestDelivery;
    }

    public void setRequestDelivery(RequestDelivery requestDelivery) {
        this.requestDelivery = requestDelivery;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public ErpMaterialRequest getMaterialRequest() {
        return materialRequest;
    }

    public void setMaterialRequest(ErpMaterialRequest materialRequest) {
        this.materialRequest = materialRequest;
    }

    public Map<String, DeliveryStatus> getStatusValues() {
        return statusValues;
    }

    public void setStatusValues(Map<String, DeliveryStatus> statusValues) {
        this.statusValues = statusValues;
    }

    public boolean isNew() {
        return requestDelivery == null || requestDelivery.getId() == null;
    }

    public void clear() {
        requestDelivery = new RequestDelivery();
        id = null;
    }

    public void save() throws Exception {
        if(requestDelivery != null) {
            requestDeliveryService.save(requestDelivery);
            addDetailMessage("DELIVERY STATUS SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("material-requests.xhtml");
        }
    }

    public void remove() throws Exception {
        if(requestDelivery != null) {
            requestDeliveryService.delete(requestDelivery);
            addDetailMessage("DELIVERY STATUS DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("material-requests.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
