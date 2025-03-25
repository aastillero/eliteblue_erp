package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpPost;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.ErpPostRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpPostService extends CoreErpServiceImpl implements CoreErpService<ErpPost, Long> {

    @Autowired
    private ErpPostRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    public List<ErpPost> getAll() {
        return repository.findAll();
    }

    public List<ErpPost> getAllFilteredLocation() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        return repository.filteredByDetachmentLocation(assignedLocations);
    }

    public ErpPost findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public void delete(ErpPost erpPost) {
        repository.delete(erpPost);
    }

    public void save(ErpPost erpPost) {
        if(erpPost.getId() == null) {
            erpPost.setDateCreated(new Date());
            erpPost.setOperation(DataOperation.CREATED.name());
            erpPost.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpPost.setOperation(DataOperation.UPDATED.name());
            erpPost.setLastUpdate(new Date());
            erpPost.setLastEditedBy(super.getCurrentUser());
        }
        erpPost.setLastUpdate(new Date());
        repository.save(erpPost);
    }
}
