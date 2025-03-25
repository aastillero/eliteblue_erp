package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.bean.SessionMB;
import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.constants.EmployeeStatus;
import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.repository.ErpEmployeeRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ErpEmployeeService extends CoreErpServiceImpl implements CoreErpService<ErpEmployee, Long> {

    @Autowired
    private ErpEmployeeRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private final DecimalFormat decimalFormat = new DecimalFormat("00000000");
    private final DecimalFormat areaFormat = new DecimalFormat("00");

    @Override
    public List<ErpEmployee> getAll() {
        return repository.findAll();
    }

    public List<ErpEmployee> getAllHired() {
        return repository.getAllHired();
    }

    public List<ErpEmployee> getAllFiltered() {
        List<OperationsArea> assignedLocations = CurrentUserUtil.getOperationsAreas();
        if(CurrentUserUtil.isAdmin()) {
            return repository.findAll();
        }
        if(CurrentUserUtil.isHeadOffice()) {
            return repository.getAllFilteredByDeleted();
        }
        return repository.getAllFiltered(assignedLocations);
    }

    public List<ErpEmployee> getAllByAssignedLocation() {
        OperationsArea assignedLocation = null;
        return repository.findByAssignedLocation(assignedLocation);
    }

    @Override
    public ErpEmployee findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public List<ErpEmployee> findByFullName(String fullName) {
        String[] nameParts;
        String firstName = null;
        String lastName = null;
        if (fullName.contains(",")) {
            // Last name first with comma separator
            nameParts = fullName.split(",");
            if (nameParts.length >= 2) { // Ensure there are at least two parts
                lastName = nameParts[0].trim();
                firstName = nameParts[1].trim();
            } else {
                // Handle invalid format gracefully
                fullName = fullName.replaceAll(",", "");
                nameParts = fullName.split("\\s+");
                lastName = nameParts[nameParts.length - 1];
                firstName = fullName.substring(0, fullName.length() - lastName.length()).trim();
            }
        } else {
            // Regular case, split by whitespace
            nameParts = fullName.split("\\s+");
            lastName = nameParts[nameParts.length - 1];
            firstName = fullName.substring(0, fullName.length() - lastName.length()).trim();
        }

        if(firstName != null && lastName != null) {
            List<ErpEmployee> _listTmp = repository.findByLastnameIgnoreCaseAndFirstnameIgnoreCase(lastName, firstName);
            if(_listTmp.size() > 0) {
                //System.out.println("LASTNAME: "+lastName+", FIRSTNAME: "+firstName);
                return _listTmp;
            } else {
                //System.out.println("LASTNAME: "+firstName+", FIRSTNAME: "+lastName);
                _listTmp = repository.findByLastnameIgnoreCaseAndFirstnameIgnoreCase(firstName, lastName);
                if(_listTmp.size() > 0) {
                    return _listTmp;
                } else if(nameParts.length > 2) {
                    lastName = nameParts[0];
                    firstName = nameParts[1] + " " +nameParts[2];
                    _listTmp = repository.findByLastnameIgnoreCaseAndFirstnameIgnoreCase(lastName, firstName);
                    if(_listTmp.size() > 0) {
                        return _listTmp;
                    } else {
                        return repository.findByLastnameIgnoreCaseAndFirstnameIgnoreCase(firstName, lastName);
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public List<ErpEmployee> findByFirstnameAndLastname(String firstname, String lastname) {
        return repository.findByFirstnameIgnoreCaseAndLastnameIgnoreCase(firstname, lastname);
    }

    @Override
    public void delete(ErpEmployee erpEmployee) {
        String currentUserName = CurrentUserUtil.getFullName();
        erpEmployee.setOperation(DataOperation.REMOVED.name());
        erpEmployee.setStatus(EmployeeStatus.DELETED);
        erpEmployee.setLastEditedBy(currentUserName);
        erpEmployee.setLastUpdate(new Date());
        repository.save(erpEmployee);
    }

    @Override
    public void save(ErpEmployee erpEmployee) {
        String currentUserName = CurrentUserUtil.getFullName();
        List<OperationsArea> areas = CurrentUserUtil.getOperationsAreas();
        String operationAreaCode = "99";
        for(OperationsArea o: areas) {
            operationAreaCode = areaFormat.format(o.getId());
        }
        if(erpEmployee.getId() == null) {
            erpEmployee.setOperation(DataOperation.CREATED.name());
            erpEmployee.setLastEditedBy(currentUserName);
            erpEmployee.setCreatedBy(currentUserName);
            erpEmployee.setLastUpdate(new Date());
            erpEmployee.setDateCreated(new Date());
            BigDecimal seq = repository.getNextSequenceByName("emp_area_"+operationAreaCode+"_seq");
            Long seqNum = seq.longValue();
            /*if(seq.intValue() <= 1) {
                Integer isEmpty = repository.getIsEmpty();
                if(isEmpty != 0){
                    seqNum += 1;
                }
            }
            else {
                seqNum += 1;
            }*/
            erpEmployee.setEmployeeId(operationAreaCode + decimalFormat.format(seqNum));
        }
        else {
            erpEmployee.setOperation(DataOperation.UPDATED.name());
            erpEmployee.setLastEditedBy(currentUserName);
            erpEmployee.setLastUpdate(new Date());
        }
        repository.save(erpEmployee);
    }

    public void saveAll(List<ErpEmployee> employees) {
        repository.saveAll(employees);
    }
}
