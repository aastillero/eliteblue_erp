package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpHoliday;
import io.eliteblue.erp.core.repository.ErpHolidayRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpHolidayService extends CoreErpServiceImpl implements CoreErpService<ErpHoliday, Long> {

    @Autowired
    private ErpHolidayRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpHoliday> getAll() {
        return repository.findAll();
    }

    public ErpHoliday findByName(String name) {
        return repository.findByName(name);
    }

    public ErpHoliday findByDate(Date d) {
        return repository.findByHolidayDate(d);
    }

    @Override
    public ErpHoliday findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpHoliday erpHoliday) {
        repository.delete(erpHoliday);
    }

    @Override
    public void save(ErpHoliday erpHoliday) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpHoliday.getId() == null) {
            erpHoliday.setCreatedBy(currentUserName);
            erpHoliday.setDateCreated(new Date());
            erpHoliday.setLastEditedBy(currentUserName);
            erpHoliday.setLastUpdate(new Date());
            erpHoliday.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpHoliday.setLastEditedBy(currentUserName);
            erpHoliday.setLastUpdate(new Date());
            erpHoliday.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpHoliday);
    }
}
