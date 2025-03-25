package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpMaterialRequest;
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

public class LazyErpMaterialRequestModel extends LazyDataModel<ErpMaterialRequest> {

    private List<ErpMaterialRequest> dataSource;

    public LazyErpMaterialRequestModel(List<ErpMaterialRequest> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ErpMaterialRequest getRowData(String rowKey) {
        for(ErpMaterialRequest erpMaterialRequest: dataSource) {
            if (erpMaterialRequest.getId() == Integer.parseInt(rowKey)) {
                return erpMaterialRequest;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpMaterialRequest erpMaterialRequest) {
        return String.valueOf(erpMaterialRequest.getId());
    }

    @Override
    public List<ErpMaterialRequest> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getDetachment().getName().compareTo(o2.getDetachment().getName())));

        // apply offset & filters
        List<ErpMaterialRequest> erpMaterialRequests = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return erpMaterialRequests;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpMaterialRequest erpMaterialRequest = (ErpMaterialRequest) o;
            return erpMaterialRequest.getDetachment().getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
