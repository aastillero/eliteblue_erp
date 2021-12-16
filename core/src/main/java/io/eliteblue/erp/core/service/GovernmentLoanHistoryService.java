package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.GovernmentLoanHistory;
import io.eliteblue.erp.core.repository.GovernmentLoanHistoryRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GovernmentLoanHistoryService extends CoreErpServiceImpl implements CoreErpService<GovernmentLoanHistory, Long> {

    @Autowired
    private GovernmentLoanHistoryRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<GovernmentLoanHistory> getAll() {
        return repository.findAll();
    }

    @Override
    public GovernmentLoanHistory findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(GovernmentLoanHistory governmentLoanHistory) {
        repository.delete(governmentLoanHistory);
    }

    @Override
    public void save(GovernmentLoanHistory governmentLoanHistory) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(governmentLoanHistory.getId() == null) {
            governmentLoanHistory.setCreatedBy(currentUserName);
            governmentLoanHistory.setDateCreated(new Date());
            governmentLoanHistory.setLastEditedBy(currentUserName);
            governmentLoanHistory.setLastUpdate(new Date());
            governmentLoanHistory.setOperation(DataOperation.CREATED.name());
        }
        else {
            governmentLoanHistory.setLastEditedBy(currentUserName);
            governmentLoanHistory.setLastUpdate(new Date());
            governmentLoanHistory.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(governmentLoanHistory);
    }
}
