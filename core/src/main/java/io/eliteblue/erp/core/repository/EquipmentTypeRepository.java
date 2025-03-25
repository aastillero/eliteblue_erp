package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import io.eliteblue.erp.core.model.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {

    EquipmentType findByName(String name);

    List<EquipmentType> findByCategoryAndSubCategory(EquipmentCategory category, EquipmentSubCategory subCategory);
}
