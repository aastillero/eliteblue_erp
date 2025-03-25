package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpSil;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;

import javax.faces.context.FacesContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LazyErpSilModel extends LazyDataModel<ErpSil> {

    private List<ErpSil> dataSource;

    public LazyErpSilModel(List<ErpSil> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ErpSil getRowData(String rowKey) {
        for (ErpSil erpSil : dataSource) {
            if (erpSil.getId() == Integer.parseInt(rowKey)) {
                return erpSil;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpSil erpSil) { return String.valueOf(erpSil.getId()); }

    @Override
    public List<ErpSil> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getEmployee().getLastname().compareTo(o2.getEmployee().getLastname())));

        // apply offset & filters
        List<ErpSil> erpSils = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return erpSils;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpSil erpSil = (ErpSil) o;
            return erpSil.getEmployee().getFirstname().toLowerCase().contains(filterText)
                    || erpSil.getEmployee().getLastname().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
