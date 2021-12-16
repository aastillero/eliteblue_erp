package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpPayroll;
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

public class LazyErpPayrollModel extends LazyDataModel<ErpPayroll> {

    private List<ErpPayroll> dataSource;

    public LazyErpPayrollModel(List<ErpPayroll> dataSource){ this.dataSource = dataSource; }

    @Override
    public ErpPayroll getRowData(String rowKey) {
        for (ErpPayroll erpPayroll : dataSource) {
            if (erpPayroll.getId() == Integer.parseInt(rowKey)) {
                return erpPayroll;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpPayroll erpPayroll) {
        return String.valueOf(erpPayroll.getId());
    }

    @Override
    public List<ErpPayroll> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<ErpPayroll> erpPayrolls = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<ErpPayroll>> comparators = sortBy.values().stream()
                    .map(o -> new ErpPayrollLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ErpPayroll> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            erpPayrolls.sort(cp);
        }

        setRowCount((int) rowCount);

        return erpPayrolls;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpPayroll erpPayroll = (ErpPayroll) o;
            return true;
        }

        return matching;
    }
}
