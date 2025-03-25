package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDTR;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpWorkSchedule;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.WorkScheduleRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class WorkScheduleService extends CoreErpServiceImpl implements CoreErpService<ErpWorkSchedule, Long> {

    @Autowired
    private WorkScheduleRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<ErpWorkSchedule> getAll() {
        return repository.findAll();
    }

    public List<ErpWorkSchedule> getAllFilteredLocation() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        List<ErpDetachment> detachments = CurrentUserUtil.getDetachments();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -5);
        Date today = cal.getTime();
        if(CurrentUserUtil.isAdmin() || CurrentUserUtil.isHeadOffice()) {
            return repository.getAllFilteredDate(today);
        } else if(detachments != null && detachments.size() > 0) {
            return repository.getAllFilteredDetachment(detachments, today);
        } else {
            return repository.getAllFiltered(assignedLocations, today);
        }
    }

    public List<ErpWorkSchedule> getAllFilteredStartAndEndDate(Date startDate, Date endDate) {
        //System.out.println("endDate: "+endDate+" startDate:"+startDate);
        List<ErpWorkSchedule> retVal = repository.getAllFilteredStartAndEndDate(endDate, startDate);
        //System.out.println("NUMBER OF FILTERED: "+retVal.size());
        //retVal.stream().forEach(itm->System.out.println("("+itm.getId()+") ["+itm.getErpDetachment().getName()+"] - "+itm.getStatus().name()));
        return retVal;
    }

    public ErpWorkSchedule getDetachmentFilteredStartAndEndDate(ErpDetachment detachment, Date startDate, Date endDate) {
        List<ErpWorkSchedule> erpWorkSchedules = repository.getWorkSchedFilteredStartAndEndDate(detachment, endDate, startDate);
        ErpWorkSchedule retVal = null;
        for(ErpWorkSchedule workSchedule: erpWorkSchedules) {
            if(workSchedule.getStatus().equals(ApprovalStatus.APPROVED)) {
                retVal = workSchedule;
                break;
            }
        }
        return retVal;
    }

    @Override
    public ErpWorkSchedule findById(Long aLong) {
        return repository.getOne(aLong);
    }

    @Override
    public void delete(ErpWorkSchedule erpWorkSchedule) {
        repository.delete(erpWorkSchedule);
    }

    @Override
    public void save(ErpWorkSchedule erpWorkSchedule) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(erpWorkSchedule.getId() == null) {
            erpWorkSchedule.setCreatedBy(currentUserName);
            erpWorkSchedule.setDateCreated(new Date());
            erpWorkSchedule.setLastEditedBy(currentUserName);
            erpWorkSchedule.setLastUpdate(new Date());
            erpWorkSchedule.setOperation(DataOperation.CREATED.name());
        }
        else {
            erpWorkSchedule.setLastEditedBy(currentUserName);
            erpWorkSchedule.setLastUpdate(new Date());
            erpWorkSchedule.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(erpWorkSchedule);
    }
}
