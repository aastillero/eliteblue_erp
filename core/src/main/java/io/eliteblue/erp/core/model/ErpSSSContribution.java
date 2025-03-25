package io.eliteblue.erp.core.model;

import javax.persistence.*;

@Entity
@Table(name = "SSS_CONTRIBUTION")
public class ErpSSSContribution extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sss_seq")
    @SequenceGenerator(name = "sss_seq", sequenceName = "sss_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "RANGE_FRM")
    private Double rangeFrom;

    @Column(name = "RANGE_TO")
    private Double rangeTo;

    @Column(name = "ER")
    private Double er;

    @Column(name = "EE")
    private Double ee;

    @Column(name = "EC")
    private Double ec;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(Double rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public Double getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(Double rangeTo) {
        this.rangeTo = rangeTo;
    }

    public Double getEr() {
        return er;
    }

    public void setEr(Double er) {
        this.er = er;
    }

    public Double getEe() {
        return ee;
    }

    public void setEe(Double ee) {
        this.ee = ee;
    }

    public Double getEc() {
        return ec;
    }

    public void setEc(Double ec) {
        this.ec = ec;
    }
}
