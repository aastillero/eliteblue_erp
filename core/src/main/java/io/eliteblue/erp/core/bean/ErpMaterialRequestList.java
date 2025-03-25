package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpMaterialRequestModel;
import io.eliteblue.erp.core.model.ErpMaterialRequest;
import io.eliteblue.erp.core.service.ErpMaterialRequestService;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Named
@ViewScoped
public class ErpMaterialRequestList implements Serializable {

    @Autowired
    private ErpMaterialRequestService erpMaterialRequestService;

    private LazyDataModel<ErpMaterialRequest> lazyErpMaterialRequests;

    private List<ErpMaterialRequest> filteredErpMaterialRequests;

    private List<ErpMaterialRequest> erpMaterialRequests;

    private ErpMaterialRequest selectedErpMaterialRequest;

    @PostConstruct
    public void init() {
        erpMaterialRequests = erpMaterialRequestService.getAll();
        lazyErpMaterialRequests = new LazyErpMaterialRequestModel(erpMaterialRequests);
        lazyErpMaterialRequests.setRowCount(10);
    }

    public LazyDataModel<ErpMaterialRequest> getLazyErpMaterialRequests() {
        return lazyErpMaterialRequests;
    }

    public void setLazyErpMaterialRequests(LazyDataModel<ErpMaterialRequest> lazyErpMaterialRequests) {
        this.lazyErpMaterialRequests = lazyErpMaterialRequests;
    }

    public List<ErpMaterialRequest> getFilteredErpMaterialRequests() {
        return filteredErpMaterialRequests;
    }

    public void setFilteredErpMaterialRequests(List<ErpMaterialRequest> filteredErpMaterialRequests) {
        this.filteredErpMaterialRequests = filteredErpMaterialRequests;
    }

    public List<ErpMaterialRequest> getErpMaterialRequests() {
        return erpMaterialRequests;
    }

    public void setErpMaterialRequests(List<ErpMaterialRequest> erpMaterialRequests) {
        this.erpMaterialRequests = erpMaterialRequests;
    }

    public ErpMaterialRequest getSelectedErpMaterialRequest() {
        return selectedErpMaterialRequest;
    }

    public void setSelectedErpMaterialRequest(ErpMaterialRequest selectedErpMaterialRequest) {
        this.selectedErpMaterialRequest = selectedErpMaterialRequest;
    }

    public void onRowSelect(SelectEvent<ErpMaterialRequest> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("material-req-form.xhtml?id="+selectedErpMaterialRequest.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpMaterialRequest> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("material-req-form.xhtml?id="+selectedErpMaterialRequest.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpMaterialRequest erpMaterialRequest = (ErpMaterialRequest) value;
        return erpMaterialRequest.getDetachment().getName().toLowerCase().contains(filterText);
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
