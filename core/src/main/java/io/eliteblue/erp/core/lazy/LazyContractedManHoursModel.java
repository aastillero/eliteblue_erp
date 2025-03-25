package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ContractedManHours;
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

public class LazyContractedManHoursModel extends LazyDataModel<ContractedManHours> {

    private List<ContractedManHours> dataSource;

    public LazyContractedManHoursModel(List<ContractedManHours> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ContractedManHours getRowData(String rowKey) {
        for(ContractedManHours contractedManHours: dataSource) {
            if (contractedManHours.getId() == Integer.parseInt(rowKey)) {
                return contractedManHours;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ContractedManHours contractedManHours) {
        return String.valueOf(contractedManHours.getId());
    }

    @Override
    public List<ContractedManHours> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<ContractedManHours> contractedManHours = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<ContractedManHours>> comparators = sortBy.values().stream()
                    .map(o -> new ContractedManHoursLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ContractedManHours> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            contractedManHours.sort(cp);
        }

        setRowCount((int) rowCount);

        return contractedManHours;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ContractedManHours contractedManHours = (ContractedManHours) o;
            return true;
        }

        return matching;
    }
}
