package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ELITEBLUE_APPROVAL")
public class RequisitionApproval extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_approval_seq")
    @SequenceGenerator(name = "req_approval_seq", sequenceName = "req_approval_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "ASEC_APPROVED")
    @NotNull
    private Boolean asecApproved;

    @Column(name = "HEAD_OP_APPROVED")
    @NotNull
    private Boolean headOperationsApproved;

    @Column(name = "CEO_APPROVED")
    @NotNull
    private Boolean ceoApproved;

    @Column(name = "PURCHASING_APPROVED")
    @NotNull
    private Boolean scoutApproved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAsecApproved() {
        return asecApproved;
    }

    public void setAsecApproved(Boolean asecApproved) {
        this.asecApproved = asecApproved;
    }

    public Boolean getHeadOperationsApproved() {
        return headOperationsApproved;
    }

    public void setHeadOperationsApproved(Boolean headOperationsApproved) {
        this.headOperationsApproved = headOperationsApproved;
    }

    public Boolean getCeoApproved() {
        return ceoApproved;
    }

    public void setCeoApproved(Boolean ceoApproved) {
        this.ceoApproved = ceoApproved;
    }

    public Boolean getScoutApproved() {
        return scoutApproved;
    }

    public void setScoutApproved(Boolean scoutApproved) {
        this.scoutApproved = scoutApproved;
    }
}
