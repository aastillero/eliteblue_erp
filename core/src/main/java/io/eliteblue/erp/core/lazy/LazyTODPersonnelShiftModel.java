package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.TODPersonnelShift;
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

public class LazyTODPersonnelShiftModel extends LazyDataModel<TODPersonnelShift> {

    private List<TODPersonnelShift> dataSource;

    public LazyTODPersonnelShiftModel(List<TODPersonnelShift> dataSource) { this.dataSource = dataSource; }

    @Override
    public TODPersonnelShift getRowData(String rowKey) {
        for(TODPersonnelShift todPersonnelShift: dataSource) {
            if (todPersonnelShift.getId() == Integer.parseInt(rowKey)) {
                return todPersonnelShift;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(TODPersonnelShift todPersonnelShift) {
        return String.valueOf(todPersonnelShift.getId());
    }

    @Override
    public List<TODPersonnelShift> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getPersonnelPosition().compareTo(o2.getPersonnelPosition())));

        // apply offset & filters
        List<TODPersonnelShift> todPersonnelShifts = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return todPersonnelShifts;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            TODPersonnelShift todPersonnelShift = (TODPersonnelShift) o;
            return true;
        }

        return matching;
    }
}
