package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.PulloutRequest;
import io.eliteblue.erp.core.repository.PulloutRequestRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PulloutRequestService extends CoreErpServiceImpl implements CoreErpService<PulloutRequest, Long> {

    @Autowired
    private PulloutRequestRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<PulloutRequest> getAll() {
        return repository.findAll();
    }

    public List<PulloutRequest> getAllFilteredByDetachment() {
        List<ErpDetachment> detachments = CurrentUserUtil.getDetachments();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.findAll();
        }
        return repository.findByDetachmentIn(detachments);
    }

    @Override
    public PulloutRequest findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(PulloutRequest pulloutRequest) {
        repository.delete(pulloutRequest);
    }

    @Override
    public void save(PulloutRequest pulloutRequest) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(pulloutRequest.getId() == null) {
            pulloutRequest.setCreatedBy(currentUserName);
            pulloutRequest.setDateCreated(new Date());
            pulloutRequest.setLastEditedBy(currentUserName);
            pulloutRequest.setLastUpdate(new Date());
            pulloutRequest.setOperation(DataOperation.CREATED.name());
        }
        else {
            pulloutRequest.setLastEditedBy(currentUserName);
            pulloutRequest.setLastUpdate(new Date());
            pulloutRequest.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(pulloutRequest);
    }
}
