package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.CompanyPosition;
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

public class LazyCompanyPositionModel extends LazyDataModel<CompanyPosition> {

    private List<CompanyPosition> dataSource;

    public LazyCompanyPositionModel(List<CompanyPosition> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CompanyPosition getRowData(String rowKey) {
        for(CompanyPosition companyPosition: dataSource) {
            if (companyPosition.getId() == Integer.parseInt(rowKey)) {
                return companyPosition;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(CompanyPosition companyPosition) {
        return String.valueOf(companyPosition.getId());
    }

    @Override
    public List<CompanyPosition> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<CompanyPosition> companyPositions = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return companyPositions;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            CompanyPosition companyPosition = (CompanyPosition) o;
            return companyPosition.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
