package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "COMPANY_POSTION")
public class CompanyPosition extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comp_pos_seq")
    @SequenceGenerator(name = "comp_pos_seq", sequenceName = "comp_pos_seq", allocationSize = 1)
    private Long id;

    @Column(name = "POSITION_NAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

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
}
