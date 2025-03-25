package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.GovernmentDeductions;
import io.eliteblue.erp.core.repository.GovernmentDeductionsRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GovernmentDeductionService extends CoreErpServiceImpl implements CoreErpService<GovernmentDeductions, Long> {

    @Autowired
    private GovernmentDeductionsRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<GovernmentDeductions> getAll() {
        return repository.findAll();
    }

    @Override
    public GovernmentDeductions findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(GovernmentDeductions governmentDeductions) {
        repository.delete(governmentDeductions);
    }

    @Override
    public void save(GovernmentDeductions governmentDeductions) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(governmentDeductions.getId() == null) {
            governmentDeductions.setCreatedBy(currentUserName);
            governmentDeductions.setDateCreated(new Date());
            governmentDeductions.setLastEditedBy(currentUserName);
            governmentDeductions.setLastUpdate(new Date());
            governmentDeductions.setOperation(DataOperation.CREATED.name());
        }
        else {
            governmentDeductions.setLastEditedBy(currentUserName);
            governmentDeductions.setLastUpdate(new Date());
            governmentDeductions.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(governmentDeductions);
    }
}
