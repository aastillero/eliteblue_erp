package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.TODClient;
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

public class LazyTODClientModel extends LazyDataModel<TODClient> {

    private List<TODClient> dataSource;

    public LazyTODClientModel(List<TODClient> dataSource) { this.dataSource = dataSource; }

    @Override
    public TODClient getRowData(String rowKey) {
        for(TODClient todClient: dataSource) {
            if (todClient.getId() == Integer.parseInt(rowKey)) {
                return todClient;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(TODClient todClient) {
        return String.valueOf(todClient.getId());
    }

    @Override
    public List<TODClient> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getClientName().compareTo(o2.getClientName())));

        // apply offset & filters
        List<TODClient> todClients = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return todClients;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();
            //System.out.println("FILTER TOD CLIENT: "+filterText);

            TODClient todClient = (TODClient) o;
            return todClient.getClientName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
