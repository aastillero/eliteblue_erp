package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ScoutLoan;
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

public class LazyScoutLoanModel extends LazyDataModel<ScoutLoan> {

    private List<ScoutLoan> dataSource;

    public LazyScoutLoanModel(List<ScoutLoan> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ScoutLoan getRowData(String rowKey) {
        for (ScoutLoan scoutLoan : dataSource) {
            if (scoutLoan.getId() == Integer.parseInt(rowKey)) {
                return scoutLoan;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ScoutLoan scoutLoan) {
        return String.valueOf(scoutLoan.getId());
    }

    @Override
    public List<ScoutLoan> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<ScoutLoan> scoutLoans = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<ScoutLoan>> comparators = sortBy.values().stream()
                    .map(o -> new ScoutLoanLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ScoutLoan> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            scoutLoans.sort(cp);
        }

        setRowCount((int) rowCount);

        return scoutLoans;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = filterBy.size() <= 0;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            //System.out.println("FILTERED VALUE: "+filterValue);
            //System.out.println("FILTERED FIELD: "+filter.getField());
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ScoutLoan scoutLoan = (ScoutLoan) o;
            return scoutLoan.getLoanType().name().contains(filterText)
                    || (scoutLoan.getEmployeeBorrower().getErpDetachment() != null && scoutLoan.getEmployeeBorrower().getErpDetachment().getName().toLowerCase().contains(filterText))
                    || scoutLoan.getEmployeeBorrower().getFirstname().toLowerCase().contains(filterText)
                    || scoutLoan.getEmployeeBorrower().getLastname().toLowerCase().contains(filterText)
                    || (scoutLoan.getEmployeeBorrower().getFirstname().toLowerCase()+" "+scoutLoan.getEmployeeBorrower().getLastname().toLowerCase()).contains(filterText);
        }

        return matching;
    }
}
