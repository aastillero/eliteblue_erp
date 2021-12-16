package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ScoutDeductions;
import io.eliteblue.erp.core.repository.ScoutDeductionsRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScoutDeductionService extends CoreErpServiceImpl implements CoreErpService<ScoutDeductions, Long> {

    @Autowired
    private ScoutDeductionsRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ScoutDeductions> getAll() {
        return repository.findAll();
    }

    @Override
    public ScoutDeductions findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ScoutDeductions scoutDeductions) {
        repository.delete(scoutDeductions);
    }

    @Override
    public void save(ScoutDeductions scoutDeductions) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(scoutDeductions.getId() == null) {
            scoutDeductions.setCreatedBy(currentUserName);
            scoutDeductions.setDateCreated(new Date());
            scoutDeductions.setLastEditedBy(currentUserName);
            scoutDeductions.setLastUpdate(new Date());
            scoutDeductions.setOperation(DataOperation.CREATED.name());
        }
        else {
            scoutDeductions.setLastEditedBy(currentUserName);
            scoutDeductions.setLastUpdate(new Date());
            scoutDeductions.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(scoutDeductions);
    }
}
