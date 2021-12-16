package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpConfig;
import io.eliteblue.erp.core.repository.ErpConfigRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpConfigService extends CoreErpServiceImpl implements CoreErpService<ErpConfig, Long> {

    @Autowired
    private ErpConfigRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpConfig> getAll() {
        return repository.findAll();
    }

    @Override
    public ErpConfig findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public ErpConfig findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public void delete(ErpConfig erpConfig) {
        repository.delete(erpConfig);
    }

    @Override
    public void save(ErpConfig erpConfig) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpConfig.getId() == null) {
            erpConfig.setCreatedBy(currentUserName);
            erpConfig.setDateCreated(new Date());
            erpConfig.setLastEditedBy(currentUserName);
            erpConfig.setLastUpdate(new Date());
            erpConfig.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpConfig.setLastEditedBy(currentUserName);
            erpConfig.setLastUpdate(new Date());
            erpConfig.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpConfig);
    }
}
