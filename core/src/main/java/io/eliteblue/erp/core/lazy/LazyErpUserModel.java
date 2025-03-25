package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.security.ErpUser;
import org.apache.commons.collections4.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LazyErpUserModel extends LazyDataModel<ErpUser> {

    private List<ErpUser> dataSource;

    public LazyErpUserModel(List<ErpUser> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ErpUser getRowData(String rowKey) {
        for (ErpUser erpUser : dataSource) {
            if (erpUser.getId() == Integer.parseInt(rowKey)) {
                return erpUser;
            }
        }

        return null;
    }

    @Override
    public String getRowKey(ErpUser erpUser) {
        return String.valueOf(erpUser.getId());
    }

    @Override
    public List<ErpUser> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // apply offset & filters
        /*List<ErpUser> erpUsers = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());*/
        Collections.sort(dataSource, (o1, o2) -> (o1.getUsername().compareTo(o2.getUsername())));

        // sort
        /*if (!sortBy.isEmpty()) {
            List<Comparator<ErpUser>> comparators = sortBy.values().stream()
                    .map(o -> new ErpUserLazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<ErpUser> cp = ComparatorUtils.chainedComparator(comparators); // from apache
            erpUsers.sort(cp);
        }*/
        List<ErpUser> erpUsers = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return erpUsers;
    }

    @Override
    public void forEach(Consumer<? super ErpUser> action) {

    }

    @Override
    public Spliterator<ErpUser> spliterator() {
        return null;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpUser erpUser = (ErpUser) o;
            return erpUser.getUsername().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
