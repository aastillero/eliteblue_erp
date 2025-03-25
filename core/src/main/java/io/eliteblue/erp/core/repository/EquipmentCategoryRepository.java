package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {

    EquipmentCategory findByName(String name);
}
