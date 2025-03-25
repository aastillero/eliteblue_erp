package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDTR;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.ErpDTRRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ErpDTRService extends CoreErpServiceImpl implements CoreErpService<ErpDTR, Long> {

    @Autowired
    private ErpDTRRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpDTR> getAll() {
        return repository.findAll();
    }

    public List<ErpDTR> getAllFilteredStartAndEndDate(Date startDate, Date endDate) {
        //System.out.println("endDate: "+endDate+" startDate:"+startDate);
        List<ErpDTR> retVal = repository.getAllFilteredStartAndEndDate(endDate, startDate);
        //System.out.println("NUMBER OF FILTERED: "+retVal.size());
        //retVal.stream().forEach(itm->System.out.println("("+itm.getId()+") ["+itm.getErpDetachment().getName()+"] - "+itm.getStatus().name()));
        return retVal;
    }

    public ErpDTR getDetachmentFilteredStartAndEndDate(ErpDetachment detachment, Date startDate, Date endDate) {
        List<ErpDTR> erpDTRS = repository.getDetachmentFilteredStartAndEndDate(detachment, endDate, startDate);
        ErpDTR retVal = null;
        for(ErpDTR dtr: erpDTRS) {
            if(dtr.getStatus().equals(ApprovalStatus.APPROVED)) {
                retVal = dtr;
                break;
            }
        }
        return retVal;
    }

    public List<ErpDTR> getAllDetachmentFilteredStartAndEndDate(ErpDetachment detachment, Date startDate, Date endDate) {
        List<ErpDTR> erpDTRS = repository.getDetachmentFilteredStartAndEndDate(detachment, endDate, startDate);
        return erpDTRS.stream().filter(itm->itm.getStatus().equals(ApprovalStatus.APPROVED)).collect(Collectors.toList());
    }

    public List<ErpDTR> getAllFilteredLocation() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        List<ErpDetachment> detachments = CurrentUserUtil.getDetachments();
        //System.out.println("detachments: "+detachments);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -5);
        Date today = cal.getTime();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.getAllFilteredDate(today);
        } else if (detachments != null && detachments.size() > 0) {
            return repository.getAllFilteredDetachment(detachments, today);
        }
        return repository.getAllFiltered(assignedLocations, today);
    }

    @Override
    public ErpDTR findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpDTR erpDTR) {
        repository.delete(erpDTR);
    }

    @Override
    public void save(ErpDTR erpDTR) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpDTR.getId() == null) {
            erpDTR.setCreatedBy(currentUserName);
            erpDTR.setDateCreated(new Date());
            erpDTR.setLastEditedBy(currentUserName);
            erpDTR.setLastUpdate(new Date());
            erpDTR.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpDTR.setLastEditedBy(currentUserName);
            erpDTR.setLastUpdate(new Date());
            erpDTR.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpDTR);
    }
}
