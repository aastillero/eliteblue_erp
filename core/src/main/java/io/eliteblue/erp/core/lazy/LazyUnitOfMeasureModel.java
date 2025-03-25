package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.UnitOfMeasure;
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

public class LazyUnitOfMeasureModel extends LazyDataModel<UnitOfMeasure> {

    private List<UnitOfMeasure> dataSource;

    public LazyUnitOfMeasureModel(List<UnitOfMeasure> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UnitOfMeasure getRowData(String rowKey) {
        for(UnitOfMeasure unitOfMeasure: dataSource) {
            if (unitOfMeasure.getId() == Integer.parseInt(rowKey)) {
                return unitOfMeasure;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(UnitOfMeasure unitOfMeasure) {
        return String.valueOf(unitOfMeasure.getId());
    }

    @Override
    public List<UnitOfMeasure> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getMeasure().compareTo(o2.getMeasure())));

        // apply offset & filters
        List<UnitOfMeasure> unitOfMeasures = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return unitOfMeasures;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            UnitOfMeasure unitOfMeasure = (UnitOfMeasure) o;
            return unitOfMeasure.getMeasure().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
