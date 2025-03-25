package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.RequestDelivery;
import io.eliteblue.erp.core.repository.RequestDeliveryRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RequestDeliveryService extends CoreErpServiceImpl implements CoreErpService<RequestDelivery, Long> {

    @Autowired
    private RequestDeliveryRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<RequestDelivery> getAll() {
        return repository.findAll();
    }

    @Override
    public RequestDelivery findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(RequestDelivery requestDelivery) {
        repository.delete(requestDelivery);
    }

    @Override
    public void save(RequestDelivery requestDelivery) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(requestDelivery.getId() == null) {
            requestDelivery.setCreatedBy(currentUserName);
            requestDelivery.setDateCreated(new Date());
            requestDelivery.setLastEditedBy(currentUserName);
            requestDelivery.setLastUpdate(new Date());
            requestDelivery.setOperation(DataOperation.CREATED.name());
        }
        else {
            requestDelivery.setLastEditedBy(currentUserName);
            requestDelivery.setLastUpdate(new Date());
            requestDelivery.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(requestDelivery);
    }
}
