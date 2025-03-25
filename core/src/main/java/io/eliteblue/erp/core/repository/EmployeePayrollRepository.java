package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.DetachmentPayroll;
import io.eliteblue.erp.core.model.EmployeePayroll;
import io.eliteblue.erp.core.model.ErpEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeePayrollRepository extends JpaRepository<EmployeePayroll, Long> {

    @Query(value = "SELECT e FROM EmployeePayroll e WHERE e.employeePayroll = :empPayroll AND e.coverPeriodEnd = :operationEnd AND e.coverPeriodStart = :operationStart")
    List<EmployeePayroll> getEmployeeFilteredCutOff(@Param("empPayroll") ErpEmployee employee, @Param("operationEnd") Date endDate, @Param("operationStart") Date startDate);

    List<EmployeePayroll> findByEmployeePayrollAndDetachmentPayroll(ErpEmployee emp, DetachmentPayroll dp);
}
