package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEquipment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.identifiers.ErpEquipmentId;
import io.eliteblue.erp.core.repository.ErpEquipmentRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ErpEquipmentService extends CoreErpServiceImpl implements CoreErpService<ErpEquipment, ErpEquipmentId>{

    @Autowired
    private ErpEquipmentRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpEquipment> getAll() {
        return repository.findAll();
    }

    public List<ErpEquipment> getAllFilteredByDetachments() {
        List<ErpDetachment> assignedDetachments = CurrentUserUtil.getDetachments();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.findAll();
        }
        return repository.findByDetachmentIn(assignedDetachments);
    }

    public List<ErpEquipment> getAllFilteredByDetachment(ErpDetachment detachment) {
        return repository.findByDetachment(detachment);
    }

    public List<ErpEquipment> getAllDetachmentIn(List<ErpDetachment> detachments) {
        return repository.findByDetachmentIn(detachments);
    }

    public List<ErpEquipment> getAllFilteredByLocation() {
        List<OperationsArea> assignedLocation = CurrentUserUtil.getOperationsAreas();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.findAll();
        }
        return repository.findByLocationIn(assignedLocation);
    }

    public List<ErpEquipment> getAllFilteredByDetachmentLocation() {
        List<OperationsArea> assignedLocation = CurrentUserUtil.getOperationsAreas();
        List<ErpDetachment> detachments = CurrentUserUtil.getDetachments();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.findAll();
        } else if(detachments != null && detachments.size() > 0) {
            return repository.findByDetachmentIn(detachments);
        } else if(assignedLocation != null && assignedLocation.size() > 0) {
            // no detachments assigned
            // get filtered locations
            return getAllFilteredByLocation();
        } else {
            return null;
        }
        //return repository.getByDetachmentLocation(assignedLocation);
    }

    @Override
    public ErpEquipment findById(ErpEquipmentId erpEquipmentId) {
        return repository.findById(erpEquipmentId).orElse(null);
    }

    public ErpEquipment findByCodenum(String refNum) {
        return repository.findByRefNum(refNum);
    }

    @Override
    public void delete(ErpEquipment erpEquipment) {
        repository.delete(erpEquipment);
    }

    @Override
    public void save(ErpEquipment erpEquipment) {
        if(erpEquipment.getId() == null) {
            erpEquipment.setDateCreated(new Date());
            erpEquipment.setOperation(DataOperation.CREATED.name());
            erpEquipment.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpEquipment.setOperation(DataOperation.UPDATED.name());
            erpEquipment.setLastUpdate(new Date());
            erpEquipment.setLastEditedBy(super.getCurrentUser());
        }
        erpEquipment.setLastUpdate(new Date());
        repository.save(erpEquipment);
    }
}
