package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.TODShift;

import javax.persistence.*;

@Entity
@Table(name = "TOD_PERSONNEL_SHIFT")
public class TODPersonnelShift extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tod_personnel_shift_seq")
    @SequenceGenerator(name = "tod_personnel_shift_seq", sequenceName = "tod_personnel_shift_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "PERSONNEL_POSITION", length = 50)
    private String personnelPosition; // GUARD / SG / PIC / DC

    @Column(name = "SHIFT", length = 20)
    @Enumerated(EnumType.STRING)
    private TODShift shift; // DAY SHIFT or NIGHT SHIFT

    @Column(name = "NO_OF_SECURITY")
    private Double noSecurity;

    @Column(name = "TOD")
    private Double tod; // 12 or 8

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

    @ManyToOne
    @JoinColumn(name = "tod_id", nullable = false)
    private TODClient todClient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonnelPosition() {
        return personnelPosition;
    }

    public void setPersonnelPosition(String personnelPosition) {
        this.personnelPosition = personnelPosition;
    }

    public TODShift getShift() {
        return shift;
    }

    public void setShift(TODShift shift) {
        this.shift = shift;
    }

    public Double getNoSecurity() {
        return noSecurity;
    }

    public void setNoSecurity(Double noSecurity) {
        this.noSecurity = noSecurity;
    }

    public Double getTod() {
        return tod;
    }

    public void setTod(Double tod) {
        this.tod = tod;
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

    public TODClient getTodClient() {
        return todClient;
    }

    public void setTodClient(TODClient todClient) {
        this.todClient = todClient;
    }
}
