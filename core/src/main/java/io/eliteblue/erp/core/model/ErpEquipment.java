package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.model.identifiers.ErpEquipmentId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@IdClass(ErpEquipmentId.class)
@Table(name = "ERP_EQUIPMENT")
public class ErpEquipment extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equip_seq")
    @SequenceGenerator(name = "equip_seq", sequenceName = "equip_seq", allocationSize = 1)
    private Long id;

    @Id
    @Column(name = "REF_NUMBER")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String refNum;

    @OneToOne
    @JoinColumn(name = "EQUIP_TYPE", referencedColumnName = "id")
    @NotNull
    private EquipmentType equipmentType;

    @OneToOne
    @JoinColumn(name = "EQUIP_STATUS", referencedColumnName = "id")
    @NotNull
    private EquipmentStatus equipmentStatus;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SERIAL_NO")
    private String serialNo;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EXPIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @OneToOne
    @JoinColumn(name = "AREA_ID", referencedColumnName = "id")
    private OperationsArea location;

    @OneToOne
    @JoinColumn(name = "DETACHMENT_ID", referencedColumnName = "id")
    private ErpDetachment detachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OperationsArea getLocation() {
        return location;
    }

    public void setLocation(OperationsArea location) {
        this.location = location;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public EquipmentStatus getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(EquipmentStatus equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }
}
