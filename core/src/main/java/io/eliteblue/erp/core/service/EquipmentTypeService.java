package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EquipmentCategory;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import io.eliteblue.erp.core.model.EquipmentType;
import io.eliteblue.erp.core.repository.EquipmentTypeRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EquipmentTypeService extends CoreErpServiceImpl implements CoreErpService<EquipmentType, Long> {

    @Autowired
    private EquipmentTypeRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<EquipmentType> getAll() {
        return repository.findAll();
    }

    @Override
    public EquipmentType findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public EquipmentType findByName(String name) {
        return repository.findByName(name);
    }

    public List<EquipmentType> findByCategoryAndSubCategory(EquipmentCategory category, EquipmentSubCategory subCategory) {
        return repository.findByCategoryAndSubCategory(category, subCategory);
    }

    @Override
    public void delete(EquipmentType equipmentType) {
        repository.delete(equipmentType);
    }

    @Override
    public void save(EquipmentType equipmentType) {
        if(equipmentType.getId() == null) {
            equipmentType.setDateCreated(new Date());
            equipmentType.setOperation(DataOperation.CREATED.name());
            equipmentType.setCreatedBy(super.getCurrentUser());
        }
        else {
            equipmentType.setOperation(DataOperation.UPDATED.name());
            equipmentType.setLastUpdate(new Date());
            equipmentType.setLastEditedBy(super.getCurrentUser());
        }
        equipmentType.setLastUpdate(new Date());
        repository.save(equipmentType);
    }
}
