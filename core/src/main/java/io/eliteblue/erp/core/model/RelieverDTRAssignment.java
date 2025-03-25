package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RELIEVER_DTR_ASSIGNMENT")
public class RelieverDTRAssignment extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rlv_dtr_assn_seq")
    @SequenceGenerator(name = "rlv_dtr_assn_seq", sequenceName = "rlv_dtr_assn_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "TOTAL_DAYS")
    private Double totalDays;

    @ManyToOne
    @JoinColumn(name = "reliever_detachment_id", nullable = false)
    private ErpDetachment relieverDetachment;

    @ManyToOne
    @JoinColumn(name = "dtr_assn_id", nullable = false)
    private ErpDTRAssignment erpDTRAssignment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelieverDTRAssignment that = (RelieverDTRAssignment) o;
        return Objects.equals(relieverDetachment, that.relieverDetachment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relieverDetachment);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Double totalDays) {
        this.totalDays = totalDays;
    }

    public ErpDetachment getRelieverDetachment() {
        return relieverDetachment;
    }

    public void setRelieverDetachment(ErpDetachment relieverDetachment) {
        this.relieverDetachment = relieverDetachment;
    }

    public ErpDTRAssignment getErpDTRAssignment() {
        return erpDTRAssignment;
    }

    public void setErpDTRAssignment(ErpDTRAssignment erpDTRAssignment) {
        this.erpDTRAssignment = erpDTRAssignment;
    }
}
