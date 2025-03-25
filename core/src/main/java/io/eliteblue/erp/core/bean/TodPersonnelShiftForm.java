package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.Shift;
import io.eliteblue.erp.core.constants.TODShift;
import io.eliteblue.erp.core.model.TODClient;
import io.eliteblue.erp.core.model.TODPersonnelShift;
import io.eliteblue.erp.core.service.TODClientService;
import io.eliteblue.erp.core.service.TODPersonnelShiftService;
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
public class TodPersonnelShiftForm implements Serializable {

    @Autowired
    private TODPersonnelShiftService todPersonnelShiftService;

    @Autowired
    private TODClientService todClientService;

    private Long id;
    private Long todClientId;
    private TODClient todClient;
    private TODPersonnelShift todPersonnelShift;

    private Map<String, TODShift> shiftValues;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            todPersonnelShift = todPersonnelShiftService.findById(Long.valueOf(id));
            todClient = todPersonnelShift.getTodClient();
            todClientId = todClient.getId();
        } else {
            todPersonnelShift = new TODPersonnelShift();
            if(has(todClientId)) {
                todClient = todClientService.findById(todClientId);
                todPersonnelShift.setTodClient(todClient);
            }
        }
        shiftValues = new HashMap<>();
        for(TODShift g: TODShift.values()) {
            shiftValues.put(g.name(), g);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTodClientId() {
        return todClientId;
    }

    public void setTodClientId(Long todClientId) {
        this.todClientId = todClientId;
    }

    public TODClient getTodClient() {
        return todClient;
    }

    public void setTodClient(TODClient todClient) {
        this.todClient = todClient;
    }

    public TODPersonnelShift getTodPersonnelShift() {
        return todPersonnelShift;
    }

    public void setTodPersonnelShift(TODPersonnelShift todPersonnelShift) {
        this.todPersonnelShift = todPersonnelShift;
    }

    public Map<String, TODShift> getShiftValues() {
        return shiftValues;
    }

    public void setShiftValues(Map<String, TODShift> shiftValues) {
        this.shiftValues = shiftValues;
    }

    public String backBtnPressed() { return "tod-client-form?id="+todClientId+"faces-redirect=true&includeViewParams=true"; }

    public void clear() {
        todPersonnelShift = new TODPersonnelShift();
        id = null;
    }

    public boolean isNew() {
        return todPersonnelShift == null || todPersonnelShift.getId() == null;
    }

    public void remove() throws Exception {
        if(has(todPersonnelShift) && has(todPersonnelShift.getId())) {
            String name = todPersonnelShift.getPersonnelPosition();
            todPersonnelShiftService.delete(todPersonnelShift);
            addDetailMessage("PERSONNEL SHIFT DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("tod-client-form.xhtml?id="+todClientId);
        }
    }

    public void save() throws Exception {
        if(todPersonnelShift != null) {
            if(has(todPersonnelShift.getNoSecurity()) && has(todPersonnelShift.getTod())) {
                Double noSecurity = todPersonnelShift.getNoSecurity();
                Double tod = todPersonnelShift.getTod();
                todPersonnelShift.setTotalHrs1to15(tod * noSecurity * 15);
                todPersonnelShift.setTotalHrs16to28(tod * noSecurity * 13);
                todPersonnelShift.setTotalHrs16to29(tod * noSecurity * 14);
                todPersonnelShift.setTotalHrs16to30(tod * noSecurity * 15);
                todPersonnelShift.setTotalHrs16to31(tod * noSecurity * 16);
            }
            if(has(todClient)) {
                todPersonnelShift.setTodClient(todClient);
            }
            todPersonnelShiftService.save(todPersonnelShift);
            addDetailMessage("PERSONNEL SHIFT SAVED", todPersonnelShift.getPersonnelPosition(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("tod-client-form.xhtml?id="+todClientId);
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
