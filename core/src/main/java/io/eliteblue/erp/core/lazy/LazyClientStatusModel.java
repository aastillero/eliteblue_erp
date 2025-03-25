package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ClientStatus;
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

public class LazyClientStatusModel extends LazyDataModel<ClientStatus> {

    private List<ClientStatus> dataSource;

    public LazyClientStatusModel(List<ClientStatus> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ClientStatus getRowData(String rowKey) {
        for(ClientStatus clientStatus: dataSource) {
            if (clientStatus.getId() == Integer.parseInt(rowKey)) {
                return clientStatus;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ClientStatus clientStatus) {
        return String.valueOf(clientStatus.getId());
    }

    @Override
    public List<ClientStatus> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<ClientStatus> clientStatuses = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return clientStatuses;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ClientStatus clientStatus = (ClientStatus) o;
            return clientStatus.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
