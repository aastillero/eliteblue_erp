package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "EMPLOYEE_PAYROLL")
public class EmployeePayroll extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "epayroll_seq")
    @SequenceGenerator(name = "epayroll_seq", sequenceName = "epayroll_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "EMP_ID", referencedColumnName = "id")
    @NotNull
    private ErpEmployee employeePayroll;

    @ManyToOne
    @JoinColumn(name = "detachment_payroll_id", nullable = false)
    private DetachmentPayroll detachmentPayroll;

    @OneToOne
    @JoinColumn(name = "BASIC_SALARY_ID", referencedColumnName = "id")
    @NotNull
    private BasicSalary basicSalary;

    @OneToOne
    @JoinColumn(name = "SALARY_DEDUCTIONS_ID", referencedColumnName = "id")
    @NotNull
    private SalaryDeductions salaryDeductions;

    @Column(name = "COVER_PERIOD_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date coverPeriodStart;

    @Column(name = "COVER_PERIOD_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date coverPeriodEnd;

    @Column(name = "ECOLA")
    private Double ecola;

    @Column(name = "ALLOWANCE")
    private Double allowance;

    @Column(name = "SIL")
    private Double sil;

    @Column(name = "PATERNITY_LEAVE")
    private Double paternityLeave;

    @Column(name = "THIRTEENTH_MONTH")
    private Double thirteenthMonth;

    @Column(name = "ADJUSTMENTS")
    private Double adjustments;

    @Column(name = "GROSS_SALARY")
    private Double grossSalary;

    @Column(name = "TOTAL_NET_PAY")
    private Double totalNetPay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpEmployee getEmployeePayroll() {
        return employeePayroll;
    }

    public void setEmployeePayroll(ErpEmployee employeePayroll) {
        this.employeePayroll = employeePayroll;
    }

    public BasicSalary getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BasicSalary basicSalary) {
        this.basicSalary = basicSalary;
    }

    public SalaryDeductions getSalaryDeductions() {
        return salaryDeductions;
    }

    public void setSalaryDeductions(SalaryDeductions salaryDeductions) {
        this.salaryDeductions = salaryDeductions;
    }

    public Double getEcola() {
        return ecola;
    }

    public void setEcola(Double ecola) {
        this.ecola = ecola;
    }

    public Double getAllowance() {
        return allowance;
    }

    public void setAllowance(Double allowance) {
        this.allowance = allowance;
    }

    public Double getSil() {
        return sil;
    }

    public void setSil(Double sil) {
        this.sil = sil;
    }

    public Double getPaternityLeave() {
        return paternityLeave;
    }

    public void setPaternityLeave(Double paternityLeave) {
        this.paternityLeave = paternityLeave;
    }

    public Double getThirteenthMonth() {
        return thirteenthMonth;
    }

    public void setThirteenthMonth(Double thirteenthMonth) {
        this.thirteenthMonth = thirteenthMonth;
    }

    public Double getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(Double adjustments) {
        this.adjustments = adjustments;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getTotalNetPay() {
        return totalNetPay;
    }

    public void setTotalNetPay(Double totalNetPay) {
        this.totalNetPay = totalNetPay;
    }

    public Date getCoverPeriodStart() {
        return coverPeriodStart;
    }

    public void setCoverPeriodStart(Date coverPeriodStart) {
        this.coverPeriodStart = coverPeriodStart;
    }

    public Date getCoverPeriodEnd() {
        return coverPeriodEnd;
    }

    public void setCoverPeriodEnd(Date coverPeriodEnd) {
        this.coverPeriodEnd = coverPeriodEnd;
    }

    public DetachmentPayroll getDetachmentPayroll() {
        return detachmentPayroll;
    }

    public void setDetachmentPayroll(DetachmentPayroll detachmentPayroll) {
        this.detachmentPayroll = detachmentPayroll;
    }
}
