package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EmployeePayroll;
import io.eliteblue.erp.core.repository.EmployeePayrollRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EmployeePayrollService extends CoreErpServiceImpl implements CoreErpService<EmployeePayroll, Long> {

    @Autowired
    private EmployeePayrollRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<EmployeePayroll> getAll() {
        return repository.findAll();
    }

    @Override
    public EmployeePayroll findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(EmployeePayroll employeePayroll) {
        repository.delete(employeePayroll);
    }

    @Override
    public void save(EmployeePayroll employeePayroll) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(employeePayroll.getId() == null) {
            employeePayroll.setCreatedBy(currentUserName);
            employeePayroll.setDateCreated(new Date());
            employeePayroll.setLastEditedBy(currentUserName);
            employeePayroll.setLastUpdate(new Date());
            employeePayroll.setOperation(DataOperation.CREATED.name());
        }
        else {
            employeePayroll.setLastEditedBy(currentUserName);
            employeePayroll.setLastUpdate(new Date());
            employeePayroll.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(employeePayroll);
    }
}
