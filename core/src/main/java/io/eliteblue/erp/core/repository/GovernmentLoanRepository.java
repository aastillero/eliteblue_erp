package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.GovernmentLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GovernmentLoanRepository extends JpaRepository<GovernmentLoan, Long> {

    List<GovernmentLoan> findAllByEmployeeBorrower(ErpEmployee employee);

    List<GovernmentLoan> findByDateStarted(Date dateStarted);

    List<GovernmentLoan> findByDateStartedAndEmployeeBorrowerAndLoanType(Date dateStarted, ErpEmployee erpEmployee, GovernmentLoanType governmentLoanType);
}
