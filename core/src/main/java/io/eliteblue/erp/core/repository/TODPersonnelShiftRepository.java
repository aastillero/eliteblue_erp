package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.TODClient;
import io.eliteblue.erp.core.model.TODPersonnelShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TODPersonnelShiftRepository extends JpaRepository<TODPersonnelShift, Long> {

    List<TODPersonnelShift> findByTodClient(TODClient client);
}
