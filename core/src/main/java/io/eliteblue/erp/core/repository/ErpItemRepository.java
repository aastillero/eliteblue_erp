package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpItemRepository extends JpaRepository<ErpItem, Long> {

    ErpItem findByName(String name);
}
