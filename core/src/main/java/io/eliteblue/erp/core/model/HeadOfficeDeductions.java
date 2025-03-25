package io.eliteblue.erp.core.model;

import javax.persistence.*;

@Entity
@Table(name = "HEAD_OFFICE_DEDUCTIONS")
public class HeadOfficeDeductions extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ho_deductions_seq")
    @SequenceGenerator(name = "ho_deductions_seq", sequenceName = "ho_deductions_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "DEDUCT_AMOUNT")
    private Double deductionAmount;

    @Column(name = "DEDUCTION_DESCRIPTION", columnDefinition = "TEXT")
    private String deductionDescription;

    @OneToOne
    @JoinColumn(name = "HEAD_OFFICE_LOAN_ID", referencedColumnName = "id")
    private HeadOfficeLoan headOfficeLoan;

    @ManyToOne
    @JoinColumn(name = "salary_deduction_id", nullable = false)
    private SalaryDeductions salaryDeductions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(Double deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public String getDeductionDescription() {
        return deductionDescription;
    }

    public void setDeductionDescription(String deductionDescription) {
        this.deductionDescription = deductionDescription;
    }

    public SalaryDeductions getSalaryDeductions() {
        return salaryDeductions;
    }

    public void setSalaryDeductions(SalaryDeductions salaryDeductions) {
        this.salaryDeductions = salaryDeductions;
    }

    public HeadOfficeLoan getHeadOfficeLoan() {
        return headOfficeLoan;
    }

    public void setHeadOfficeLoan(HeadOfficeLoan headOfficeLoan) {
        this.headOfficeLoan = headOfficeLoan;
    }
}
