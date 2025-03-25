package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "UNIT_OF_MEASURE")
public class UnitOfMeasure extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_of_measure_seq")
    @SequenceGenerator(name = "unit_of_measure_seq", sequenceName = "unit_of_measure_seq", allocationSize = 1)
    private Long id;

    @Column(name = "MEASURE", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String measure;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
