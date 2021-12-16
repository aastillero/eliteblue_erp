package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "ERP_CONFIG")
public class ErpConfig extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conf_seq")
    @SequenceGenerator(name = "conf_seq", sequenceName = "conf_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "NAME", length = 50)
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @Column(name = "STR_VAL", length = 50)
    private String strValue;

    @Column(name = "NUM_VAL")
    private Double numValue;

    @Column(name = "DATE_VAL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Double getNumValue() {
        return numValue;
    }

    public void setNumValue(Double numValue) {
        this.numValue = numValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
}
