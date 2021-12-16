package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.ErpHoliday;
import org.apache.commons.collections4.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;

import javax.faces.context.FacesContext;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LazyErpHolidayModel extends LazyDataModel<ErpHoliday> {

    private List<ErpHoliday> dataSource;

    public LazyErpHolidayModel(List<ErpHoliday> dataSource){ this.dataSource = dataSource; }

    @Override
    public ErpHoliday getRowData(String rowKey) {
        for (ErpHoliday erpHoliday : dataSource) {
            if (erpHoliday.getId() == Integer.parseInt(rowKey)) {
                return erpHoliday;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpHoliday erpHoliday) {
        return String.valueOf(erpHoliday.getId());
    }

    @Override
    public List<ErpHoliday> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<ErpHoliday> erpHolidays = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<ErpHoliday>> comparators = sortBy.values().stream()
                    .map(o -> new ErpHolidayLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ErpHoliday> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            erpHolidays.sort(cp);
        }

        setRowCount((int) rowCount);

        return erpHolidays;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            return true;
        }

        return matching;
    }
}
