package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "EQUIPMENT_STATUS")
public class EquipmentStatus extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equip_stat_seq")
    @SequenceGenerator(name = "equip_stat_seq", sequenceName = "equip_stat_seq", allocationSize = 1)
    private Long id;

    @Column(name = "STATUS_NAME", length = 50)
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
