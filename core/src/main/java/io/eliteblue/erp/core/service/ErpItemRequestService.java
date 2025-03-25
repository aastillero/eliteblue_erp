package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpItemRequest;
import io.eliteblue.erp.core.repository.ErpItemRequestRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpItemRequestService extends CoreErpServiceImpl implements CoreErpService<ErpItemRequest, Long> {

    @Autowired
    private ErpItemRequestRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpItemRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public ErpItemRequest findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpItemRequest erpItemRequest) {
        repository.delete(erpItemRequest);
    }

    @Override
    public void save(ErpItemRequest erpItemRequest) {
        if(erpItemRequest.getId() == null) {
            erpItemRequest.setDateCreated(new Date());
            erpItemRequest.setOperation(DataOperation.CREATED.name());
            erpItemRequest.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpItemRequest.setOperation(DataOperation.UPDATED.name());
            erpItemRequest.setLastUpdate(new Date());
            erpItemRequest.setLastEditedBy(super.getCurrentUser());
        }
        erpItemRequest.setLastUpdate(new Date());
        repository.save(erpItemRequest);
    }
}
