package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.PulloutRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PulloutRequestRepository extends JpaRepository<PulloutRequest, Long> {

    List<PulloutRequest> findByDetachmentIn(List<ErpDetachment> detachments);
}
