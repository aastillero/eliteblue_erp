package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.UnitOfMeasure;
import io.eliteblue.erp.core.service.UnitOfMeasureService;
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
public class UnitOfMeasureForm implements Serializable {

    @Autowired
    private UnitOfMeasureService unitOfMeasureService;

    private Long id;
    private UnitOfMeasure unitOfMeasure;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            unitOfMeasure = unitOfMeasureService.findById(Long.valueOf(id));
        } else {
            unitOfMeasure = new UnitOfMeasure();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void clear() {
        unitOfMeasure = new UnitOfMeasure();
        id = null;
    }

    public boolean isNew() {
        return unitOfMeasure == null || unitOfMeasure.getId() == null;
    }

    public void remove() throws Exception {
        if(has(unitOfMeasure) && has(unitOfMeasure.getId())) {
            String name = unitOfMeasure.getMeasure();
            unitOfMeasureService.delete(unitOfMeasure);
            addDetailMessage("MEASURE DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("units.xhtml");
        }
    }

    public void save() throws Exception {
        if(unitOfMeasure != null) {
            unitOfMeasureService.save(unitOfMeasure);
            addDetailMessage("MEASURE SAVED", unitOfMeasure.getMeasure(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("units.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
