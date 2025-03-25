package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpSSSContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpSSSContributionRepository extends JpaRepository<ErpSSSContribution, Long> {

    List<ErpSSSContribution> findByRangeFromGreaterThanEqualAndRangeToLessThanEqual(Double fromSalary, Double toSalary);

    @Query(value = "SELECT e FROM ErpSSSContribution e WHERE :salary BETWEEN e.rangeFrom AND e.rangeTo")
    List<ErpSSSContribution> getByRange(@Param("salary") Double salary);
}
