package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpTimeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeScheduleRepository extends JpaRepository<ErpTimeSchedule, Long> {

    ErpTimeSchedule findByDescription(String description);

    List<ErpTimeSchedule> findByErpDetachment(ErpDetachment detachment);
}
