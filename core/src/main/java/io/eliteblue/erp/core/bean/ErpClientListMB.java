package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpClientModel;
import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.service.ClientStatusService;
import io.eliteblue.erp.core.service.ErpClientService;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class ErpClientListMB implements Serializable {

    @Autowired
    private ErpClientService erpClientService;

    @Autowired
    private ClientStatusService clientStatusService;

    private LazyDataModel<ErpClient> lazyErpClients;

    private List<ErpClient> filteredErpClients;

    private List<ErpClient> clients;

    private ErpClient selectedClient;

    private String selectedStatus;

    private Map<String, String> statusValues;

    @PostConstruct
    public void init() {
        clients = erpClientService.getAll();
        clients.sort(Comparator.comparing(ErpClient::getName));
        lazyErpClients = new LazyErpClientModel(clients);
        lazyErpClients.setRowCount(10);

        List<ClientStatus> statuses = clientStatusService.getAll();
        statusValues = new HashMap<>();
        for (ClientStatus cs: statuses) {
            statusValues.put(cs.getName(), cs.getName());
        }
    }

    public LazyDataModel<ErpClient> getLazyErpClients() {
        return lazyErpClients;
    }

    public void setLazyErpClients(LazyDataModel<ErpClient> lazyErpClients) {
        this.lazyErpClients = lazyErpClients;
    }

    public List<ErpClient> getFilteredErpClients() {
        return filteredErpClients;
    }

    public void setFilteredErpClients(List<ErpClient> filteredErpClients) {
        this.filteredErpClients = filteredErpClients;
    }

    public List<ErpClient> getClients() {
        return clients;
    }

    public void setClients(List<ErpClient> clients) {
        this.clients = clients;
    }

    public ErpClient getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(ErpClient selectedClient) {
        this.selectedClient = selectedClient;
    }

    public String getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public Map<String, String> getStatusValues() {
        return statusValues;
    }

    public void setStatusValues(Map<String, String> statusValues) {
        this.statusValues = statusValues;
    }

    public void onRowSelect(SelectEvent<ErpClient> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("client-form.xhtml?id="+selectedClient.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpClient> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("client-form.xhtml?id="+selectedClient.getId());
    }

    public void getClientByStatus() {
        //System.out.println("STATUS ["+selectedStatus+"] CLIENT NOW IS FILTERED!!!");
        ClientStatus clientStatus = clientStatusService.findByName(selectedStatus);
        clients = erpClientService.getByClientStatus(clientStatus);
        lazyErpClients = new LazyErpClientModel(clients);
        lazyErpClients.setRowCount(10);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpClient erpClient = (ErpClient) value;
        return erpClient.getName().toLowerCase().contains(filterText);
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