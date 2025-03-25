package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpItemRequestRepository extends JpaRepository<ErpItemRequest, Long> {

}
