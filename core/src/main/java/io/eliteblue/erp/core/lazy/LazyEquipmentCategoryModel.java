package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.EquipmentCategory;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

public class LazyEquipmentCategoryModel extends LazyDataModel<EquipmentCategory> {

    private List<EquipmentCategory> dataSource;

    public LazyEquipmentCategoryModel(List<EquipmentCategory> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EquipmentCategory getRowData(String rowKey) {
        for(EquipmentCategory equipmentCategory: dataSource) {
            if (equipmentCategory.getId() == Integer.parseInt(rowKey)) {
                return equipmentCategory;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(EquipmentCategory equipmentCategory) {
        return String.valueOf(equipmentCategory.getId());
    }

    @Override
    public List<EquipmentCategory> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, Comparator.comparing(EquipmentCategory::getName));

        // apply offset & filters
        List<EquipmentCategory> equipmentCategories = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return equipmentCategories;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            EquipmentCategory equipmentCategory = (EquipmentCategory) o;
            return equipmentCategory.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
