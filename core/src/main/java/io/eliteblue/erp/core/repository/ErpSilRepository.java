package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.ErpSil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpSilRepository extends JpaRepository<ErpSil, Long> {

    List<ErpSil> findAllByEmployee(ErpEmployee employee);

    @Query(value = "SELECT e FROM ErpSil e WHERE e.employee.erpDetachment = :detachment")
    List<ErpSil> findAllByDetachment(@Param("detachment") ErpDetachment detachment);
}
