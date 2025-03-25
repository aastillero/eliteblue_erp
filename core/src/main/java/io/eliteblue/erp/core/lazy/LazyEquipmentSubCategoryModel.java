package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;

import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

public class LazyEquipmentSubCategoryModel extends LazyDataModel<EquipmentSubCategory> {

    private List<EquipmentSubCategory> dataSource;

    public LazyEquipmentSubCategoryModel(List<EquipmentSubCategory> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EquipmentSubCategory getRowData(String rowKey) {
        for(EquipmentSubCategory equipmentSubCategory: dataSource) {
            if (equipmentSubCategory.getId() == Integer.parseInt(rowKey)) {
                return equipmentSubCategory;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(EquipmentSubCategory equipmentSubCategory) {
        return String.valueOf(equipmentSubCategory.getId());
    }

    @Override
    public List<EquipmentSubCategory> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, Comparator.comparing(EquipmentSubCategory::getName));

        // apply offset & filters
        List<EquipmentSubCategory> equipmentSubCategories = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return equipmentSubCategories;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            EquipmentSubCategory equipmentSubCategory = (EquipmentSubCategory) o;
            return equipmentSubCategory.getName().toLowerCase().contains(filterText);
        }

        return matching;
    }
}
