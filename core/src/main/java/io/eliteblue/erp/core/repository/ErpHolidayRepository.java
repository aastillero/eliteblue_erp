package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ErpHolidayRepository extends JpaRepository<ErpHoliday, Long> {

    ErpHoliday findByName(String name);

    ErpHoliday findByHolidayDate(Date d);
}
