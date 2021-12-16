package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.BasicSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicSalaryRepository extends JpaRepository<BasicSalary, Long> {

}
