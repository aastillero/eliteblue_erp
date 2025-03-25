package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.CompanyPosition;
import io.eliteblue.erp.core.repository.CompanyPositionRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class CompanyPositionService extends CoreErpServiceImpl implements CoreErpService<CompanyPosition, Long> {

    @Autowired
    private CompanyPositionRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<CompanyPosition> getAll() {
        return repository.findAll();
    }

    @Override
    public CompanyPosition findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public CompanyPosition findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(CompanyPosition companyPosition) {
        repository.delete(companyPosition);
    }

    @Override
    public void save(CompanyPosition companyPosition) {
        if(companyPosition.getId() == null) {
            companyPosition.setDateCreated(new Date());
            companyPosition.setOperation(DataOperation.CREATED.name());
            companyPosition.setCreatedBy(super.getCurrentUser());
        }
        else {
            companyPosition.setOperation(DataOperation.UPDATED.name());
            companyPosition.setLastUpdate(new Date());
            companyPosition.setLastEditedBy(super.getCurrentUser());
        }
        companyPosition.setLastUpdate(new Date());
        repository.save(companyPosition);
    }
}
