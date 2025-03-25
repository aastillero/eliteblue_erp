package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.TODClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TODClientRepository extends JpaRepository<TODClient, Long> {

    List<TODClient> findByErpDetachment(ErpDetachment detachment);
}
