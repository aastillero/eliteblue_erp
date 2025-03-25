package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.EquipmentStatus;
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

public class LazyEquipmentStatusModel extends LazyDataModel<EquipmentStatus> {

    private List<EquipmentStatus> dataSource;

    public LazyEquipmentStatusModel(List<EquipmentStatus> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EquipmentStatus getRowData(String rowKey) {
        for(EquipmentStatus equipmentStatus: dataSource) {
            if (equipmentStatus.getId() == Integer.parseInt(rowKey)) {
                return equipmentStatus;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(EquipmentStatus equipmentStatus) {
        return String.valueOf(equipmentStatus.getId());
    }

    @Override
    public List<EquipmentStatus> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<EquipmentStatus> equipmentStatuses = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return equipmentStatuses;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            EquipmentStatus equipmentStatus = (EquipmentStatus) o;
            return equipmentStatus.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
