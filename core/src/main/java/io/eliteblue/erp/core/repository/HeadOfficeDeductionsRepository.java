package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.HeadOfficeDeductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadOfficeDeductionsRepository extends JpaRepository<HeadOfficeDeductions, Long> {

}
