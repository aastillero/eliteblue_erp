package io.eliteblue.erp.core.repository;

import io.eliteblue.erp.core.model.ErpPost;
import io.eliteblue.erp.core.model.OperationsArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpPostRepository extends JpaRepository<ErpPost, Long> {

    @Query("SELECT p FROM ErpPost p WHERE p.erpDetachment.location IN ?1")
    List<ErpPost> filteredByDetachmentLocation(List<OperationsArea> areas);

    ErpPost findByName(String name);
}
