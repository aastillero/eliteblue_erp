package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.RequestDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestDeliveryRepository extends JpaRepository<RequestDelivery, Long> {

}
