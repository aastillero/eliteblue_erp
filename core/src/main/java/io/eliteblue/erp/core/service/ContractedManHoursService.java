package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ContractedManHours;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.ContractedManHoursRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ContractedManHoursService extends CoreErpServiceImpl implements CoreErpService<ContractedManHours, Long> {

    @Autowired
    private ContractedManHoursRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ContractedManHours> getAll() {
        return repository.findAll();
    }

    public List<ContractedManHours> getFilteredDetachmentAndPeriod(ErpDetachment detachment, Date startDate, Date endDate) {
        if (detachment != null && startDate != null && endDate != null) {
            return repository.getAllFilteredDetachment(detachment, startDate, endDate);
        }
        return null;
    }

    @Override
    public ContractedManHours findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ContractedManHours contractedManHours) {
        repository.delete(contractedManHours);
    }

    @Override
    public void save(ContractedManHours contractedManHours) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(contractedManHours.getId() == null) {
            contractedManHours.setCreatedBy(currentUserName);
            contractedManHours.setDateCreated(new Date());
            contractedManHours.setLastEditedBy(currentUserName);
            contractedManHours.setLastUpdate(new Date());
            contractedManHours.setOperation(DataOperation.CREATED.name());
        }
        else {
            contractedManHours.setLastEditedBy(currentUserName);
            contractedManHours.setLastUpdate(new Date());
            contractedManHours.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(contractedManHours);
    }
}
