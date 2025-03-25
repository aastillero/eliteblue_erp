package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EquipmentSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentSubCategoryRepository extends JpaRepository<EquipmentSubCategory, Long> {

    EquipmentSubCategory findByName(String name);
}
