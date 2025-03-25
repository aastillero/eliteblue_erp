package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ERP_ITEM_REQUEST")
public class ErpItemRequest extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_req_seq")
    @SequenceGenerator(name = "item_req_seq", sequenceName = "item_req_seq", allocationSize = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ITEM", referencedColumnName = "id")
    @NotNull
    private ErpItem item;

    @Column(name = "REQ_QUANTITY")
    private Double reqQuantity;

    @Column(name = "PARTICULARS")
    private String particulars;

    @Column(name = "STOCK_ON_HAND")
    private Double stockOnHand;

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

    public ErpItem getItem() {
        return item;
    }

    public void setItem(ErpItem item) {
        this.item = item;
    }

    public Double getReqQuantity() {
        return reqQuantity;
    }

    public void setReqQuantity(Double reqQuantity) {
        this.reqQuantity = reqQuantity;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public Double getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(Double stockOnHand) {
        this.stockOnHand = stockOnHand;
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
