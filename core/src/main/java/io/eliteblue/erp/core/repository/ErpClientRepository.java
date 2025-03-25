package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.OperationsArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpClientRepository extends JpaRepository<ErpClient, Long> {

    @Query("SELECT c FROM ErpClient c LEFT JOIN c.erpDetachments d WHERE d.location IN ?1")
    List<ErpClient> findByDetachmentLocations(List<OperationsArea> areas);

    List<ErpClient> findByClientStatus(ClientStatus status);

    ErpClient findByName(String name);
}
