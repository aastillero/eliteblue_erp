package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.PulloutRequest;
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

public class LazyPulloutRequestModel extends LazyDataModel<PulloutRequest> {

    private List<PulloutRequest> dataSource;

    public LazyPulloutRequestModel(List<PulloutRequest> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PulloutRequest getRowData(String rowKey) {
        for(PulloutRequest pulloutRequest: dataSource) {
            if (pulloutRequest.getId() == Integer.parseInt(rowKey)) {
                return pulloutRequest;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(PulloutRequest pulloutRequest) {
        return String.valueOf(pulloutRequest.getId());
    }

    @Override
    public List<PulloutRequest> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getRequestDate().compareTo(o2.getRequestDate())));

        // apply offset & filters
        List<PulloutRequest> pulloutRequests = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return pulloutRequests;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            PulloutRequest pulloutRequest = (PulloutRequest) o;
            return pulloutRequest.getEquipment().getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
