package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpItem;
import io.eliteblue.erp.core.model.ErpItemRequest;
import io.eliteblue.erp.core.model.ErpMaterialRequest;
import io.eliteblue.erp.core.service.ErpItemRequestService;
import io.eliteblue.erp.core.service.ErpItemService;
import io.eliteblue.erp.core.service.ErpMaterialRequestService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpItemRequestForm implements Serializable {

    @Autowired
    private ErpItemRequestService erpItemRequestService;

    @Autowired
    private ErpMaterialRequestService erpMaterialRequestService;

    @Autowired
    private ErpItemService erpItemService;

    private ErpItem erpItem;

    private Map<String, Long> items;

    private Long id;

    private Long materialId;

    private ErpMaterialRequest materialRequest;

    private ErpItemRequest erpItemRequest;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpItemRequest = erpItemRequestService.findById(Long.valueOf(id));
            materialRequest = erpItemRequest.getErpMaterialRequest();
            materialId = materialRequest.getId();
            erpItem = erpItemRequest.getItem();
        } else {
            erpItemRequest = new ErpItemRequest();
            erpItem = new ErpItem();
            if(has(materialId)) {
                materialRequest = erpMaterialRequestService.findById(Long.valueOf(materialId));
                erpItemRequest.setErpMaterialRequest(materialRequest);
            }
        }

        List<ErpItem> erpItems = erpItemService.getAll();
        items = new HashMap<>();
        for(ErpItem i: erpItems) {
            items.put(i.getName(), i.getId());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public ErpItemRequest getErpItemRequest() {
        return erpItemRequest;
    }

    public void setErpItemRequest(ErpItemRequest erpItemRequest) {
        this.erpItemRequest = erpItemRequest;
    }

    public ErpItem getErpItem() {
        return erpItem;
    }

    public void setErpItem(ErpItem erpItem) {
        this.erpItem = erpItem;
    }

    public Map<String, Long> getItems() {
        return items;
    }

    public void setItems(Map<String, Long> items) {
        this.items = items;
    }

    public boolean isNew() {
        return erpItemRequest == null || erpItemRequest.getId() == null;
    }

    public void clear() {
        erpItemRequest = new ErpItemRequest();
        id = null;
    }

    public void save() throws Exception {
        if(erpItemRequest != null) {
            if(erpItem != null) {
                ErpItem itemTemp = erpItemService.findById(erpItem.getId());
                erpItemRequest.setItem(itemTemp);
            }
            erpItemRequestService.save(erpItemRequest);
            addDetailMessage("ITEM REQUEST SAVED", erpItemRequest.getItem().getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("material-requests.xhtml");
        }
    }

    public void remove() throws Exception {
        if(erpItemRequest != null) {
            String name = erpItemRequest.getItem().getName();
            erpItemRequestService.delete(erpItemRequest);
            addDetailMessage("ITEM DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("material-requests.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
