package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.GovernmentLoanType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "GOVT_LOAN")
public class GovernmentLoan extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "govt_loan_seq")
    @SequenceGenerator(name = "govt_loan_seq", sequenceName = "govt_loan_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "LOAN_AMOUNT")
    private Double loanAmount;

    @Column(name = "BALANCE_AMOUNT")
    private Double balanceAmount;

    @Column(name = "PAID_AMOUNT")
    private Double paidAmount;

    @Column(name = "DEDUCT_AMOUNT")
    private Double deductionAmount;

    @Column(name = "DATE_STARTED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStarted;

    @OneToOne
    @JoinColumn(name = "BORROWER_ID", referencedColumnName = "id")
    @NotNull
    private ErpEmployee employeeBorrower;

    @Column(name = "LOAN_TYPE", length = 40)
    @Enumerated(EnumType.STRING)
    private GovernmentLoanType loanType;

    @OneToMany(mappedBy = "governmentLoan", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<GovernmentLoanHistory> loanHistory;

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

    public GovernmentLoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(GovernmentLoanType loanType) {
        this.loanType = loanType;
    }

    public Set<GovernmentLoanHistory> getLoanHistory() {
        return loanHistory;
    }

    public void setLoanHistory(Set<GovernmentLoanHistory> loanHistory) {
        this.loanHistory = loanHistory;
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
