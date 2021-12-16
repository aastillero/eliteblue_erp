package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpSSSContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpSSSContributionRepository extends JpaRepository<ErpSSSContribution, Long> {

    List<ErpSSSContribution> findByRangeFromGreaterThanEqualAndRangeToLessThanEqual(Double fromSalary, Double toSalary);
}
