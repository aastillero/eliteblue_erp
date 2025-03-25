package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EmploymentHistory;
import io.eliteblue.erp.core.repository.EmploymentHistoryRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EmploymentHistoryService extends CoreErpServiceImpl implements CoreErpService<EmploymentHistory, Long> {

    @Autowired
    private EmploymentHistoryRepository repository;

    @Override
    public List<EmploymentHistory> getAll() {
        return repository.findAll();
    }

    @Override
    public EmploymentHistory findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(EmploymentHistory employmentHistory) {
        repository.delete(employmentHistory);
    }

    @Override
    public void save(EmploymentHistory employmentHistory) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(employmentHistory.getId() == null) {
            employmentHistory.setCreatedBy(currentUserName);
            employmentHistory.setDateCreated(new Date());
            employmentHistory.setLastEditedBy(currentUserName);
            employmentHistory.setLastUpdate(new Date());
            employmentHistory.setOperation(DataOperation.CREATED.name());
        }
        else {
            employmentHistory.setLastEditedBy(currentUserName);
            employmentHistory.setLastUpdate(new Date());
            employmentHistory.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(employmentHistory);
    }
}
