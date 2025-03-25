package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.EmployeeChildren;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeChildrenRepository extends JpaRepository<EmployeeChildren, Long> {

    EmployeeChildren findByCompleteName(String completeName);
}
