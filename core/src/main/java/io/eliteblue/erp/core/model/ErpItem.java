package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ERP_ITEM")
public class ErpItem extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "item_seq", allocationSize = 1)
    private Long id;

    @Column(name = "NAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToOne
    @JoinColumn(name = "MEASUREMENT", referencedColumnName = "id")
    @NotNull
    private UnitOfMeasure measurement;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UnitOfMeasure getMeasurement() {
        return measurement;
    }

    public void setMeasurement(UnitOfMeasure measurement) {
        this.measurement = measurement;
    }
}
