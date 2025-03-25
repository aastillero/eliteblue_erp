package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.RequisitionApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionApprovalRepository extends JpaRepository<RequisitionApproval, Long> {

}
