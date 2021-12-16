package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.HeadOfficeLoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadOfficeLoanHistoryRepository extends JpaRepository<HeadOfficeLoanHistory, Long> {

}
