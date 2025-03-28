package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.GovernmentDeductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernmentDeductionsRepository extends JpaRepository<GovernmentDeductions, Long> {

}
