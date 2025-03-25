package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CONTRACTED_MAN_HOURS")
public class ContractedManHours extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contracted_man_hrs_seq")
    @SequenceGenerator(name = "contracted_man_hrs_seq", sequenceName = "contracted_man_hrs_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "COVER_PERIOD_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date coverPeriodStart;

    @Column(name = "COVER_PERIOD_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date coverPeriodEnd;

    @Column(name = "TOTAL_MAN_HOURS")
    private Double totalManHours;

    @Column(name = "TOTAL_ADJUSTED_MAN_HOURS")
    private Double totalAdjustedManHours;

    @ManyToOne
    @JoinColumn(name = "erp_detachment_id", nullable = false)
    private ErpDetachment erpDetachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment erpDetachment) {
        this.erpDetachment = erpDetachment;
    }

    public Double getTotalManHours() {
        return totalManHours;
    }

    public void setTotalManHours(Double totalManHours) {
        this.totalManHours = totalManHours;
    }

    public Double getTotalAdjustedManHours() {
        return totalAdjustedManHours;
    }

    public void setTotalAdjustedManHours(Double totalAdjustedManHours) {
        this.totalAdjustedManHours = totalAdjustedManHours;
    }
}
