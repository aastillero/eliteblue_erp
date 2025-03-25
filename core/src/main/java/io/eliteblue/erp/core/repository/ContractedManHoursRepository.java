package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ContractedManHours;
import io.eliteblue.erp.core.model.ErpDetachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContractedManHoursRepository extends JpaRepository<ContractedManHours, Long> {

    @Query(value = "SELECT e FROM ContractedManHours e WHERE e.erpDetachment = :detachment AND e.coverPeriodStart <= :coverPeriodStart AND e.coverPeriodEnd >= :coverPeriodEnd")
    List<ContractedManHours> getAllFilteredDetachment(@Param("detachment") ErpDetachment detachment, @Param("coverPeriodStart") Date startDate, @Param("coverPeriodEnd") Date endDate);
}
