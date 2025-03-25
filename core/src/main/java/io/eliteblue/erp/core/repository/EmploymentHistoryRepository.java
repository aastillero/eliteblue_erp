package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {

}
