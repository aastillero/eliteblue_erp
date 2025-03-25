package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpSSSContribution;
import io.eliteblue.erp.core.repository.ErpSSSContributionRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpSSSContributionService extends CoreErpServiceImpl implements CoreErpService<ErpSSSContribution, Long> {

    @Autowired
    private ErpSSSContributionRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpSSSContribution> getAll() {
        return repository.findAll();
    }

    public List<ErpSSSContribution> getAllFromRange(Double salary) {
        return repository.getByRange(salary);
    }

    @Override
    public ErpSSSContribution findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpSSSContribution erpSSSContribution) {
        repository.delete(erpSSSContribution);
    }

    @Override
    public void save(ErpSSSContribution erpSSSContribution) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpSSSContribution.getId() == null) {
            erpSSSContribution.setCreatedBy(currentUserName);
            erpSSSContribution.setDateCreated(new Date());
            erpSSSContribution.setLastEditedBy(currentUserName);
            erpSSSContribution.setLastUpdate(new Date());
            erpSSSContribution.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpSSSContribution.setLastEditedBy(currentUserName);
            erpSSSContribution.setLastUpdate(new Date());
            erpSSSContribution.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpSSSContribution);
    }
}
