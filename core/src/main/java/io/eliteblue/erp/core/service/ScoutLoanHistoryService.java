package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ScoutLoanHistory;
import io.eliteblue.erp.core.repository.ScoutLoanHistoryRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScoutLoanHistoryService extends CoreErpServiceImpl implements CoreErpService<ScoutLoanHistory, Long> {

    @Autowired
    private ScoutLoanHistoryRepository repository;

    @Override
    public List<ScoutLoanHistory> getAll() {
        return repository.findAll();
    }

    @Override
    public ScoutLoanHistory findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ScoutLoanHistory scoutLoanHistory) {
        repository.delete(scoutLoanHistory);
    }

    @Override
    public void save(ScoutLoanHistory scoutLoanHistory) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(scoutLoanHistory.getId() == null) {
            scoutLoanHistory.setCreatedBy(currentUserName);
            scoutLoanHistory.setDateCreated(new Date());
            scoutLoanHistory.setLastEditedBy(currentUserName);
            scoutLoanHistory.setLastUpdate(new Date());
            scoutLoanHistory.setOperation(DataOperation.CREATED.name());
        }
        else {
            scoutLoanHistory.setLastEditedBy(currentUserName);
            scoutLoanHistory.setLastUpdate(new Date());
            scoutLoanHistory.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(scoutLoanHistory);
    }
}
