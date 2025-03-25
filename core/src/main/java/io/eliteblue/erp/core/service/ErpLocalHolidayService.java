package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpLocalHoliday;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.ErpLocalHolidayRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ErpLocalHolidayService extends CoreErpServiceImpl implements CoreErpService<ErpLocalHoliday, Long> {

    @Autowired
    private ErpLocalHolidayRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpLocalHoliday> getAll() {
        return repository.findAll();
    }

    @Override
    public ErpLocalHoliday findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public List<ErpLocalHoliday> findByDateRange(OperationsArea area, Date start, Date stop) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return repository.findAllBetweenDate(area, format.format(start), format.format(stop));
    }

    @Override
    public void delete(ErpLocalHoliday erpLocalHoliday) {
        repository.delete(erpLocalHoliday);
    }

    @Override
    public void save(ErpLocalHoliday erpLocalHoliday) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpLocalHoliday.getId() == null) {
            erpLocalHoliday.setCreatedBy(currentUserName);
            erpLocalHoliday.setDateCreated(new Date());
            erpLocalHoliday.setLastEditedBy(currentUserName);
            erpLocalHoliday.setLastUpdate(new Date());
            erpLocalHoliday.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpLocalHoliday.setLastEditedBy(currentUserName);
            erpLocalHoliday.setLastUpdate(new Date());
            erpLocalHoliday.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpLocalHoliday);
    }
}
