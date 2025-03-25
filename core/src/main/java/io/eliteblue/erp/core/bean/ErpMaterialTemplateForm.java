package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.AttendanceType;
import io.eliteblue.erp.core.constants.HolidayType;
import io.eliteblue.erp.core.constants.WorkSchedLegend;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import io.eliteblue.erp.core.util.ExcelUtils;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpMaterialTemplateForm implements Serializable {

    private final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    private final String DAYOFF_COLOR = "FFFFC000";
    private final String ABSENT_COLOR = "FFFF0000";
    private final String SIL_COLOR = "FF00B050";
    private final String[] columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpMaterialRequestService erpMaterialRequestService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private List<ErpDetachment> detachments;

    private UploadedFile file;

    private LocalDate localStart;

    @PostConstruct
    public void init() {
        detachments = erpDetachmentService.getAllCurrentDetachment();
        LocalDate today = LocalDate.now();
        localStart = LocalDate.now();
        if(today.getDayOfMonth() <= 14) {
            //localStart = LocalDate.of(today.getYear(), today.getMonth().minus(1), 16);
            LocalDate monthstart = LocalDate.of(today.getYear(),today.getMonth().minus(1),1);
        } else {
            localStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
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

    public String backBtnPressed() { return "dtr-list?faces-redirect=true&includeViewParams=true"; }

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

            ExcelUtils.initializeWithFilename("material_req.xlsx", "Sheet1");
            ExcelUtils.setCell(0,1, detachment.getId());
            ExcelUtils.setCell(2,1, detachment.getName());
            ExcelUtils.setCell(1,1, today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.setCell(3,1, currentUserUtil.getFullName());

            // Define the list of values for the dropdown
            String[] values = {"Option 1", "Option 2", "Option 3"};

            CellRangeAddressList addressList = new CellRangeAddressList(7, 7, 0, 0);
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) ExcelUtils.worksheet);
            DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(values);
            DataValidation dataValidation = dvHelper.createValidation(dvConstraint, addressList);

            // Make sure the validation includes the dropdown arrow
            dataValidation.setSuppressDropDownArrow(true);

            ExcelUtils.worksheet.addValidationData(dataValidation);


            String detachmentName = detachment.getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "MATERIAL_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            ExcelUtils.workbook.close();
            //FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-templates.xhtml");
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-templates.xhtml");
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
