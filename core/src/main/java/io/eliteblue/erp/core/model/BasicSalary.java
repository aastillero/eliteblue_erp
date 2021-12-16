package io.eliteblue.erp.core.model;

import javax.persistence.*;

@Entity
@Table(name = "BASIC_SALARY")
public class BasicSalary extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bsalary_seq")
    @SequenceGenerator(name = "bsalary_seq", sequenceName = "bsalary_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "DAILY_WAGE")
    private Double dailyWage;

    @Column(name = "HOURLY_RATE")
    private Double hourlyWage;

    @Column(name = "TOTAL_DAYS_WORKED")
    private Double totalDaysWorked;

    @Column(name = "BASIC_HOURS_WORKED")
    private Double basicHoursWorked; // no OT and others only basic

    @Column(name = "TOTAL_HOURS_WORKED")
    private Double totalHoursWorked; // with OT, SPECIAL HOLIDAY, REGULAR HOLIDAY

    @Column(name = "TOTAL_DAYS_OT")
    private Double totalDaysOT; // total Days with Overtime

    @Column(name = "TOTAL_DAYS_ND")
    private Double totalDaysND; // total Days with Night work

    @Column(name = "BASIC_SALARY")
    private Double basicSalary;

    // Overtime
    @Column(name = "OT_NO_DAYS")
    private Double ot_no_days;

    @Column(name = "OT_NO_EXCESS_4HRS")
    private Double ot_no_excess_4hours;

    @Column(name = "OT_HRS_PER_DAY")
    private Double ot_hours_per_day;

    @Column(name = "OT_TOTAL_HRS")
    private Double ot_total_hours;

    @Column(name = "OT_RATE")
    private Double ot_rate;

    @Column(name = "OT_PAY")
    private Double ot_pay;

    // Night Differential
    @Column(name = "ND_NO_DAYS")
    private Double nd_no_days;

    @Column(name = "ND_HOURLY_RATE")
    private Double nd_hourly_rate;

    @Column(name = "ND_PAY")
    private Double nd_pay;

    // Rest Day
    @Column(name = "RD_8HRS")
    private Double rd_eight_hrs;

    @Column(name = "RD_12HRS")
    private Double rd_twelve_hrs;

    @Column(name = "RD_8HRS_RATE")
    private Double rd_eight_hrs_rate;

    @Column(name = "RD_12HRS_RATE")
    private Double rd_twelve_hrs_rate;

    @Column(name = "RD_REG_DAY_OT")
    private Double rd_reg_day_ot;

    @Column(name = "RD_PAY")
    private Double rd_pay;

    // Regular Holiday
    @Column(name = "RH_8HRS")
    private Double rh_eight_hrs;

    @Column(name = "RH_12HRS")
    private Double rh_twelve_hrs;

    @Column(name = "RH_EXCESS_OT_HRS")
    private Double rh_excess_ot_hrs;

    @Column(name = "RH_EXCESS_8HRS")
    private Double rh_excess_8hrs;

    @Column(name = "RH_PAY")
    private Double rh_pay;

    @Column(name = "RH_OT_RATE")
    private Double rh_ot_rate;

    @Column(name = "RH_OT_PAY")
    private Double rh_ot_pay;

    @Column(name = "RH_NDIFF_DAYS")
    private Double rh_nightdiff_days;

    @Column(name = "RH_NDIFF_RATE")
    private Double rh_nightdiff_rate;

    @Column(name = "RH_NDIFF_PAY")
    private Double rh_nightdiff_pay;

    // Special Holiday
    @Column(name = "SH_8HRS")
    private Double sh_eight_hrs;

    @Column(name = "SH_12HRS")
    private Double sh_twelve_hrs;

    @Column(name = "SH_EXCESS_OT_HRS")
    private Double sh_excess_ot_hrs;

    @Column(name = "SH_EXCESS_8HRS")
    private Double sh_excess_8hrs;

    @Column(name = "SH_PAY")
    private Double sh_pay;

    @Column(name = "SH_OT_RATE")
    private Double sh_ot_rate;

    @Column(name = "SH_OT_PAY")
    private Double sh_ot_pay;

    @Column(name = "SH_NDIFF_DAYS")
    private Double sh_nightdiff_days;

    @Column(name = "SH_NDIFF_RATE")
    private Double sh_nightdiff_rate;

    @Column(name = "SH_NDIFF_PAY")
    private Double sh_nightdiff_pay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(Double dailyWage) {
        this.dailyWage = dailyWage;
    }

    public Double getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(Double hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public Double getTotalDaysWorked() {
        return totalDaysWorked;
    }

    public void setTotalDaysWorked(Double totalDaysWorked) {
        this.totalDaysWorked = totalDaysWorked;
    }

    public Double getBasicHoursWorked() {
        return basicHoursWorked;
    }

    public void setBasicHoursWorked(Double basicHoursWorked) {
        this.basicHoursWorked = basicHoursWorked;
    }

    public Double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(Double totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public Double getTotalDaysOT() {
        return totalDaysOT;
    }

    public void setTotalDaysOT(Double totalDaysOT) {
        this.totalDaysOT = totalDaysOT;
    }

    public Double getTotalDaysND() {
        return totalDaysND;
    }

    public void setTotalDaysND(Double totalDaysND) {
        this.totalDaysND = totalDaysND;
    }

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Double getOt_no_days() {
        return ot_no_days;
    }

    public void setOt_no_days(Double ot_no_days) {
        this.ot_no_days = ot_no_days;
    }

    public Double getOt_no_excess_4hours() {
        return ot_no_excess_4hours;
    }

    public void setOt_no_excess_4hours(Double ot_no_excess_4hours) {
        this.ot_no_excess_4hours = ot_no_excess_4hours;
    }

    public Double getOt_hours_per_day() {
        return ot_hours_per_day;
    }

    public void setOt_hours_per_day(Double ot_hours_per_day) {
        this.ot_hours_per_day = ot_hours_per_day;
    }

    public Double getOt_total_hours() {
        return ot_total_hours;
    }

    public void setOt_total_hours(Double ot_total_hours) {
        this.ot_total_hours = ot_total_hours;
    }

    public Double getOt_rate() {
        return ot_rate;
    }

    public void setOt_rate(Double ot_rate) {
        this.ot_rate = ot_rate;
    }

    public Double getOt_pay() {
        return ot_pay;
    }

    public void setOt_pay(Double ot_pay) {
        this.ot_pay = ot_pay;
    }

    public Double getNd_no_days() {
        return nd_no_days;
    }

    public void setNd_no_days(Double nd_no_days) {
        this.nd_no_days = nd_no_days;
    }

    public Double getNd_hourly_rate() {
        return nd_hourly_rate;
    }

    public void setNd_hourly_rate(Double nd_hourly_rate) {
        this.nd_hourly_rate = nd_hourly_rate;
    }

    public Double getNd_pay() {
        return nd_pay;
    }

    public void setNd_pay(Double nd_pay) {
        this.nd_pay = nd_pay;
    }

    public Double getRd_eight_hrs() {
        return rd_eight_hrs;
    }

    public void setRd_eight_hrs(Double rd_eight_hrs) {
        this.rd_eight_hrs = rd_eight_hrs;
    }

    public Double getRd_twelve_hrs() {
        return rd_twelve_hrs;
    }

    public void setRd_twelve_hrs(Double rd_twelve_hrs) {
        this.rd_twelve_hrs = rd_twelve_hrs;
    }

    public Double getRd_eight_hrs_rate() {
        return rd_eight_hrs_rate;
    }

    public void setRd_eight_hrs_rate(Double rd_eight_hrs_rate) {
        this.rd_eight_hrs_rate = rd_eight_hrs_rate;
    }

    public Double getRd_twelve_hrs_rate() {
        return rd_twelve_hrs_rate;
    }

    public void setRd_twelve_hrs_rate(Double rd_twelve_hrs_rate) {
        this.rd_twelve_hrs_rate = rd_twelve_hrs_rate;
    }

    public Double getRd_reg_day_ot() {
        return rd_reg_day_ot;
    }

    public void setRd_reg_day_ot(Double rd_reg_day_ot) {
        this.rd_reg_day_ot = rd_reg_day_ot;
    }

    public Double getRd_pay() {
        return rd_pay;
    }

    public void setRd_pay(Double rd_pay) {
        this.rd_pay = rd_pay;
    }

    public Double getRh_eight_hrs() {
        return rh_eight_hrs;
    }

    public void setRh_eight_hrs(Double rh_eight_hrs) {
        this.rh_eight_hrs = rh_eight_hrs;
    }

    public Double getRh_twelve_hrs() {
        return rh_twelve_hrs;
    }

    public void setRh_twelve_hrs(Double rh_twelve_hrs) {
        this.rh_twelve_hrs = rh_twelve_hrs;
    }

    public Double getRh_excess_ot_hrs() {
        return rh_excess_ot_hrs;
    }

    public void setRh_excess_ot_hrs(Double rh_excess_ot_hrs) {
        this.rh_excess_ot_hrs = rh_excess_ot_hrs;
    }

    public Double getRh_excess_8hrs() {
        return rh_excess_8hrs;
    }

    public void setRh_excess_8hrs(Double rh_excess_8hrs) {
        this.rh_excess_8hrs = rh_excess_8hrs;
    }

    public Double getRh_pay() {
        return rh_pay;
    }

    public void setRh_pay(Double rh_pay) {
        this.rh_pay = rh_pay;
    }

    public Double getRh_ot_rate() {
        return rh_ot_rate;
    }

    public void setRh_ot_rate(Double rh_ot_rate) {
        this.rh_ot_rate = rh_ot_rate;
    }

    public Double getRh_ot_pay() {
        return rh_ot_pay;
    }

    public void setRh_ot_pay(Double rh_ot_pay) {
        this.rh_ot_pay = rh_ot_pay;
    }

    public Double getRh_nightdiff_days() {
        return rh_nightdiff_days;
    }

    public void setRh_nightdiff_days(Double rh_nightdiff_days) {
        this.rh_nightdiff_days = rh_nightdiff_days;
    }

    public Double getRh_nightdiff_rate() {
        return rh_nightdiff_rate;
    }

    public void setRh_nightdiff_rate(Double rh_nightdiff_rate) {
        this.rh_nightdiff_rate = rh_nightdiff_rate;
    }

    public Double getRh_nightdiff_pay() {
        return rh_nightdiff_pay;
    }

    public void setRh_nightdiff_pay(Double rh_nightdiff_pay) {
        this.rh_nightdiff_pay = rh_nightdiff_pay;
    }

    public Double getSh_eight_hrs() {
        return sh_eight_hrs;
    }

    public void setSh_eight_hrs(Double sh_eight_hrs) {
        this.sh_eight_hrs = sh_eight_hrs;
    }

    public Double getSh_twelve_hrs() {
        return sh_twelve_hrs;
    }

    public void setSh_twelve_hrs(Double sh_twelve_hrs) {
        this.sh_twelve_hrs = sh_twelve_hrs;
    }

    public Double getSh_excess_ot_hrs() {
        return sh_excess_ot_hrs;
    }

    public void setSh_excess_ot_hrs(Double sh_excess_ot_hrs) {
        this.sh_excess_ot_hrs = sh_excess_ot_hrs;
    }

    public Double getSh_excess_8hrs() {
        return sh_excess_8hrs;
    }

    public void setSh_excess_8hrs(Double sh_excess_8hrs) {
        this.sh_excess_8hrs = sh_excess_8hrs;
    }

    public Double getSh_pay() {
        return sh_pay;
    }

    public void setSh_pay(Double sh_pay) {
        this.sh_pay = sh_pay;
    }

    public Double getSh_ot_rate() {
        return sh_ot_rate;
    }

    public void setSh_ot_rate(Double sh_ot_rate) {
        this.sh_ot_rate = sh_ot_rate;
    }

    public Double getSh_ot_pay() {
        return sh_ot_pay;
    }

    public void setSh_ot_pay(Double sh_ot_pay) {
        this.sh_ot_pay = sh_ot_pay;
    }

    public Double getSh_nightdiff_days() {
        return sh_nightdiff_days;
    }

    public void setSh_nightdiff_days(Double sh_nightdiff_days) {
        this.sh_nightdiff_days = sh_nightdiff_days;
    }

    public Double getSh_nightdiff_rate() {
        return sh_nightdiff_rate;
    }

    public void setSh_nightdiff_rate(Double sh_nightdiff_rate) {
        this.sh_nightdiff_rate = sh_nightdiff_rate;
    }

    public Double getSh_nightdiff_pay() {
        return sh_nightdiff_pay;
    }

    public void setSh_nightdiff_pay(Double sh_nightdiff_pay) {
        this.sh_nightdiff_pay = sh_nightdiff_pay;
    }
}
