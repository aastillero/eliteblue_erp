package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "AREA")
public class OperationsArea extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "area_seq")
    @SequenceGenerator(name = "area_seq", sequenceName = "area_seq", allocationSize = 1, initialValue = 50)
    private Long id;

    @Column(name = "LOCATION", length = 50, unique = true)
    @NotNull
    @Size(min = 2, max = 50)
    String location;

    @Column(name = "RATE_SG")
    private Double rateSG;

    @Column(name = "RATE_AC")
    private Double rateAC;

    @Column(name = "RATE_DC")
    private Double rateDC;

    @Column(name = "RATE_ADC")
    private Double rateADC;

    @Column(name = "RATE_SIC")
    private Double rateSIC;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getRateSG() {
        return rateSG;
    }

    public void setRateSG(Double rateSG) {
        this.rateSG = rateSG;
    }

    public Double getRateAC() {
        return rateAC;
    }

    public void setRateAC(Double rateAC) {
        this.rateAC = rateAC;
    }

    public Double getRateDC() {
        return rateDC;
    }

    public void setRateDC(Double rateDC) {
        this.rateDC = rateDC;
    }

    public Double getRateADC() {
        return rateADC;
    }

    public void setRateADC(Double rateADC) {
        this.rateADC = rateADC;
    }

    public Double getRateSIC() {
        return rateSIC;
    }

    public void setRateSIC(Double rateSIC) {
        this.rateSIC = rateSIC;
    }
}
