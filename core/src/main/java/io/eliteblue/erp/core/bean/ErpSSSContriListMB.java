package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpClientModel;
import io.eliteblue.erp.core.lazy.LazyErpSSSContributionModel;
import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.ErpSSSContribution;
import io.eliteblue.erp.core.service.ErpSSSContributionService;
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
public class ErpSSSContriListMB implements Serializable {

    @Autowired
    private ErpSSSContributionService erpSSSContributionService;

    private LazyDataModel<ErpSSSContribution> lazyErpContributions;

    private List<ErpSSSContribution> filteredErpSSSContributions;

    private List<ErpSSSContribution> erpSSSContributions;

    private ErpSSSContribution selectedErpContribution;

    @PostConstruct
    public void init() {
        erpSSSContributions = erpSSSContributionService.getAll();
        lazyErpContributions = new LazyErpSSSContributionModel(erpSSSContributions);
        lazyErpContributions.setRowCount(10);
    }

    public LazyDataModel<ErpSSSContribution> getLazyErpContributions() {
        return lazyErpContributions;
    }

    public void setLazyErpContributions(LazyDataModel<ErpSSSContribution> lazyErpContributions) {
        this.lazyErpContributions = lazyErpContributions;
    }

    public List<ErpSSSContribution> getFilteredErpSSSContributions() {
        return filteredErpSSSContributions;
    }

    public void setFilteredErpSSSContributions(List<ErpSSSContribution> filteredErpSSSContributions) {
        this.filteredErpSSSContributions = filteredErpSSSContributions;
    }

    public List<ErpSSSContribution> getErpSSSContributions() {
        return erpSSSContributions;
    }

    public void setErpSSSContributions(List<ErpSSSContribution> erpSSSContributions) {
        this.erpSSSContributions = erpSSSContributions;
    }

    public ErpSSSContribution getSelectedErpContribution() {
        return selectedErpContribution;
    }

    public void setSelectedErpContribution(ErpSSSContribution selectedErpContribution) {
        this.selectedErpContribution = selectedErpContribution;
    }

    public void onRowSelect(SelectEvent<ErpSSSContribution> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("sss-contribution-form.xhtml?id="+selectedErpContribution.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpSSSContribution> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("sss-contribution-form.xhtml?id="+selectedErpContribution.getId());
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