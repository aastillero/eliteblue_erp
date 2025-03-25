package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpMaterialRequest;
import io.eliteblue.erp.core.repository.ErpMaterialRequestRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpMaterialRequestService extends CoreErpServiceImpl implements CoreErpService<ErpMaterialRequest, Long> {

    @Autowired
    private ErpMaterialRequestRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpMaterialRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public ErpMaterialRequest findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpMaterialRequest erpMaterialRequest) {
        repository.delete(erpMaterialRequest);
    }

    @Override
    public void save(ErpMaterialRequest erpMaterialRequest) {
        if(erpMaterialRequest.getId() == null) {
            erpMaterialRequest.setDateCreated(new Date());
            erpMaterialRequest.setOperation(DataOperation.CREATED.name());
            erpMaterialRequest.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpMaterialRequest.setOperation(DataOperation.UPDATED.name());
            erpMaterialRequest.setLastUpdate(new Date());
            erpMaterialRequest.setLastEditedBy(super.getCurrentUser());
        }
        erpMaterialRequest.setLastUpdate(new Date());
        repository.save(erpMaterialRequest);
    }
}
