package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.DetachmentPayroll;
import io.eliteblue.erp.core.model.ErpClient;
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

public class LazyDetachmentPayrollModel extends LazyDataModel<DetachmentPayroll> {

    private List<DetachmentPayroll> dataSource;

    public LazyDetachmentPayrollModel(List<DetachmentPayroll> dataSource){ this.dataSource = dataSource; }

    @Override
    public DetachmentPayroll getRowData(String rowKey) {
        for (DetachmentPayroll detachmentPayroll : dataSource) {
            if (detachmentPayroll.getId() == Integer.parseInt(rowKey)) {
                return detachmentPayroll;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(DetachmentPayroll detachmentPayroll) {
        return String.valueOf(detachmentPayroll.getId());
    }

    @Override
    public List<DetachmentPayroll> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        List<DetachmentPayroll> detachmentPayrolls = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        if (!sortBy.isEmpty()) {
            List<Comparator<DetachmentPayroll>> comparators = sortBy.values().stream()
                    .map(o -> new DetachmentPayrollLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<DetachmentPayroll> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            detachmentPayrolls.sort(cp);
        }

        setRowCount((int) rowCount);

        return detachmentPayrolls;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            DetachmentPayroll detachmentPayroll = (DetachmentPayroll) o;
            return detachmentPayroll.getDetachment().getName().toLowerCase().contains(filterText)
                    || detachmentPayroll.getDetachment().getLocation().getLocation().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
