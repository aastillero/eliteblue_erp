package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.RequisitionApproval;
import io.eliteblue.erp.core.repository.RequisitionApprovalRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RequisitionApprovalService extends CoreErpServiceImpl implements CoreErpService<RequisitionApproval, Long> {

    @Autowired
    private RequisitionApprovalRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<RequisitionApproval> getAll() {
        return repository.findAll();
    }

    @Override
    public RequisitionApproval findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(RequisitionApproval requisitionApproval) {
        repository.delete(requisitionApproval);
    }

    @Override
    public void save(RequisitionApproval requisitionApproval) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(requisitionApproval.getId() == null) {
            requisitionApproval.setCreatedBy(currentUserName);
            requisitionApproval.setDateCreated(new Date());
            requisitionApproval.setLastEditedBy(currentUserName);
            requisitionApproval.setLastUpdate(new Date());
            requisitionApproval.setOperation(DataOperation.CREATED.name());
        }
        else {
            requisitionApproval.setLastEditedBy(currentUserName);
            requisitionApproval.setLastUpdate(new Date());
            requisitionApproval.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(requisitionApproval);
    }
}
