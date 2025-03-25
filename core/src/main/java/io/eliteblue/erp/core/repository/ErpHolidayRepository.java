package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ErpHolidayRepository extends JpaRepository<ErpHoliday, Long> {

    ErpHoliday findByName(String name);

    ErpHoliday findByHolidayDate(Date d);

    @Query("SELECT h FROM ErpHoliday h WHERE TO_CHAR(h.holidayDate, 'MM-DD') BETWEEN :start AND :stop")
    List<ErpHoliday> findAllBetweenDate(String start, String stop);
}
