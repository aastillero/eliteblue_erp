package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.AttendanceType;
import io.eliteblue.erp.core.constants.WorkSchedLegend;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpDTRForm implements Serializable {

    @Autowired
    private ErpDTRService erpDTRService;

    @Autowired
    private ErpDTRAssignmentService erpDTRAssignmentService;

    @Autowired
    private ErpDTRScheduleService erpDTRScheduleService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

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
        for(ErpDetachment edp: erpDetachmentService.getAllFilteredLocation()) {
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
            erpDTR.setStatus(ApprovalStatus.APPROVED);
            erpDTRService.save(erpDTR);
        }
        return "dtr-list?faces-redirect=true&includeViewParams=true";
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
}
