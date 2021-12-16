package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.GovernmentLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernmentLoanRepository extends JpaRepository<GovernmentLoan, Long> {

}
