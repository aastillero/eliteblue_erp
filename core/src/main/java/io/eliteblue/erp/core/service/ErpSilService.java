package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.ErpSil;
import io.eliteblue.erp.core.repository.ErpSilRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpSilService extends CoreErpServiceImpl implements CoreErpService<ErpSil, Long> {

    @Autowired
    private ErpSilRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpSil> getAll() {
        return repository.findAll();
    }

    public List<ErpSil> findAllByEmployee(ErpEmployee e) {
        return repository.findAllByEmployee(e);
    }

    public List<ErpSil> findAllByDetachment(ErpDetachment d) {
        return repository.findAllByDetachment(d);
    }

    @Override
    public ErpSil findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpSil erpSil) {
        repository.delete(erpSil);
    }

    @Override
    public void save(ErpSil erpSil) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpSil.getId() == null) {
            erpSil.setCreatedBy(currentUserName);
            erpSil.setDateCreated(new Date());
            erpSil.setLastEditedBy(currentUserName);
            erpSil.setLastUpdate(new Date());
            erpSil.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpSil.setLastEditedBy(currentUserName);
            erpSil.setLastUpdate(new Date());
            erpSil.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpSil);
    }
}
