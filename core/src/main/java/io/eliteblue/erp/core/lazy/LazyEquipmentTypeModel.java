package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.EquipmentType;
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

public class LazyEquipmentTypeModel extends LazyDataModel<EquipmentType> {

    private List<EquipmentType> dataSource;

    public LazyEquipmentTypeModel(List<EquipmentType> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EquipmentType getRowData(String rowKey) {
        for(EquipmentType equipmentType: dataSource) {
            if (equipmentType.getId() == Integer.parseInt(rowKey)) {
                return equipmentType;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(EquipmentType equipmentType) {
        return String.valueOf(equipmentType.getId());
    }

    @Override
    public List<EquipmentType> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<EquipmentType> equipmentTypes = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return equipmentTypes;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            EquipmentType equipmentType = (EquipmentType) o;
            return equipmentType.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
