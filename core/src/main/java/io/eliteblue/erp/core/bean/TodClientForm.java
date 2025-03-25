package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyTODPersonnelShiftModel;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.TODClient;
import io.eliteblue.erp.core.model.TODPersonnelShift;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.TODClientService;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class TodClientForm implements Serializable {

    @Autowired
    private TODClientService todClientService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    private Long id;
    private TODClient todClient;

    private LazyDataModel<TODPersonnelShift> lazyTODPersonnelShifts;
    private List<TODPersonnelShift> filteredTODPersonnelShifts;
    private List<TODPersonnelShift> todPersonnelShifts;
    private TODPersonnelShift selectedTODPersonnelShift;

    private Map<String, Long> detachments;
    private ErpDetachment detachment;
    private Long detachmentId;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            todClient = todClientService.findById(Long.valueOf(id));
            todPersonnelShifts = new ArrayList<TODPersonnelShift>(todClient.getPersonnelShifts());
            lazyTODPersonnelShifts = new LazyTODPersonnelShiftModel(todPersonnelShifts);
            detachment = todClient.getErpDetachment();
            detachmentId = detachment.getId();
        } else {
            todClient = new TODClient();
            if(has(detachmentId)) {
                detachment = erpDetachmentService.findById(detachmentId);
                todClient.setErpDetachment(detachment);
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

    public TODClientService getTodClientService() {
        return todClientService;
    }

    public void setTodClientService(TODClientService todClientService) {
        this.todClientService = todClientService;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TODClient getTodClient() {
        return todClient;
    }

    public void setTodClient(TODClient todClient) {
        this.todClient = todClient;
    }

    public LazyDataModel<TODPersonnelShift> getLazyTODPersonnelShifts() {
        return lazyTODPersonnelShifts;
    }

    public void setLazyTODPersonnelShifts(LazyDataModel<TODPersonnelShift> lazyTODPersonnelShifts) {
        this.lazyTODPersonnelShifts = lazyTODPersonnelShifts;
    }

    public List<TODPersonnelShift> getFilteredTODPersonnelShifts() {
        return filteredTODPersonnelShifts;
    }

    public void setFilteredTODPersonnelShifts(List<TODPersonnelShift> filteredTODPersonnelShifts) {
        this.filteredTODPersonnelShifts = filteredTODPersonnelShifts;
    }

    public List<TODPersonnelShift> getTodPersonnelShifts() {
        return todPersonnelShifts;
    }

    public void setTodPersonnelShifts(List<TODPersonnelShift> todPersonnelShifts) {
        this.todPersonnelShifts = todPersonnelShifts;
    }

    public TODPersonnelShift getSelectedTODPersonnelShift() {
        return selectedTODPersonnelShift;
    }

    public void setSelectedTODPersonnelShift(TODPersonnelShift selectedTODPersonnelShift) {
        this.selectedTODPersonnelShift = selectedTODPersonnelShift;
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

    public Long getDetachmentId() {
        return detachmentId;
    }

    public void setDetachmentId(Long detachmentId) {
        this.detachmentId = detachmentId;
    }

    public void clear() {
        todClient = new TODClient();
        id = null;
    }

    public boolean isNew() {
        return todClient == null || todClient.getId() == null;
    }

    public String newPersonnelShiftPressed() {
        return "personnel-shift-form?todClientId="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public void onRowSelect(SelectEvent<TODPersonnelShift> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("personnel-shift-form.xhtml?id="+selectedTODPersonnelShift.getId()+"&todClientId="+todClient.getId());
    }

    public void onRowUnselect(UnselectEvent<TODPersonnelShift> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("personnel-shift-form.xhtml?id="+selectedTODPersonnelShift.getId()+"&todClientId="+todClient.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        TODPersonnelShift todPersonnelShift = (TODPersonnelShift) value;
        return todPersonnelShift.getPersonnelPosition().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public void remove() throws Exception {
        if(has(todClient) && has(todClient.getId())) {
            String name = todClient.getClientName();
            todClientService.delete(todClient);
            addDetailMessage("CLIENT DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("tod-clients.xhtml");
        }
    }

    public void save() throws Exception {
        if(todClient != null) {
            if(has(detachment.getId())) {
                detachment = erpDetachmentService.findById(detachment.getId());
                todClient.setErpDetachment(detachment);
            }
            todClientService.save(todClient);
            addDetailMessage("CLIENT SAVED", todClient.getClientName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("tod-clients.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
