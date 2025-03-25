package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EquipmentType;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEquipment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.identifiers.ErpEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpEquipmentRepository extends JpaRepository<ErpEquipment, ErpEquipmentId> {

    ErpEquipment findByEquipmentType(EquipmentType equipmentType);
    ErpEquipment findByRefNum(String refNum);
    List<ErpEquipment> findByDetachmentIn(List<ErpDetachment> detachments);
    List<ErpEquipment> findByDetachment(ErpDetachment detachments);
    List<ErpEquipment> findByLocationIn(List<OperationsArea> locations);

    @Query("SELECT e FROM ErpEquipment e WHERE e.detachment.location IN ?1")
    List<ErpEquipment> getByDetachmentLocation(List<OperationsArea> areas);
}
