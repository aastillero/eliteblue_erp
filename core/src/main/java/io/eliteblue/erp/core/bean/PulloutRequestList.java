package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyPulloutRequestModel;
import io.eliteblue.erp.core.model.PulloutRequest;
import io.eliteblue.erp.core.service.PulloutRequestService;
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
public class PulloutRequestList implements Serializable {

    @Autowired
    private PulloutRequestService pulloutRequestService;

    private LazyDataModel<PulloutRequest> lazyPulloutRequests;

    private List<PulloutRequest> filteredPulloutRequests;

    private List<PulloutRequest> pulloutRequests;

    private PulloutRequest selectedPulloutRequest;

    @PostConstruct
    public void init() {
        pulloutRequests = pulloutRequestService.getAllFilteredByDetachment();
        lazyPulloutRequests = new LazyPulloutRequestModel(pulloutRequests);
        lazyPulloutRequests.setRowCount(10);
    }

    public LazyDataModel<PulloutRequest> getLazyPulloutRequests() {
        return lazyPulloutRequests;
    }

    public void setLazyPulloutRequests(LazyDataModel<PulloutRequest> lazyPulloutRequests) {
        this.lazyPulloutRequests = lazyPulloutRequests;
    }

    public List<PulloutRequest> getFilteredPulloutRequests() {
        return filteredPulloutRequests;
    }

    public void setFilteredPulloutRequests(List<PulloutRequest> filteredPulloutRequests) {
        this.filteredPulloutRequests = filteredPulloutRequests;
    }

    public List<PulloutRequest> getPulloutRequests() {
        return pulloutRequests;
    }

    public void setPulloutRequests(List<PulloutRequest> pulloutRequests) {
        this.pulloutRequests = pulloutRequests;
    }

    public PulloutRequest getSelectedPulloutRequest() {
        return selectedPulloutRequest;
    }

    public void setSelectedPulloutRequest(PulloutRequest selectedPulloutRequest) {
        this.selectedPulloutRequest = selectedPulloutRequest;
    }

    public void onRowSelect(SelectEvent<PulloutRequest> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("pullout-req-form.xhtml?id="+selectedPulloutRequest.getId());
    }

    public void onRowUnselect(UnselectEvent<PulloutRequest> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("pullout-req-form.xhtml?id="+selectedPulloutRequest.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        PulloutRequest pulloutRequest = (PulloutRequest) value;
        return pulloutRequest.getEquipment().getName().toLowerCase().contains(filterText);
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
