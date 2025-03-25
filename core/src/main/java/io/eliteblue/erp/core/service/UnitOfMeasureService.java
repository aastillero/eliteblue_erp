package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.UnitOfMeasure;
import io.eliteblue.erp.core.repository.UnitOfMeasureRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class UnitOfMeasureService extends CoreErpServiceImpl implements CoreErpService<UnitOfMeasure, Long> {

    @Autowired
    private UnitOfMeasureRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<UnitOfMeasure> getAll() {
        return repository.findAll();
    }

    @Override
    public UnitOfMeasure findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public UnitOfMeasure findByMeasure(String measure) {
        return repository.findByMeasure(measure);
    }

    @Override
    public void delete(UnitOfMeasure unitOfMeasure) {
        repository.delete(unitOfMeasure);
    }

    @Override
    public void save(UnitOfMeasure unitOfMeasure) {
        if(unitOfMeasure.getId() == null) {
            unitOfMeasure.setDateCreated(new Date());
            unitOfMeasure.setOperation(DataOperation.CREATED.name());
            unitOfMeasure.setCreatedBy(super.getCurrentUser());
        }
        else {
            unitOfMeasure.setOperation(DataOperation.UPDATED.name());
            unitOfMeasure.setLastUpdate(new Date());
            unitOfMeasure.setLastEditedBy(super.getCurrentUser());
        }
        unitOfMeasure.setLastUpdate(new Date());
        repository.save(unitOfMeasure);
    }
}
