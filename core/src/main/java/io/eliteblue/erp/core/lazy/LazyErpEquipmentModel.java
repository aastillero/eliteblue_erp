package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpEquipment;
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

public class LazyErpEquipmentModel extends LazyDataModel<ErpEquipment> {

    private List<ErpEquipment> dataSource;

    public LazyErpEquipmentModel(List<ErpEquipment> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ErpEquipment getRowData(String rowKey) {
        for(ErpEquipment erpEquipment: dataSource) {
            if (erpEquipment.getId() == Integer.parseInt(rowKey)) {
                return erpEquipment;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(ErpEquipment erpEquipment) {
        return String.valueOf(erpEquipment.getId());
    }

    @Override
    public List<ErpEquipment> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = dataSource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .count();

        // sort source
        Collections.sort(dataSource, (o1, o2) -> (o1.getName().compareTo(o2.getName())));

        // apply offset & filters
        List<ErpEquipment> erpEquipments = dataSource.stream()
                .skip(first)
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .limit(pageSize)
                .collect(Collectors.toList());

        setRowCount((int) rowCount);

        return erpEquipments;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Object o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();
            String filterText = (filterValue == null) ? null : filterValue.toString().trim().toLowerCase();

            ErpEquipment erpEquipment = (ErpEquipment) o;
            boolean detachmentContains = false;
            if(erpEquipment.getDetachment() != null) {
                detachmentContains = erpEquipment.getDetachment().getName().toLowerCase().contains(filterText);
            }

            return erpEquipment.getName().toLowerCase().contains(filterText)
                    || detachmentContains
                    || erpEquipment.getRefNum().equals(filterText);
        }

        return matching;
    }
}
