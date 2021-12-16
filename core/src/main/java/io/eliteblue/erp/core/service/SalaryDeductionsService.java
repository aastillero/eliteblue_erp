package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.SalaryDeductions;
import io.eliteblue.erp.core.repository.SalaryDeductionsRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SalaryDeductionsService extends CoreErpServiceImpl implements CoreErpService<SalaryDeductions, Long> {

    @Autowired
    public SalaryDeductionsRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<SalaryDeductions> getAll() {
        return repository.findAll();
    }

    @Override
    public SalaryDeductions findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(SalaryDeductions salaryDeductions) {
        repository.delete(salaryDeductions);
    }

    @Override
    public void save(SalaryDeductions salaryDeductions) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(salaryDeductions.getId() == null) {
            salaryDeductions.setCreatedBy(currentUserName);
            salaryDeductions.setDateCreated(new Date());
            salaryDeductions.setLastEditedBy(currentUserName);
            salaryDeductions.setLastUpdate(new Date());
            salaryDeductions.setOperation(DataOperation.CREATED.name());
        }
        else {
            salaryDeductions.setLastEditedBy(currentUserName);
            salaryDeductions.setLastUpdate(new Date());
            salaryDeductions.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(salaryDeductions);
    }
}
