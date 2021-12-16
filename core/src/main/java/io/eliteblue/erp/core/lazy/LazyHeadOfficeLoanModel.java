package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.HeadOfficeLoan;
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

public class LazyHeadOfficeLoanModel extends LazyDataModel<HeadOfficeLoan> {

    private List<HeadOfficeLoan> dataSource;

    public LazyHeadOfficeLoanModel(List<HeadOfficeLoan> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HeadOfficeLoan getRowData(String rowKey) {
        for (HeadOfficeLoan headOfficeLoan : dataSource) {
            if (headOfficeLoan.getId() == Integer.parseInt(rowKey)) {
                return headOfficeLoan;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(HeadOfficeLoan headOfficeLoan) {
        return String.valueOf(headOfficeLoan.getId());
    }

    @Override
    public List<HeadOfficeLoan> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<HeadOfficeLoan> headOfficeLoans = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<HeadOfficeLoan>> comparators = sortBy.values().stream()
                    .map(o -> new HeadOfficeLoanLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<HeadOfficeLoan> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            headOfficeLoans.sort(cp);
        }

        setRowCount((int) rowCount);

        return headOfficeLoans;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = filterBy.size() <= 0;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            //System.out.println("FILTERED VALUE: "+filterValue);
            //System.out.println("FILTERED FIELD: "+filter.getField());
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            HeadOfficeLoan headOfficeLoan = (HeadOfficeLoan) o;
            return headOfficeLoan.getLoanType().name().contains(filterText)
                    || headOfficeLoan.getEmployeeBorrower().getFirstname().toLowerCase().contains(filterText)
                    || headOfficeLoan.getEmployeeBorrower().getLastname().toLowerCase().contains(filterText)
                    || (headOfficeLoan.getEmployeeBorrower().getFirstname().toLowerCase()+" "+headOfficeLoan.getEmployeeBorrower().getLastname().toLowerCase()).contains(filterText);
        }

        return matching;
    }
}
