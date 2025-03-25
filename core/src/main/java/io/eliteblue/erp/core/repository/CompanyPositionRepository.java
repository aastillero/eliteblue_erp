package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.CompanyPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPositionRepository extends JpaRepository<CompanyPosition, Long> {

    CompanyPosition findByName(String name);
}
