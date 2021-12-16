package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyHeadOfficeLoanModel;
import io.eliteblue.erp.core.model.HeadOfficeLoan;
import io.eliteblue.erp.core.service.HeadOfficeLoanService;
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
public class HeadOfficeLoanList implements Serializable {

    @Autowired
    private HeadOfficeLoanService headOfficeLoanService;

    private LazyDataModel<HeadOfficeLoan> lazyHeadOfficeLoans;

    private List<HeadOfficeLoan> filteredHeadOfficeLoans;

    private List<HeadOfficeLoan> headOfficeLoans;

    private HeadOfficeLoan selectedHeadOfficeLoan;

    @PostConstruct
    public void init() {
        headOfficeLoans = headOfficeLoanService.getAll();
        lazyHeadOfficeLoans = new LazyHeadOfficeLoanModel(headOfficeLoans);
        lazyHeadOfficeLoans.setRowCount(10);
    }

    public LazyDataModel<HeadOfficeLoan> getLazyHeadOfficeLoans() {
        return lazyHeadOfficeLoans;
    }

    public void setLazyHeadOfficeLoans(LazyDataModel<HeadOfficeLoan> lazyHeadOfficeLoans) {
        this.lazyHeadOfficeLoans = lazyHeadOfficeLoans;
    }

    public List<HeadOfficeLoan> getFilteredHeadOfficeLoans() {
        return filteredHeadOfficeLoans;
    }

    public void setFilteredHeadOfficeLoans(List<HeadOfficeLoan> filteredHeadOfficeLoans) {
        this.filteredHeadOfficeLoans = filteredHeadOfficeLoans;
    }

    public List<HeadOfficeLoan> getHeadOfficeLoans() {
        return headOfficeLoans;
    }

    public void setHeadOfficeLoans(List<HeadOfficeLoan> headOfficeLoans) {
        this.headOfficeLoans = headOfficeLoans;
    }

    public HeadOfficeLoan getSelectedHeadOfficeLoan() {
        return selectedHeadOfficeLoan;
    }

    public void setSelectedHeadOfficeLoan(HeadOfficeLoan selectedHeadOfficeLoan) {
        this.selectedHeadOfficeLoan = selectedHeadOfficeLoan;
    }

    public void onRowSelect(SelectEvent<HeadOfficeLoan> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("head-office-loan-form.xhtml?id="+ selectedHeadOfficeLoan.getId());
    }

    public void onRowUnselect(UnselectEvent<HeadOfficeLoan> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("head-office-loan-form.xhtml?id="+ selectedHeadOfficeLoan.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        HeadOfficeLoan headOfficeLoan = (HeadOfficeLoan) value;
        return headOfficeLoan.getLoanType().name().contains(filterText)
            || headOfficeLoan.getEmployeeBorrower().getFirstname().toLowerCase().contains(filterText)
            || headOfficeLoan.getEmployeeBorrower().getLastname().toLowerCase().contains(filterText)
            || (headOfficeLoan.getEmployeeBorrower().getFirstname().toLowerCase()+" "+headOfficeLoan.getEmployeeBorrower().getLastname().toLowerCase()).contains(filterText);
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
