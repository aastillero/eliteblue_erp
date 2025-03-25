package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "TOD_CLIENT")
public class TODClient extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tod_client_seq")
    @SequenceGenerator(name = "tod_client_seq", sequenceName = "tod_client_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "erp_detachment_id", nullable = false)
    private ErpDetachment erpDetachment;

    @Column(name = "CLIENT_NAME", length = 50)
    private String clientName;

    @OneToMany(mappedBy = "todClient", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<TODPersonnelShift> personnelShifts;

    @Column(name = "TOTAL_NO_OF_SECURITY")
    private Double totalNoSecurity;

    @Column(name = "TOLTAL_TOD")
    private Double totalTOD; // 12 or 8

    @Column(name = "TOTAL_HRS_1_15")
    private Double totalHrs1to15;

    @Column(name = "TOTAL_HRS_16_28")
    private Double totalHrs16to28;

    @Column(name = "TOTAL_HRS_16_29")
    private Double totalHrs16to29;

    @Column(name = "TOTAL_HRS_16_30")
    private Double totalHrs16to30;

    @Column(name = "TOTAL_HRS_16_31")
    private Double totalHrs16to31;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment erpDetachment) {
        this.erpDetachment = erpDetachment;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Set<TODPersonnelShift> getPersonnelShifts() {
        return personnelShifts;
    }

    public void setPersonnelShifts(Set<TODPersonnelShift> personnelShifts) {
        this.personnelShifts = personnelShifts;
    }

    public Double getTotalNoSecurity() {
        return totalNoSecurity;
    }

    public void setTotalNoSecurity(Double totalNoSecurity) {
        this.totalNoSecurity = totalNoSecurity;
    }

    public Double getTotalTOD() {
        return totalTOD;
    }

    public void setTotalTOD(Double totalTOD) {
        this.totalTOD = totalTOD;
    }

    public Double getTotalHrs1to15() {
        return totalHrs1to15;
    }

    public void setTotalHrs1to15(Double totalHrs1to15) {
        this.totalHrs1to15 = totalHrs1to15;
    }

    public Double getTotalHrs16to28() {
        return totalHrs16to28;
    }

    public void setTotalHrs16to28(Double totalHrs16to28) {
        this.totalHrs16to28 = totalHrs16to28;
    }

    public Double getTotalHrs16to29() {
        return totalHrs16to29;
    }

    public void setTotalHrs16to29(Double totalHrs16to29) {
        this.totalHrs16to29 = totalHrs16to29;
    }

    public Double getTotalHrs16to30() {
        return totalHrs16to30;
    }

    public void setTotalHrs16to30(Double totalHrs16to30) {
        this.totalHrs16to30 = totalHrs16to30;
    }

    public Double getTotalHrs16to31() {
        return totalHrs16to31;
    }

    public void setTotalHrs16to31(Double totalHrs16to31) {
        this.totalHrs16to31 = totalHrs16to31;
    }
}
