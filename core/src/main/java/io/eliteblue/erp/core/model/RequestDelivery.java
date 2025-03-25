package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.DeliveryStatus;

import javax.persistence.*;

@Entity
@Table(name = "REQUEST_DELIVERY")
public class RequestDelivery extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_delivery_seq")
    @SequenceGenerator(name = "req_delivery_seq", sequenceName = "req_delivery_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "DELIVERY_STATUS", length = 50)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(name = "REMARKS")
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "material_request_id", nullable = false)
    private ErpMaterialRequest erpMaterialRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ErpMaterialRequest getErpMaterialRequest() {
        return erpMaterialRequest;
    }

    public void setErpMaterialRequest(ErpMaterialRequest erpMaterialRequest) {
        this.erpMaterialRequest = erpMaterialRequest;
    }
}
