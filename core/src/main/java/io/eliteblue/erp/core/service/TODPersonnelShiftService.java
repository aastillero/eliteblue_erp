package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.TODClient;
import io.eliteblue.erp.core.model.TODPersonnelShift;
import io.eliteblue.erp.core.repository.TODPersonnelShiftRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TODPersonnelShiftService extends CoreErpServiceImpl implements CoreErpService<TODPersonnelShift, Long> {

    @Autowired
    private TODPersonnelShiftRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<TODPersonnelShift> getAll() {
        return repository.findAll();
    }

    public List<TODPersonnelShift> findByClient(TODClient client) {
        return repository.findByTodClient(client);
    }

    @Override
    public TODPersonnelShift findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(TODPersonnelShift todPersonnelShift) {
        repository.delete(todPersonnelShift);
    }

    @Override
    public void save(TODPersonnelShift todPersonnelShift) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(todPersonnelShift.getId() == null) {
            todPersonnelShift.setCreatedBy(currentUserName);
            todPersonnelShift.setDateCreated(new Date());
            todPersonnelShift.setLastEditedBy(currentUserName);
            todPersonnelShift.setLastUpdate(new Date());
            todPersonnelShift.setOperation(DataOperation.CREATED.name());
        }
        else {
            todPersonnelShift.setLastEditedBy(currentUserName);
            todPersonnelShift.setLastUpdate(new Date());
            todPersonnelShift.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(todPersonnelShift);
    }
}
