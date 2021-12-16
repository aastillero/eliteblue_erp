package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.DetachmentPayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetachmentPayrollRepository extends JpaRepository<DetachmentPayroll, Long> {

}
