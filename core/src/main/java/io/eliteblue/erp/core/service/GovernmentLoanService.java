package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.repository.GovernmentLoanRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GovernmentLoanService extends CoreErpServiceImpl implements CoreErpService<GovernmentLoan, Long> {

    @Autowired
    private GovernmentLoanRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<GovernmentLoan> getAll() {
        return repository.findAll();
    }

    @Override
    public GovernmentLoan findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(GovernmentLoan governmentLoan) {
        repository.delete(governmentLoan);
    }

    @Override
    public void save(GovernmentLoan governmentLoan) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(governmentLoan.getId() == null) {
            governmentLoan.setCreatedBy(currentUserName);
            governmentLoan.setDateCreated(new Date());
            governmentLoan.setLastEditedBy(currentUserName);
            governmentLoan.setLastUpdate(new Date());
            governmentLoan.setOperation(DataOperation.CREATED.name());
        }
        else {
            governmentLoan.setLastEditedBy(currentUserName);
            governmentLoan.setLastUpdate(new Date());
            governmentLoan.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(governmentLoan);
    }
}
