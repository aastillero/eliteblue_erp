package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpPost;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

public class LazyErpPostModel extends LazyDataModel<ErpPost> {

    private List<ErpPost> dataSource;

    public LazyErpPostModel(List<ErpPost> dataSource) { this.dataSource = dataSource; }

    @Override
    public ErpPost getRowData(String rowKey) {
        for (ErpPost erpPost : dataSource) {
            if (erpPost.getId() == Integer.parseInt(rowKey)) {
                return erpPost;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpPost erpPost) { return String.valueOf(erpPost.getId()); }

    @Override
    public List<ErpPost> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<ErpPost> erpPosts = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        // sort
        /*if (!sortBy.isEmpty()) {
            List<Comparator<ErpPost>> comparators = sortBy.values().stream()
                    .map(o -> new ErpPostLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ErpPost> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            erpPosts.sort(cp);
        }*/

        setRowCount((int) rowCount);

        return erpPosts;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpPost erpPost = (ErpPost) o;
            return erpPost.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
