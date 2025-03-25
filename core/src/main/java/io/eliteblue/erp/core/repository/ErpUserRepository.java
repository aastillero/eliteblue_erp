package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpDetachment;
import org.springframework.data.jpa.repository.JpaRepository;
import io.eliteblue.erp.core.model.security.ErpUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpUserRepository extends JpaRepository<ErpUser, Long> {

    ErpUser findByUsername(String username);

    List<ErpUser> findByErpDetachmentsContaining(ErpDetachment detachment);
}
