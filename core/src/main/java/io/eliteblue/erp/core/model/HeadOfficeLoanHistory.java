package io.eliteblue.erp.core.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "OFFICE_LOAN_HISTORY")
public class HeadOfficeLoanHistory extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_loan_hist_seq")
    @SequenceGenerator(name = "office_loan_hist_seq", sequenceName = "office_loan_hist_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "office_loan_id", nullable = false)
    private HeadOfficeLoan officeLoan;

    @Column(name = "CHANGES", columnDefinition = "TEXT")
    private String changes;

    @Column(name = "REASON", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "PREV_BALANCE_AMOUNT")
    private Double prevBalanceAmount;

    @Column(name = "PREV_PAID_AMOUNT")
    private Double prevPaidAmount;

    @Column(name = "UPDATED_BALANCE_AMOUNT")
    private Double updatedBalanceAmount;

    @Column(name = "UPDATED_PAID_AMOUNT")
    private Double updatedPaidAmount;

    @Column(name = "PREV_LOAN_AMOUNT", columnDefinition = "NUMERIC", length = 20)
    private Double previousLoanAmount;

    @Column(name = "UPDATED_LOAN_AMOUNT", columnDefinition = "NUMERIC", length = 20)
    private Double updatedLoanAmount;

    @Column(name = "PREV_DEDUCT_AMOUNT", columnDefinition = "NUMERIC", length = 20)
    private Double prevDeductionAmount;

    @Column(name = "UPDATED_DEDUCT_AMOUNT", columnDefinition = "NUMERIC", length = 20)
    private Double updatedDeductionAmount;

    @Column(name = "PREV_LOAN_DESCRIPTION", columnDefinition = "TEXT")
    private String previousLoanDescription;

    @Column(name = "UPDATED_LOAN_DESCRIPTION", columnDefinition = "TEXT")
    private String updatedLoanDescription;

    @Column(name = "PREV_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date previousStartDate;

    @Column(name = "UPDATED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedStartDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HeadOfficeLoan getOfficeLoan() {
        return officeLoan;
    }

    public void setOfficeLoan(HeadOfficeLoan officeLoan) {
        this.officeLoan = officeLoan;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getPreviousLoanAmount() {
        return previousLoanAmount;
    }

    public void setPreviousLoanAmount(Double previousLoanAmount) {
        this.previousLoanAmount = previousLoanAmount;
    }

    public Double getUpdatedLoanAmount() {
        return updatedLoanAmount;
    }

    public void setUpdatedLoanAmount(Double updatedLoanAmount) {
        this.updatedLoanAmount = updatedLoanAmount;
    }

    public Double getPrevDeductionAmount() {
        return prevDeductionAmount;
    }

    public void setPrevDeductionAmount(Double prevDeductionAmount) {
        this.prevDeductionAmount = prevDeductionAmount;
    }

    public Double getUpdatedDeductionAmount() {
        return updatedDeductionAmount;
    }

    public void setUpdatedDeductionAmount(Double updatedDeductionAmount) {
        this.updatedDeductionAmount = updatedDeductionAmount;
    }

    public Date getPreviousStartDate() {
        return previousStartDate;
    }

    public void setPreviousStartDate(Date previousStartDate) {
        this.previousStartDate = previousStartDate;
    }

    public Date getUpdatedStartDate() {
        return updatedStartDate;
    }

    public void setUpdatedStartDate(Date updatedStartDate) {
        this.updatedStartDate = updatedStartDate;
    }

    public String getPreviousLoanDescription() {
        return previousLoanDescription;
    }

    public void setPreviousLoanDescription(String previousLoanDescription) {
        this.previousLoanDescription = previousLoanDescription;
    }

    public String getUpdatedLoanDescription() {
        return updatedLoanDescription;
    }

    public void setUpdatedLoanDescription(String updatedLoanDescription) {
        this.updatedLoanDescription = updatedLoanDescription;
    }

    public Double getPrevBalanceAmount() {
        return prevBalanceAmount;
    }

    public void setPrevBalanceAmount(Double prevBalanceAmount) {
        this.prevBalanceAmount = prevBalanceAmount;
    }

    public Double getPrevPaidAmount() {
        return prevPaidAmount;
    }

    public void setPrevPaidAmount(Double prevPaidAmount) {
        this.prevPaidAmount = prevPaidAmount;
    }

    public Double getUpdatedBalanceAmount() {
        return updatedBalanceAmount;
    }

    public void setUpdatedBalanceAmount(Double updatedBalanceAmount) {
        this.updatedBalanceAmount = updatedBalanceAmount;
    }

    public Double getUpdatedPaidAmount() {
        return updatedPaidAmount;
    }

    public void setUpdatedPaidAmount(Double updatedPaidAmount) {
        this.updatedPaidAmount = updatedPaidAmount;
    }
}
