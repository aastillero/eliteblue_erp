package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.EquipmentSubCategory;
import io.eliteblue.erp.core.repository.EquipmentSubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EquipmentSubCategoryService extends CoreErpServiceImpl implements CoreErpService<EquipmentSubCategory, Long> {

    @Autowired
    private EquipmentSubCategoryRepository repository;

    @Override
    public List<EquipmentSubCategory> getAll() {
        return repository.findAll();
    }

    @Override
    public EquipmentSubCategory findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public EquipmentSubCategory findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(EquipmentSubCategory equipmentSubCategory) {
        repository.delete(equipmentSubCategory);
    }

    @Override
    public void save(EquipmentSubCategory equipmentSubCategory) {
        if(equipmentSubCategory.getId() == null) {
            equipmentSubCategory.setDateCreated(new Date());
            equipmentSubCategory.setOperation(DataOperation.CREATED.name());
            equipmentSubCategory.setCreatedBy(super.getCurrentUser());
        }
        else {
            equipmentSubCategory.setOperation(DataOperation.UPDATED.name());
            equipmentSubCategory.setLastUpdate(new Date());
            equipmentSubCategory.setLastEditedBy(super.getCurrentUser());
        }
        equipmentSubCategory.setLastUpdate(new Date());
        repository.save(equipmentSubCategory);
    }
}
