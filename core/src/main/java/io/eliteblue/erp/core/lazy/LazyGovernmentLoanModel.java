package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.GovernmentLoan;
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

public class LazyGovernmentLoanModel extends LazyDataModel<GovernmentLoan> {

    private List<GovernmentLoan> dataSource;

    public LazyGovernmentLoanModel(List<GovernmentLoan> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public GovernmentLoan getRowData(String rowKey) {
        for (GovernmentLoan governmentLoan : dataSource) {
            if (governmentLoan.getId() == Integer.parseInt(rowKey)) {
                return governmentLoan;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(GovernmentLoan governmentLoan) {
        return String.valueOf(governmentLoan.getId());
    }

    @Override
    public List<GovernmentLoan> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<GovernmentLoan> governmentLoans = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<GovernmentLoan>> comparators = sortBy.values().stream()
                    .map(o -> new GovernmentLoanLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<GovernmentLoan> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            governmentLoans.sort(cp);
        }

        setRowCount((int) rowCount);

        return governmentLoans;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = filterBy.size() <= 0;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            //System.out.println("FILTERED VALUE: "+filterValue);
            //System.out.println("FILTERED FIELD: "+filter.getField());
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            GovernmentLoan governmentLoan = (GovernmentLoan) o;
            /*if(governmentLoan.getEmployeeBorrower().getErpDetachment() != null && governmentLoan.getEmployeeBorrower().getErpDetachment().getName().toLowerCase().contains(filterText)) {
                System.out.println(governmentLoan.getEmployeeBorrower().getFirstname()+" "+governmentLoan.getEmployeeBorrower().getLastname());
            }*/
            return governmentLoan.getLoanType().name().contains(filterText)
                    || (governmentLoan.getEmployeeBorrower().getErpDetachment() != null && governmentLoan.getEmployeeBorrower().getErpDetachment().getName().toLowerCase().contains(filterText))
                    || governmentLoan.getEmployeeBorrower().getFirstname().toLowerCase().contains(filterText)
                    || governmentLoan.getEmployeeBorrower().getLastname().toLowerCase().contains(filterText)
                    || (governmentLoan.getEmployeeBorrower().getFirstname().toLowerCase()+" "+governmentLoan.getEmployeeBorrower().getLastname().toLowerCase()).contains(filterText);
        }

        return matching;
    }
}
