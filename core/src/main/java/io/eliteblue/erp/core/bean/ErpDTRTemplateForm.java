package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.*;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpDTRTemplateForm implements Serializable {

    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private final String DAYOFF_COLOR = "FFFFC000";
    private final String ABSENT_COLOR = "FFFF0000";
    private final String SIL_COLOR = "FF00B050";
    private final String BTR_COLOR = "FFED7D31";
    private final String CCTV_COLOR = "FF7030A0";
    private final String SG_COLOR = "FF00B0F0";
    private final String RD_COLOR1 = "FFED7D31";
    private final String RD_COLOR2 = "FFA5A5A5";
    private final String RD_COLOR3 = "FFC00000";
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
    private ErpHolidayService erpHolidayService;

    @Autowired
    private ErpLocalHolidayService erpLocalHolidayService;

    /*@Autowired
    private ContractedManHoursService contractedManHoursService;*/

    @Autowired
    private TODClientService todClientService;

    @Autowired
    private TODPersonnelShiftService todPersonnelShiftService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private List<ErpDetachment> detachments;

    private UploadedFile file;

    private LocalDate localStart;

    private LocalDate localEnd;

    @PostConstruct
    public void init() {
        detachments = erpDetachmentService.getAllCurrentDetachment();
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

    public String backBtnPressed() { return "dtr-list?faces-redirect=true&includeViewParams=true"; }

    public AttendanceType getAttendanceType (CellStyle styleStart, CellStyle styleStop) {
        AttendanceType retVal = null;
        if(has(styleStart) && has(styleStop)) {
            Color fillColorStart = styleStart.getFillForegroundColorColor();
            Color fillColorStop = styleStop.getFillForegroundColorColor();
            if(has(fillColorStart) && has(fillColorStop)) {
                String startColor = ((XSSFColor) fillColorStart).getARGBHex();
                String stopColor = ((XSSFColor) fillColorStop).getARGBHex();
                //System.out.println("HAS FILL COLOR ...["+startColor+"] and ["+stopColor+"]");
                if (startColor.equals(stopColor)) {
                    if (startColor.equals(ABSENT_COLOR)) {
                        retVal = AttendanceType.ABSENT;
                    } else if (startColor.equals(DAYOFF_COLOR)) {
                        retVal = AttendanceType.DAY_OFF;
                    } else if (startColor.equals(SIL_COLOR)) {
                        retVal = AttendanceType.SIL;
                    } else if (startColor.equals(BTR_COLOR)) {
                        retVal = AttendanceType.BTR;
                    } else if (startColor.equals(CCTV_COLOR)) {
                        retVal = AttendanceType.CCTV_OP;
                    } else if (startColor.equals(SG_COLOR)) {
                        retVal = AttendanceType.SG;
                    }
                }
                //System.out.println("ATTENDANCE TYPE: "+retVal);
            }
        }
        return retVal;
    }

    public ErpDetachment getRelieverDetachment(CellStyle styleStart, CellStyle styleStop) throws Exception {
        ErpDetachment retVal = null;
        if(has(styleStart) && has(styleStop)) {
            Color fillColorStart = styleStart.getFillForegroundColorColor();
            Color fillColorStop = styleStop.getFillForegroundColorColor();
            if (has(fillColorStart) && has(fillColorStop)) {
                String startColor = ((XSSFColor) fillColorStart).getARGBHex();
                String stopColor = ((XSSFColor) fillColorStop).getARGBHex();
                //System.out.println("HAS FILL COLOR ...[" + startColor + "] and [" + stopColor + "]");
                if (startColor.equals(stopColor)) {
                    Long rdID = null;
                    String detachmentStringId = null;
                    if(startColor.equals(RD_COLOR1)) {
                        detachmentStringId = (String) ExcelUtils.getCellData(2,33);
                    } else if (startColor.equals(RD_COLOR2)) {
                        detachmentStringId = (String) ExcelUtils.getCellData(3,33);
                    } else if (startColor.equals(RD_COLOR3)) {
                        detachmentStringId = (String) ExcelUtils.getCellData(4,33);
                    }

                    if(detachmentStringId == null) {
                        return null;
                    } else if(!detachmentStringId.isEmpty()) {
                        rdID = Long.parseLong(detachmentStringId);
                    }
                    if(rdID != null) {
                        retVal = erpDetachmentService.findById(rdID);
                    }
                }
            }
        }
        return retVal;
    }

    public void fileUpload() throws Exception {
        System.out.println("FILE: "+file);
        if (has(file) && file.getSize() > 0) {
            ExcelUtils.initializeWithInputStream(file.getInputStream(), "Sheet1");
            String detachmentStringId = (String) ExcelUtils.getCellData(0,1);
            if(!has(detachmentStringId) || detachmentStringId.isEmpty()) {
                addDetailMessage("FAILED UPLOAD", file.getFileName() + " has no Detachment ID.", FacesMessage.SEVERITY_ERROR);
            } else {
                // find date by range
                //Date date1=new SimpleDateFormat("MM-dd-yyyy").parse("04-01-2022");
                //Date date2=new SimpleDateFormat("MM-dd-yyyy").parse("04-15-2022");
                //List<ErpHoliday> holidays = erpHolidayService.findByDateRange(date1, date2);
                //System.out.println("HOLIDAYS: "+holidays.size());

                Long detachmentId = Long.parseLong(detachmentStringId);
                System.out.println("Detachment ID: " + detachmentId);
                ErpDetachment detachment = erpDetachmentService.findById(detachmentId);
                ErpDTR erpDTR = new ErpDTR();
                erpDTR.setErpDetachment(detachment);
                erpDTR.setStatus(ApprovalStatus.PENDING);
                String startDateString = (String) ExcelUtils.getCellData(1, 1);
                String stopDateString = (String) ExcelUtils.getCellData(2, 1);
                String uploadReason = (String) ExcelUtils.getCellData(2, 12);
                Date startDate = new Date();
                Date stopDate = new Date();
                if(has(startDateString) && !startDateString.isEmpty() && has(stopDateString) && !stopDateString.isEmpty()) {

                    // background color
                    //System.out.println("Cell Text: "+ExcelUtils.getCell(4, 27));
                    XSSFCell cell = (XSSFCell) ExcelUtils.getCell(4, 27);
                    if(cell.getCellStyle().getFillForegroundColorColor().getARGBHex() != null) {
                        //System.out.println("ARGB HEX: "+cell.getCellStyle().getFillForegroundColorColor().getARGBHex());
                    }

                    //System.out.println("COLOR: "+s.getFillBackgroundColorColor().getRGB().toString());
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
                    List<ErpHoliday> holidays = erpHolidayService.findByDateRange(startDate, stopDate);
                    List<ErpLocalHoliday> localHolidays = erpLocalHolidayService.findByDateRange(detachment.getLocation(), startDate, stopDate);
                    System.out.println("HOLIDAYS: "+holidays.size());
                    System.out.println("LOCAL HOLIDAYS: "+localHolidays.size());

                    if(has(detachment)) {
                        System.out.println("DETACHMENT: "+detachment.getName());
                        Set<ErpDTRAssignment> erpDTRAssignments = new HashSet<>();
                        Set<ErpDTRSchedule> erpDTRSchedules = new HashSet<>();
                        Set<RelieverDTRAssignment> relieverDTRAssignments = new HashSet<>();

                        if(has(detachment.getAssignedEmployees()) && detachment.getAssignedEmployees().size() > 0) {
                            //System.out.println("HAS ASSIGNED EMPLOYEE");
                            List<ErpEmployee> employees = new ArrayList<>(detachment.getAssignedEmployees());
                            employees = employees.stream().filter(itm -> itm.getStatus().equals(EmployeeStatus.HIRED)).collect(Collectors.toList());
                            employees.sort(Comparator.comparing(ErpEmployee::getLastname));
                            int x = 10;
                            int totalManHours = 0;
                            for(ErpEmployee emp : employees) {
                                //System.out.println("HAS EMPLOYEES");
                                //System.out.println(emp.getFirstname()+" "+emp.getLastname());
                                ErpDTRAssignment erpDTRAssignment = new ErpDTRAssignment();
                                String employeeId = (String) ExcelUtils.getCellData(x, 53);
                                if(has(employeeId) && !employeeId.isEmpty()) {
                                    erpDTRAssignment.setEmployeeAssigned(findEmployeeById(employees, employeeId));
                                } else {
                                    //System.out.println("EMPLOYEE ID IS EMPTY");
                                    x++;
                                    continue;
                                }
                                if(!has(erpDTRAssignment.getEmployeeAssigned())) {
                                    //System.out.println("HAS NO ASSIGNED EMPLOYEE");
                                    x++;
                                    continue;
                                }
                                //System.out.println("EMPLOYEE ASSIGNED  ==="+erpDTRAssignment.getEmployeeAssigned().getFirstname()+" "+erpDTRAssignment.getEmployeeAssigned().getLastname());
                                erpDTRAssignment.setErpDTR(erpDTR);

                                // generate workdays
                                LocalDate _startDate = convertToLocalDateViaInstant(erpDTR.getCutoffStart());
                                LocalDate _stopDate = convertToLocalDateViaInstant(erpDTR.getCutoffEnd());
                                //erpDTRSchedules.addAll(generateDTRSchedules(_startDate, _stopDate, erpDTRAssignment, detachment, employees.size(), x));
                                int y = 2;
                                int totalHours = 0;
                                int totalBasicHours = 0;
                                int totalBasicND = 0;
                                int totalBasicOT = 0;
                                int totalBasicOTExcess = 0;
                                int totalDayHours = 0;
                                int totalNightHours = 0;
                                int totalWorkDays = 0;
                                int totalBasicWorkDays = 0;
                                int totalWorkRegularHolidays = 0;
                                int totalNDRegularHolidays = 0;
                                int totalOTRegularHolidays = 0;
                                int totalOTExcessRegularHolidays = 0;
                                int totalWorkSpecialHolidays = 0;
                                int totalNDSpecialHolidays = 0;
                                int totalOTSpecialHolidays = 0;
                                int totalOTExcessSpecialHolidays = 0;
                                int totalNDHours = 0;
                                int totalOTHours = 0;
                                int totalOTExcess = 0;
                                int totalSIL = 0;
                                int totalBTR = 0;
                                int totalSG = 0;
                                int totalCCTV = 0;
                                int totalAbsent = 0;
                                int successiveDuty = 0;
                                int dayCount = 1;
                                boolean isRdp = (emp.getRestDayPayEntitled() != null) ? emp.getRestDayPayEntitled() : false;
                                boolean hasPrevious = false;
                                Date availedStart = null;
                                //System.out.println("EMPLOYEES: "+employees.size());
                                int ns = x + employees.size() + 5;
                                for (LocalDate date = _startDate; (date.isBefore(_stopDate) || date.isEqual(_stopDate)); date = date.plusDays(1)) {
                                    //System.out.println("x-count: "+x+" ns-count: "+ns);
                                    ErpDTRSchedule erpDTRSchedule = new ErpDTRSchedule();
                                    RelieverDTRAssignment relieverDTRAssignment = new RelieverDTRAssignment();
                                    erpDTRSchedule.setErpDTRAssignment(erpDTRAssignment);
                                    relieverDTRAssignment.setErpDTRAssignment(erpDTRAssignment);
                                    HolidayType holidayType = null;
                                    if(holidays.size() > 0) {
                                        ErpHoliday _holidayTemp = getHolidayByDate(holidays, date);
                                        if(_holidayTemp != null) {
                                            holidayType = _holidayTemp.getHolidayType();
                                            //System.out.println(holidayType.name()+" HOLIDAY: "+_holidayTemp.getName()+" ["+_holidayTemp.getHolidayDate()+"]");
                                        }
                                    } else if(localHolidays.size() > 0) {
                                        ErpLocalHoliday _localHolidayTemp = getLocalHolidayByDate(localHolidays, date);
                                        if(_localHolidayTemp != null){
                                            holidayType = HolidayType.SPECIAL;
                                            //System.out.println(holidayType.name()+" HOLIDAY: "+_localHolidayTemp.getName()+" ["+_localHolidayTemp.getHolidayDate()+"]");
                                        }
                                    }


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
                                            Long temp_ds = (time_ds[0] < 0) ? time_ds[0] * -1 : time_ds[0];
                                            Long temp_ns = (time_ns[0] < 0) ? time_ns[0] * -1 : time_ns[0];
                                            Long temp_nsds = temp_ds + temp_ns;
                                            Long temp_ot = 0L;
                                            Long temp_ot_ns = 0L;
                                            Long temp_ote = 0L;
                                            Long temp_wd = 0L;
                                            Long temp_nd = 0L;

                                            if(has(styleStart_ds) && has(styleStop_ds)) {
                                                AttendanceType t = getAttendanceType(styleStart_ds, styleStop_ds);
                                                ErpDetachment relieverDetach = getRelieverDetachment(styleStart_ds, styleStop_ds);
                                                if(t != null) {
                                                    //System.out.println("DS ["+t.name()+"]");
                                                    erpDTRSchedule.setAttendance(t);
                                                }
                                                if(relieverDetach != null) {
                                                    // set reliever detachment
                                                    erpDTRSchedule.setRelieverDetachment(relieverDetach);
                                                    relieverDTRAssignment.setRelieverDetachment(relieverDetach);
                                                    relieverDTRAssignment.setTotalDays(1.0);
                                                }
                                            }
                                            if(has(styleStart_ns) && has(styleStop_ns)) {
                                                AttendanceType t = getAttendanceType(styleStart_ns, styleStop_ns);
                                                ErpDetachment relieverDetach = getRelieverDetachment(styleStart_ns, styleStop_ns);
                                                if(t != null) {
                                                    //System.out.println("start["+timeStart_ns+"] stop["+timeStop_ns+"] == NS ["+t.name()+"]");
                                                    erpDTRSchedule.setAttendance(t);
                                                }
                                                if(relieverDetach != null) {
                                                    // set reliever detachment
                                                    erpDTRSchedule.setRelieverDetachment(relieverDetach);
                                                    relieverDTRAssignment.setRelieverDetachment(relieverDetach);
                                                    relieverDTRAssignment.setTotalDays(1.0);
                                                }
                                            }
                                            if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.SIL)) {
                                                //System.out.println("SIL Date: "+date);
                                                if(totalSIL <= 0) {
                                                    // set Availed Start
                                                    availedStart = convertToDateViaInstant(date);
                                                }
                                                totalSIL++;
                                                //continue;
                                            }
                                            if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.BTR)) {
                                                totalBTR++;
                                            } else if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.SG)) {
                                                totalSG++;
                                            } else if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.CCTV_OP)) {
                                                totalCCTV++;
                                            } else if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.ABSENT)) {
                                                totalAbsent++;
                                            }

                                            if(has(time_ds[0])){
                                                totalHours += temp_ds;
                                                if(temp_ds >= 8) {
                                                    if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.BTR)) {
                                                        temp_wd += temp_ds;
                                                    } else {
                                                        if(temp_ds != 16)
                                                            temp_ot += temp_ds - 8;
                                                        temp_wd += new Double(Math.floor(temp_ds / 8)).longValue();
                                                    }
                                                }
                                            }
                                            if(has(time_ns[0])){
                                                totalHours += temp_ns;
                                                if(temp_ns >= 8) {
                                                    if(has(erpDTRSchedule.getAttendance()) && erpDTRSchedule.getAttendance().equals(AttendanceType.BTR)) {
                                                        temp_wd += temp_ns;
                                                    } else {
                                                        if(temp_ns != 16)
                                                            temp_ot_ns += temp_ns - 8;
                                                        temp_ot += temp_ot_ns;
                                                        temp_wd += new Double(Math.floor(temp_ns / 8)).longValue();
                                                        temp_nd += temp_ns - temp_ot_ns;
                                                    }
                                                }
                                            }
                                            if(detachment.getExcessOT() != null && detachment.getExcessOT()) {
                                                temp_ote = temp_ot;
                                                temp_ot = 0L;
                                            }

                                            if(holidayType == null) {
                                                totalBasicOTExcess += temp_ote;
                                                totalBasicOT += temp_ot;
                                                totalBasicWorkDays += temp_wd;
                                                totalBasicND += temp_nd;
                                                totalBasicHours += temp_nsds - temp_ot;
                                            } else if(holidayType.equals(HolidayType.REGULAR)) {
                                                totalOTExcessRegularHolidays += temp_ote;
                                                totalOTRegularHolidays += temp_ot;
                                                totalWorkRegularHolidays += temp_nsds - temp_ot;
                                                totalNDRegularHolidays += temp_nd;
                                            } else if(holidayType.equals(HolidayType.SPECIAL)) {
                                                totalOTExcessSpecialHolidays += temp_ote;
                                                totalOTSpecialHolidays += temp_ot;
                                                totalWorkSpecialHolidays += temp_nsds - temp_ot;
                                                totalNDSpecialHolidays += temp_nd;
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

                                            if(has(time_ds[0])){
                                                totalDayHours += temp_ds;
                                            }
                                            if(has(time_ns[0])) {
                                                totalNightHours += temp_ns;
                                            }
                                            totalOTExcess += temp_ote;
                                            totalOTHours += temp_ot;
                                            totalWorkDays += temp_wd;
                                            totalNDHours += temp_nd;

                                            erpDTRSchedule.setTotalHours(temp_nsds.intValue());
                                            if(has(time_ds[0]))
                                                erpDTRSchedule.setTotalHoursDay(temp_ds.intValue());
                                            if(has(time_ns[0])) {
                                                erpDTRSchedule.setTotalHoursNight(temp_ns.intValue());
                                            }
                                            // rdp successive
                                            if(temp_wd != null && temp_wd > 0 && isRdp) {
                                                if(successiveDuty > 0) {
                                                    if(hasPrevious) {
                                                        successiveDuty += 1;
                                                    }
                                                } else {
                                                    if(dayCount == 1 || dayCount == 8) {
                                                        successiveDuty += 1;
                                                        hasPrevious = true;
                                                    }
                                                }
                                            } else if(temp_wd == 0 && isRdp) {
                                                hasPrevious = false;
                                                if(successiveDuty < 7) {
                                                    successiveDuty = 0;
                                                }
                                            }
                                            //System.out.println("Time Total Day: "+erpDTRSchedule.getTotalHoursDay());
                                            //System.out.println("Time Total Night: "+erpDTRSchedule.getTotalHoursNight());
                                            //System.out.println("Time Total OT: "+temp_ot);
                                            //System.out.println("Time Total OT Excess: "+temp_ote);
                                            //System.out.println("Time Total ND: "+temp_nd);
                                            //System.out.println("Time Total Workdays: "+temp_wd);
                                            //System.out.println("Time Total: "+erpDTRSchedule.getTotalHours());
                                            //System.out.println("ldt_start: "+ldt_start.getDayOfMonth());
                                            //System.out.println("ldt_stop: "+ldt_stop.getDayOfMonth());
                                            //System.out.println("WD START: " + ldt_start);
                                            //System.out.println("WD STOP: " + ldt_stop);
                                            //System.out.println("WD LEGEND: "+legend.name());
                                            if(erpDTRSchedule.getRelieverDetachment() != null) {
                                                // check if existing
                                                for (RelieverDTRAssignment existingAssignment : relieverDTRAssignments) {
                                                    if (existingAssignment.equals(relieverDTRAssignment)) {
                                                        existingAssignment.setTotalDays(existingAssignment.getTotalDays() + relieverDTRAssignment.getTotalDays());
                                                        break;
                                                    }
                                                }
                                                relieverDTRAssignments.add(relieverDTRAssignment);
                                            }
                                            erpDTRSchedules.add(erpDTRSchedule);
                                        } else {
                                            //System.out.println("DATE: "+date);
                                            //System.out.println("TIME START: "+timeStart_ds);
                                            //System.out.println("TIME STOP: "+timeStop_ds);
                                            //System.out.println("HAS NO LEGEND VALUE!");
                                        }
                                    } else {
                                        //System.out.println("DATE: "+date);
                                        //System.out.println("HAS NO TIME SCHEDULE");
                                    }
                                    y += 3;
                                    dayCount++;
                                }
                                erpDTRAssignment.setSchedules(erpDTRSchedules);
                                //System.out.println("Reliever Detachments: "+relieverDTRAssignments.size());
                                if(relieverDTRAssignments.size() > 0) {
                                    erpDTRAssignment.setRelieverDTRAssignments(relieverDTRAssignments);
                                }
                                erpDTRAssignment.setTotalBasicHours(totalBasicHours);
                                //System.out.println("EMPLOYEE ASSIGNED  ==="+erpDTRAssignment.getEmployeeAssigned().getFirstname()+" "+erpDTRAssignment.getEmployeeAssigned().getLastname());
                                //System.out.println("Grand Total Basic Hours: "+erpDTRAssignment.getTotalBasicHours());
                                erpDTRAssignment.setTotalBasicOTHours(totalBasicOT);
                                //System.out.println("Grand Total Basic OT: "+totalBasicOT);
                                erpDTRAssignment.setTotalBasicOTExcessHours(totalBasicOTExcess);
                                //System.out.println("Grand Total Basic OT Excess: "+totalBasicOTExcess);
                                erpDTRAssignment.setTotalBasicNDHours(totalBasicND);
                                //System.out.println("Grand Total Basic ND: "+totalBasicND);
                                erpDTRAssignment.setTotalBasicWorkDays(totalBasicWorkDays);
                                //System.out.println("Grand Total Basic Work Days: "+totalBasicWorkDays);
                                if(successiveDuty >= 7 && isRdp) {
                                    erpDTRAssignment.setHasRdp(true);
                                    if(successiveDuty >= 14) {
                                        erpDTRAssignment.setTotalRdp(2);
                                    } else {
                                        erpDTRAssignment.setTotalRdp(1);
                                    }
                                } else {
                                    erpDTRAssignment.setHasRdp(false);
                                    erpDTRAssignment.setTotalRdp(0);
                                }

                                if(totalSIL > 0) {
                                    ErpEmployee empl = erpDTRAssignment.getEmployeeAssigned();
                                    if(has(empl.getSil()) && empl.getSil() >= totalSIL) {
                                        erpDTRAssignment.setTotalSIL(totalSIL);
                                        if(availedStart != null) {
                                            erpDTRAssignment.setDateAvailedStart(availedStart);
                                        }
                                    }
                                    else {
                                        addDetailMessage("FAILED UPLOAD", empl.getFirstname() + " " +empl.getLastname() + " has no more SIL left.", FacesMessage.SEVERITY_ERROR);
                                        return;
                                    }
                                }
                                //System.out.println("Grand Total SIL Days: "+totalSIL);
                                erpDTRAssignment.setTotalBTRDays(totalBTR);
                                //System.out.println("Grand Total BTR Days: "+totalBTR);
                                erpDTRAssignment.setTotalSGDays(totalSG);
                                //System.out.println("Grand Total SG Days: "+totalSG);
                                erpDTRAssignment.setTotalCCTVDays(totalCCTV);
                                //System.out.println("Grand Total CCTV Days: "+totalCCTV);
                                erpDTRAssignment.setTotalAbsentDays(totalAbsent);
                                //System.out.println("Grand Total Absent Days: "+totalAbsent);

                                erpDTRAssignment.setTotalWorkHours(totalHours);
                                //System.out.println("Grand Total Hours: "+erpDTRAssignment.getTotalWorkHours());
                                erpDTRAssignment.setTotalHoursDay(totalDayHours);
                                //System.out.println("Grand Total Day Hours: "+erpDTRAssignment.getTotalHoursDay());
                                erpDTRAssignment.setTotalHoursNight(totalNightHours);
                                //System.out.println("Grand Total Night Hours: "+erpDTRAssignment.getTotalHoursNight());
                                erpDTRAssignment.setTotalNDHours(totalNDHours);
                                //System.out.println("Grand Total ND Hours: "+totalNDHours);
                                erpDTRAssignment.setTotalOTHours(totalOTHours);
                                //System.out.println("Grand Total OT Hours: "+totalOTHours);
                                erpDTRAssignment.setTotalOTExcess(totalOTExcess);
                                //System.out.println("Grand Total OT Excess Hours: "+totalOTExcess);
                                erpDTRAssignment.setTotalWorkDays(totalWorkDays);
                                //System.out.println("Grand Total Work Days: "+totalWorkDays);

                                erpDTRAssignment.setTotalNDRegularHolidayHrs(totalNDRegularHolidays);
                                //System.out.println("Grand Total REGULAR HOLIDAY ND: "+totalNDRegularHolidays);
                                erpDTRAssignment.setTotalOTRegularHolidayHrs(totalOTRegularHolidays);
                                //System.out.println("Grand Total REGULAR HOLIDAY OT: "+totalOTRegularHolidays);
                                erpDTRAssignment.setTotalOTExcessRegularHolidayHrs(totalOTExcessRegularHolidays);
                                //System.out.println("Grand Total REGULAR HOLIDAY OT Excess: "+totalOTExcessRegularHolidays);
                                erpDTRAssignment.setTotalRegularHolidayHrs(totalWorkRegularHolidays);
                                //System.out.println("Grand Total REGULAR HOLIDAY Hours: "+totalWorkRegularHolidays);

                                erpDTRAssignment.setTotalNDSpecialHolidayHrs(totalNDSpecialHolidays);
                                //System.out.println("Grand Total SPECIAL HOLIDAY ND: "+totalNDSpecialHolidays);
                                erpDTRAssignment.setTotalOTSpecialHolidayHrs(totalOTSpecialHolidays);
                                //System.out.println("Grand Total SPECIAL HOLIDAY OT: "+totalOTSpecialHolidays);
                                erpDTRAssignment.setTotalOTExcessSpecialHolidayHrs(totalOTExcessSpecialHolidays);
                                //System.out.println("Grand Total SPECIAL HOLIDAY OT Excess: "+totalOTExcessSpecialHolidays);
                                erpDTRAssignment.setTotalSpecialHolidayHrs(totalWorkSpecialHolidays);
                                //System.out.println("Grand Total SPECIAL HOLIDAY Hours: "+totalWorkSpecialHolidays);
                                erpDTRAssignments.add(erpDTRAssignment);
                                //System.out.println("ASSIGNMENT FOR ["+erpDTRAssignment.getEmployeeAssigned().getFirstname()+" "+erpDTRAssignment.getEmployeeAssigned().getLastname()+"]");
                                //System.out.println("WorkDays: "+(y-2));
                                //System.out.println("Total workdays:"+workAssignment.getTotalWorkDay());

                                int individualManHours = totalDayHours + totalNightHours;
                                //System.out.println("Grand Total Individual Man Hours: "+individualManHours);
                                totalManHours += individualManHours;
                                //System.out.println("Workdays["+totalWorkDays+"] + totalSIL["+totalSIL+"] = TOTAL MAN Hours: "+totalManHours);
                                x++;
                            }

                            // get contracted man hours
                            /*List<ContractedManHours> contractedManHours = contractedManHoursService.getFilteredDetachmentAndPeriod(detachment, startDate, stopDate);
                            if(has(contractedManHours) && !contractedManHours.isEmpty()) {
                                ContractedManHours cmh = contractedManHours.get(0);
                                if((totalManHours != cmh.getTotalAdjustedManHours()) && (uploadReason == null  || uploadReason.isEmpty())) {
                                    addDetailMessage("FAILED UPLOAD", detachment.getName() + " Total Man Hours ("+totalManHours+") does not match with Contracted Man Hours ("+cmh.getTotalAdjustedManHours()+"). Please Specify Upload Reason.", FacesMessage.SEVERITY_ERROR);
                                    return;
                                } else {
                                    System.out.println("Upload REASON is set...");
                                    erpDTR.setUploadReason(uploadReason);
                                }
                                System.out.println("Contracted Man Hours execution done...");
                            }*/
                            Double contracted = getTotalContracted(detachment, startDate, stopDate);
                            //System.out.println("CONTRACTED: "+contracted);
                            if(!hasMatchedContractedManHours(detachment, totalManHours, startDate, stopDate) && (uploadReason == null  || uploadReason.isEmpty())) {
                                addDetailMessage("FAILED UPLOAD", detachment.getName() + " Total Man Hours ("+totalManHours+") does not match with Contracted Man Hours ("+contracted+"). Please Specify Upload Reason.", FacesMessage.SEVERITY_ERROR);
                                return;
                            } else {
                                System.out.println("Upload REASON is set...");
                                erpDTR.setUploadReason(uploadReason);
                            }

                            erpDTR.setAssignments(erpDTRAssignments);
                            //System.out.println("DTR ASSIGN: "+erpDTRAssignments.size());
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

    public boolean hasMatchedContractedManHours(ErpDetachment detachment, int totalManHours, Date start, Date stop) {
        boolean retVal = false;
        Double totalHours = getTotalContracted(detachment, start, stop);
        System.out.println("TotalManHours: "+Double.valueOf(totalManHours)+" vs Contracted: "+totalHours);
        if(Double.valueOf(totalManHours).equals(totalHours)) {
            retVal = true;
        }
        return retVal;
    }

    public Double getTotalContracted(ErpDetachment detachment, Date dtStart, Date dtStop) {
        Calendar start = Calendar.getInstance();
        start.setTime(dtStart);
        Calendar stop = Calendar.getInstance();
        stop.setTime(dtStop);
        List<TODClient> clients = todClientService.findByDetachment(detachment);
        Double totalHours = 0.0;
        for(TODClient c: clients) {
            List<TODPersonnelShift> shifts = todPersonnelShiftService.findByClient(c);
            for(TODPersonnelShift s: shifts) {
                //switch (noOfDays) {
                if(start.get(Calendar.DAY_OF_MONTH) == 1) {
                    totalHours += s.getTotalHrs1to15();
                } else if(start.get(Calendar.DAY_OF_MONTH) == 16 && stop.get(Calendar.DAY_OF_MONTH) == 28) {
                    totalHours += s.getTotalHrs16to28();
                } else if(start.get(Calendar.DAY_OF_MONTH) == 16 && stop.get(Calendar.DAY_OF_MONTH) == 29) {
                    totalHours += s.getTotalHrs16to29();
                } else if(start.get(Calendar.DAY_OF_MONTH) == 16 && stop.get(Calendar.DAY_OF_MONTH) == 30) {
                    totalHours += s.getTotalHrs16to30();
                } else if(start.get(Calendar.DAY_OF_MONTH) == 16 && stop.get(Calendar.DAY_OF_MONTH) == 31) {
                    totalHours += s.getTotalHrs16to31();
                }
                //    default:
                //}
            }
        }
        return totalHours;
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

    private ErpLocalHoliday getLocalHolidayByDate(List<ErpLocalHoliday> localHolidays, LocalDate d) {
        ErpLocalHoliday retVal = null;
        Date dt = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
        for(ErpLocalHoliday lh: localHolidays) {
            if(isDateEqual(lh.getHolidayDate(), dt)) {
                retVal = lh;
                break;
            }
        }
        return retVal;
    }

    private ErpHoliday getHolidayByDate(List<ErpHoliday> holidays, LocalDate d) {
        ErpHoliday retVal = null;
        Date dt = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
        for(ErpHoliday eh: holidays) {
            if(isDateEqual(eh.getHolidayDate(), dt)) {
                retVal = eh;
                break;
            }
        }
        return  retVal;
    }

    private boolean isDateEqual(Date src, Date comp) {
        SimpleDateFormat _format = new SimpleDateFormat("MM-dd");
        if(_format.format(src).equals(_format.format(comp))) {
            return true;
        }
        else {
            return false;
        }
    }

    private Long[] getTime(LocalDateTime dob, LocalDateTime now) {
        final int MINUTES_PER_HOUR = 60;
        final int SECONDS_PER_MINUTE = 60;
        final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        if (duration.isNegative()) {
            // Add 24 hours to correct for the negative duration
            duration = duration.plus(Duration.ofHours(24));
        }

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
        System.out.println("EMPLOYEE ID NOT MATCHED: "+employeeId);
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
            LocalDate startSched = localStart;
            LocalDate stopSched = localEnd;
            List<String> legends = new ArrayList<>();

            /*if(today.getDayOfMonth() <= 14) {
                startSched = LocalDate.of(today.getYear(), today.getMonth(), 16);
                LocalDate monthstart = LocalDate.of(today.getYear(),today.getMonth(),1);
                stopSched = monthstart.plusDays(monthstart.lengthOfMonth()-1);
            } else {
                startSched = LocalDate.of(today.getYear(), today.getMonth().plus(1), 1);
                stopSched = LocalDate.of(today.getYear(), today.getMonth().plus(1), 15);
            }*/

            ExcelUtils.initializeWithFilename("dtr_template.xlsx", "Sheet1");
            // locked style cells
            CellStyle locked = ExcelUtils.workbook.createCellStyle();
            ExcelUtils.setAllowCellsFormat();
            ExcelUtils.worksheet.protectSheet("3l1t3blue2010");
            locked.setLocked(true);
            int hoursShift = 12;
            for(ErpTimeSchedule itm: detachment.getErpTimeSchedules()) {
                LocalTime startTime = itm.getStartTime().toLocalTime();
                LocalTime endTime = itm.getEndTime().toLocalTime();
                Duration duration = Duration.between(startTime, endTime);
                if (duration.isNegative()) {
                    // Add 24 hours to correct for the negative duration
                    duration = duration.plus(Duration.ofHours(24));
                }
                long totalMinutes = duration.toMinutes();
                hoursShift = (int) (totalMinutes / 60);
            }
            ExcelUtils.setCell(0,2, hoursShift);
            ExcelUtils.setCell(0,1, detachment.getId());
            ExcelUtils.setCell(5,2, "DETACHMENT: "+detachment.getName());
            ExcelUtils.setCell(5,8, "AREA OF RESPONSIBILITY: "+detachment.getLocation().getLocation());
            ExcelUtils.setCell(2,3, currentUserUtil.getFullName());
            ExcelUtils.getCellStyle(2,3).setLocked(false);
            ExcelUtils.setCell(1,1, startSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.getCellStyle(1,1).setLocked(false);
            ExcelUtils.setCell(2,1, stopSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.getCellStyle(2,1).setLocked(false);
            ExcelUtils.getCellStyle(2,12).setLocked(false);
            if(has(legends) && legends.size() > 0) {
                legends.add(WorkSchedLegend.DO.name());
            }
            // reliever detachments
            List<ErpDetachment> relieverDetachments = currentUserUtil.getRelieverDetachments();
            int relieverDetachmentCount = 2;
            //System.out.println("RELIEVER COUNT: "+relieverDetachments.size());
            for(ErpDetachment rd: relieverDetachments) {
                //System.out.println("RELIEVER DET: "+rd.getName());
                if(relieverDetachmentCount <= 4) {
                    ExcelUtils.setCell(relieverDetachmentCount,27, rd.getName());
                    ExcelUtils.setCell(relieverDetachmentCount,33, rd.getId());
                    relieverDetachmentCount += 1;
                }
            }
            ExcelUtils.evaluateCell(5,0);
            ExcelUtils.evaluateCell(6,0);
            int a =11, b=0, c=0, d=0;
            // loop from start date to end date
            int x = generateDTRTable(detachment, startSched, stopSched, hasAssignedEmployees, hasSchedules, false,8);
            b = x;

            // copy rows
            int endRow = (x - 9) + 10;
            ExcelUtils.copyRange(7, 9, x+2);
            x = x + 3;
            c = x + 3;
            ExcelUtils.setCell(x, 0, "NIGHT SHIFT");
            x = generateDTRTable(detachment, startSched, stopSched, hasAssignedEmployees, hasSchedules, true, x);
            d = x;

            // time validator
            /*DataValidationHelper dvHelper = ExcelUtils.worksheet.getDataValidationHelper();
            DataValidationConstraint timeConstraint = dvHelper.createTimeConstraint(
                    DataValidationConstraint.OperatorType.BETWEEN, "0:00", "23:59");
            // set where the validation would occur
            System.out.println("b["+b+"] firstRange: "+ExcelUtils.getCellData(a, 2));
            int lastCol = 49;
            Cell lastCell = ExcelUtils.getCell(b, lastCol);
            if(lastCell == null || lastCell.getCellType() == null || lastCell.getCellType() == CellType.BLANK) {
                lastCol = 46;
                System.out.println("lastRange: "+ExcelUtils.getCellData(b, lastCol));
            } else {
                System.out.println("lastRangeElse: "+ExcelUtils.getCellData(b, lastCol));
            }
            CellRangeAddressList addressList1 = new CellRangeAddressList(a, b, 2, lastCol);
            DataValidation validation1 = dvHelper.createValidation(timeConstraint, addressList1);
            CellRangeAddressList addressList2 = new CellRangeAddressList(c, d, 2, lastCol);
            DataValidation validation2 = dvHelper.createValidation(timeConstraint, addressList2);

            // Set error message
            validation1.createErrorBox("Error", "Invalid input! Please enter time in 24-hour format.");
            validation1.setShowErrorBox(true);
            validation2.createErrorBox("Error", "Invalid input! Please enter time in 24-hour format.");
            validation2.setShowErrorBox(true);

            // Add the data validation to the sheet
            ExcelUtils.worksheet.addValidationData(validation1);
            ExcelUtils.worksheet.addValidationData(validation2);*/

            CellStyle oldStyle = ExcelUtils.getCellStyle(0, 0);
            XSSFCellStyle s = ExcelUtils.getWorkBook().createCellStyle();
            s.cloneStyleFrom(oldStyle);
            XSSFFont font = ExcelUtils.getWorkBook().createFont();
            font.setBold(true);
            font.setFamily(s.getFont().getFamily());
            font.setItalic(s.getFont().getItalic());
            font.setFontHeightInPoints(s.getFont().getFontHeightInPoints());
            font.setColor(XSSFFont.COLOR_RED);
            s.setFont(font);
            ExcelUtils.setCell(x+2, 50, "Total Hours:", s);
            String sumFormula = "SUM(AY"+a+":AY"+b+", AY"+c+":AY"+d+")";
            System.out.println("FORMULA: "+sumFormula);
            ExcelUtils.setCellFormula(x+2, 51, sumFormula, s);

            String detachmentName = detachment.getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "DTR_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            ExcelUtils.workbook.close();
            //FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-temtemplatesplates.xhtml");
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-.xhtml");
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

        System.out.println("ASSIGNED EMPLOYEES: "+hasAssignedEmployees+" SCHED: "+hasSchedules);
        if(hasAssignedEmployees && hasSchedules){
            List<ErpEmployee> employees = new ArrayList<>(detachment.getAssignedEmployees());
            employees = employees.stream().filter(itm -> itm.getStatus().equals(EmployeeStatus.HIRED)).collect(Collectors.toList());
            employees.sort(Comparator.comparing(ErpEmployee::getLastname));
            x += 2;
            for(ErpEmployee emp: employees) {
                // if employee is not hired do not include
                /*if(!emp.getStatus().equals(EmployeeStatus.HIRED)) {
                    continue;
                }*/
                //System.out.println("X["+x+"] EMPLOYEE: "+emp.getFirstname() + " " + emp.getLastname()+" "+emp.getStatus().name());
                //ExcelUtils.setCell(x, 0, x-9);
                if(has(emp.getFirstname()) && has(emp.getLastname())) {
                    ExcelUtils.setCell(x, 0, emp.getLastname() + ", " + emp.getFirstname());
                }
                y = 2;
                z = 0;
                for (LocalDate date = startSched; (date.isBefore(stopSched) || date.isEqual(stopSched)); date = date.plusDays(1)) {
                    CellStyle style = ExcelUtils.workbook.createCellStyle();
                    style.setAlignment(HorizontalAlignment.CENTER);
                    //System.out.println("x: "+x+" y: "+y);
                    ExcelUtils.setCellTime(x, y, "00:00:00");
                    ExcelUtils.setCellTime(x, y+1, "00:00:00");
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
            // totals
        }

        return x;
    }

    public void generateFile(Workbook workbook, String fileName) throws IOException {
        final FacesContext fc = FacesContext.getCurrentInstance();
        final ExternalContext externalContext = fc.getExternalContext();
        System.out.println("FILENAME: "+fileName);

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

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date
                .from(dateToConvert.atStartOfDay(ZoneId.systemDefault())
                        .toInstant());
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
