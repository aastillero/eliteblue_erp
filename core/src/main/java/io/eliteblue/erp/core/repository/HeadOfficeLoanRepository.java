package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.HeadOfficeLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeadOfficeLoanRepository extends JpaRepository<HeadOfficeLoan, Long> {

    List<HeadOfficeLoan> findAllByEmployeeBorrower(ErpEmployee employee);
}
