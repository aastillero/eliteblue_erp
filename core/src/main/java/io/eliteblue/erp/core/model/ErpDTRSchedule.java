package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.AttendanceType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "ERP_DTR_SCHEDULE")
public class ErpDTRSchedule extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dtr_sched_seq")
    @SequenceGenerator(name = "dtr_sched_seq", sequenceName = "dtr_sched_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "DAY_SHIFT_START")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date dayShiftStart;

    @Column(name = "DAY_SHIFT_END")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date dayShiftEnd;

    @Column(name = "NIGHT_SHIFT_START")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date nightShiftStart;

    @Column(name = "NIGHT_SHIFT_END")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date nightShiftEnd;

    @Column(name = "ATTENDANCE", length = 20)
    @Enumerated(EnumType.STRING)
    private AttendanceType attendance;

    @Column(name = "TOTAL_HOURS")
    private Integer totalHours;

    @Column(name = "TOTAL_HOURS_DAY")
    private Integer totalHoursDay;

    @Column(name = "TOTAL_HOURS_NIGHT")
    private Integer totalHoursNight;

    @ManyToOne
    @JoinColumn(name = "dtr_assn_id", nullable = false)
    private ErpDTRAssignment erpDTRAssignment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDayShiftStart() {
        return dayShiftStart;
    }

    public void setDayShiftStart(Date dayShiftStart) {
        this.dayShiftStart = dayShiftStart;
    }

    public Date getDayShiftEnd() {
        return dayShiftEnd;
    }

    public void setDayShiftEnd(Date dayShiftEnd) {
        this.dayShiftEnd = dayShiftEnd;
    }

    public Date getNightShiftStart() {
        return nightShiftStart;
    }

    public void setNightShiftStart(Date nightShiftStart) {
        this.nightShiftStart = nightShiftStart;
    }

    public Date getNightShiftEnd() {
        return nightShiftEnd;
    }

    public void setNightShiftEnd(Date nightShiftEnd) {
        this.nightShiftEnd = nightShiftEnd;
    }

    public ErpDTRAssignment getErpDTRAssignment() {
        return erpDTRAssignment;
    }

    public void setErpDTRAssignment(ErpDTRAssignment erpDTRAssignment) {
        this.erpDTRAssignment = erpDTRAssignment;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public AttendanceType getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceType attendance) {
        this.attendance = attendance;
    }

    public Integer getTotalHoursDay() {
        return totalHoursDay;
    }

    public void setTotalHoursDay(Integer totalHoursDay) {
        this.totalHoursDay = totalHoursDay;
    }

    public Integer getTotalHoursNight() {
        return totalHoursNight;
    }

    public void setTotalHoursNight(Integer totalHoursNight) {
        this.totalHoursNight = totalHoursNight;
    }
}
