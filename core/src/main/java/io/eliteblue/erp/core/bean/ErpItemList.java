package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyErpItemModel;
import io.eliteblue.erp.core.model.ErpItem;
import io.eliteblue.erp.core.service.ErpItemService;
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
public class ErpItemList implements Serializable {

    @Autowired
    private ErpItemService itemService;

    private LazyDataModel<ErpItem> lazyErpItems;

    private List<ErpItem> filteredErpItems;

    private List<ErpItem> erpItems;

    private ErpItem selectedErpItem;

    @PostConstruct
    public void init() {
        erpItems = itemService.getAll();
        lazyErpItems = new LazyErpItemModel(erpItems);
        lazyErpItems.setRowCount(10);
    }

    public LazyDataModel<ErpItem> getLazyErpItems() {
        return lazyErpItems;
    }

    public void setLazyErpItems(LazyDataModel<ErpItem> lazyErpItems) {
        this.lazyErpItems = lazyErpItems;
    }

    public List<ErpItem> getFilteredErpItems() {
        return filteredErpItems;
    }

    public void setFilteredErpItems(List<ErpItem> filteredErpItems) {
        this.filteredErpItems = filteredErpItems;
    }

    public List<ErpItem> getErpItems() {
        return erpItems;
    }

    public void setErpItems(List<ErpItem> erpItems) {
        this.erpItems = erpItems;
    }

    public ErpItem getSelectedErpItem() {
        return selectedErpItem;
    }

    public void setSelectedErpItem(ErpItem selectedErpItem) {
        this.selectedErpItem = selectedErpItem;
    }

    public void onRowSelect(SelectEvent<ErpItem> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("item-form.xhtml?id="+selectedErpItem.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpItem> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("item-form.xhtml?id="+selectedErpItem.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpItem erpItem = (ErpItem) value;
        return erpItem.getName().toLowerCase().contains(filterText);
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
