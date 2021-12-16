package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.DetachmentPayroll;
import io.eliteblue.erp.core.repository.DetachmentPayrollRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DetachmentPayrollService extends CoreErpServiceImpl implements CoreErpService<DetachmentPayroll, Long> {

    @Autowired
    private DetachmentPayrollRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<DetachmentPayroll> getAll() {
        return repository.findAll();
    }

    @Override
    public DetachmentPayroll findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(DetachmentPayroll detachmentPayroll) {
        repository.delete(detachmentPayroll);
    }

    @Override
    public void save(DetachmentPayroll detachmentPayroll) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(detachmentPayroll.getId() == null) {
            detachmentPayroll.setCreatedBy(currentUserName);
            detachmentPayroll.setDateCreated(new Date());
            detachmentPayroll.setLastEditedBy(currentUserName);
            detachmentPayroll.setLastUpdate(new Date());
            detachmentPayroll.setOperation(DataOperation.CREATED.name());
        }
        else {
            detachmentPayroll.setLastEditedBy(currentUserName);
            detachmentPayroll.setLastUpdate(new Date());
            detachmentPayroll.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(detachmentPayroll);
    }
}
