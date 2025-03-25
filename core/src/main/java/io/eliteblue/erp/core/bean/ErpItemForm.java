package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpItem;
import io.eliteblue.erp.core.model.UnitOfMeasure;
import io.eliteblue.erp.core.service.ErpItemService;
import io.eliteblue.erp.core.service.UnitOfMeasureService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpItemForm implements Serializable {

    @Autowired
    private ErpItemService erpItemService;

    @Autowired
    private UnitOfMeasureService unitOfMeasureService;

    private Long id;
    private ErpItem erpItem;

    private List<SelectItem> measures;
    private String measureValue;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpItem = erpItemService.findById(Long.valueOf(id));
            if(has(erpItem) && has(erpItem.getMeasurement())) {
                measureValue = erpItem.getMeasurement().getMeasure();
            }
        } else {
            erpItem = new ErpItem();
        }

        List<UnitOfMeasure> unitOfMeasures = unitOfMeasureService.getAll();
        measures = new ArrayList<SelectItem>();
        for(UnitOfMeasure m: unitOfMeasures) {
            SelectItem itm = new SelectItem(m.getMeasure(), m.getMeasure());
            measures.add(itm);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpItem getErpItem() {
        return erpItem;
    }

    public void setErpItem(ErpItem erpItem) {
        this.erpItem = erpItem;
    }

    public List<SelectItem> getMeasures() {
        return measures;
    }

    public void setMeasures(List<SelectItem> measures) {
        this.measures = measures;
    }

    public String getMeasureValue() {
        return measureValue;
    }

    public void setMeasureValue(String measureValue) {
        this.measureValue = measureValue;
    }

    public void clear() {
        erpItem = new ErpItem();
        id = null;
    }

    public boolean isNew() {
        return erpItem == null || erpItem.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpItem) && has(erpItem.getId())) {
            String name = erpItem.getName();
            erpItemService.delete(erpItem);
            addDetailMessage("ITEM DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("items.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpItem != null) {
            if(has(measureValue)) {
                erpItem.setMeasurement(unitOfMeasureService.findByMeasure(measureValue));
            }
            erpItemService.save(erpItem);
            addDetailMessage("ITEM SAVED", erpItem.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("items.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
