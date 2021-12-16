package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.ErpSSSContribution;
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

public class LazyErpSSSContributionModel extends LazyDataModel<ErpSSSContribution> {

    private List<ErpSSSContribution> dataSource;

    public LazyErpSSSContributionModel(List<ErpSSSContribution> dataSource){ this.dataSource = dataSource; }

    @Override
    public ErpSSSContribution getRowData(String rowKey) {
        for (ErpSSSContribution erpSSSContribution : dataSource) {
            if (erpSSSContribution.getId() == Integer.parseInt(rowKey)) {
                return erpSSSContribution;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpSSSContribution erpSSSContribution) {
        return String.valueOf(erpSSSContribution.getId());
    }

    @Override
    public List<ErpSSSContribution> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<ErpSSSContribution> erpSSSContributions = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<ErpSSSContribution>> comparators = sortBy.values().stream()
                    .map(o -> new ErpSSSContributionLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ErpSSSContribution> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            erpSSSContributions.sort(cp);
        }

        setRowCount((int) rowCount);

        return erpSSSContributions;
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
