package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpItem;
import io.eliteblue.erp.core.repository.ErpItemRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpItemService extends CoreErpServiceImpl implements CoreErpService<ErpItem, Long> {

    @Autowired
    private ErpItemRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpItem> getAll() {
        return repository.findAll();
    }

    @Override
    public ErpItem findById(Long erpItemId) {
        return repository.findById(erpItemId).orElse(null);
    }

    @Override
    public void delete(ErpItem erpItem) {
        repository.delete(erpItem);
    }

    @Override
    public void save(ErpItem erpItem) {
        if(erpItem.getId() == null) {
            erpItem.setDateCreated(new Date());
            erpItem.setOperation(DataOperation.CREATED.name());
            erpItem.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpItem.setOperation(DataOperation.UPDATED.name());
            erpItem.setLastUpdate(new Date());
            erpItem.setLastEditedBy(super.getCurrentUser());
        }
        erpItem.setLastUpdate(new Date());
        repository.save(erpItem);
    }
}
