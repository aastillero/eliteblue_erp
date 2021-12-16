package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EmployeePayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePayrollRepository extends JpaRepository<EmployeePayroll, Long> {

}
