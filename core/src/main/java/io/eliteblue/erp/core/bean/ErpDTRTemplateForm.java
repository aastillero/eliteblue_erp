package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.AttendanceType;
import io.eliteblue.erp.core.constants.WorkSchedLegend;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.primefaces.model.file.UploadedFile;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpDTRTemplateForm implements Serializable {

    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private final String DAYOFF_COLOR = "FFFFC000";
    private final String ABSENT_COLOR = "FFFF0000";
    private final String[] columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpDTRService erpDTRService;

    @Autowired
    private ErpDTRAssignmentService erpDTRAssignmentService;

    @Autowired
    private ErpDTRScheduleService erpDTRScheduleService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private List<ErpDetachment> detachments;

    private UploadedFile file;

    @PostConstruct
    public void init() {
        detachments = erpDetachmentService.getAllFilteredLocation();
    }

    public List<ErpDetachment> getDetachments() {
        return detachments;
    }

    public void setDetachments(List<ErpDetachment> detachments) {
        this.detachments = detachments;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String backBtnPressed() { return "dtr-list?faces-redirect=true&includeViewParams=true"; }

    public void fileUpload() throws Exception {
        System.out.println("FILE: "+file);
        if (has(file) && file.getSize() > 0) {
            ExcelUtils.initializeWithInputStream(file.getInputStream(), "Sheet1");
            String detachmentStringId = (String) ExcelUtils.getCellData(0,1);
            if(!has(detachmentStringId) || detachmentStringId.isEmpty()) {
                addDetailMessage("FAILED UPLOAD", file.getFileName() + " has no Detachment ID.", FacesMessage.SEVERITY_ERROR);
            } else {
                Long detachmentId = Long.parseLong(detachmentStringId);
                System.out.println("Detachment ID: " + detachmentId);
                ErpDetachment detachment = erpDetachmentService.findById(detachmentId);
                ErpDTR erpDTR = new ErpDTR();
                erpDTR.setErpDetachment(detachment);
                erpDTR.setStatus(ApprovalStatus.PENDING);
                String startDateString = (String) ExcelUtils.getCellData(1, 1);
                String stopDateString = (String) ExcelUtils.getCellData(2, 1);
                Date startDate = new Date();
                Date stopDate = new Date();
                if(has(startDateString) && !startDateString.isEmpty() && has(stopDateString) && !stopDateString.isEmpty()) {
                    startDate = format.parse(startDateString);
                    stopDate = format.parse(stopDateString);
                    LocalDate ld_start = convertToLocalDateViaInstant(startDate);
                    LocalDate ld_stop = convertToLocalDateViaInstant(stopDate);
                    erpDTR.setCutoffStart(startDate);
                    erpDTR.setCutoffEnd(stopDate);
                    System.out.println("START: "+startDate);
                    System.out.println("STOP: "+stopDate);
                    String preparedBy = (String) ExcelUtils.getCellData(2,3);
                    System.out.println("in charge: "+preparedBy);

                    if(has(detachment)) {
                        System.out.println("DETACHMENT: "+detachment.getName());
                        Set<ErpDTRAssignment> erpDTRAssignments = new HashSet<>();
                        Set<ErpDTRSchedule> erpDTRSchedules = new HashSet<>();

                        if(has(detachment.getAssignedEmployees()) && detachment.getAssignedEmployees().size() > 0) {
                            System.out.println("HAS ASSIGNED EMPLOYEE");
                            List<ErpEmployee> employees = new ArrayList<>(detachment.getAssignedEmployees());
                            employees.sort(Comparator.comparing(ErpEmployee::getLastname));
                            int x = 10;
                            for(int e = 0; e < employees.size(); e++) {
                                System.out.println("HAS EMPLOYEES");
                                ErpDTRAssignment erpDTRAssignment = new ErpDTRAssignment();
                                String employeeId = (String) ExcelUtils.getCellData(x, 53);
                                if(has(employeeId) && !employeeId.isEmpty()) {
                                    erpDTRAssignment.setEmployeeAssigned(findEmployeeById(employees, employeeId));
                                } else {
                                    continue;
                                }
                                if(!has(erpDTRAssignment.getEmployeeAssigned())) {
                                    continue;
                                }
                                //System.out.println(erpDTRAssignment.getEmployeeAssigned().getFirstname()+" "+erpDTRAssignment.getEmployeeAssigned().getLastname());
                                erpDTRAssignment.setErpDTR(erpDTR);

                                // generate workdays
                                LocalDate _startDate = convertToLocalDateViaInstant(erpDTR.getCutoffStart());
                                LocalDate _stopDate = convertToLocalDateViaInstant(erpDTR.getCutoffEnd());
                                //erpDTRSchedules.addAll(generateDTRSchedules(_startDate, _stopDate, erpDTRAssignment, detachment, employees.size(), x));
                                int y = 2;
                                int totalHours = 0;
                                int totalDayHours = 0;
                                int totalDays = 0;
                                int ns = x + employees.size() + 5;
                                for (LocalDate date = _startDate; (date.isBefore(_stopDate) || date.isEqual(_stopDate)); date = date.plusDays(1)) {
                                    ErpDTRSchedule erpDTRSchedule = new ErpDTRSchedule();
                                    erpDTRSchedule.setErpDTRAssignment(erpDTRAssignment);
                                    if(has(detachment.getErpTimeSchedules()) && detachment.getErpTimeSchedules().size() > 0) {
                                        //System.out.println("HAS TIME SCHEDULES.");
                                        //String legendValue = (String) ExcelUtils.getCellData(x, y);
                                        String timeStart_ds = (String) ExcelUtils.getCellData(x, y);
                                        String timeStop_ds = (String) ExcelUtils.getCellData(x, (y+1));
                                        String timeStart_ns = (String) ExcelUtils.getCellData(ns, y);
                                        String timeStop_ns = (String) ExcelUtils.getCellData(ns, (y+1));
                                        //System.out.println("TIME START: "+timeStart_ds);
                                        //System.out.println("TIME STOP: "+timeStop_ds);
                                        if(has(timeStart_ds) && !timeStart_ds.isEmpty() && has(timeStop_ds) && !timeStop_ds.isEmpty()) {
                                            //System.out.println("HAS TIME START AND STOP.");
                                            //WorkSchedLegend legend = WorkSchedLegend.valueOf(legendValue);
                                            //ErpTimeSchedule timeSchedule = findByLegend(detachment.getErpTimeSchedules(), legend);
                                            int timeStartHr_ds = (timeStart_ds.contains(":")) ? Integer.parseInt(timeStart_ds.split(":")[0]) : 0;
                                            int timeStartMin_ds = (timeStart_ds.contains(":")) ? Integer.parseInt(timeStart_ds.split(":")[1]) : 0;
                                            int timeStopHr_ds = (timeStop_ds.contains(":")) ? Integer.parseInt(timeStop_ds.split(":")[0]) : 0;
                                            int timeStopMin_ds = (timeStop_ds.contains(":")) ? Integer.parseInt(timeStop_ds.split(":")[1]) : 0;

                                            int timeStartHr_ns = (timeStart_ns.contains(":")) ? Integer.parseInt(timeStart_ns.split(":")[0]) : 0;
                                            int timeStartMin_ns = (timeStart_ns.contains(":")) ? Integer.parseInt(timeStart_ns.split(":")[1]) : 0;
                                            int timeStopHr_ns = (timeStop_ns.contains(":")) ? Integer.parseInt(timeStop_ns.split(":")[0]) : 0;
                                            int timeStopMin_ns = (timeStop_ns.contains(":")) ? Integer.parseInt(timeStop_ns.split(":")[1]) : 0;

                                            CellStyle styleStart_ds = ExcelUtils.getCellStyle(x, y);
                                            CellStyle styleStop_ds = ExcelUtils.getCellStyle(x, (y+1));

                                            CellStyle styleStart_ns = ExcelUtils.getCellStyle(ns, y);
                                            CellStyle styleStop_ns = ExcelUtils.getCellStyle(ns, (y+1));

                                            LocalDateTime ldt_start_ds = date.atTime(timeStartHr_ds, timeStartMin_ds, 0);
                                            LocalDateTime ldt_stop_ds = date.atTime(timeStopHr_ds, timeStopMin_ds, 0);

                                            LocalDateTime ldt_start_ns = date.atTime(timeStartHr_ns, timeStartMin_ns, 0);
                                            LocalDateTime ldt_stop_ns = date.atTime(timeStopHr_ns, timeStopMin_ns, 0);

                                            Long time_ds[] = getTime(ldt_start_ds, ldt_stop_ds);
                                            Long time_ns[] = getTime(ldt_start_ns, ldt_stop_ns);
                                            if(has(time_ds[0])){
                                                totalHours += time_ds[0];
                                            }
                                            if(has(time_ns[0])){
                                                totalHours += time_ns[0];
                                            }

                                            erpDTRSchedule.setDayShiftStart(convertToDateViaInstant(ldt_start_ds));
                                            erpDTRSchedule.setDayShiftEnd(convertToDateViaInstant(ldt_stop_ds));
                                            erpDTRSchedule.setNightShiftStart(convertToDateViaInstant(ldt_start_ns));
                                            erpDTRSchedule.setNightShiftEnd(convertToDateViaInstant(ldt_stop_ns));
                                            if(timeStartHr_ds < 15) {
                                                erpDTRSchedule.setAttendance(AttendanceType.DAY_SHIFT);
                                            } else if(timeStartHr_ds >= 15) {
                                                erpDTRSchedule.setAttendance(AttendanceType.MID_SHIFT);
                                            }

                                            if(has(time_ns[0]) && time_ns[0] > 0) {
                                                erpDTRSchedule.setAttendance(AttendanceType.NIGHT_SHIFT);
                                            }

                                            if(has(styleStart_ds) && has(styleStop_ds)) {
                                                Color fillColorStart = styleStart_ds.getFillForegroundColorColor();
                                                Color fillColorStop = styleStop_ds.getFillForegroundColorColor();
                                                if(has(fillColorStart) && has(fillColorStop)) {
                                                    String startColor = ((XSSFColor) fillColorStart).getARGBHex();
                                                    String stopColor = ((XSSFColor) fillColorStop).getARGBHex();

                                                    if (startColor.equals(stopColor)) {
                                                        if (startColor.equals(ABSENT_COLOR)) {
                                                            erpDTRSchedule.setAttendance(AttendanceType.ABSENT);
                                                        } else if (startColor.equals(DAYOFF_COLOR)) {
                                                            erpDTRSchedule.setAttendance(AttendanceType.DAY_OFF);
                                                        }
                                                    }
                                                }
                                            }
                                            totalDayHours += (has(time_ds[0])) ? time_ds[0].intValue() : 0;
                                            totalDayHours += (has(time_ns[0])) ? time_ns[0].intValue() : 0;
                                            erpDTRSchedule.setTotalHours(totalDayHours);
                                            if(has(time_ds[0]))
                                                erpDTRSchedule.setTotalHoursDay(time_ds[0].intValue());
                                            if(has(time_ns[0])) {
                                                if(time_ns[0].intValue() < 0) {
                                                    erpDTRSchedule.setTotalHoursNight((time_ns[0].intValue()) * -1);
                                                } else {
                                                    erpDTRSchedule.setTotalHoursNight(time_ns[0].intValue());
                                                }
                                            }
                                            //System.out.println("Time Total: "+erpDTRSchedule.getTotalHours());
                                            //System.out.println("ldt_start: "+ldt_start.getDayOfMonth());
                                            //System.out.println("ldt_stop: "+ldt_stop.getDayOfMonth());
                                            //System.out.println("WD START: " + ldt_start);
                                            //System.out.println("WD STOP: " + ldt_stop);
                                            //System.out.println("WD LEGEND: "+legend.name());
                                        } else {
                                            System.out.println("HAS NO LEGEND VALUE!");
                                        }
                                    } else {
                                        System.out.println("HAS NO TIME SCHEDULE");
                                    }
                                    erpDTRSchedules.add(erpDTRSchedule);
                                    y += 3;
                                }
                                erpDTRAssignment.setSchedules(erpDTRSchedules);
                                erpDTRAssignment.setTotalWorkHours(totalHours);
                                erpDTRAssignments.add(erpDTRAssignment);
                                //System.out.println("WorkDays: "+(y-2));
                                //System.out.println("Total workdays:"+workAssignment.getTotalWorkDay());
                                x++;
                            }

                            erpDTR.setAssignments(erpDTRAssignments);
                            System.out.println("DTR ASSIGN: "+erpDTRAssignments.size());
                            erpDTRService.save(erpDTR);
                            for(ErpDTRAssignment wa: erpDTRAssignments) {
                                //System.out.println("WORK ASSN: "+wa);
                                erpDTRAssignmentService.save(wa);
                            }
                            for(ErpDTRSchedule wd: erpDTRSchedules) {
                                erpDTRScheduleService.save(wd);
                            }

                            addDetailMessage("SUCCESSFUL", file.getFileName() + " is uploaded.", FacesMessage.SEVERITY_INFO);
                        } else {
                            addDetailMessage("FAILED UPLOAD", file.getFileName() + " Detachment has no Assigned employees.", FacesMessage.SEVERITY_ERROR);
                        }
                    } else {
                        addDetailMessage("FAILED UPLOAD", file.getFileName() + " cannot find Detachment.", FacesMessage.SEVERITY_ERROR);
                    }
                } else {
                    addDetailMessage("FAILED UPLOAD", file.getFileName() + " cannot find Stop and Start Date.", FacesMessage.SEVERITY_ERROR);
                }
            }
        } else {
            addDetailMessage("FAILED UPLOAD", "No File Selected.", FacesMessage.SEVERITY_ERROR);
        }
    }

    public Set<ErpDTRSchedule> generateDTRSchedules(LocalDate _startDate, LocalDate _stopDate, ErpDTRAssignment erpDTRAssignment, ErpDetachment detachment, int employeeCnt, int x) throws Exception {
        Set<ErpDTRSchedule> erpDTRSchedules = new HashSet<>();
        int y = 2;
        int totalHours = 0;
        int totalDayHours = 0;
        int totalDays = 0;
        int ns = x + employeeCnt + 5;
        for (LocalDate date = _startDate; (date.isBefore(_stopDate) || date.isEqual(_stopDate)); date = date.plusDays(1)) {
            ErpDTRSchedule erpDTRSchedule = new ErpDTRSchedule();
            erpDTRSchedule.setErpDTRAssignment(erpDTRAssignment);
            if(has(detachment.getErpTimeSchedules()) && detachment.getErpTimeSchedules().size() > 0) {
                //String legendValue = (String) ExcelUtils.getCellData(x, y);
                String timeStart_ds = (String) ExcelUtils.getCellData(x, y);
                String timeStop_ds = (String) ExcelUtils.getCellData(x, (y+1));
                String timeStart_ns = (String) ExcelUtils.getCellData(ns, y);
                String timeStop_ns = (String) ExcelUtils.getCellData(ns, (y+1));
                System.out.println("TIME START: "+timeStart_ds);
                System.out.println("TIME STOP: "+timeStop_ds);
                if(has(timeStart_ds) && !timeStart_ds.isEmpty() && has(timeStop_ds) && !timeStop_ds.isEmpty()) {
                    //WorkSchedLegend legend = WorkSchedLegend.valueOf(legendValue);
                    //ErpTimeSchedule timeSchedule = findByLegend(detachment.getErpTimeSchedules(), legend);
                    int timeStartHr_ds = (timeStart_ds.contains(":")) ? Integer.parseInt(timeStart_ds.split(":")[0]) : 0;
                    int timeStartMin_ds = (timeStart_ds.contains(":")) ? Integer.parseInt(timeStart_ds.split(":")[1]) : 0;
                    int timeStopHr_ds = (timeStop_ds.contains(":")) ? Integer.parseInt(timeStop_ds.split(":")[0]) : 0;
                    int timeStopMin_ds = (timeStop_ds.contains(":")) ? Integer.parseInt(timeStop_ds.split(":")[1]) : 0;

                    int timeStartHr_ns = (timeStart_ns.contains(":")) ? Integer.parseInt(timeStart_ns.split(":")[0]) : 0;
                    int timeStartMin_ns = (timeStart_ns.contains(":")) ? Integer.parseInt(timeStart_ns.split(":")[1]) : 0;
                    int timeStopHr_ns = (timeStop_ns.contains(":")) ? Integer.parseInt(timeStop_ns.split(":")[0]) : 0;
                    int timeStopMin_ns = (timeStop_ns.contains(":")) ? Integer.parseInt(timeStop_ns.split(":")[1]) : 0;

                    CellStyle styleStart_ds = ExcelUtils.getCellStyle(x, y);
                    CellStyle styleStop_ds = ExcelUtils.getCellStyle(x, (y+1));

                    CellStyle styleStart_ns = ExcelUtils.getCellStyle(ns, y);
                    CellStyle styleStop_ns = ExcelUtils.getCellStyle(ns, (y+1));

                    LocalDateTime ldt_start_ds = date.atTime(timeStartHr_ds, timeStartMin_ds, 0);
                    LocalDateTime ldt_stop_ds = date.atTime(timeStopHr_ds, timeStopMin_ds, 0);

                    LocalDateTime ldt_start_ns = date.atTime(timeStartHr_ns, timeStartMin_ns, 0);
                    LocalDateTime ldt_stop_ns = date.atTime(timeStopHr_ns, timeStopMin_ns, 0);

                    Long time_ds[] = getTime(ldt_start_ds, ldt_stop_ds);
                    Long time_ns[] = getTime(ldt_start_ns, ldt_stop_ns);
                    if(has(time_ds[0])){
                        totalHours += time_ds[0];
                    }
                    if(has(time_ns[0])){
                        totalHours += time_ns[0];
                    }

                    erpDTRSchedule.setDayShiftStart(convertToDateViaInstant(ldt_start_ds));
                    erpDTRSchedule.setDayShiftEnd(convertToDateViaInstant(ldt_stop_ds));
                    erpDTRSchedule.setNightShiftStart(convertToDateViaInstant(ldt_start_ns));
                    erpDTRSchedule.setNightShiftEnd(convertToDateViaInstant(ldt_stop_ns));
                    if(timeStartHr_ds < 15) {
                        erpDTRSchedule.setAttendance(AttendanceType.DAY_SHIFT);
                    } else if(timeStartHr_ds >= 15) {
                        erpDTRSchedule.setAttendance(AttendanceType.MID_SHIFT);
                    }

                    if(has(time_ns[0]) && time_ns[0] > 0) {
                        erpDTRSchedule.setAttendance(AttendanceType.NIGHT_SHIFT);
                    }

                    if(has(styleStart_ds) && has(styleStop_ds)) {
                        Color fillColorStart = styleStart_ds.getFillForegroundColorColor();
                        Color fillColorStop = styleStop_ds.getFillForegroundColorColor();
                        if(has(fillColorStart) && has(fillColorStop)) {
                            String startColor = ((XSSFColor) fillColorStart).getARGBHex();
                            String stopColor = ((XSSFColor) fillColorStop).getARGBHex();

                            if (startColor.equals(stopColor)) {
                                if (startColor.equals(ABSENT_COLOR)) {
                                    erpDTRSchedule.setAttendance(AttendanceType.ABSENT);
                                } else if (startColor.equals(DAYOFF_COLOR)) {
                                    erpDTRSchedule.setAttendance(AttendanceType.DAY_OFF);
                                }
                            }
                        }
                    }
                    totalDayHours += (has(time_ds[0])) ? time_ds[0].intValue() : 0;
                    totalDayHours += (has(time_ns[0])) ? time_ns[0].intValue() : 0;
                    erpDTRSchedule.setTotalHours(totalDayHours);
                    System.out.println("Time Total: "+erpDTRSchedule.getTotalHours());
                    //System.out.println("ldt_start: "+ldt_start.getDayOfMonth());
                    //System.out.println("ldt_stop: "+ldt_stop.getDayOfMonth());
                    //System.out.println("WD START: " + ldt_start);
                    //System.out.println("WD STOP: " + ldt_stop);
                    //System.out.println("WD LEGEND: "+legend.name());
                } else {
                    System.out.println("HAS NO LEGEND VALUE!");
                }
            } else {
                System.out.println("HAS NO TIME SCHEDULE");
            }
            erpDTRSchedules.add(erpDTRSchedule);
            y += 3;
        }
        erpDTRAssignment.setSchedules(erpDTRSchedules);
        erpDTRAssignment.setTotalWorkHours(totalHours);
        //erpDTRAssignment.setTotalWorkDays();
        System.out.println("SUM Hours: "+totalHours);
        return erpDTRSchedules;
    }

    private Long[] getTime(LocalDateTime dob, LocalDateTime now) {
        final int MINUTES_PER_HOUR = 60;
        final int SECONDS_PER_MINUTE = 60;
        final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        Long seconds = duration.getSeconds();

        Long hours = seconds / SECONDS_PER_HOUR;
        Long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        Long secs = (seconds % SECONDS_PER_MINUTE);

        return new Long[]{hours, minutes, secs};
    }

    public ErpTimeSchedule findByLegend(Set<ErpTimeSchedule> erpTimeSchedules, WorkSchedLegend legend) {
        for(ErpTimeSchedule ts: erpTimeSchedules) {
            if(ts.getLegend().equals(legend)) {
                return ts;
            }
        }
        return null;
    }

    public ErpEmployee findEmployeeById(List<ErpEmployee> employees, String employeeId) {
        for(ErpEmployee emp: employees) {
            if(emp.getEmployeeId().equals(employeeId)) {
                return emp;
            }
        }
        return null;
    }

    public void downloadFile(String detachId) throws Exception {
        //System.out.println("DETACHMENT ID: " + detachId);
        if(has(detachId)) {
            Long detId = Long.parseLong(detachId);
            ErpDetachment detachment = erpDetachmentService.findById(detId);
            boolean hasAssignedEmployees = (has(detachment) && detachment.getAssignedEmployees() != null && detachment.getAssignedEmployees().size() > 0);
            boolean hasSchedules = (has(detachment) && detachment.getErpTimeSchedules() != null && detachment.getErpTimeSchedules().size() > 0);
            LocalDate today = LocalDate.now();
            LocalDate startSched = null;
            LocalDate stopSched = null;
            List<String> legends = new ArrayList<>();

            if(today.getDayOfMonth() <= 14) {
                startSched = LocalDate.of(today.getYear(), today.getMonth(), 16);
                LocalDate monthstart = LocalDate.of(today.getYear(),today.getMonth(),1);
                stopSched = monthstart.plusDays(monthstart.lengthOfMonth()-1);
            } else {
                startSched = LocalDate.of(today.getYear(), today.getMonth().plus(1), 1);
                stopSched = LocalDate.of(today.getYear(), today.getMonth().plus(1), 15);
            }

            ExcelUtils.initializeWithFilename("dtr_template.xlsx", "Sheet1");
            // locked style cells
            CellStyle locked = ExcelUtils.workbook.createCellStyle();
            locked.setLocked(true);
            ExcelUtils.setCell(0,1, detachment.getId());
            ExcelUtils.setCell(5,2, "DETACHMENT: "+detachment.getName());
            ExcelUtils.setCell(5,8, "AREA OF RESPONSIBILITY: "+detachment.getLocation().getLocation());
            ExcelUtils.setCell(2,3, currentUserUtil.getFullName());
            ExcelUtils.setCell(1,1, startSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.setCell(2,1, stopSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            if(has(legends) && legends.size() > 0) {
                legends.add(WorkSchedLegend.DO.name());
            }
            ExcelUtils.evaluateCell(5,0);
            ExcelUtils.evaluateCell(6,0);
            // loop from start date to end date
            int x = generateDTRTable(detachment, startSched, stopSched, hasAssignedEmployees, hasSchedules, false,8);

            // copy rows
            int endRow = (x - 9) + 10;
            ExcelUtils.copyRange(7, 9, x+2);
            x = x + 3;
            ExcelUtils.setCell(x, 0, "NIGHT SHIFT");
            x = generateDTRTable(detachment, startSched, stopSched, hasAssignedEmployees, hasSchedules, true, x);

            String detachmentName = detachment.getName().replaceAll(" ", "_");
            generateFile(ExcelUtils.workbook, "DTR_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            ExcelUtils.workbook.close();
            //FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-templates.xhtml");
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-templates.xhtml");
        }
    }

    public Integer generateDTRTable(ErpDetachment detachment, LocalDate startSched, LocalDate stopSched, boolean hasAssignedEmployees, boolean hasSchedules, boolean isNS, int x) throws Exception {
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

        if(hasAssignedEmployees && hasSchedules){
            List<ErpEmployee> employees = new ArrayList<>(detachment.getAssignedEmployees());
            employees.sort(Comparator.comparing(ErpEmployee::getLastname));
            x += 2;
            for(ErpEmployee emp: employees) {
                //System.out.println("EMPLOYEE: "+emp.getFirstname() + " " + emp.getLastname());
                //ExcelUtils.setCell(x, 0, x-9);
                if(has(emp.getFirstname()) && has(emp.getLastname())) {
                    ExcelUtils.setCell(x, 0, emp.getLastname() + ", " + emp.getFirstname());
                }
                y = 2;
                z = 0;
                for (LocalDate date = startSched; (date.isBefore(stopSched) || date.isEqual(stopSched)); date = date.plusDays(1)) {
                    CellStyle style = ExcelUtils.workbook.createCellStyle();
                    style.setAlignment(HorizontalAlignment.CENTER);
                    ExcelUtils.setCellTime(x, y, "00:00:00");
                    ExcelUtils.setCellTime(x, y+1, "00:00:00");
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
                int employeesCnt = employees.size();
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

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
