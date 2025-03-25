package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ERP_DTR_ASSIGNMENT")
public class ErpDTRAssignment extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dtr_assn_seq")
    @SequenceGenerator(name = "dtr_assn_seq", sequenceName = "dtr_assn_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ASSN_EMP_ID", referencedColumnName = "id")
    @NotNull
    private ErpEmployee employeeAssigned;

    @ManyToOne
    @JoinColumn(name = "dtr_id", nullable = false)
    private ErpDTR erpDTR;

    @OneToMany(mappedBy = "erpDTRAssignment", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpDTRSchedule> schedules;

    @Column(name = "TOTAL_WORK_HOURS")
    private Integer totalWorkHours;

    @Column(name = "TOTAL_WORK_DAYS")
    private Integer totalWorkDays;

    @Column(name = "TOTAL_HOURS_NIGHT")
    private Integer totalHoursNight;

    @Column(name = "TOTAL_HOURS_DAY")
    private Integer totalHoursDay;

    @Column(name = "TOTAL_BASIC_HOURS")
    private Integer totalBasicHours;

    @Column(name = "TOTAL_BASIC_OT_HOURS")
    private Integer totalBasicOTHours;

    @Column(name = "TOTAL_BASIC_OTEXCESS_HOURS")
    private Integer totalBasicOTExcessHours;

    @Column(name = "TOTAL_BASIC_ND_HOURS")
    private Integer totalBasicNDHours;

    @Column(name = "TOTAL_BASIC_WORK_DAYS")
    private Integer totalBasicWorkDays;

    @Column(name = "TOTAL_OT_HOURS")
    private Integer totalOTHours;

    @Column(name = "TOTAL_OTEXCESS_HOURS")
    private Integer totalOTExcess;

    @Column(name = "TOTAL_ND_HOURS")
    private Integer totalNDHours;

    @Column(name = "TOTAL_REG_HOLIDAY_HOURS")
    private Integer totalRegularHolidayHrs;

    @Column(name = "TOTAL_OT_REG_HOLIDAY_HOURS")
    private Integer totalOTRegularHolidayHrs;

    @Column(name = "TOTAL_OTEXCESS_REG_HOLIDAY_HOURS")
    private Integer totalOTExcessRegularHolidayHrs;

    @Column(name = "TOTAL_ND_REG_HOLIDAY_HOURS")
    private Integer totalNDRegularHolidayHrs;

    @Column(name = "TOTAL_SPECIAL_HOLIDAY_HOURS")
    private Integer totalSpecialHolidayHrs;

    @Column(name = "TOTAL_OT_SPECIAL_HOLIDAY_HOURS")
    private Integer totalOTSpecialHolidayHrs;

    @Column(name = "TOTAL_OTEXCESS_SPECIAL_HOLIDAY_HOURS")
    private Integer totalOTExcessSpecialHolidayHrs;

    @Column(name = "TOTAL_ND_SPECIAL_HOLIDAY_HOURS")
    private Integer totalNDSpecialHolidayHrs;

    @Column(name = "TOTAL_BTR_HOURS")
    private Integer totalBTRHours;

    @Column(name = "TOTAL_BTR_DAYS")
    private Integer totalBTRDays;

    @Column(name = "TOTAL_SG_DAYS")
    private Integer totalSGDays;

    @Column(name = "TOTAL_ABSENT_DAYS")
    private Integer totalAbsentDays;

    @Column(name = "TOTAL_CCTV_DAYS")
    private Integer totalCCTVDays;

    @Column(name = "TOTAL_SIL")
    private Integer totalSIL;

    @Column(name = "HAS_RESTDAYPAY")
    private Boolean hasRdp;

    @Column(name = "TOTAL_RDP")
    private Integer totalRdp;

    @Column(name = "DATE_AVAILED_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAvailedStart;

    @OneToMany(mappedBy = "erpDTRAssignment", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<RelieverDTRAssignment> relieverDTRAssignments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpEmployee getEmployeeAssigned() {
        return employeeAssigned;
    }

    public void setEmployeeAssigned(ErpEmployee employeeAssigned) {
        this.employeeAssigned = employeeAssigned;
    }

    public ErpDTR getErpDTR() {
        return erpDTR;
    }

    public void setErpDTR(ErpDTR erpDTR) {
        this.erpDTR = erpDTR;
    }

    public Set<ErpDTRSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<ErpDTRSchedule> schedules) {
        this.schedules = schedules;
    }

    public Integer getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(Integer totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public Integer getTotalWorkDays() {
        return totalWorkDays;
    }

    public void setTotalWorkDays(Integer totalWorkDays) {
        this.totalWorkDays = totalWorkDays;
    }

    public Integer getTotalHoursNight() {
        return totalHoursNight;
    }

    public void setTotalHoursNight(Integer totalHoursNight) {
        this.totalHoursNight = totalHoursNight;
    }

    public Integer getTotalHoursDay() {
        return totalHoursDay;
    }

    public void setTotalHoursDay(Integer totalHoursDay) {
        this.totalHoursDay = totalHoursDay;
    }

    public Integer getTotalBasicHours() {
        return totalBasicHours;
    }

    public void setTotalBasicHours(Integer totalBasicHours) {
        this.totalBasicHours = totalBasicHours;
    }

    public Integer getTotalOTHours() {
        return totalOTHours;
    }

    public void setTotalOTHours(Integer totalOTHours) {
        this.totalOTHours = totalOTHours;
    }

    public Integer getTotalOTExcess() {
        return totalOTExcess;
    }

    public void setTotalOTExcess(Integer totalOTExcess) {
        this.totalOTExcess = totalOTExcess;
    }

    public Integer getTotalNDHours() {
        return totalNDHours;
    }

    public void setTotalNDHours(Integer totalNDHours) {
        this.totalNDHours = totalNDHours;
    }

    public Integer getTotalRegularHolidayHrs() {
        return totalRegularHolidayHrs;
    }

    public void setTotalRegularHolidayHrs(Integer totalRegularHolidayHrs) {
        this.totalRegularHolidayHrs = totalRegularHolidayHrs;
    }

    public Integer getTotalOTRegularHolidayHrs() {
        return totalOTRegularHolidayHrs;
    }

    public void setTotalOTRegularHolidayHrs(Integer totalOTRegularHolidayHrs) {
        this.totalOTRegularHolidayHrs = totalOTRegularHolidayHrs;
    }

    public Integer getTotalOTExcessRegularHolidayHrs() {
        return totalOTExcessRegularHolidayHrs;
    }

    public void setTotalOTExcessRegularHolidayHrs(Integer totalOTExcessRegularHolidayHrs) {
        this.totalOTExcessRegularHolidayHrs = totalOTExcessRegularHolidayHrs;
    }

    public Integer getTotalNDRegularHolidayHrs() {
        return totalNDRegularHolidayHrs;
    }

    public void setTotalNDRegularHolidayHrs(Integer totalNDRegularHolidayHrs) {
        this.totalNDRegularHolidayHrs = totalNDRegularHolidayHrs;
    }

    public Integer getTotalSpecialHolidayHrs() {
        return totalSpecialHolidayHrs;
    }

    public void setTotalSpecialHolidayHrs(Integer totalSpecialHolidayHrs) {
        this.totalSpecialHolidayHrs = totalSpecialHolidayHrs;
    }

    public Integer getTotalOTSpecialHolidayHrs() {
        return totalOTSpecialHolidayHrs;
    }

    public void setTotalOTSpecialHolidayHrs(Integer totalOTSpecialHolidayHrs) {
        this.totalOTSpecialHolidayHrs = totalOTSpecialHolidayHrs;
    }

    public Integer getTotalOTExcessSpecialHolidayHrs() {
        return totalOTExcessSpecialHolidayHrs;
    }

    public void setTotalOTExcessSpecialHolidayHrs(Integer totalOTExcessSpecialHolidayHrs) {
        this.totalOTExcessSpecialHolidayHrs = totalOTExcessSpecialHolidayHrs;
    }

    public Integer getTotalNDSpecialHolidayHrs() {
        return totalNDSpecialHolidayHrs;
    }

    public void setTotalNDSpecialHolidayHrs(Integer totalNDSpecialHolidayHrs) {
        this.totalNDSpecialHolidayHrs = totalNDSpecialHolidayHrs;
    }

    public Integer getTotalBasicOTHours() {
        return totalBasicOTHours;
    }

    public void setTotalBasicOTHours(Integer totalBasicOTHours) {
        this.totalBasicOTHours = totalBasicOTHours;
    }

    public Integer getTotalBasicOTExcessHours() {
        return totalBasicOTExcessHours;
    }

    public void setTotalBasicOTExcessHours(Integer totalBasicOTExcessHours) {
        this.totalBasicOTExcessHours = totalBasicOTExcessHours;
    }

    public Integer getTotalBasicNDHours() {
        return totalBasicNDHours;
    }

    public void setTotalBasicNDHours(Integer totalBasicNDHours) {
        this.totalBasicNDHours = totalBasicNDHours;
    }

    public Integer getTotalBasicWorkDays() {
        return totalBasicWorkDays;
    }

    public void setTotalBasicWorkDays(Integer totalBasicWorkDays) {
        this.totalBasicWorkDays = totalBasicWorkDays;
    }

    public Integer getTotalSIL() {
        return totalSIL;
    }

    public void setTotalSIL(Integer totalSIL) {
        this.totalSIL = totalSIL;
    }

    public Date getDateAvailedStart() {
        return dateAvailedStart;
    }

    public void setDateAvailedStart(Date dateAvailedStart) {
        this.dateAvailedStart = dateAvailedStart;
    }

    public Boolean getHasRdp() {
        return hasRdp;
    }

    public void setHasRdp(Boolean hasRdp) {
        this.hasRdp = hasRdp;
    }

    public Integer getTotalRdp() {
        return totalRdp;
    }

    public void setTotalRdp(Integer totalRdp) {
        this.totalRdp = totalRdp;
    }

    public Integer getTotalBTRHours() {
        return totalBTRHours;
    }

    public void setTotalBTRHours(Integer totalBTRHours) {
        this.totalBTRHours = totalBTRHours;
    }

    public Integer getTotalBTRDays() {
        return totalBTRDays;
    }

    public void setTotalBTRDays(Integer totalBTRDays) {
        this.totalBTRDays = totalBTRDays;
    }

    public Integer getTotalSGDays() {
        return totalSGDays;
    }

    public void setTotalSGDays(Integer totalSGDays) {
        this.totalSGDays = totalSGDays;
    }

    public Integer getTotalCCTVDays() {
        return totalCCTVDays;
    }

    public void setTotalCCTVDays(Integer totalCCTVDays) {
        this.totalCCTVDays = totalCCTVDays;
    }

    public Integer getTotalAbsentDays() {
        return totalAbsentDays;
    }

    public void setTotalAbsentDays(Integer totalAbsentDays) {
        this.totalAbsentDays = totalAbsentDays;
    }

    public Set<RelieverDTRAssignment> getRelieverDTRAssignments() {
        return relieverDTRAssignments;
    }

    public void setRelieverDTRAssignments(Set<RelieverDTRAssignment> relieverDTRAssignments) {
        this.relieverDTRAssignments = relieverDTRAssignments;
    }
}
