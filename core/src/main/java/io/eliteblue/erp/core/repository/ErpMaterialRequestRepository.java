package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpMaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpMaterialRequestRepository extends JpaRepository<ErpMaterialRequest, Long> {

}
