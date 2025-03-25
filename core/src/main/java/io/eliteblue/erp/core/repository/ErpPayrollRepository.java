package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpPayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ErpPayrollRepository extends JpaRepository<ErpPayroll, Long> {

    @Query(value = "SELECT e FROM ErpPayroll e WHERE e.coverPeriodEnd = :operationEnd AND e.coverPeriodStart = :operationStart")
    List<ErpPayroll> getAllFilteredStartAndEndDate(@Param("operationEnd") Date endDate, @Param("operationStart") Date startDate);
}
