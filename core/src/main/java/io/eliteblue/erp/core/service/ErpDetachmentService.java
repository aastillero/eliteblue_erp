package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpEquipment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.repository.ErpDetachmentRepository;
import io.eliteblue.erp.core.repository.ErpEquipmentRepository;
import io.eliteblue.erp.core.repository.ErpUserRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ErpDetachmentService extends CoreErpServiceImpl implements CoreErpService<ErpDetachment, Long> {

    @Autowired
    private ErpDetachmentRepository repository;

    @Autowired
    private ErpUserRepository erpUserRepository;

    @Autowired
    private ErpEquipmentRepository erpEquipmentRepository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    public List<ErpDetachment> getAll() {
        return repository.findAll();
    }

    public List<ErpDetachment> getAllFilteredLocation() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        return repository.findByLocationIn(assignedLocations);
    }

    public List<ErpDetachment> getAllFilteredDetachment() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        List<ErpDetachment> detachments = CurrentUserUtil.getDetachments();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.findByLocationIn(assignedLocations);
        }
        if (detachments != null && detachments.size() > 0) {
            List<Long> detachmentIds = detachments.stream().map(ErpDetachment::getId).collect(Collectors.toList());
            return repository.getAllFilteredDetachment(detachmentIds);
        }
        return repository.findByLocationIn(assignedLocations);
    }

    public List<ErpDetachment> getAllCurrentDetachment() {
        return CurrentUserUtil.getDetachments();
    }

    public List<ErpDetachment> filteredLocationOrCurrent() {
        if(getAllCurrentDetachment() != null && getAllCurrentDetachment().size() > 0) {
            return getAllCurrentDetachment();
        } else {
            return getAllFilteredLocation();
        }
    }

    public ErpDetachment findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public void delete(ErpDetachment erpDetachment) {
        // remove references from ErpUser
        List<ErpUser> erpUsers = erpUserRepository.findByErpDetachmentsContaining(erpDetachment);
        List<ErpDetachment> detachments = new ArrayList<>();
        detachments.add(erpDetachment);
        //List<ErpEquipment> erpEquipments = erpEquipmentRepository.findByDetachmentIn(detachments);
        for(ErpUser u: erpUsers) {
            u.setErpDetachments(null);
            erpUserRepository.save(u);
        }
        /*for(ErpEquipment e: erpEquipments) {
            e.setDetachment(null);
            erpEquipmentRepository.save(e);
        }*/
        repository.delete(erpDetachment);
    }

    public void save(ErpDetachment erpDetachment) {
        if(erpDetachment.getId() == null) {
            erpDetachment.setDateCreated(new Date());
            erpDetachment.setOperation(DataOperation.CREATED.name());
            erpDetachment.setCreatedBy(super.getCurrentUser());
        }
        else {
            erpDetachment.setOperation(DataOperation.UPDATED.name());
            erpDetachment.setLastUpdate(new Date());
            erpDetachment.setLastEditedBy(super.getCurrentUser());
        }
        erpDetachment.setLastUpdate(new Date());
        repository.save(erpDetachment);
    }
}
