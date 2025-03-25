package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyClientStatusModel;
import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.service.ClientStatusService;
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
public class ClientStatusList implements Serializable {

    @Autowired
    private ClientStatusService clientStatusService;

    private LazyDataModel<ClientStatus> lazyClientStatus;

    private List<ClientStatus> filteredClientStatus;

    private List<ClientStatus> clientStatuses;

    private ClientStatus selectedClientStatus;

    @PostConstruct
    public void init() {
        clientStatuses = clientStatusService.getAll();
        lazyClientStatus = new LazyClientStatusModel(clientStatuses);
        lazyClientStatus.setRowCount(10);
    }

    public LazyDataModel<ClientStatus> getLazyClientStatus() {
        return lazyClientStatus;
    }

    public void setLazyClientStatus(LazyDataModel<ClientStatus> lazyClientStatus) {
        this.lazyClientStatus = lazyClientStatus;
    }

    public List<ClientStatus> getFilteredClientStatus() {
        return filteredClientStatus;
    }

    public void setFilteredClientStatus(List<ClientStatus> filteredClientStatus) {
        this.filteredClientStatus = filteredClientStatus;
    }

    public List<ClientStatus> getClientStatuses() {
        return clientStatuses;
    }

    public void setClientStatuses(List<ClientStatus> clientStatuses) {
        this.clientStatuses = clientStatuses;
    }

    public ClientStatus getSelectedClientStatus() {
        return selectedClientStatus;
    }

    public void setSelectedClientStatus(ClientStatus selectedClientStatus) {
        this.selectedClientStatus = selectedClientStatus;
    }

    public void onRowSelect(SelectEvent<ClientStatus> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("client-stat-form.xhtml?id="+selectedClientStatus.getId());
    }

    public void onRowUnselect(UnselectEvent<ClientStatus> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("client-stat-form.xhtml?id="+selectedClientStatus.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ClientStatus clientStatus = (ClientStatus) value;
        return clientStatus.getName().toLowerCase().contains(filterText);
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
