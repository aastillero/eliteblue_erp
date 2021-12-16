package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.ApprovalStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "ERP_DTR")
public class ErpDTR extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dtr_seq")
    @SequenceGenerator(name = "dtr_seq", sequenceName = "dtr_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "CUTOFF_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cutoffStart;

    @Column(name = "CUTOFF_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cutoffEnd;

    @Column(name = "APPROVAL_STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @ManyToOne
    @JoinColumn(name = "erp_detachment_id", nullable = false)
    private ErpDetachment erpDetachment;

    @OneToMany(mappedBy = "erpDTR", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpDTRAssignment> assignments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCutoffStart() {
        return cutoffStart;
    }

    public void setCutoffStart(Date cutoffStart) {
        this.cutoffStart = cutoffStart;
    }

    public Date getCutoffEnd() {
        return cutoffEnd;
    }

    public void setCutoffEnd(Date cutoffEnd) {
        this.cutoffEnd = cutoffEnd;
    }

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment erpDetachment) {
        this.erpDetachment = erpDetachment;
    }

    public Set<ErpDTRAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Set<ErpDTRAssignment> assignments) {
        this.assignments = assignments;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }
}
