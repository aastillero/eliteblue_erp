package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentStatusRepository extends JpaRepository<EquipmentStatus, Long> {

    EquipmentStatus findByName(String name);
}
