package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.TODClient;
import io.eliteblue.erp.core.repository.TODClientRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TODClientService extends CoreErpServiceImpl implements CoreErpService<TODClient, Long> {

    @Autowired
    private TODClientRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<TODClient> getAll() {
        return repository.findAll();
    }

    @Override
    public TODClient findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public List<TODClient> findByDetachment(ErpDetachment detachment) {
        return repository.findByErpDetachment(detachment);
    }

    @Override
    public void delete(TODClient todClient) {
        repository.delete(todClient);
    }

    @Override
    public void save(TODClient todClient) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(todClient.getId() == null) {
            todClient.setCreatedBy(currentUserName);
            todClient.setDateCreated(new Date());
            todClient.setLastEditedBy(currentUserName);
            todClient.setLastUpdate(new Date());
            todClient.setOperation(DataOperation.CREATED.name());
        }
        else {
            todClient.setLastEditedBy(currentUserName);
            todClient.setLastUpdate(new Date());
            todClient.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(todClient);
    }
}
