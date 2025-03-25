package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpSilModel;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.ErpSil;
import io.eliteblue.erp.core.model.view.SilMonitoring;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.ErpSilService;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class SilList implements Serializable {

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpSilService silService;

    private Long id;

    private ErpDetachment erpDetachment;

    private List<ErpSil> erpSils;

    private LazyDataModel<ErpSil> lazyErpSils;

    private List<ErpSil> filteredErpSils;

    private ErpSil selectedSil;

    public void init() {
        if (Faces.isAjaxRequest()) {
            return;
        }
        erpSils = new ArrayList<>();
        if (has(id)) {
            erpDetachment = erpDetachmentService.findById(Long.valueOf(id));
            if(has(erpDetachment) && has(erpDetachment.getAssignedEmployees())) {
                erpSils = silService.findAllByDetachment(erpDetachment);
                erpSils.sort(Comparator.comparing(sil -> sil.getEmployee().getLastname()));
                lazyErpSils = new LazyErpSilModel(erpSils);
                lazyErpSils.setRowCount(10);
            }
        }
        else {
            erpDetachment = new ErpDetachment();
        }
    }

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment erpDetachment) {
        this.erpDetachment = erpDetachment;
    }

    public List<ErpSil> getErpSils() {
        return erpSils;
    }

    public void setErpSils(List<ErpSil> erpSils) {
        this.erpSils = erpSils;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LazyDataModel<ErpSil> getLazyErpSils() {
        return lazyErpSils;
    }

    public void setLazyErpSils(LazyDataModel<ErpSil> lazyErpSils) {
        this.lazyErpSils = lazyErpSils;
    }

    public List<ErpSil> getFilteredErpSils() {
        return filteredErpSils;
    }

    public void setFilteredErpSils(List<ErpSil> filteredErpSils) {
        this.filteredErpSils = filteredErpSils;
    }

    public ErpSil getSelectedSil() {
        return selectedSil;
    }

    public void setSelectedSil(ErpSil selectedSil) {
        this.selectedSil = selectedSil;
    }

    public void removeSil() throws Exception {
        if(has(selectedSil)) {
            silService.delete(selectedSil);
            selectedSil = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("SIL Removed"));
            FacesContext.getCurrentInstance().getExternalContext().redirect("sil-list.xhtml?id="+erpDetachment.getId()+"&clientId="+erpDetachment.getErpClient().getId());
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpSil erpSil = (ErpSil) value;
        return erpSil.getEmployee().getLastname().toLowerCase().contains(filterText)
                || erpSil.getEmployee().getFirstname().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }
}
