package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.RelieverDTRAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelieverDTRAssignmentRepository extends JpaRepository<RelieverDTRAssignment, Long> {

}
