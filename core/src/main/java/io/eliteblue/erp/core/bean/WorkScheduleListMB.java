package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.EmployeeStatus;
import io.eliteblue.erp.core.constants.WorkSchedLegend;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.ErpDTRAssignmentService;
import io.eliteblue.erp.core.service.ErpDTRService;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.WorkScheduleService;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class WorkScheduleListMB implements Serializable {

    private final String[] columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Autowired
    private ErpDTRService erpDTRService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpDTRAssignmentService erpDTRAssignmentService;

    @Autowired
    private WorkScheduleService workScheduleService;

    private List<ErpWorkSchedule> filteredErpWorkSchedules;

    private List<ErpWorkSchedule> workSchedules;

    private ErpWorkSchedule selectedWorkSchedule;

    private LocalDate localStart;

    private LocalDate localEnd;

    private String searchStart;

    private String searchEnd;

    private Map<String, Long> detachments;

    private ErpDetachment detachment;

    private Date coverStart;

    private Date coverEnd;

    private Map<String, ApprovalStatus> statusValues;

    private ApprovalStatus statusSelected;

    private String status;

    @PostConstruct
    public void init() throws Exception {
        detachments = new HashMap<>();
        if(has(searchStart) && has(searchEnd)) {
            //System.out.println("Search Start:"+searchStart+" Search End:"+searchEnd+" Status:"+status);
            statusSelected = ApprovalStatus.valueOf(status);
            Date start = format.parse(searchStart);
            Date end = format.parse(searchEnd);
            workSchedules = workScheduleService.getAllFilteredStartAndEndDate(start, end);
            workSchedules = workSchedules.stream().filter(itm->itm.getStatus().equals(statusSelected)).collect(Collectors.toList());
        } else {
            workSchedules = workScheduleService.getAllFilteredLocation();
        }

        LocalDate today = LocalDate.now();
        localStart = LocalDate.now();
        localEnd = LocalDate.now();
        if(today.getDayOfMonth() <= 14) {
            localStart = LocalDate.of(today.getYear(), today.getMonth().minus(1), 16);
            LocalDate monthstart = LocalDate.of(today.getYear(),today.getMonth().minus(1),1);
            localEnd = monthstart.plusDays(monthstart.lengthOfMonth()-1);
        } else {
            localStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
            localEnd = LocalDate.of(today.getYear(), today.getMonth(), 15);
        }
        coverStart = convertToDateViaInstant(localStart);
        coverEnd = convertToDateViaInstant(localEnd);

        if(!has(detachment)) {
            detachment = new ErpDetachment();
        }
        for(ErpDetachment edp: erpDetachmentService.getAll()) {
            detachments.put(edp.getName(), edp.getId());
        }
        statusValues = new HashMap<>();
        for(ApprovalStatus ap: ApprovalStatus.values()) {
            statusValues.put(ap.name(), ap);
        }
    }

    public List<ErpWorkSchedule> getFilteredErpWorkSchedules() {
        return filteredErpWorkSchedules;
    }

    public void setFilteredErpWorkSchedules(List<ErpWorkSchedule> filteredErpWorkSchedules) {
        this.filteredErpWorkSchedules = filteredErpWorkSchedules;
    }

    public List<ErpWorkSchedule> getWorkSchedules() {
        return workSchedules;
    }

    public void setWorkSchedules(List<ErpWorkSchedule> workSchedules) {
        this.workSchedules = workSchedules;
    }

    public ErpWorkSchedule getSelectedWorkSchedule() {
        return selectedWorkSchedule;
    }

    public void setSelectedWorkSchedule(ErpWorkSchedule selectedWorkSchedule) {
        this.selectedWorkSchedule = selectedWorkSchedule;
    }

    public LocalDate getLocalStart() {
        return localStart;
    }

    public void setLocalStart(LocalDate localStart) {
        this.localStart = localStart;
    }

    public LocalDate getLocalEnd() {
        return localEnd;
    }

    public void setLocalEnd(LocalDate localEnd) {
        this.localEnd = localEnd;
    }

    public String getSearchStart() {
        return searchStart;
    }

    public void setSearchStart(String searchStart) {
        this.searchStart = searchStart;
    }

    public String getSearchEnd() {
        return searchEnd;
    }

    public void setSearchEnd(String searchEnd) {
        this.searchEnd = searchEnd;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public Date getCoverStart() {
        return coverStart;
    }

    public void setCoverStart(Date coverStart) {
        this.coverStart = coverStart;
    }

    public Date getCoverEnd() {
        return coverEnd;
    }

    public void setCoverEnd(Date coverEnd) {
        this.coverEnd = coverEnd;
    }

    public Map<String, ApprovalStatus> getStatusValues() {
        return statusValues;
    }

    public void setStatusValues(Map<String, ApprovalStatus> statusValues) {
        this.statusValues = statusValues;
    }

    public ApprovalStatus getStatusSelected() {
        return statusSelected;
    }

    public void setStatusSelected(ApprovalStatus statusSelected) {
        this.statusSelected = statusSelected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String downloadTemplate() {
        return "ws-download-templates?faces-redirect=true&includeViewParams=true";
    }

    public void onRowSelect(SelectEvent<ErpWorkSchedule> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("workschedule-form.xhtml?id="+selectedWorkSchedule.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpWorkSchedule> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("workschedule-form.xhtml?id="+selectedWorkSchedule.getId());
    }

    public String search() {
        //System.out.println("Search Start: "+format.format(coverStart)+" Search End:"+format.format(coverEnd));
        return "workschedules?status="+statusSelected.name()+"&coverStart="+format.format(coverStart)+"&coverEnd="+format.format(coverEnd)+"&faces-redirect=true&includeViewParams=true";
    }

    public void downloadFile() throws Exception {
        if(has(detachment) && has(detachment.getId())) {
            ErpDetachment det = erpDetachmentService.findById(detachment.getId());
            Date startDate = convertToDateViaInstant(localStart);
            Date endDate = convertToDateViaInstant(localEnd);
            ErpWorkSchedule workSchedule = workScheduleService.getDetachmentFilteredStartAndEndDate(det, startDate, endDate);
            if(has(workSchedule)) {
                boolean hasAssignedEmployees = (has(det) && det.getAssignedEmployees() != null && det.getAssignedEmployees().size() > 0);
                boolean hasSchedules = (has(det) && det.getErpTimeSchedules() != null && det.getErpTimeSchedules().size() > 0);
                LocalDate today = LocalDate.now();
                LocalDate startSched = localStart;
                LocalDate stopSched = localEnd;
                List<String> legends = new ArrayList<>();

                ExcelUtils.initializeWithFilename("work_schedule.xlsx", "Sheet1");
                // locked style cells
                CellStyle locked = ExcelUtils.workbook.createCellStyle();
                ExcelUtils.worksheet.protectSheet("3l1t3blue2010");
                locked.setLocked(false);
                ExcelUtils.setCell(0,1, det.getId());
                ExcelUtils.setCell(7,2, "DETACHMENT: "+det.getName());
                ExcelUtils.setCell(7,8, "AREA OF RESPONSIBILITY: "+det.getLocation().getLocation());
                ExcelUtils.setCell(2,3, "");
                ExcelUtils.getCellStyle(2,3).setLocked(false);
                ExcelUtils.setCell(2,8, "");
                ExcelUtils.getCellStyle(2,8).setLocked(false);
                ExcelUtils.setCell(2,13, "");
                ExcelUtils.getCellStyle(2,13).setLocked(false);
                ExcelUtils.setCell(1,1, startSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                ExcelUtils.getCellStyle(1,1).setLocked(false);
                ExcelUtils.setCell(2,1, stopSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                ExcelUtils.getCellStyle(2,1).setLocked(false);
                for(ErpTimeSchedule ts: det.getErpTimeSchedules()) {
                    if(ts.getLegend().equals(WorkSchedLegend.DS)) {
                        CellStyle quotedPrefix = ExcelUtils.getCellStyle(4,5);
                        quotedPrefix.setQuotePrefixed(true);
                        ExcelUtils.setCell(4, 5, "= Day Shift ("+ts.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))+"-"+ts.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))+")", quotedPrefix);
                        legends.add(WorkSchedLegend.DS.name());
                    } else if(ts.getLegend().equals(WorkSchedLegend.NS)) {
                        CellStyle quotedPrefix = ExcelUtils.getCellStyle(5,5);
                        quotedPrefix.setQuotePrefixed(true);
                        ExcelUtils.setCell(5, 5, "= Night Shift ("+ts.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))+"-"+ts.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))+")", quotedPrefix);
                        legends.add(WorkSchedLegend.NS.name());
                    } else if(ts.getLegend().equals(WorkSchedLegend.MID)) {
                        ExcelUtils.setCell(4,9,"MID");
                        CellStyle quotedPrefix = ExcelUtils.getCellStyle(4,10);
                        quotedPrefix.setQuotePrefixed(true);
                        ExcelUtils.setCell(4, 10, "= Mid Shift ("+ts.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))+"-"+ts.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))+")", quotedPrefix);
                        legends.add(WorkSchedLegend.MID.name());
                    }
                }
                if(has(legends) && legends.size() > 0) {
                    legends.add(WorkSchedLegend.DO.name());
                    legends.add(WorkSchedLegend.H24.name());
                    legends.add(WorkSchedLegend.L.name());
                }
                ExcelUtils.evaluateCell(7,0);
                ExcelUtils.evaluateCell(4,1);
                ExcelUtils.evaluateCell(8,0);
                // loop from start date to end date
                int y = 2;
                int totalDays = 0;
                for (LocalDate date = startSched; (date.isBefore(stopSched) || date.isEqual(stopSched)); date = date.plusDays(1), y++) {
                    ExcelUtils.setCell(10, y, date.getDayOfMonth());
                    totalDays += 1;
                }
                ExcelUtils.setCell(10, 18, "NO. DAYS");
                if(hasAssignedEmployees && hasSchedules){
                    int x = 11;
                    List<ErpWorkAssignment> assignments = new ArrayList<>(workSchedule.getWorkAssignments());
                    assignments.sort(Comparator.comparing((ErpWorkAssignment ewa) -> ewa.getEmployeeAssigned().getLastname()));
                    //List<ErpEmployee> employees = new ArrayList<>(detachment.getAssignedEmployees());
                    //employees = employees.stream().filter(itm -> itm.getStatus().equals(EmployeeStatus.HIRED)).collect(Collectors.toList());
                    //employees.sort(Comparator.comparing(ErpEmployee::getLastname));
                    //for(ErpEmployee emp: employees) {
                    for(ErpWorkAssignment assn: assignments) {
                        // check if employee is not hired do not include
                        ErpEmployee emp = assn.getEmployeeAssigned();
                        //System.out.println("EMPLOYEE: "+emp.getFirstname() + " " + emp.getLastname()+" "+emp.getStatus().name());
                        if(has(emp.getFirstname()) && has(emp.getLastname())) {
                            ExcelUtils.setCell(x, 0, emp.getLastname() + ", " + emp.getFirstname());
                        }
                        if(has(emp.getPosition())) {
                            CellStyle style = ExcelUtils.workbook.createCellStyle();
                            style.setAlignment(HorizontalAlignment.LEFT);
                            ExcelUtils.setCell(x, 1, emp.getPosition(), style);
                        }
                        y = 2;
                        for (LocalDate date = startSched; (date.isBefore(stopSched) || date.isEqual(stopSched)); date = date.plusDays(1), y++) {
                            CellStyle style = ExcelUtils.workbook.createCellStyle();
                            style.setAlignment(HorizontalAlignment.CENTER);
                            LocalDate _dateTime = date;
                            List<ErpWorkDay> workDays = assn.getWorkDays().stream().filter(itm->convertToLocalDateViaInstant(itm.getShiftStart()).equals(_dateTime)).collect(Collectors.toList());
                            if(has(workDays) && workDays.size() > 0) {
                                ErpWorkDay workDay = workDays.get(0);
                                ExcelUtils.setCell(x, y, workDay.getShiftSchedule().name(), style);
                            } else {
                                ExcelUtils.setCell(x, y, "DS", style);
                            }
                            ExcelUtils.getCellStyle(x,y).setLocked(false);
                            DataValidationHelper dvHelper = ExcelUtils.worksheet.getDataValidationHelper();
                            DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint((has(det.getErpTimeSchedules()) && det.getErpTimeSchedules().size() > 0) ? legends.toArray(new String[0]) : new String[]{"DS", "NS", "DO", "H24", "L"});
                            CellRangeAddressList addressList = new CellRangeAddressList(x, x, y, y);
                            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                            validation.setShowErrorBox(true);
                            ExcelUtils.worksheet.addValidationData(validation);
                        }
                        CellStyle style = ExcelUtils.workbook.createCellStyle();
                        style.setAlignment(HorizontalAlignment.LEFT);
                        ExcelUtils.setCell(x, 18, y-2, style);
                        //System.out.println("FORMULA: "+(totalDays)+"+COUNTIF(C"+(x+1)+":Q"+(x+1)+",\"*H24*\")-COUNTIF(C"+(x+1)+":Q"+(x+1)+",\"*DO*\")");
                        ExcelUtils.setCellFormula(x, 18, (totalDays)+"+COUNTIF(C"+(x+1)+":Q"+(x+1)+",\"*H24*\")-COUNTIF(C"+(x+1)+":Q"+(x+1)+",\"*DO*\")", style);
                        style = ExcelUtils.workbook.createCellStyle();
                        Font font = ExcelUtils.workbook.createFont();
                        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                        style.setFont(font);
                        ExcelUtils.setCell(x, 19, emp.getEmployeeId(), style);
                        x++;
                    }
                }

                String detachmentName = det.getName().replaceAll(" ", "_");
                if(detachmentName.length() > 8) {
                    detachmentName = detachmentName.substring(0, 7);
                }
                generateFile(ExcelUtils.workbook, "DTR_" + detachmentName + today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
                ExcelUtils.workbook.close();
            } else {
                addDetailMessage("DOWNLOAD FAILED", "COULD NOT FIND DTR", FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-list.xhtml");
            }
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-list.xhtml");
        }
    }

    public Integer generateDTRTable(ErpDetachment detachment, List<ErpDTRAssignment> erpDTRAssignments, LocalDate startSched, LocalDate stopSched, boolean hasAssignedEmployees, boolean hasSchedules, boolean isNS, int x) throws Exception {
        // loop from start date to end date
        int y = 2;
        int z = 0;
        for (LocalDate date = startSched; (date.isBefore(stopSched) || date.isEqual(stopSched)); date = date.plusDays(1)) {
            if(!isNS) {
                ExcelUtils.setCellFormula(x, y, "(B2)+" + z);
            } else {
                ExcelUtils.setCellFormula(x, y, "TEXT((B2)+"+z+", \"d-MMM\") & \" to \" & TEXT((B2)+"+(z+1)+", \"d-MMM\")");
            }
            //ExcelUtils.setCellFormula(7, y, "UPPER(TEXT((B2)+"+z+",\"aaa\"))");
            //ExcelUtils.setCell(7, y, "");
            ExcelUtils.evaluateCell(x, y);
            //ExcelUtils.evaluateCell(7, y);
            y += 3;
            z += 1;
        }
        //System.out.println("ZED VAL: "+z);
        while(z<=15) {
            //System.out.println("CLEAR CELL");
            ExcelUtils.setCell(x, y, "");
            ExcelUtils.setCell(x-1, y, "");
            ExcelUtils.setCell(x+1, y, "");
            ExcelUtils.setCell(x+1, y+1, "");
            ExcelUtils.setCell(x+1, y+2, "");
            y += 3;
            z += 1;
        }

        System.out.println("ASSIGNED EMPLOYEES: "+hasAssignedEmployees+" SCHED: "+hasSchedules);
        if(hasAssignedEmployees && hasSchedules){
            x += 2;
            for(ErpDTRAssignment assn: erpDTRAssignments) {
                ErpEmployee emp = assn.getEmployeeAssigned();
                //System.out.println("X["+x+"] EMPLOYEE: "+emp.getFirstname() + " " + emp.getLastname()+" "+emp.getStatus().name());
                if(has(emp.getFirstname()) && has(emp.getLastname())) {
                    ExcelUtils.setCell(x, 0, emp.getLastname() + ", " + emp.getFirstname());
                }
                y = 2;
                z = 0;
                //assn.getSchedules().stream().map(itm->itm.getDayShiftStart()+" "+itm.getNightShiftStart()).forEach(System.out::println);
                for (LocalDate date = startSched; (date.isBefore(stopSched) || date.isEqual(stopSched)); date = date.plusDays(1)) {
                    CellStyle style = ExcelUtils.workbook.createCellStyle();
                    style.setAlignment(HorizontalAlignment.CENTER);
                    LocalDate _dateTime = date;
                    //System.out.println("EMP["+emp.getFirstname()+" "+emp.getLastname()+"] START SCHED: "+date);
                    List<ErpDTRSchedule> schedules = (isNS) ? assn.getSchedules().stream().filter(itm->convertToLocalDateViaInstant(itm.getNightShiftStart()).equals(_dateTime)) .collect(Collectors.toList())
                            : assn.getSchedules().stream().filter(itm->convertToLocalDateViaInstant(itm.getDayShiftStart()).equals(_dateTime)).collect(Collectors.toList());Collectors.toList();
                    if(has(schedules) && schedules.size() > 0) {
                        ErpDTRSchedule sched = schedules.get(0);
                        DateFormat _format = new SimpleDateFormat("HH:mm:ss");
                        Date _currStart = (isNS) ? sched.getNightShiftStart() : sched.getDayShiftStart();
                        Date _currEnd = (isNS) ? sched.getNightShiftEnd() : sched.getDayShiftEnd();
                        ExcelUtils.setCellTime(x, y, _format.format(_currStart));
                        ExcelUtils.setCellTime(x, y+1, _format.format(_currEnd));
                    } else {
                        ExcelUtils.setCellTime(x, y, "00:00:00");
                        ExcelUtils.setCellTime(x, y+1, "00:00:00");
                    }
                    ExcelUtils.getCellStyle(x, y).setLocked(false);
                    ExcelUtils.getCellStyle(x,y+1).setLocked(false);
                    CellStyle cellStyle = ExcelUtils.workbook.createCellStyle();
                    CreationHelper createHelper1 = ExcelUtils.workbook.getCreationHelper();
                    cellStyle.setDataFormat(createHelper1.createDataFormat().getFormat("HH:MM"));
                    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    ExcelUtils.setCellFormula(x, y+2, "IF("+columns[y]+(x+1)+">"+columns[y+1]+(x+1)+",1+"+columns[y+1]+(x+1)+","+columns[y+1]+(x+1)+")-"+columns[y]+(x+1), cellStyle);
                    y += 3;
                    z += 1;
                }
                CellStyle style = ExcelUtils.workbook.createCellStyle();
                CreationHelper createHelper1 = ExcelUtils.workbook.getCreationHelper();
                style.setDataFormat(createHelper1.createDataFormat().getFormat("0.00"));
                style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                ExcelUtils.setCellFormula(x, 50,"SUM(E"+(x+1)+"+H"+(x+1)+"+K"+(x+1)+"+N"+(x+1)+"+Q"+(x+1)+"+T"+(x+1)+"+W"+(x+1)+"+Z"+(x+1)+"+AC"+(x+1)+"+AF"+(x+1)+"+AI"+(x+1)+"+AL"+(x+1)+"+AO"+(x+1)+"+AR"+(x+1)+"+AU"+(x+1)+"+AX"+(x+1)+")*24");
                ExcelUtils.setCellFormula(x, 51, "IFERROR(AY"+(x+1)+"/C1, \"0\")");
                int employeesCnt = erpDTRAssignments.size();
                if(!isNS) {
                    ExcelUtils.setCellFormula(x, 52, "AZ" + (x + 1) + "+AZ" + (employeesCnt + 5 + (x + 1)));
                } else {
                    ExcelUtils.setCellFormula(x, 52, "AZ" + ((x + 1) - (employeesCnt + 5)) + "+AZ" + (x + 1));
                }
                style = ExcelUtils.workbook.createCellStyle();
                Font font = ExcelUtils.workbook.createFont();
                font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                style.setFont(font);
                ExcelUtils.setCell(x, 53, emp.getEmployeeId(), style);
                x++;
            }
            // totals
        }

        return x;
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

    public String convertStartDate(ErpDTR erpDTR, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(erpDTR.getCutoffStart());
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date
                .from(dateToConvert.atStartOfDay(ZoneId.systemDefault())
                        .toInstant());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpWorkSchedule erpWorkSchedule = (ErpWorkSchedule) value;
        return erpWorkSchedule.getDescription().toLowerCase().contains(filterText);
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