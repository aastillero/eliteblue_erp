package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyCompanyPositionModel;
import io.eliteblue.erp.core.model.CompanyPosition;
import io.eliteblue.erp.core.service.CompanyPositionService;
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
public class CompanyPositionList implements Serializable {

    @Autowired
    private CompanyPositionService companyPositionService;

    private LazyDataModel<CompanyPosition> lazyCompanyPosition;

    private List<CompanyPosition> filteredCompanyPosition;

    private List<CompanyPosition> companyPositions;

    private CompanyPosition selectedCompanyPosition;

    @PostConstruct
    public void init() {
        companyPositions = companyPositionService.getAll();
        lazyCompanyPosition = new LazyCompanyPositionModel(companyPositions);
        lazyCompanyPosition.setRowCount(10);
    }

    public LazyDataModel<CompanyPosition> getLazyCompanyPosition() {
        return lazyCompanyPosition;
    }

    public void setLazyCompanyPosition(LazyDataModel<CompanyPosition> lazyCompanyPosition) {
        this.lazyCompanyPosition = lazyCompanyPosition;
    }

    public List<CompanyPosition> getFilteredCompanyPosition() {
        return filteredCompanyPosition;
    }

    public void setFilteredCompanyPosition(List<CompanyPosition> filteredCompanyPosition) {
        this.filteredCompanyPosition = filteredCompanyPosition;
    }

    public List<CompanyPosition> getCompanyPositions() {
        return companyPositions;
    }

    public void setCompanyPositions(List<CompanyPosition> companyPositions) {
        this.companyPositions = companyPositions;
    }

    public CompanyPosition getSelectedCompanyPosition() {
        return selectedCompanyPosition;
    }

    public void setSelectedCompanyPosition(CompanyPosition selectedCompanyPosition) {
        this.selectedCompanyPosition = selectedCompanyPosition;
    }

    public void onRowSelect(SelectEvent<CompanyPosition> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("comp-pos-form.xhtml?id="+selectedCompanyPosition.getId());
    }

    public void onRowUnselect(UnselectEvent<CompanyPosition> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("comp-pos-form.xhtml?id="+selectedCompanyPosition.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        CompanyPosition companyPosition = (CompanyPosition) value;
        return companyPosition.getName().toLowerCase().contains(filterText);
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
