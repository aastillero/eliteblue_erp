package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpItem;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;

import javax.faces.context.FacesContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LazyErpItemModel extends LazyDataModel<ErpItem> {

    private List<ErpItem> dataSource;

    public LazyErpItemModel(List<ErpItem> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ErpItem getRowData(String rowKey) {
        for(ErpItem erpItem: dataSource) {
            if (erpItem.getId() == Integer.parseInt(rowKey)) {
                return erpItem;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpItem erpItem) {
        return String.valueOf(erpItem.getId());
    }

    @Override
    public List<ErpItem> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<ErpItem> erpItems = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return erpItems;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpItem erpClient = (ErpItem) o;
            return erpClient.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
