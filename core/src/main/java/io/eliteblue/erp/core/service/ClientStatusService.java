package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.repository.ClientStatusRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ClientStatusService extends CoreErpServiceImpl implements CoreErpService<ClientStatus, Long> {

    @Autowired
    private ClientStatusRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ClientStatus> getAll() {
        return repository.findAll();
    }

    @Override
    public ClientStatus findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public ClientStatus findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(ClientStatus clientStatus) {
        repository.delete(clientStatus);
    }

    @Override
    public void save(ClientStatus clientStatus) {
        if(clientStatus.getId() == null) {
            clientStatus.setDateCreated(new Date());
            clientStatus.setOperation(DataOperation.CREATED.name());
            clientStatus.setCreatedBy(super.getCurrentUser());
        }
        else {
            clientStatus.setOperation(DataOperation.UPDATED.name());
            clientStatus.setLastUpdate(new Date());
            clientStatus.setLastEditedBy(super.getCurrentUser());
        }
        clientStatus.setLastUpdate(new Date());
        repository.save(clientStatus);
    }
}
