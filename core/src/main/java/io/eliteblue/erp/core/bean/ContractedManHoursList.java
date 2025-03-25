package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyContractedManHoursModel;
import io.eliteblue.erp.core.model.ContractedManHours;
import io.eliteblue.erp.core.service.ContractedManHoursService;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Named
@ViewScoped
public class ContractedManHoursList implements Serializable {

    private final String pattern = "MMMM dd,yyyy";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    @Autowired
    private ContractedManHoursService contractedManHoursService;

    private List<ContractedManHours> contractedManHoursList;
    private List<ContractedManHours> filteredContractedManHours;
    private LazyDataModel<ContractedManHours> lazyContractedManHours;
    private ContractedManHours selectedContractedManHours;

    @PostConstruct
    public void init() {
        contractedManHoursList = contractedManHoursService.getAll();
        lazyContractedManHours = new LazyContractedManHoursModel(contractedManHoursList);
        lazyContractedManHours.setRowCount(10);
    }

    public List<ContractedManHours> getContractedManHoursList() {
        return contractedManHoursList;
    }

    public void setContractedManHoursList(List<ContractedManHours> contractedManHoursList) {
        this.contractedManHoursList = contractedManHoursList;
    }

    public List<ContractedManHours> getFilteredContractedManHours() {
        return filteredContractedManHours;
    }

    public void setFilteredContractedManHours(List<ContractedManHours> filteredContractedManHours) {
        this.filteredContractedManHours = filteredContractedManHours;
    }

    public LazyDataModel<ContractedManHours> getLazyContractedManHours() {
        return lazyContractedManHours;
    }

    public void setLazyContractedManHours(LazyDataModel<ContractedManHours> lazyContractedManHours) {
        this.lazyContractedManHours = lazyContractedManHours;
    }

    public ContractedManHours getSelectedContractedManHours() {
        return selectedContractedManHours;
    }

    public void setSelectedContractedManHours(ContractedManHours selectedContractedManHours) {
        this.selectedContractedManHours = selectedContractedManHours;
    }

    public void onRowSelect(SelectEvent<ContractedManHours> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("contracted-form.xhtml?id="+ selectedContractedManHours.getId());
    }

    public void onRowUnselect(UnselectEvent<ContractedManHours> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("contracted-form.xhtml?id="+ selectedContractedManHours.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ContractedManHours contractedManHours = (ContractedManHours) value;
        return true;
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
