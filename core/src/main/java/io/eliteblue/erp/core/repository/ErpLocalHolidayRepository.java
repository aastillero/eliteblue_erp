package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpLocalHoliday;
import io.eliteblue.erp.core.model.OperationsArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpLocalHolidayRepository extends JpaRepository<ErpLocalHoliday, Long> {

    @Query("SELECT h FROM ErpLocalHoliday h WHERE h.operationsArea = :area AND TO_CHAR(h.holidayDate, 'MM-DD') BETWEEN :start AND :stop")
    List<ErpLocalHoliday> findAllBetweenDate(OperationsArea area, String start, String stop);
}
