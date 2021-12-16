package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpPayrollModel;
import io.eliteblue.erp.core.model.ErpPayroll;
import io.eliteblue.erp.core.service.ErpPayrollService;
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
public class ErpPayrollList implements Serializable {

    @Autowired
    private ErpPayrollService erpPayrollService;

    private LazyDataModel<ErpPayroll> lazyErpPayrolls;

    private List<ErpPayroll> filteredErpPayrolls;

    private List<ErpPayroll> payrolls;

    private ErpPayroll selectedPayroll;

    @PostConstruct
    public void init() {
        payrolls = erpPayrollService.getAll();
        lazyErpPayrolls = new LazyErpPayrollModel(payrolls);
        lazyErpPayrolls.setRowCount(10);
    }

    public LazyDataModel<ErpPayroll> getLazyErpPayrolls() {
        return lazyErpPayrolls;
    }

    public void setLazyErpPayrolls(LazyDataModel<ErpPayroll> lazyErpPayrolls) {
        this.lazyErpPayrolls = lazyErpPayrolls;
    }

    public List<ErpPayroll> getFilteredErpPayrolls() {
        return filteredErpPayrolls;
    }

    public void setFilteredErpPayrolls(List<ErpPayroll> filteredErpPayrolls) {
        this.filteredErpPayrolls = filteredErpPayrolls;
    }

    public List<ErpPayroll> getPayrolls() {
        return payrolls;
    }

    public void setPayrolls(List<ErpPayroll> payrolls) {
        this.payrolls = payrolls;
    }

    public ErpPayroll getSelectedPayroll() {
        return selectedPayroll;
    }

    public void setSelectedPayroll(ErpPayroll selectedPayroll) {
        this.selectedPayroll = selectedPayroll;
    }

    public void onRowSelect(SelectEvent<ErpPayroll> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-form.xhtml?id="+ selectedPayroll.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpPayroll> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-form.xhtml?id="+ selectedPayroll.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpPayroll erpPayroll = (ErpPayroll) value;
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