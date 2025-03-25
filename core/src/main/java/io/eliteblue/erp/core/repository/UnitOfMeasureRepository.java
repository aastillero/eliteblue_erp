package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, Long> {

    UnitOfMeasure findByMeasure(String measure);
}
