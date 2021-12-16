package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyScoutLoanModel;
import io.eliteblue.erp.core.model.ScoutLoan;
import io.eliteblue.erp.core.service.ScoutLoanService;
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
public class ScoutLoanList implements Serializable {

    @Autowired
    private ScoutLoanService scoutLoanService;

    private LazyDataModel<ScoutLoan> lazyScoutLoans;

    private List<ScoutLoan> filteredScoutLoans;

    private List<ScoutLoan> scoutLoans;

    private ScoutLoan selectedScoutLoan;

    @PostConstruct
    public void init() {
        scoutLoans = scoutLoanService.getAll();
        lazyScoutLoans = new LazyScoutLoanModel(scoutLoans);
        lazyScoutLoans.setRowCount(10);
    }

    public LazyDataModel<ScoutLoan> getLazyScoutLoans() {
        return lazyScoutLoans;
    }

    public void setLazyScoutLoans(LazyDataModel<ScoutLoan> lazyScoutLoans) {
        this.lazyScoutLoans = lazyScoutLoans;
    }

    public List<ScoutLoan> getFilteredScoutLoans() {
        return filteredScoutLoans;
    }

    public void setFilteredScoutLoans(List<ScoutLoan> filteredScoutLoans) {
        this.filteredScoutLoans = filteredScoutLoans;
    }

    public List<ScoutLoan> getScoutLoans() {
        return scoutLoans;
    }

    public void setScoutLoans(List<ScoutLoan> scoutLoans) {
        this.scoutLoans = scoutLoans;
    }

    public ScoutLoan getSelectedScoutLoan() {
        return selectedScoutLoan;
    }

    public void setSelectedScoutLoan(ScoutLoan selectedScoutLoan) {
        this.selectedScoutLoan = selectedScoutLoan;
    }

    public void onRowSelect(SelectEvent<ScoutLoan> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("scout-loan-form.xhtml?id="+ selectedScoutLoan.getId());
    }

    public void onRowUnselect(UnselectEvent<ScoutLoan> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("scout-loan-form.xhtml?id="+ selectedScoutLoan.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ScoutLoan scoutLoan = (ScoutLoan) value;
        return scoutLoan.getLoanType().name().contains(filterText)
            || scoutLoan.getEmployeeBorrower().getFirstname().toLowerCase().contains(filterText)
            || scoutLoan.getEmployeeBorrower().getLastname().toLowerCase().contains(filterText)
            || (scoutLoan.getEmployeeBorrower().getFirstname().toLowerCase()+" "+scoutLoan.getEmployeeBorrower().getLastname().toLowerCase()).contains(filterText);
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
