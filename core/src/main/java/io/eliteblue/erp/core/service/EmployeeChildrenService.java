package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EmployeeChildren;
import io.eliteblue.erp.core.repository.EmployeeChildrenRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EmployeeChildrenService extends CoreErpServiceImpl implements CoreErpService<EmployeeChildren, Long> {

    @Autowired
    private EmployeeChildrenRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<EmployeeChildren> getAll() {
        return repository.findAll();
    }

    @Override
    public EmployeeChildren findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(EmployeeChildren employeeChildren) {
        repository.delete(employeeChildren);
    }

    @Override
    public void save(EmployeeChildren employeeChildren) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(employeeChildren.getId() == null) {
            employeeChildren.setCreatedBy(currentUserName);
            employeeChildren.setDateCreated(new Date());
            employeeChildren.setLastEditedBy(currentUserName);
            employeeChildren.setLastUpdate(new Date());
            employeeChildren.setOperation(DataOperation.CREATED.name());
        }
        else {
            employeeChildren.setLastEditedBy(currentUserName);
            employeeChildren.setLastUpdate(new Date());
            employeeChildren.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(employeeChildren);
    }
}
