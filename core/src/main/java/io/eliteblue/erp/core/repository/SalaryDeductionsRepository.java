package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.SalaryDeductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryDeductionsRepository extends JpaRepository<SalaryDeductions, Long> {

}
