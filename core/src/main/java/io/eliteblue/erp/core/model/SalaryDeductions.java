package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "SALARY_DEDUCTIONS")
public class SalaryDeductions extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deductions_seq")
    @SequenceGenerator(name = "deductions_seq", sequenceName = "deductions_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    // Contributions
    @Column(name = "SSS_PREMIUM")
    private Double sssPremium;

    @Column(name = "HDMF_PREMIUM")
    private Double hdmfPremium;

    @Column(name = "PHIC_PREMIUM")
    private Double phicPremium;

    @OneToMany(mappedBy = "salaryDeductions", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<HeadOfficeDeductions> headOfficeDeductions;

    @OneToMany(mappedBy = "salaryDeductions", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ScoutDeductions> scoutDeductions;

    @Column(name = "TOTAL_DEDUCTIONS")
    private Double totalDeductions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getSssPremium() {
        return sssPremium;
    }

    public void setSssPremium(Double sssPremium) {
        this.sssPremium = sssPremium;
    }

    public Double getHdmfPremium() {
        return hdmfPremium;
    }

    public void setHdmfPremium(Double hdmfPremium) {
        this.hdmfPremium = hdmfPremium;
    }

    public Double getPhicPremium() {
        return phicPremium;
    }

    public void setPhicPremium(Double phicPremium) {
        this.phicPremium = phicPremium;
    }

    public Set<HeadOfficeDeductions> getHeadOfficeDeductions() {
        return headOfficeDeductions;
    }

    public void setHeadOfficeDeductions(Set<HeadOfficeDeductions> headOfficeDeductions) {
        this.headOfficeDeductions = headOfficeDeductions;
    }

    public Set<ScoutDeductions> getScoutDeductions() {
        return scoutDeductions;
    }

    public void setScoutDeductions(Set<ScoutDeductions> scoutDeductions) {
        this.scoutDeductions = scoutDeductions;
    }

    public Double getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(Double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }
}
