package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.ModeOfPayment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "ERP_SIL")
public class ErpSil extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sil_seq")
    @SequenceGenerator(name = "sil_seq", sequenceName = "sil_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "DAYS_AVAILED")
    private Double noDaysAvailed;

    @Column(name = "AMOUNT_AVAILED")
    private Double amountAvailed;

    @Column(name = "DATE_AVAILED_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAvailedStart;

    @Column(name = "DATE_AVAILED_STOP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAvailedStop;

    @Column(name = "DATE_OF_PAYMENT_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfPaymentStart;

    @Column(name = "DATE_OF_PAYMENT_STOP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfPaymentStop;

    @Column(name = "MODE_OF_PAYMENT", length = 20)
    @Enumerated(EnumType.STRING)
    private ModeOfPayment modeOfPayment;

    @Column(name = "DAYS_UNAVAILED")
    private Double noDaysUnAvailed;

    @Column(name = "AMOUNT_UNAVAILED")
    private Double amountUnAvailed;

    @Column(name = "DATE_COVERED_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCoveredStart;

    @Column(name = "DATE_COVERED_STOP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCoveredStop;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private ErpEmployee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getNoDaysAvailed() {
        return noDaysAvailed;
    }

    public void setNoDaysAvailed(Double noDaysAvailed) {
        this.noDaysAvailed = noDaysAvailed;
    }

    public Double getAmountAvailed() {
        return amountAvailed;
    }

    public void setAmountAvailed(Double amountAvailed) {
        this.amountAvailed = amountAvailed;
    }

    public ModeOfPayment getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(ModeOfPayment modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public Double getNoDaysUnAvailed() {
        return noDaysUnAvailed;
    }

    public void setNoDaysUnAvailed(Double noDaysUnAvailed) {
        this.noDaysUnAvailed = noDaysUnAvailed;
    }

    public Double getAmountUnAvailed() {
        return amountUnAvailed;
    }

    public void setAmountUnAvailed(Double amountUnAvailed) {
        this.amountUnAvailed = amountUnAvailed;
    }

    public Date getDateAvailedStart() {
        return dateAvailedStart;
    }

    public void setDateAvailedStart(Date dateAvailedStart) {
        this.dateAvailedStart = dateAvailedStart;
    }

    public Date getDateAvailedStop() {
        return dateAvailedStop;
    }

    public void setDateAvailedStop(Date dateAvailedStop) {
        this.dateAvailedStop = dateAvailedStop;
    }

    public Date getDateCoveredStart() {
        return dateCoveredStart;
    }

    public void setDateCoveredStart(Date dateCoveredStart) {
        this.dateCoveredStart = dateCoveredStart;
    }

    public Date getDateCoveredStop() {
        return dateCoveredStop;
    }

    public void setDateCoveredStop(Date dateCoveredStop) {
        this.dateCoveredStop = dateCoveredStop;
    }

    public ErpEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(ErpEmployee employee) {
        this.employee = employee;
    }

    public Date getDateOfPaymentStart() {
        return dateOfPaymentStart;
    }

    public void setDateOfPaymentStart(Date dateOfPaymentStart) {
        this.dateOfPaymentStart = dateOfPaymentStart;
    }

    public Date getDateOfPaymentStop() {
        return dateOfPaymentStop;
    }

    public void setDateOfPaymentStop(Date dateOfPaymentStop) {
        this.dateOfPaymentStop = dateOfPaymentStop;
    }
}
