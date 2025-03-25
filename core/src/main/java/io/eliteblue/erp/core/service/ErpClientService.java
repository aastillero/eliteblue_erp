package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.model.ErpClient;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.ErpClientRepository;
import io.eliteblue.erp.core.repository.ErpDetachmentRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpClientService extends CoreErpServiceImpl implements CoreErpService<ErpClient, Long> {

    @Autowired
    private ErpClientRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    public List<ErpClient> getAll() {
        return repository.findAll();
    }

    public List<ErpClient> getByClientStatus(ClientStatus status) {
        return repository.findByClientStatus(status);
    }

    public List<ErpClient> getAllFilteredLocation() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        return repository.findByDetachmentLocations(assignedLocations);
    }

    public ErpClient findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public void delete(ErpClient erpClient) {
        repository.delete(erpClient);
    }

    public void save(ErpClient erpClient) {
        if(erpClient.getId() == null) {
            erpClient.setDateCreated(new Date());
            erpClient.setOperation(DataOperation.CREATED.name());
            erpClient.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpClient.setOperation(DataOperation.UPDATED.name());
            erpClient.setLastUpdate(new Date());
            erpClient.setLastEditedBy(super.getCurrentUser());
        }
        erpClient.setLastUpdate(new Date());
        repository.save(erpClient);
    }
}
