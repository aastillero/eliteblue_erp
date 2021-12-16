package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.EmployeePayroll;
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

public class LazyEmployeePayrollModel extends LazyDataModel<EmployeePayroll> {

    private List<EmployeePayroll> dataSource;

    public LazyEmployeePayrollModel(List<EmployeePayroll> dataSource){ this.dataSource = dataSource; }

    @Override
    public EmployeePayroll getRowData(String rowKey) {
        for (EmployeePayroll employeePayroll : dataSource) {
            if (employeePayroll.getId() == Integer.parseInt(rowKey)) {
                return employeePayroll;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(EmployeePayroll employeePayroll) {
        return String.valueOf(employeePayroll.getId());
    }

    @Override
    public List<EmployeePayroll> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<EmployeePayroll> employeePayrolls = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<EmployeePayroll>> comparators = sortBy.values().stream()
                    .map(o -> new EmployeePayrollLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<EmployeePayroll> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            employeePayrolls.sort(cp);
        }

        setRowCount((int) rowCount);

        return employeePayrolls;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            EmployeePayroll employeePayroll = (EmployeePayroll) o;
            return employeePayroll.getEmployeePayroll().getFirstname().toLowerCase().contains(filterText)
                    || employeePayroll.getEmployeePayroll().getLastname().toLowerCase().contains(filterText)
                    || employeePayroll.getEmployeePayroll().getGender().name().toLowerCase().contains(filterText)
                    || employeePayroll.getEmployeePayroll().getEmployeeId().toLowerCase().contains(filterText)
                    || employeePayroll.getEmployeePayroll().getStatus().name().toLowerCase().contains(filterText)
                    || (employeePayroll.getEmployeePayroll().getFirstname().toLowerCase()+" "+employeePayroll.getEmployeePayroll().getLastname().toLowerCase()).contains(filterText);
        }

        return matching;
    }
}
