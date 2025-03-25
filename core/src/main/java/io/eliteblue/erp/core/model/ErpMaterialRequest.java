package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.model.security.ErpUser;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "ERP_MATERIAL_REQUEST")
public class ErpMaterialRequest extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_req_seq")
    @SequenceGenerator(name = "material_req_seq", sequenceName = "material_req_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "REQUESTER_ID", referencedColumnName = "id")
    @NotNull
    private ErpUser requester;

    @OneToOne
    @JoinColumn(name = "DETACHMENT_ID", referencedColumnName = "id")
    @NotNull
    private ErpDetachment detachment;

    @OneToMany(mappedBy = "erpMaterialRequest", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpItemRequest> itemRequested;

    @OneToMany(mappedBy = "erpMaterialRequest", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<RequestDelivery> requestDeliveries;

    @Column(name = "APPROVAL_STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @OneToOne
    @JoinColumn(name = "MR_APPROVAL_ID", referencedColumnName = "id")
    private RequisitionApproval approval;

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

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public Set<ErpItemRequest> getItemRequested() {
        return itemRequested;
    }

    public void setItemRequested(Set<ErpItemRequest> itemRequested) {
        this.itemRequested = itemRequested;
    }

    public RequisitionApproval getApproval() {
        return approval;
    }

    public void setApproval(RequisitionApproval approval) {
        this.approval = approval;
    }

    public Set<RequestDelivery> getRequestDeliveries() {
        return requestDeliveries;
    }

    public void setRequestDeliveries(Set<RequestDelivery> requestDeliveries) {
        this.requestDeliveries = requestDeliveries;
    }
}
