package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.constants.GovernmentLoanType;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.GovernmentLoan;
import io.eliteblue.erp.core.repository.GovernmentLoanRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public List<GovernmentLoan> getLoansByEmployee(ErpEmployee employee) {
        return repository.findAllByEmployeeBorrower(employee);
    }

    public List<GovernmentLoan> getLoansByDateStarted(Date date) {
        return repository.findByDateStarted(date);
    }

    public List<GovernmentLoan> filterLoansByDateEmployeeSSS(Date date, ErpEmployee employee) {
        return repository.findByDateStartedAndEmployeeBorrowerAndLoanType(date, employee, GovernmentLoanType.SSS_SALARY_LOAN);
    }

    @Override
    public GovernmentLoan findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(GovernmentLoan governmentLoan) {
        repository.delete(governmentLoan);
    }

    @Transactional
    public void deleteAllGovernmentLoans(List<GovernmentLoan> governmentLoans) {
        repository.deleteAll(governmentLoans);
    }

    @Transactional
    public void saveAll(List<GovernmentLoan> governmentLoans) {
        for(GovernmentLoan governmentLoan: governmentLoans) {
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
        }
        repository.saveAll(governmentLoans);
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
