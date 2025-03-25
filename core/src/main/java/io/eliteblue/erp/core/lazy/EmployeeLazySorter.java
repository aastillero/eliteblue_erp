package io.eliteblue.erp.core.lazy;

import io.eliteblue.erp.core.model.ErpEmployee;
import org.primefaces.model.SortOrder;

import java.lang.reflect.Field;
import java.util.Comparator;

public class EmployeeLazySorter implements Comparator<ErpEmployee> {

    private String sortField;
    private SortOrder sortOrder;

    public EmployeeLazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(ErpEmployee o1, ErpEmployee o2) {
        //System.out.println("sortField: "+sortField);
        try {
            if(sortField.equals("statusFilter")) {
                this.sortField = "status";
            }
            Field f1 = o1.getClass().getDeclaredField(sortField);
            f1.setAccessible(true);
            Field f2 = o1.getClass().getDeclaredField(sortField);
            f2.setAccessible(true);
            Object value1 = f1.get(o1);
            Object value2 = f2.get(o2);

            int value = ((Comparable)value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
