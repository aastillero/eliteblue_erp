package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "EQUIPMENT_TYPE")
public class EquipmentType extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equip_type_seq")
    @SequenceGenerator(name = "equip_type_seq", sequenceName = "equip_type_seq", allocationSize = 1)
    private Long id;

    @Column(name = "NAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @OneToOne
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "id")
    private EquipmentCategory category;

    @OneToOne
    @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "id")
    private EquipmentSubCategory subCategory;

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

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }

    public EquipmentSubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(EquipmentSubCategory subCategory) {
        this.subCategory = subCategory;
    }
}
