package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.*;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import io.eliteblue.erp.core.util.ExcelUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.beans.factory.annotation.Autowired;

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
public class ErpDTRForm implements Serializable {

    private final String DAYOFF_COLOR = "FFFFC000";
    private final String ABSENT_COLOR = "FFFF0000";
    private final String SIL_COLOR = "FF00B050";
    private final String BTR_COLOR = "FFED7D31";
    private final String CCTV_COLOR = "FF7030A0";
    private final String SG_COLOR = "FF00B0F0";

    private final String[] columns = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"};

    @Autowired
    private ErpDTRService erpDTRService;

    @Autowired
    private ErpDTRAssignmentService erpDTRAssignmentService;

    @Autowired
    private ErpDTRScheduleService erpDTRScheduleService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private ErpEmployeeService employeeService;

    @Autowired
    private ErpSilService silService;

    private Long id;
    private Long detachmentId;
    private ErpDetachment detachment;
    private ErpDTR erpDTR;
    private Map<String, Long> detachments;

    private List<ErpDTRAssignment> filteredErpDTRAssignment;
    private List<ErpDTRAssignment> erpDTRAssignments;
    private ErpDTRAssignment selectedDTRAssignment;

    private List<String> workingDays;
    private LocalDate startDate;
    private LocalDate stopDate;

    private int hoursShift;

    private WorkSchedLegend legend;

    public void init() {
        workingDays = new ArrayList<>();
        legend = WorkSchedLegend.DO;
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpDTR = erpDTRService.findById(Long.valueOf(id));
            detachment = erpDTR.getErpDetachment();
            detachmentId = detachment.getId();
            detachment.getErpTimeSchedules().forEach(itm -> {
                LocalTime startTime = itm.getStartTime().toLocalTime();
                LocalTime endTime = itm.getEndTime().toLocalTime();
                Duration duration = Duration.between(startTime, endTime);
                if (duration.isNegative()) {
                    // Add 24 hours to correct for the negative duration
                    duration = duration.plus(Duration.ofHours(24));
                }
                long totalMinutes = duration.toMinutes();
                hoursShift = (int) (totalMinutes / 60);
                //hoursShift = duration.toHoursPart();
                //System.out.println("start["+startTime+"] - end["+endTime+"] hourShift: "+hoursShift);
            });
            if(erpDTR != null && erpDTR.getAssignments() != null && erpDTR.getAssignments().size() > 0) {
                erpDTRAssignments = new ArrayList<>(erpDTR.getAssignments());
                DateFormat format = new SimpleDateFormat("dd");
                erpDTRAssignments.sort(Comparator.comparing((ErpDTRAssignment ewa) -> ewa.getEmployeeAssigned().getLastname()));
                for(ErpDTRAssignment wa: erpDTRAssignments) {
                    List<ErpDTRSchedule> schedules = new ArrayList<>(wa.getSchedules());
                    schedules.sort(Comparator.comparing((ErpDTRSchedule wd) -> wd.getDayShiftStart()));
                    wa.setSchedules(new LinkedHashSet<>(schedules));
                }
            }
            if(has(erpDTR.getCutoffStart()) && has(erpDTR.getCutoffEnd())) {
                startDate = convertToLocalDateViaInstant(erpDTR.getCutoffStart());
                stopDate = convertToLocalDateViaInstant(erpDTR.getCutoffEnd());
                for (LocalDate date = startDate; (date.isBefore(stopDate) || date.isEqual(stopDate)); date = date.plusDays(1)) {
                    workingDays.add(""+date.getDayOfMonth());
                }
            }
        } else {
            erpDTR = new ErpDTR();
            detachment = new ErpDetachment();
            if(has(detachmentId)) {
                detachment = erpDetachmentService.findById(detachmentId);
                erpDTR.setErpDetachment(detachment);
            }
        }
        detachments = new HashMap<>();
        for(ErpDetachment edp: erpDetachmentService.getAllCurrentDetachment()) {
            detachments.put(edp.getName(), edp.getId());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkSchedLegend getLegend() {
        return legend;
    }

    public void setLegend(WorkSchedLegend legend) {
        this.legend = legend;
    }

    public ErpDTR getErpDTR() {
        return erpDTR;
    }

    public void setErpDTR(ErpDTR erpDTR) {
        this.erpDTR = erpDTR;
    }

    public Long getDetachmentId() {
        return detachmentId;
    }

    public void setDetachmentId(Long detachmentId) {
        this.detachmentId = detachmentId;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public List<ErpDTRAssignment> getFilteredErpDTRAssignment() {
        return filteredErpDTRAssignment;
    }

    public void setFilteredErpDTRAssignment(List<ErpDTRAssignment> filteredErpDTRAssignment) {
        this.filteredErpDTRAssignment = filteredErpDTRAssignment;
    }

    public List<ErpDTRAssignment> getErpDTRAssignments() {
        return erpDTRAssignments;
    }

    public void setDTRAssignments(List<ErpDTRAssignment> erpDTRAssignments) {
        this.erpDTRAssignments = erpDTRAssignments;
    }

    public ErpDTRAssignment getSelectedDTRAssignment() {
        return selectedDTRAssignment;
    }

    public void setSelectedDTRAssignment(ErpDTRAssignment selectedDTRAssignment) {
        this.selectedDTRAssignment = selectedDTRAssignment;
    }

    public List<String> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDate stopDate) {
        this.stopDate = stopDate;
    }

    public int getHoursShift() {
        return hoursShift;
    }

    public void setHoursShift(int hoursShift) {
        this.hoursShift = hoursShift;
    }

    public void onRowSelect(SelectEvent<ErpWorkAssignment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-assn-form.xhtml?id="+selectedDTRAssignment.getId()+"&dtrId="+id);
    }

    public void onRowUnselect(UnselectEvent<ErpWorkAssignment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-assn-form.xhtml?id="+selectedDTRAssignment.getId()+"&dtrId="+id);
    }

    public String backBtnPressed() { return "dtr-list?faces-redirect=true&includeViewParams=true"; }

    public String newAssignmentPressed() {
        return "dtr-assn-form?dtrId="+id+"faces-redirect=true&includeViewParams=true";
    }

    public String approve() {
        if(erpDTR != null) {
            for(ErpDTRAssignment assn: erpDTR.getAssignments()) {
                if(has(assn.getTotalSIL()) && assn.getTotalSIL() > 0) {
                    // subtract SIL in employee
                    Double empSIL = assn.getEmployeeAssigned().getSil();
                    assn.getEmployeeAssigned().setSil(empSIL - assn.getTotalSIL());
                    employeeService.save(assn.getEmployeeAssigned());
                    // save SIL in database
                    ErpSil sil = new ErpSil();
                    sil.setNoDaysAvailed(0.0+assn.getTotalSIL());
                    Double basic = assn.getEmployeeAssigned().getSalaryRate();
                    if(has(basic) && basic > 0) {
                        sil.setAmountAvailed(basic * sil.getNoDaysAvailed());
                        if(has(assn.getEmployeeAssigned().getSil()) && assn.getEmployeeAssigned().getSil() > 0) {
                            sil.setAmountUnAvailed(basic * assn.getEmployeeAssigned().getSil());
                        } else {
                            sil.setAmountUnAvailed(0.0);
                        }
                    }
                    else {
                        sil.setAmountAvailed(0.0);
                    }
                    if(has(assn.getDateAvailedStart())) {
                        sil.setDateAvailedStart(assn.getDateAvailedStart());
                        Calendar cal = Calendar.getInstance();
                        Calendar nCal = Calendar.getInstance();
                        cal.setTime(assn.getDateAvailedStart());
                        int days = cal.get(Calendar.DAY_OF_MONTH);
                        if(sil.getNoDaysAvailed() > 1) {
                            days += sil.getNoDaysAvailed() - 1;
                        }
                        nCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), days, 0, 0);
                        sil.setDateAvailedStop(nCal.getTime());
                    }
                    sil.setDateOfPaymentStart(erpDTR.getCutoffStart());
                    sil.setDateOfPaymentStop(erpDTR.getCutoffEnd());
                    sil.setModeOfPayment(ModeOfPayment.PAYROLL);
                    sil.setNoDaysUnAvailed(assn.getEmployeeAssigned().getSil());
                    if(has(sil.getNoDaysUnAvailed()) && sil.getNoDaysUnAvailed() > 0) {
                        sil.setAmountUnAvailed(sil.getNoDaysUnAvailed() * basic);
                    } else {
                        sil.setAmountUnAvailed(0.0);
                    }
                    if(has(assn.getEmployeeAssigned().getJoinedDate())) {
                        sil.setDateCoveredStart(getCoveredStart(assn.getEmployeeAssigned().getJoinedDate()));
                        sil.setDateCoveredStop(getCoveredStop(assn.getEmployeeAssigned().getJoinedDate()));
                        //System.out.println("COVERED START: "+sil.getDateCoveredStart());
                        //System.out.println("COVERED STOP: "+sil.getDateCoveredStop());
                    }
                    sil.setEmployee(assn.getEmployeeAssigned());
                    silService.save(sil);

                }
            }
            erpDTR.setStatus(ApprovalStatus.APPROVED);
            erpDTRService.save(erpDTR);
            // change other approved STATUS to PENDING
            List<ErpDTR> approvedDTRs = erpDTRService.getAllDetachmentFilteredStartAndEndDate(detachment, erpDTR.getCutoffStart(), erpDTR.getCutoffEnd());
            for(ErpDTR apd: approvedDTRs) {
               if(!apd.getId().equals(erpDTR.getId())){
                   apd.setStatus(ApprovalStatus.PENDING);
                   //System.out.println("erpDTR ID: "+erpDTR.getId()+" changed to PENDING");
                   erpDTRService.save(apd);
               }
            }
        }
        return "dtr-list?faces-redirect=true&includeViewParams=true";
    }

    public Date getCoveredStart(Date joinedDate) {
        Date retVal;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.setTime(joinedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),0, 0);
        retVal = calendar.getTime();
        return retVal;
    }

    public Date getCoveredStop(Date joinedDate) {
        Date retVal;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        year += 1;
        cal.setTime(joinedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
        retVal = calendar.getTime();
        return retVal;
    }

    public String reject() {
        if(erpDTR != null) {
            erpDTR.setStatus(ApprovalStatus.REJECTED);
            erpDTRService.save(erpDTR);
        }
        return "dtr-list?faces-redirect=true&includeViewParams=true";
    }

    public void clear() {
        erpDTR = new ErpDTR();
        id = null;
    }

    public boolean isNew() {
        return erpDTR == null || erpDTR.getId() == null;
    }

    public void remove() throws Exception {
        if(has(erpDTR) && has(erpDTR.getId())) {
            erpDTRService.delete(erpDTR);
            addDetailMessage("DTR DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-list.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpDTR != null) {
            Set<ErpDTRAssignment> dtrAssignments = new HashSet<>();
            Set<ErpDTRSchedule> schedules = new HashSet<>();

            if(has(detachment.getId())) {
                detachment = erpDetachmentService.findById(detachment.getId());
                erpDTR.setErpDetachment(detachment);
                erpDTR.setStatus(ApprovalStatus.PENDING);
                if(has(detachment) && erpDTR.getId() == null) {
                    // generate work assignment
                    if(has(detachment.getAssignedEmployees()) && detachment.getAssignedEmployees().size() > 0) {
                        for(ErpEmployee emp: detachment.getAssignedEmployees()) {
                            ErpDTRAssignment dtrAssignment = new ErpDTRAssignment();
                            dtrAssignment.setEmployeeAssigned(emp);
                            dtrAssignment.setErpDTR(erpDTR);

                            // generate workdays
                            LocalDate _startDate = convertToLocalDateViaInstant(erpDTR.getCutoffStart());
                            LocalDate _stopDate = convertToLocalDateViaInstant(erpDTR.getCutoffEnd());
                            for (LocalDate date = _startDate; (date.isBefore(_stopDate) || date.isEqual(_stopDate)); date = date.plusDays(1)) {
                                ErpDTRSchedule dtrSchedule = new ErpDTRSchedule();
                                dtrSchedule.setErpDTRAssignment(dtrAssignment);
                                if(has(detachment.getErpTimeSchedules()) && detachment.getErpTimeSchedules().size() > 0) {
                                    ErpTimeSchedule timeSchedule = detachment.getErpTimeSchedules().iterator().next();
                                    LocalDateTime ldt_start = date.atTime(timeSchedule.getStartTime().toLocalTime());
                                    LocalDateTime ldt_stop = date.atTime(timeSchedule.getEndTime().toLocalTime());
                                    dtrSchedule.setDayShiftStart(convertToDateViaInstant(ldt_start));
                                    dtrSchedule.setDayShiftEnd(convertToDateViaInstant(ldt_stop));
                                    if(timeSchedule.getStartTime().toLocalTime().getHour() < 12) {
                                        dtrSchedule.setAttendance(AttendanceType.DAY_SHIFT);
                                    } else {
                                        dtrSchedule.setAttendance(AttendanceType.NIGHT_SHIFT);
                                    }
                                    dtrSchedule.setTotalHours(0);
                                }
                                schedules.add(dtrSchedule);
                            }
                            dtrAssignment.setSchedules(schedules);
                            dtrAssignment.setTotalWorkHours(0);
                            dtrAssignments.add(dtrAssignment);
                        }
                    }
                    erpDTR.setAssignments(dtrAssignments);
                }
            }
            erpDTRService.save(erpDTR);
            for(ErpDTRAssignment wa: dtrAssignments) {
                erpDTRAssignmentService.save(wa);
            }
            for(ErpDTRSchedule wd: schedules) {
                erpDTRScheduleService.save(wd);
            }
            addDetailMessage("DTR SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("dtr-list.xhtml");
        }
    }

    public void downloadFile() throws Exception {
        if(has(erpDTR) && has(detachmentId)) {
            ErpDetachment det = erpDetachmentService.findById(detachmentId);
            boolean hasAssignedEmployees = (has(det) && det.getAssignedEmployees() != null && det.getAssignedEmployees().size() > 0);
            boolean hasSchedules = (has(det) && det.getErpTimeSchedules() != null && det.getErpTimeSchedules().size() > 0);
            LocalDate today = LocalDate.now();
            LocalDate startSched = convertToLocalDateViaInstant(erpDTR.getCutoffStart());
            LocalDate stopSched = convertToLocalDateViaInstant(erpDTR.getCutoffEnd());
            List<String> legends = new ArrayList<>();

            ExcelUtils.initializeWithFilename("dtr_template.xlsx", "Sheet1");
            // locked style cells
            CellStyle locked = ExcelUtils.workbook.createCellStyle();
            ExcelUtils.setAllowCellsFormat();
            ExcelUtils.worksheet.protectSheet("3l1t3blue2010");
            locked.setLocked(true);
            ExcelUtils.setCell(0,1, detachmentId);
            ExcelUtils.setCell(5,2, "DETACHMENT: "+det.getName());
            ExcelUtils.setCell(5,8, "AREA OF RESPONSIBILITY: "+det.getLocation().getLocation());
            ExcelUtils.setCell(2,3, erpDTR.getCreatedBy());
            ExcelUtils.getCellStyle(2,3).setLocked(false);
            ExcelUtils.setCell(1,1, startSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.getCellStyle(1,1).setLocked(false);
            ExcelUtils.setCell(2,1, stopSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.getCellStyle(2,1).setLocked(false);
            ExcelUtils.getCellStyle(2,12).setLocked(false);
            if(has(erpDTR.getUploadReason())) {
                ExcelUtils.setCell(2,12, erpDTR.getUploadReason());
            }
            if(has(legends) && legends.size() > 0) {
                legends.add(WorkSchedLegend.DO.name());
            }
            ExcelUtils.evaluateCell(5,0);
            ExcelUtils.evaluateCell(6,0);
            int a =11, b=0, c=0, d=0;
            // loop from start date to end date
            int x = generateDTRTable(det, startSched, stopSched, hasAssignedEmployees, hasSchedules, false,8);
            b = x;

            // copy rows
            int endRow = (x - 9) + 10;
            ExcelUtils.copyRange(7, 9, x+2);
            x = x + 3;
            c = x + 3;
            ExcelUtils.setCell(x, 0, "NIGHT SHIFT");
            x = generateDTRTable(det, startSched, stopSched, hasAssignedEmployees, hasSchedules, true, x);
            d = x;

            CellStyle oldStyle = ExcelUtils.getCellStyle(10, 0);
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
            //System.out.println("FORMULA: "+sumFormula);
            ExcelUtils.setCellFormula(x+2, 51, sumFormula, s);

            String detachmentName = detachment.getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "DTR_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            ExcelUtils.workbook.close();
        } else {
            addDetailMessage("DOWNLOAD FAILED", "COULD NOT DOWNLOAD FILE", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().redirect("ws-download-templates.xhtml");
        }
    }

    public void downloadShiftSummary() throws Exception {
        if(has(erpDTR) && has(detachmentId)) {
            ErpDetachment det = erpDetachmentService.findById(detachmentId);
            boolean hasAssignedEmployees = (has(det) && det.getAssignedEmployees() != null && det.getAssignedEmployees().size() > 0);
            boolean hasSchedules = (has(det) && det.getErpTimeSchedules() != null && det.getErpTimeSchedules().size() > 0);
            LocalDate today = LocalDate.now();
            LocalDate startSched = convertToLocalDateViaInstant(erpDTR.getCutoffStart());
            LocalDate stopSched = convertToLocalDateViaInstant(erpDTR.getCutoffEnd());
            List<String> legends = new ArrayList<>();

            ExcelUtils.initializeWithFilename("shift_summary.xlsx", "Sheet1");
            // locked style cells
            CellStyle locked = ExcelUtils.workbook.createCellStyle();
            ExcelUtils.setAllowCellsFormat();
            ExcelUtils.worksheet.protectSheet("3l1t3blue2010");
            locked.setLocked(true);
            ExcelUtils.setCell(0,1, detachmentId);
            ExcelUtils.setCell(5,2, "DETACHMENT: "+det.getName());
            ExcelUtils.setCell(5,8, "AREA OF RESPONSIBILITY: "+det.getLocation().getLocation());
            ExcelUtils.setCell(2,3, erpDTR.getCreatedBy());
            ExcelUtils.getCellStyle(2,3).setLocked(false);
            ExcelUtils.setCell(1,1, startSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.getCellStyle(1,1).setLocked(false);
            ExcelUtils.setCell(2,1, stopSched.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            ExcelUtils.getCellStyle(2,1).setLocked(false);
            ExcelUtils.getCellStyle(2,12).setLocked(false);
            int x = 10;
            //System.out.println("HAS-EMP ["+hasAssignedEmployees+"] HAS-SCHED ["+hasSchedules+"]");
            if(hasAssignedEmployees && hasSchedules){
                for(ErpDTRAssignment assn: erpDTRAssignments) {
                    ErpEmployee emp = assn.getEmployeeAssigned();
                    //System.out.println("X["+x+"] EMPLOYEE: "+emp.getFirstname() + " " + emp.getLastname()+" "+emp.getStatus().name());
                    if(has(emp.getFirstname()) && has(emp.getLastname())) {
                        ExcelUtils.setCell(x, 0, emp.getLastname() + ", " + emp.getFirstname());
                    }
                    ExcelUtils.setCell(x, 2, assn.getTotalWorkDays());
                    ExcelUtils.setCell(x, 3, assn.getTotalSIL());
                    ExcelUtils.setCell(x, 4, assn.getTotalBTRDays());
                    ExcelUtils.setCell(x, 5, assn.getTotalBasicWorkDays());
                    ExcelUtils.setCell(x, 6, assn.getTotalBasicHours());
                    ExcelUtils.setCell(x, 7, assn.getTotalBasicOTHours());
                    ExcelUtils.setCell(x, 8, assn.getTotalBasicOTExcessHours());
                    ExcelUtils.setCell(x, 9, assn.getTotalBasicNDHours() / 8);

                    ExcelUtils.setCell(x, 10, assn.getTotalRegularHolidayHrs() / 8);
                    ExcelUtils.setCell(x, 11, assn.getTotalRegularHolidayHrs());
                    ExcelUtils.setCell(x, 12, assn.getTotalOTRegularHolidayHrs());
                    ExcelUtils.setCell(x, 13, assn.getTotalOTExcessRegularHolidayHrs());
                    ExcelUtils.setCell(x, 14, assn.getTotalNDRegularHolidayHrs() / 8);

                    ExcelUtils.setCell(x, 15, assn.getTotalSpecialHolidayHrs() / 8);
                    ExcelUtils.setCell(x, 16, assn.getTotalSpecialHolidayHrs());
                    ExcelUtils.setCell(x, 17, assn.getTotalOTSpecialHolidayHrs());
                    ExcelUtils.setCell(x, 18, assn.getTotalOTExcessSpecialHolidayHrs());
                    ExcelUtils.setCell(x, 19, assn.getTotalNDSpecialHolidayHrs() / 8);
                    x++;
                }
            }

            String detachmentName = detachment.getName().replaceAll(" ", "_");
            if(detachmentName.length() > 8) {
                detachmentName = detachmentName.substring(0, 7);
            }
            generateFile(ExcelUtils.workbook, "SUMMARY_"+detachmentName+today.format(DateTimeFormatter.ofPattern("MMddyyyy")));
            ExcelUtils.workbook.close();
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

        //System.out.println("ASSIGNED EMPLOYEES: "+hasAssignedEmployees+" SCHED: "+hasSchedules);
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
                        // set foreground color according to Attendance Type
                        //
                        //    NEEDS TO BE FIXED ON UPLOAD DTR IT ALWAYS RECORD NIGHT_SHIFT OR DAY_SHIFT
                        //
                        //System.out.println("ATTENDANCE: "+sched.getAttendance().name());
                        XSSFCellStyle attendanceTypeStyle = getAttendanceTypeColor(sched.getAttendance());
                        CellStyle attendanceStyle = ExcelUtils.workbook.createCellStyle();
                        if(attendanceTypeStyle != null) {
                            attendanceStyle.setFillForegroundColor(attendanceTypeStyle.getFillForegroundColor());
                            attendanceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        }
                        ExcelUtils.setCellTime(x, y, _format.format(_currStart), attendanceStyle);
                        ExcelUtils.setCellTime(x, y+1, _format.format(_currEnd), attendanceStyle);
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

    public XSSFCellStyle getAttendanceTypeColor(AttendanceType type) {
        XSSFCellStyle retVal = null;
        try {
            if (type.equals(AttendanceType.DAY_OFF)) {
                retVal = (XSSFCellStyle) ExcelUtils.getCellStyle(1, 8);
                return retVal;
            } else if (type.equals(AttendanceType.ABSENT)) {
                retVal = (XSSFCellStyle) ExcelUtils.getCellStyle(2, 8);
                return retVal;
            } else if (type.equals(AttendanceType.SIL)) {
                retVal = (XSSFCellStyle) ExcelUtils.getCellStyle(3, 8);
                return retVal;
            } else if (type.equals(AttendanceType.BTR)) {
                retVal = (XSSFCellStyle) ExcelUtils.getCellStyle(1, 9);
                return retVal;
            } else if (type.equals(AttendanceType.CCTV_OP)) {
                retVal = (XSSFCellStyle) ExcelUtils.getCellStyle(2, 9);
                return retVal;
            } else if (type.equals(AttendanceType.SG)) {
                retVal = (XSSFCellStyle) ExcelUtils.getCellStyle(3, 9);
                return retVal;
            } else {
                return retVal;
            }
        } catch (Exception e) {
            return retVal;
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

    public String convertStartDate(String pattern) {
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
}
