package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.HeadOfficeDeductions;
import io.eliteblue.erp.core.repository.HeadOfficeDeductionsRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class HeadOfficeDeductionsService extends CoreErpServiceImpl implements CoreErpService<HeadOfficeDeductions, Long> {

    @Autowired
    private HeadOfficeDeductionsRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<HeadOfficeDeductions> getAll() {
        return repository.findAll();
    }

    @Override
    public HeadOfficeDeductions findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(HeadOfficeDeductions headOfficeDeductions) {
        repository.delete(headOfficeDeductions);
    }

    @Override
    public void save(HeadOfficeDeductions headOfficeDeductions) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(headOfficeDeductions.getId() == null) {
            headOfficeDeductions.setCreatedBy(currentUserName);
            headOfficeDeductions.setDateCreated(new Date());
            headOfficeDeductions.setLastEditedBy(currentUserName);
            headOfficeDeductions.setLastUpdate(new Date());
            headOfficeDeductions.setOperation(DataOperation.CREATED.name());
        }
        else {
            headOfficeDeductions.setLastEditedBy(currentUserName);
            headOfficeDeductions.setLastUpdate(new Date());
            headOfficeDeductions.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(headOfficeDeductions);
    }
}
