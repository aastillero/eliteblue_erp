package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EquipmentStatus;
import io.eliteblue.erp.core.repository.EquipmentStatusRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EquipmentStatusService extends CoreErpServiceImpl implements CoreErpService<EquipmentStatus, Long> {

    @Autowired
    private EquipmentStatusRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<EquipmentStatus> getAll() {
        return repository.findAll();
    }

    @Override
    public EquipmentStatus findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public EquipmentStatus findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(EquipmentStatus equipmentStatus) {
        repository.delete(equipmentStatus);
    }

    @Override
    public void save(EquipmentStatus equipmentStatus) {
        if(equipmentStatus.getId() == null) {
            equipmentStatus.setDateCreated(new Date());
            equipmentStatus.setOperation(DataOperation.CREATED.name());
            equipmentStatus.setCreatedBy(super.getCurrentUser());
        }
        else {
            equipmentStatus.setOperation(DataOperation.UPDATED.name());
            equipmentStatus.setLastUpdate(new Date());
            equipmentStatus.setLastEditedBy(super.getCurrentUser());
        }
        equipmentStatus.setLastUpdate(new Date());
        repository.save(equipmentStatus);
    }
}
