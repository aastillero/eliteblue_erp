package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErpConfigRepository extends JpaRepository<ErpConfig, Long> {

    ErpConfig findByName(String name);
}
