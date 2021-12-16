package io.eliteblue.erp.core.model;

import javax.persistence.*;

@Entity
@Table(name = "SCOUT_DEDUCTIONS")
public class ScoutDeductions extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc_deductions_seq")
    @SequenceGenerator(name = "sc_deductions_seq", sequenceName = "sc_deductions_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "DEDUCT_AMOUNT")
    private Double deductionAmount;

    @Column(name = "DEDUCTION_DESCRIPTION", columnDefinition = "TEXT")
    private String deductionDescription;

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
}
