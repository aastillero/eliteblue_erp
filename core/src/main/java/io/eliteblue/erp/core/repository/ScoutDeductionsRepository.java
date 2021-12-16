package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ScoutDeductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoutDeductionsRepository extends JpaRepository<ScoutDeductions, Long> {

}
