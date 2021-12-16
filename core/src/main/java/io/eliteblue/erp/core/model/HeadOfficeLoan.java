package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.EmployeeLoanType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "HEAD_OFFICE_LOAN")
public class HeadOfficeLoan extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_loan_seq")
    @SequenceGenerator(name = "office_loan_seq", sequenceName = "office_loan_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "LOAN_AMOUNT")
    private Double loanAmount;

    @Column(name = "DEDUCT_AMOUNT")
    private Double deductionAmount;

    @Column(name = "BALANCE_AMOUNT")
    private Double balanceAmount;

    @Column(name = "PAID_AMOUNT")
    private Double paidAmount;

    @Column(name = "DATE_STARTED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStarted;

    @OneToOne
    @JoinColumn(name = "BORROWER_ID", referencedColumnName = "id")
    @NotNull
    private ErpEmployee employeeBorrower;

    @Column(name = "LOAN_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private EmployeeLoanType loanType;

    @Column(name = "LOAN_DESCRIPTION", columnDefinition = "TEXT")
    private String loanDescription;

    @OneToMany(mappedBy = "officeLoan", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<HeadOfficeLoanHistory> loanHistory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(Double deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public ErpEmployee getEmployeeBorrower() {
        return employeeBorrower;
    }

    public void setEmployeeBorrower(ErpEmployee employeeBorrower) {
        this.employeeBorrower = employeeBorrower;
    }

    public EmployeeLoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(EmployeeLoanType loanType) {
        this.loanType = loanType;
    }

    public Set<HeadOfficeLoanHistory> getLoanHistory() {
        return loanHistory;
    }

    public void setLoanHistory(Set<HeadOfficeLoanHistory> loanHistory) {
        this.loanHistory = loanHistory;
    }

    public String getLoanDescription() {
        return loanDescription;
    }

    public void setLoanDescription(String loanDescription) {
        this.loanDescription = loanDescription;
    }

    public Double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }
}
