package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpDetachmentRepository extends JpaRepository<ErpDetachment, Long> {

    List<ErpDetachment> findByLocationIn(List<OperationsArea> areas);
    @Query(value = "SELECT e FROM ErpDetachment e WHERE e.id IN :detachmentIds")
    List<ErpDetachment> getAllFilteredDetachment(@Param("detachmentIds") List<Long> detachments);

    ErpDetachment findByName(String name);
}
