package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyGovernmentLoanModel;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.service.GovernmentLoanService;
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
public class GovernmentLoanList implements Serializable {

    @Autowired
    private GovernmentLoanService governmentLoanService;

    private LazyDataModel<GovernmentLoan> lazyGovernmentLoans;

    private List<GovernmentLoan> filteredGovernmentLoans;

    private List<GovernmentLoan> governmentLoans;

    private GovernmentLoan selectedGovernmentLoan;

    @PostConstruct
    public void init() {
        governmentLoans = governmentLoanService.getAll();
        lazyGovernmentLoans = new LazyGovernmentLoanModel(governmentLoans);
        lazyGovernmentLoans.setRowCount(10);
    }

    public LazyDataModel<GovernmentLoan> getLazyGovernmentLoans() {
        return lazyGovernmentLoans;
    }

    public void setLazyGovernmentLoans(LazyDataModel<GovernmentLoan> lazyGovernmentLoans) {
        this.lazyGovernmentLoans = lazyGovernmentLoans;
    }

    public List<GovernmentLoan> getFilteredGovernmentLoans() {
        return filteredGovernmentLoans;
    }

    public void setFilteredGovernmentLoans(List<GovernmentLoan> filteredGovernmentLoans) {
        this.filteredGovernmentLoans = filteredGovernmentLoans;
    }

    public List<GovernmentLoan> getGovernmentLoans() {
        return governmentLoans;
    }

    public void setGovernmentLoans(List<GovernmentLoan> governmentLoans) {
        this.governmentLoans = governmentLoans;
    }

    public GovernmentLoan getSelectedGovernmentLoan() {
        return selectedGovernmentLoan;
    }

    public void setSelectedGovernmentLoan(GovernmentLoan selectedGovernmentLoan) {
        this.selectedGovernmentLoan = selectedGovernmentLoan;
    }

    public void onRowSelect(SelectEvent<GovernmentLoan> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("government-loan-form.xhtml?id="+selectedGovernmentLoan.getId());
    }

    public void onRowUnselect(UnselectEvent<GovernmentLoan> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("government-loan-form.xhtml?id="+selectedGovernmentLoan.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        GovernmentLoan governmentLoan = (GovernmentLoan) value;
        return governmentLoan.getLoanType().name().contains(filterText)
            || governmentLoan.getEmployeeBorrower().getFirstname().toLowerCase().contains(filterText)
            || governmentLoan.getEmployeeBorrower().getLastname().toLowerCase().contains(filterText)
            || (governmentLoan.getEmployeeBorrower().getFirstname().toLowerCase()+" "+governmentLoan.getEmployeeBorrower().getLastname().toLowerCase()).contains(filterText);
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
