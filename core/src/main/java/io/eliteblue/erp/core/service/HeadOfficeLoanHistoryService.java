package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.HeadOfficeLoanHistory;
import io.eliteblue.erp.core.repository.HeadOfficeLoanHistoryRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class HeadOfficeLoanHistoryService extends CoreErpServiceImpl implements CoreErpService<HeadOfficeLoanHistory, Long> {

    @Autowired
    private HeadOfficeLoanHistoryRepository repository;

    @Override
    public List<HeadOfficeLoanHistory> getAll() {
        return repository.findAll();
    }

    @Override
    public HeadOfficeLoanHistory findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(HeadOfficeLoanHistory headOfficeLoanHistory) {
        repository.delete(headOfficeLoanHistory);
    }

    @Override
    public void save(HeadOfficeLoanHistory headOfficeLoanHistory) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(headOfficeLoanHistory.getId() == null) {
            headOfficeLoanHistory.setCreatedBy(currentUserName);
            headOfficeLoanHistory.setDateCreated(new Date());
            headOfficeLoanHistory.setLastEditedBy(currentUserName);
            headOfficeLoanHistory.setLastUpdate(new Date());
            headOfficeLoanHistory.setOperation(DataOperation.CREATED.name());
        }
        else {
            headOfficeLoanHistory.setLastEditedBy(currentUserName);
            headOfficeLoanHistory.setLastUpdate(new Date());
            headOfficeLoanHistory.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(headOfficeLoanHistory);
    }
}
