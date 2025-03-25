package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.EmployeeStatus;
import io.eliteblue.erp.core.constants.SalaryType;
import io.eliteblue.erp.core.lazy.LazyEmployeeModel;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpEmployeeListMB implements Serializable {

    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private final DateFormat format2 = new SimpleDateFormat("dd-MMM-yy");

    @Autowired
    private ErpEmployeeService employeeService;

    private LazyDataModel<ErpEmployee> lazyErpEmployees;

    private List<ErpEmployee> filteredErpEmployees;

    private List<ErpEmployee> employees;

    private ErpEmployee selectedEmployee;

    private List<SortMeta> sortBy;

    private Map<String, String> employeeStatus;

    @PostConstruct
    public void init() {
        //employees = employeeService.getAll();
        employees = employeeService.getAllFiltered();
        employeeStatus = new HashMap<>();
        for(EmployeeStatus e: EmployeeStatus.values()) {
            employeeStatus.put(e.name().toUpperCase(), e.name().toUpperCase());
        }
        lazyErpEmployees = new LazyEmployeeModel(employees);
        lazyErpEmployees.setRowCount(10);
    }

    public LazyDataModel<ErpEmployee> getLazyErpEmployees() {
        return lazyErpEmployees;
    }

    public void setLazyErpEmployees(LazyDataModel<ErpEmployee> lazyErpEmployees) {
        this.lazyErpEmployees = lazyErpEmployees;
    }

    public List<ErpEmployee> getFilteredErpEmployees() {
        return filteredErpEmployees;
    }

    public void setFilteredErpEmployees(List<ErpEmployee> filteredErpEmployees) {
        this.filteredErpEmployees = filteredErpEmployees;
    }

    public List<ErpEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<ErpEmployee> employees) {
        this.employees = employees;
    }

    public ErpEmployee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(ErpEmployee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }

    public List<SortMeta> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<SortMeta> sortBy) {
        this.sortBy = sortBy;
    }

    public Map<String, String> getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(Map<String, String> employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public void onRowSelect(SelectEvent<ErpEmployee> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("employee-form.xhtml?id="+selectedEmployee.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpEmployee> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("employee-form.xhtml?id="+selectedEmployee.getId());
    }

    public void downloadEmployeesExcel() throws Exception {
        if(has(employees) && employees.size() > 0) {
            ExcelUtils.initializeWithFilename("employees_list.xlsx", "Sheet1");
            ExcelUtils.setCell(1, 2, format.format(new Date()));
            String currentUserName = CurrentUserUtil.getFullName();
            ExcelUtils.setCell(2, 2, currentUserName);
            int x = 6;
            int no = 1;
            List<ErpEmployee> filtered = employees.stream().filter(itm -> itm.getStatus().equals(EmployeeStatus.HIRED)).collect(Collectors.toList());
            filtered.sort(Comparator.comparing((ErpEmployee employee) -> employee.getLastname().trim()));
            for(ErpEmployee emp: filtered) {
                CellStyle oldStyle = null;
                /*if(no > 1) {
                    //ExcelUtils.insertRow(x, 6);
                    oldStyle = ExcelUtils.getCellStyle(x, 0);
                }*/
                ExcelUtils.setCell(x, 0, no, oldStyle);
                ExcelUtils.setCell(x, 1, emp.getEmployeeId());
                if(emp.getJoinedDate() != null){
                    try {
                        ExcelUtils.setCell(x, 2, format.format(emp.getJoinedDate()));
                    } catch (Exception e) {
                        System.out.println("error parsing joined date for: "+emp.getLastname());
                    }
                }
                ExcelUtils.setCell(x, 3, emp.getLastname());
                ExcelUtils.setCell(x, 4, emp.getFirstname());
                String midName = (emp.getMiddlename() != null && !emp.getMiddlename().isEmpty()) ? emp.getMiddlename().toUpperCase().charAt(0)+"." : "";
                ExcelUtils.setCell(x, 5, midName);
                ExcelUtils.setCell(x, 6, emp.getGender().name());
                if(emp.getCivilStatus() != null)
                    ExcelUtils.setCell(x, 7, emp.getCivilStatus().name());
                if(emp.getBirthDate() != null)
                    ExcelUtils.setCell(x, 8, format2.format(emp.getBirthDate()));
                ExcelUtils.setCell(x, 9, emp.getPosition());
                Double salaryRate = emp.getSalaryRate();
                if(salaryRate != null && salaryRate < 5000) {
                    ExcelUtils.setCell(x, 10, emp.getSalaryRate());
                    int noDays = 28; // no of working days
                    ExcelUtils.setCell(x, 10, emp.getSalaryRate() * noDays);
                } else {
                    ExcelUtils.setCell(x, 10, salaryRate);
                }
                no +=1;
                x += 1;
            }
            LocalDate today = LocalDate.now();
            generateFile(ExcelUtils.workbook, "EMPLOYEES_"+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void generateFile(Workbook workbook, String fileName) throws IOException {
        final FacesContext fc = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = fc.getExternalContext();

        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        final HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        final ServletOutputStream out = response.getOutputStream();

        workbook.write(out);
        workbook.close();
        out.flush();
        fc.responseComplete();
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        System.out.println("GLOBAL FILTER FUNCTION [EMPLOYEES] . . . . . .");
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpEmployee erpEmployee = (ErpEmployee) value;
        return erpEmployee.getFirstname().toLowerCase().contains(filterText)
            || erpEmployee.getLastname().toLowerCase().contains(filterText)
            || erpEmployee.getGender().name().toLowerCase().contains(filterText)
            || erpEmployee.getEmployeeId().toLowerCase().contains(filterText)
            || erpEmployee.getStatus().name().toLowerCase().contains(filterText)
            || (erpEmployee.getFirstname().toLowerCase()+" "+erpEmployee.getLastname().toLowerCase()).contains(filterText);
    }

    public String countStatus(String statusSelected) {
        String retVal = "";
        List<ErpEmployee> filteredEmp = employees.stream().filter(itm->itm.getStatus().name().equals(statusSelected)).collect(Collectors.toList());
        retVal = ""+filteredEmp.size();
        return retVal;
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }
}
