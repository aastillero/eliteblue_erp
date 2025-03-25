package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpPayroll;
import io.eliteblue.erp.core.repository.ErpPayrollRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpPayrollService extends CoreErpServiceImpl implements CoreErpService<ErpPayroll, Long> {

    @Autowired
    private ErpPayrollRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpPayroll> getAll() {
        return repository.findAll();
    }

    public List<ErpPayroll> getAllFilteredStartAndEndDate(Date startDate, Date endDate) {
        List<ErpPayroll> retVal = repository.getAllFilteredStartAndEndDate(endDate, startDate);
        return retVal;
    }

    @Override
    public ErpPayroll findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpPayroll erpPayroll) {
        repository.delete(erpPayroll);
    }

    @Override
    public void save(ErpPayroll erpPayroll) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpPayroll.getId() == null) {
            erpPayroll.setCreatedBy(currentUserName);
            erpPayroll.setDateCreated(new Date());
            erpPayroll.setLastEditedBy(currentUserName);
            erpPayroll.setLastUpdate(new Date());
            erpPayroll.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpPayroll.setLastEditedBy(currentUserName);
            erpPayroll.setLastUpdate(new Date());
            erpPayroll.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpPayroll);
    }
}
