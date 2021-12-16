package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ScoutLoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoutLoanHistoryRepository extends JpaRepository<ScoutLoanHistory, Long> {

}
