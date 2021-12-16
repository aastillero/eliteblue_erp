package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ScoutLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoutLoanRepository extends JpaRepository<ScoutLoan, Long> {

}
