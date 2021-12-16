package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpConfig;
import io.eliteblue.erp.core.service.ErpConfigService;
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
public class ErpConfigForm implements Serializable {

    @Autowired
    private ErpConfigService erpConfigService;

    private Long id;
    private ErpConfig erpConfig;
    private Map<String, String> componentSelect;
    private String selectedComponent;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpConfig = erpConfigService.findById(Long.valueOf(id));
            if(has(erpConfig.getDateValue())){
                selectedComponent = "date";
            } else if(has(erpConfig.getNumValue())) {
                selectedComponent = "number";
            } else {
                selectedComponent = "text";
            }
        } else {
            erpConfig = new ErpConfig();
        }
        componentSelect = new HashMap<>();
        componentSelect.put("TEXT", "text");
        componentSelect.put("NUMBER", "number");
        componentSelect.put("DATE", "date");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpConfig getErpConfig() {
        return erpConfig;
    }

    public void setErpConfig(ErpConfig erpConfig) {
        this.erpConfig = erpConfig;
    }

    public String getSelectedComponent() {
        return selectedComponent;
    }

    public void setSelectedComponent(String selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    public Map<String, String> getComponentSelect() {
        return componentSelect;
    }

    public void setComponentSelect(Map<String, String> componentSelect) {
        this.componentSelect = componentSelect;
    }

    public void clear() {
        erpConfig = new ErpConfig();
        id = null;
    }

    public boolean isNew() {
        return erpConfig == null || erpConfig.getId() == null;
    }

    public void componentTypeChange() {
        System.out.println("SELECTED COMPONENT: "+selectedComponent);
    }

    public void remove() throws Exception {
        if(has(erpConfig) && has(erpConfig.getId())) {
            String name = erpConfig.getName();
            erpConfigService.delete(erpConfig);
            addDetailMessage("CONFIG DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("configs.xhtml");
        }
    }

    public void save() throws Exception {
        if(has(erpConfig)) {
            erpConfigService.save(erpConfig);
            addDetailMessage("CONFIG SAVED", erpConfig.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("configs.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
