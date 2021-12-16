package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.BasicSalary;
import io.eliteblue.erp.core.repository.BasicSalaryRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class BasicSalaryService extends CoreErpServiceImpl implements CoreErpService<BasicSalary, Long> {

    @Autowired
    private BasicSalaryRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<BasicSalary> getAll() {
        return repository.findAll();
    }

    @Override
    public BasicSalary findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(BasicSalary basicSalary) {
        repository.delete(basicSalary);
    }

    @Override
    public void save(BasicSalary basicSalary) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(basicSalary.getId() == null) {
            basicSalary.setCreatedBy(currentUserName);
            basicSalary.setDateCreated(new Date());
            basicSalary.setLastEditedBy(currentUserName);
            basicSalary.setLastUpdate(new Date());
            basicSalary.setOperation(DataOperation.CREATED.name());
        }
        else {
            basicSalary.setLastEditedBy(currentUserName);
            basicSalary.setLastUpdate(new Date());
            basicSalary.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(basicSalary);
    }
}
