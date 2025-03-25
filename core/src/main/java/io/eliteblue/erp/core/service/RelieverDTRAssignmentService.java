package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.RelieverDTRAssignment;
import io.eliteblue.erp.core.repository.RelieverDTRAssignmentRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RelieverDTRAssignmentService extends CoreErpServiceImpl implements CoreErpService<RelieverDTRAssignment, Long> {

    @Autowired
    private RelieverDTRAssignmentRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<RelieverDTRAssignment> getAll() {
        return repository.findAll();
    }

    @Override
    public RelieverDTRAssignment findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(RelieverDTRAssignment relieverDTRAssignment) {
        repository.delete(relieverDTRAssignment);
    }

    @Override
    public void save(RelieverDTRAssignment relieverDTRAssignment) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(relieverDTRAssignment.getId() == null) {
            relieverDTRAssignment.setCreatedBy(currentUserName);
            relieverDTRAssignment.setDateCreated(new Date());
            relieverDTRAssignment.setLastEditedBy(currentUserName);
            relieverDTRAssignment.setLastUpdate(new Date());
            relieverDTRAssignment.setOperation(DataOperation.CREATED.name());
        }
        else {
            relieverDTRAssignment.setLastEditedBy(currentUserName);
            relieverDTRAssignment.setLastUpdate(new Date());
            relieverDTRAssignment.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(relieverDTRAssignment);
    }
}
