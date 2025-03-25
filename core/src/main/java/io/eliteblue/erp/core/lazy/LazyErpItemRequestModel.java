package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpItemRequest;
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

public class LazyErpItemRequestModel extends LazyDataModel<ErpItemRequest> {

    private List<ErpItemRequest> dataSource;

    public LazyErpItemRequestModel(List<ErpItemRequest> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ErpItemRequest getRowData(String rowKey) {
        for(ErpItemRequest erpItem: dataSource) {
            if (erpItem.getId() == Integer.parseInt(rowKey)) {
                return erpItem;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpItemRequest erpItem) {
        return String.valueOf(erpItem.getId());
    }

    @Override
    public List<ErpItemRequest> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getItem().getName().compareTo(o2.getItem().getName())));

        // apply offset & filters
        List<ErpItemRequest> erpItems = dataSource.stream()
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

            ErpItemRequest erpItemRequest = (ErpItemRequest) o;
            return erpItemRequest.getItem().getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
