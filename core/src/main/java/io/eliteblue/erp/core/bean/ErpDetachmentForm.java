package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyEmployeeModel;
import io.eliteblue.erp.core.lazy.LazyErpPostModel;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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
public class ErpDetachmentForm implements Serializable {

    final DateFormat format = new SimpleDateFormat("HH:mm");
    private final DateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpClientService erpClientService;

    @Autowired
    private OperationsAreaService operationsAreaService;

    @Autowired
    private ErpEmployeeService erpEmployeeService;

    @Autowired
    private ErpEquipmentService erpEquipmentService;

    @Autowired
    private ErpSilService silService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private Long id;
    private Long clientId;
    private ErpDetachment erpDetachment;
    private ErpClient client;
    private List<SelectItem> locations;
    private String locationValue;

    private LazyDataModel<ErpPost> lazyErpPosts;
    private List<ErpPost> filteredErpPosts;
    private List<ErpPost> posts;
    private ErpPost selectedPost;

    private List<ErpTimeSchedule> filteredErpTimeSchedules;
    private List<ErpTimeSchedule> erpTimeSchedules;
    private ErpTimeSchedule selectedErpTimeSchedule;

    private List<ErpEmployee> employees;
    private List<ErpEmployee> filteredEmployees;
    private LazyDataModel<ErpEmployee> lazyErpEmployees;
    private ErpEmployee selectedEmployee;
    private Map<String, Boolean> exessOTSelect;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpDetachment = erpDetachmentService.findById(Long.valueOf(id));
            if(erpDetachment != null) {
                locationValue = erpDetachment.getLocation().getLocation();
                client = erpDetachment.getErpClient();
                clientId = client.getId();
                posts = new ArrayList<ErpPost>(erpDetachment.getPosts());
                lazyErpPosts = new LazyErpPostModel(posts);
                employees = new ArrayList<>(erpDetachment.getAssignedEmployees());
                lazyErpEmployees = new LazyEmployeeModel(employees);
                erpTimeSchedules = new ArrayList<ErpTimeSchedule>(erpDetachment.getErpTimeSchedules());
            }
        } else {
            erpDetachment = new ErpDetachment();
            if(has(clientId)){
                client = erpClientService.findById(clientId);
                erpDetachment.setErpClient(client);
            }
        }
        List<OperationsArea> areas = operationsAreaService.getAll();
        locations = new ArrayList<SelectItem>();
        for(OperationsArea o: areas) {
            SelectItem itm = new SelectItem(o.getLocation(), o.getLocation());
            locations.add(itm);
        }
        exessOTSelect = new HashMap<>();
        exessOTSelect.put("NO", false);
        exessOTSelect.put("YES", true);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment detachment) {
        this.erpDetachment = detachment;
    }

    public Long getClientId() {
        return clientId;
    }

    public ErpClient getClient() {
        return client;
    }

    public void setClient(ErpClient client) {
        this.client = client;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<SelectItem> getLocations() {
        return locations;
    }

    public void setLocations(List<SelectItem> locations) {
        this.locations = locations;
    }

    public List<ErpPost> getPosts() {
        return posts;
    }

    public void setPosts(List<ErpPost> posts) {
        this.posts = posts;
    }

    public ErpPost getSelectedPost() {
        return selectedPost;
    }

    public void setSelectedPost(ErpPost selectedPost) {
        this.selectedPost = selectedPost;
    }

    public String getLocationValue() {
        return locationValue;
    }

    public void setLocationValue(String locationValue) {
        this.locationValue = locationValue;
    }

    public LazyDataModel<ErpPost> getLazyErpPosts() {
        return lazyErpPosts;
    }

    public void setLazyErpPosts(LazyDataModel<ErpPost> lazyErpPosts) {
        this.lazyErpPosts = lazyErpPosts;
    }

    public List<ErpPost> getFilteredErpPosts() {
        return filteredErpPosts;
    }

    public void setFilteredErpPosts(List<ErpPost> filteredErpPosts) {
        this.filteredErpPosts = filteredErpPosts;
    }

    public List<ErpTimeSchedule> getFilteredErpTimeSchedules() {
        return filteredErpTimeSchedules;
    }

    public void setFilteredErpTimeSchedules(List<ErpTimeSchedule> filteredErpTimeSchedules) {
        this.filteredErpTimeSchedules = filteredErpTimeSchedules;
    }

    public List<ErpTimeSchedule> getErpTimeSchedules() {
        return erpTimeSchedules;
    }

    public void setErpTimeSchedules(List<ErpTimeSchedule> erpTimeSchedules) {
        this.erpTimeSchedules = erpTimeSchedules;
    }

    public ErpTimeSchedule getSelectedErpTimeSchedule() {
        return selectedErpTimeSchedule;
    }

    public void setSelectedErpTimeSchedule(ErpTimeSchedule selectedErpTimeSchedule) {
        this.selectedErpTimeSchedule = selectedErpTimeSchedule;
    }

    public List<ErpEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<ErpEmployee> employees) {
        this.employees = employees;
    }

    public List<ErpEmployee> getFilteredEmployees() {
        return filteredEmployees;
    }

    public ErpEmployee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(ErpEmployee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }

    public void setFilteredEmployees(List<ErpEmployee> filteredEmployees) {
        this.filteredEmployees = filteredEmployees;
    }

    public LazyDataModel<ErpEmployee> getLazyErpEmployees() {
        return lazyErpEmployees;
    }

    public void setLazyErpEmployees(LazyDataModel<ErpEmployee> lazyErpEmployees) {
        this.lazyErpEmployees = lazyErpEmployees;
    }

    public Map<String, Boolean> getExessOTSelect() {
        return exessOTSelect;
    }

    public void setExessOTSelect(Map<String, Boolean> exessOTSelect) {
        this.exessOTSelect = exessOTSelect;
    }

    public DateFormat getFormat() {
        return format;
    }

    public String newPostPressed() {
        return "post-form?detachmentId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String newTimePressed() {
        return "time-schedule-form?detachmentId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String newEmployeePressed() {
        return "employee-assign?detachmentId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String backBtnPressed() { return "client-form?id="+clientId+"faces-redirect=true&includeViewParams=true"; }

    public void clear() {
        erpDetachment = new ErpDetachment();
        id = null;
    }

    public boolean isNew() {
        return erpDetachment == null || erpDetachment.getId() == null;
    }

    public void onRowSelect(SelectEvent<ErpPost> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("post-form.xhtml?id="+selectedPost.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpPost> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("post-form.xhtml?id="+selectedPost.getId());
    }

    public void onRowSelectTime(SelectEvent<ErpTimeSchedule> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("time-schedule-form.xhtml?id="+ selectedErpTimeSchedule.getId());
    }

    public void onRowUnselectTime(UnselectEvent<ErpTimeSchedule> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("time-schedule-form.xhtml?id="+ selectedErpTimeSchedule.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpPost erpPost = (ErpPost) value;
        return erpPost.getName().toLowerCase().contains(filterText);
    }

    public boolean globalEmployeeFilterFunction(Object value, Object filter, Locale locale) {
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
                || erpEmployee.getEmail().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public void removeAssignedEmployee() throws Exception {
        //this.products.remove(this.selectedProduct);
        erpDetachment.getAssignedEmployees().remove(this.selectedEmployee);
        employees.remove(this.selectedEmployee);
        //System.out.println("ASSIGNED EMPLOYEE COUNT: "+erpDetachment.getAssignedEmployees().size());
        selectedEmployee.setErpDetachment(null);
        erpEmployeeService.save(selectedEmployee);
        erpDetachmentService.save(erpDetachment);
        selectedEmployee = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Employee1 Removed"));
        FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+erpDetachment.getId()+"&clientId="+erpDetachment.getErpClient().getId());
    }

    public void remove() throws Exception {
        if(has(erpDetachment) && has(erpDetachment.getId())) {
            String name = erpDetachment.getName();
            List<ErpDetachment> dets = new ArrayList<>();
            dets.add(erpDetachment);
            List<ErpEquipment> erpEquipments = erpEquipmentService.getAllDetachmentIn(dets);
            if(erpEquipments == null || erpEquipments.size() == 0) {
                erpDetachmentService.delete(erpDetachment);
                addDetailMessage("DETACHMENT DELETED", name, FacesMessage.SEVERITY_INFO);
                FacesContext.getCurrentInstance().getExternalContext().redirect("client-form.xhtml?id=" + clientId);
            } else {
                addDetailMessage("DELETE DETACHMENT FAILED", " Equipments are added in this detachment. Delete them first.", FacesMessage.SEVERITY_ERROR);
            }
        }
    }

    public void save() throws Exception {
        if(erpDetachment != null) {
            if(locationValue != null) {
                erpDetachment.setLocation(operationsAreaService.findByLocation(locationValue));
            }
            erpDetachmentService.save(erpDetachment);
            addDetailMessage("DETACHMENT SAVED", erpDetachment.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("client-form.xhtml?id="+clientId);
        }
    }

    public void downloadSILReport() throws Exception {
        LocalDate today = LocalDate.now();
        final Double maxSIL = 5.0;
        if (has(erpDetachment)) {
            ExcelUtils.initializeWithFilename("sil_template.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            String detachmentName = erpDetachment.getName().replaceAll(" ", "_");

            ExcelUtils.setCell(3,2, capitalizeString(erpDetachment.getName().toLowerCase()));
            if(has(erpDetachment.getAssignedEmployees())) {
                ExcelUtils.setCell(3, 4, erpDetachment.getAssignedEmployees().size());
                int noCnt = 1;
                int x = 8;
                List<ErpEmployee> employees = new ArrayList<>(erpDetachment.getAssignedEmployees());
                Collections.sort(employees, (o1, o2) -> (o1.getLastname().compareTo(o2.getLastname())));
                for(ErpEmployee e: employees) {
                    // get SIL by employee
                    List<ErpSil> sils = silService.findAllByEmployee(e);
                    for(ErpSil s: sils) {
                        ExcelUtils.setCell(x, 0, noCnt);
                        ExcelUtils.setCell(x, 1, e.getStatus().name());
                        ExcelUtils.setCell(x, 2, detachmentName.replaceAll("_", " "));
                        String empFullName = e.getLastname() + ", " + e.getFirstname();
                        ExcelUtils.setCell(x, 3, empFullName);
                        if (has(e.getJoinedDate())) {
                            ExcelUtils.setCell(x, 4, format2.format(e.getJoinedDate()));
                        }
                        if (has(e.getResignedDate())) {
                            ExcelUtils.setCell(x, 5, format2.format(e.getResignedDate()));
                        }
                        ExcelUtils.setCell(x, 6, s.getNoDaysAvailed());
                        ExcelUtils.setCell(x, 7, s.getAmountAvailed());
                        SimpleDateFormat sd1 = new SimpleDateFormat("MMM dd");
                        SimpleDateFormat sd2 = new SimpleDateFormat("dd, yyyy");
                        SimpleDateFormat sd3 = new SimpleDateFormat("MMM dd yyyy");
                        //String availedDate = sd1.format(s.getDateAvailedStart()) + sd2.format(s.getDateAvailedStop());
                        String availedDate = Optional.ofNullable(s.getDateAvailedStart())
                                .map(sd1::format)
                                .orElse("") + " - " + Optional.ofNullable(s.getDateAvailedStop())
                                .map(sd2::format)
                                .orElse("");
                        ExcelUtils.setCell(x, 8, availedDate);
                        //String paymentDate = sd1.format(s.getDateOfPaymentStart()) + sd2.format(s.getDateOfPaymentStop());
                        String paymentDate = Optional.ofNullable(s.getDateOfPaymentStart())
                                .map(sd1::format)
                                .orElse("") + " - " + Optional.ofNullable(s.getDateOfPaymentStop())
                                .map(sd2::format)
                                .orElse("");
                        ExcelUtils.setCell(x, 9, paymentDate);
                        ExcelUtils.setCell(x, 10, s.getModeOfPayment().name());
                        ExcelUtils.setCell(x, 11, s.getNoDaysUnAvailed());
                        ExcelUtils.setCell(x, 12, s.getAmountUnAvailed());
                        //String coveredDate = sd3.format(s.getDateCoveredStart()) + " - " + sd3.format(s.getDateCoveredStop());
                        String coveredDate = Optional.ofNullable(s.getDateCoveredStart())
                                .map(sd3::format)
                                .orElse("") + " - " + Optional.ofNullable(s.getDateCoveredStop())
                                .map(sd3::format)
                                .orElse("");
                        ExcelUtils.setCell(x, 13, coveredDate);
                        noCnt++;
                        x++;
                    }
                }
                ExcelUtils.evaluateAllCells();
            }

            generateFile(ExcelUtils.workbook, "SIL_REPORT_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            System.out.println("Generation SUCCESS!!");
            ExcelUtils.workbook.close();
        }
    }

    public void downloadFile() throws Exception {
        LocalDate today = LocalDate.now();
        String detachmentName = erpDetachment.getName().replaceAll(" ", "_");
        if (has(erpDetachment)) {
            ExcelUtils.initializeWithFilename("assigned_emp.xlsx", "Sheet1");
            System.out.println("Excel Initialization DONE...");
            boolean hasAssignedEmployees = (has(erpDetachment) && erpDetachment.getAssignedEmployees() != null && erpDetachment.getAssignedEmployees().size() > 0);
            ExcelUtils.setCell(1, 1, format2.format(new Date()));
            ExcelUtils.setCell(2,1, capitalizeString(erpDetachment.getName().toUpperCase()));
            String currentUserName = CurrentUserUtil.getFullName();
            ExcelUtils.setCell(3, 1, currentUserName);
            if(hasAssignedEmployees) {
                int x = 7;
                List<ErpEmployee> emps = new ArrayList<>();
                emps.addAll(erpDetachment.getAssignedEmployees());
                emps.sort(Comparator.comparing((ErpEmployee ewa) -> ewa.getLastname()));
                for(ErpEmployee emp : emps) {
                    ExcelUtils.setCell(x, 0, emp.getEmployeeId());
                    ExcelUtils.setCell(x, 1, emp.getLastname().toUpperCase() + ", " + emp.getFirstname().toUpperCase());
                    ExcelUtils.setCell(x, 2, emp.getStatus().name());
                    ExcelUtils.setCell(x, 3, emp.getPosition());
                    x++;
                }
                if(detachmentName.length() > 8) {
                    detachmentName = detachmentName.substring(0, 7);
                }
                generateFile(ExcelUtils.workbook, "EMP_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
                System.out.println("Generation SUCCESS!!");
                ExcelUtils.workbook.close();
            } else {
                addDetailMessage("DETACHMENT HAS NO EMPLOYEES", detachmentName, FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+id);
            }
        } else {
            addDetailMessage("NO DETACHMENT", detachmentName, FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("detachment-form.xhtml?id="+id);
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

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
