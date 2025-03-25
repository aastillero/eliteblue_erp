package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ContractedManHours;
import org.primefaces.model.SortOrder;

import java.util.Comparator;

public class ContractedManHoursLazySorter implements Comparator<ContractedManHours> {

    private String sortField;
    private SortOrder sortOrder;

    public ContractedManHoursLazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(ContractedManHours o1, ContractedManHours o2) {
        try {
            Object value1 = o1.getClass().getField(sortField).get(o1);
            Object value2 = o2.getClass().getField(sortField).get(o2);

            int value = ((Comparable)value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
