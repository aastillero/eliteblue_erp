package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientStatusRepository extends JpaRepository<ClientStatus, Long> {

    ClientStatus findByName(String name);
}
