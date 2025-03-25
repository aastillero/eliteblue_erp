package io.eliteblue.erp.core.converters;

import io.eliteblue.erp.core.model.ErpEmployee;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.util.*;

@FacesConverter(value = "employeeAssignConverter", managed = true)
public class EmployeeAssignConverter implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        Object retVal = null;
        if(s != null) {
            retVal = fromSelect(uiComponent, s);
        }
        return retVal;
    }

    private Object fromSelect(final UIComponent currentcomponent, final String objectString) {

        if (currentcomponent.getClass() == UISelectItem.class) {
            final UISelectItem item = (UISelectItem) currentcomponent;
            final Object value = item.getValue();
            if(value instanceof ErpEmployee) {
                ErpEmployee emp = (ErpEmployee) value;
                if (objectString.equals(emp.getFirstname()+ " " +emp.getLastname())) {
                    return value;
                }
            } else {
                return null;
            }
        }

        if (currentcomponent.getClass() == UISelectItems.class) {
            final UISelectItems items = (UISelectItems) currentcomponent;
            final List<Object> elements;
            if(items.getValue() instanceof HashMap) {
                HashMap itemValues = (HashMap) items.getValue();
                elements = new ArrayList<>(itemValues.values());
            }
            else {
                elements = (List<Object>) items.getValue();
            }
            for (final Object element : elements) {
                if(element instanceof ErpEmployee) {
                    ErpEmployee emp = (ErpEmployee) element;
                    String empName = emp.getFirstname()+ " " +emp.getLastname();
                    if (objectString.equals(empName)) {
                        return element;
                    }
                }
                else {
                    System.out.println("NOT EMPLOYEE INSTANCE: "+element.getClass().getName());
                }
            }
        }

        if (!currentcomponent.getChildren().isEmpty()) {
            for (final UIComponent component : currentcomponent.getChildren()) {
                final Object result = fromSelect(component, objectString);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object employee) {
        if(employee == null) {
            return null;
        }
        if(employee instanceof ErpEmployee) {
            ErpEmployee emp = (ErpEmployee) employee;
            return emp.getFirstname()+" "+emp.getLastname();
        }
        return null;
    }
}
