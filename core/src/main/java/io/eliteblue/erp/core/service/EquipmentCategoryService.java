package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.repository.EquipmentCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EquipmentCategoryService extends CoreErpServiceImpl implements CoreErpService<EquipmentCategory, Long> {

    @Autowired
    private EquipmentCategoryRepository repository;

    @Override
    public List<EquipmentCategory> getAll() {
        return repository.findAll();
    }

    @Override
    public EquipmentCategory findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public EquipmentCategory findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(EquipmentCategory equipmentCategory) {
        repository.delete(equipmentCategory);
    }

    @Override
    public void save(EquipmentCategory equipmentCategory) {
        if(equipmentCategory.getId() == null) {
            equipmentCategory.setDateCreated(new Date());
            equipmentCategory.setOperation(DataOperation.CREATED.name());
            equipmentCategory.setCreatedBy(super.getCurrentUser());
        }
        else {
            equipmentCategory.setOperation(DataOperation.UPDATED.name());
            equipmentCategory.setLastUpdate(new Date());
            equipmentCategory.setLastEditedBy(super.getCurrentUser());
        }
        equipmentCategory.setLastUpdate(new Date());
        repository.save(equipmentCategory);
    }
}
