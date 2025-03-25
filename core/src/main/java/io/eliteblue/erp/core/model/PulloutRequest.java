package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.InventoryStatus;
import io.eliteblue.erp.core.model.security.ErpUser;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "PULLOUT_REQUEST")
public class PulloutRequest extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pullout_req_seq")
    @SequenceGenerator(name = "pullout_req_seq", sequenceName = "pullout_req_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "REQUESTER_ID", referencedColumnName = "id")
    @NotNull
    private ErpUser requester;

    @OneToOne
    @JoinColumn(name = "DETACHMENT_ID", referencedColumnName = "id")
    @NotNull
    private ErpDetachment detachment;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "EQUIPMENT_ID", referencedColumnName = "ID"),
            @JoinColumn(name = "REF_NUM", referencedColumnName = "REF_NUMBER")
    })
    @NotNull
    @NotFound(action = NotFoundAction.IGNORE)
    private ErpEquipment equipment;

    @OneToOne
    @JoinColumn(name = "EB_APPROVAL_ID", referencedColumnName = "id")
    @NotNull
    private RequisitionApproval approval;

    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name = "REQUEST_STATUS", length = 30)
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpUser getRequester() {
        return requester;
    }

    public void setRequester(ErpUser requester) {
        this.requester = requester;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public ErpEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(ErpEquipment equipment) {
        this.equipment = equipment;
    }

    public RequisitionApproval getApproval() {
        return approval;
    }

    public void setApproval(RequisitionApproval approval) {
        this.approval = approval;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }
}
