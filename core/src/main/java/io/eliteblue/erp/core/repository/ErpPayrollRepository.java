package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpPayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpPayrollRepository extends JpaRepository<ErpPayroll, Long> {

}
