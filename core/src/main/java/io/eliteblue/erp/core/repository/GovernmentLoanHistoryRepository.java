package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.GovernmentLoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernmentLoanHistoryRepository extends JpaRepository<GovernmentLoanHistory, Long> {

}
