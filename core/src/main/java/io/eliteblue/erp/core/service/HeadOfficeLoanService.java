package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.HeadOfficeLoan;
import io.eliteblue.erp.core.repository.HeadOfficeLoanRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class HeadOfficeLoanService extends CoreErpServiceImpl implements CoreErpService<HeadOfficeLoan, Long> {

    @Autowired
    private HeadOfficeLoanRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<HeadOfficeLoan> getAll() {
        return repository.findAll();
    }

    @Override
    public HeadOfficeLoan findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public List<HeadOfficeLoan> getLoansByEmployee(ErpEmployee employee) {
        return repository.findAllByEmployeeBorrower(employee);
    }

    @Override
    public void delete(HeadOfficeLoan headOfficeLoan) {
        repository.delete(headOfficeLoan);
    }

    @Override
    public void save(HeadOfficeLoan headOfficeLoan) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(headOfficeLoan.getId() == null) {
            headOfficeLoan.setCreatedBy(currentUserName);
            headOfficeLoan.setDateCreated(new Date());
            headOfficeLoan.setLastEditedBy(currentUserName);
            headOfficeLoan.setLastUpdate(new Date());
            headOfficeLoan.setOperation(DataOperation.CREATED.name());
        }
        else {
            headOfficeLoan.setLastEditedBy(currentUserName);
            headOfficeLoan.setLastUpdate(new Date());
            headOfficeLoan.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(headOfficeLoan);
    }
}
