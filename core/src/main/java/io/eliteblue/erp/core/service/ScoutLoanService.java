package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ScoutLoan;
import io.eliteblue.erp.core.repository.ScoutLoanRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScoutLoanService extends CoreErpServiceImpl implements CoreErpService<ScoutLoan, Long> {

    @Autowired
    private ScoutLoanRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ScoutLoan> getAll() {
        return repository.findAll();
    }

    @Override
    public ScoutLoan findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ScoutLoan scoutLoan) {
        repository.delete(scoutLoan);
    }

    @Override
    public void save(ScoutLoan scoutLoan) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(scoutLoan.getId() == null) {
            scoutLoan.setCreatedBy(currentUserName);
            scoutLoan.setDateCreated(new Date());
            scoutLoan.setLastEditedBy(currentUserName);
            scoutLoan.setLastUpdate(new Date());
            scoutLoan.setOperation(DataOperation.CREATED.name());
        }
        else {
            scoutLoan.setLastEditedBy(currentUserName);
            scoutLoan.setLastUpdate(new Date());
            scoutLoan.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(scoutLoan);
    }
}
