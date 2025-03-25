package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.repository.EmployeePayrollRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Component
public class EmployeePayrollService extends CoreErpServiceImpl implements CoreErpService<EmployeePayroll, Long> {

    @Autowired
    private EmployeePayrollRepository repository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    @Override
    public List<EmployeePayroll> getAll() {
        return repository.findAll();
    }

    public List<EmployeePayroll> findByEmployeeCutoff(ErpEmployee employee, Date cutOffStart, Date cutOffEnd) {
        return repository.getEmployeeFilteredCutOff(employee, cutOffEnd, cutOffStart);
    }

    @Override
    public EmployeePayroll findById(Long aLong) {
        return repository.getOne(aLong);
    }

    public EmployeePayroll findByEmployeeAndDetachmentPayroll(ErpEmployee emp, DetachmentPayroll dp) {
        List<EmployeePayroll> eps = repository.findByEmployeePayrollAndDetachmentPayroll(emp, dp);
        if(eps != null && eps.size() > 0) {
            return eps.get(0);
        }
        return null;
    }

    @Override
    public void delete(EmployeePayroll employeePayroll) {
        repository.delete(employeePayroll);
    }

    @Override
    public void save(EmployeePayroll employeePayroll) {
        String currentUserName = CurrentUserUtil.getFullName();
        if(employeePayroll.getId() == null) {
            employeePayroll.setCreatedBy(currentUserName);
            employeePayroll.setDateCreated(new Date());
            employeePayroll.setLastEditedBy(currentUserName);
            employeePayroll.setLastUpdate(new Date());
            employeePayroll.setOperation(DataOperation.CREATED.name());
        }
        else {
            employeePayroll.setLastEditedBy(currentUserName);
            employeePayroll.setLastUpdate(new Date());
            employeePayroll.setOperation(DataOperation.UPDATED.name());
        }
        repository.save(employeePayroll);
    }

    public EmployeePayroll computePayroll(EmployeePayroll employeePayroll) {
        BasicSalary basicSalary = generateBasicSalary(employeePayroll.getBasicSalary(), employeePayroll);
        SalaryDeductions salaryDeduction = employeePayroll.getSalaryDeductions();
        Double grossSal = has(basicSalary.getBasicSalary()) ? basicSalary.getBasicSalary() : 0.0;
        if(has(basicSalary.getOt_pay()))
            grossSal += basicSalary.getOt_pay();
        if(has(basicSalary.getNd_pay()))
            grossSal += basicSalary.getNd_pay();
        if(has(basicSalary.getRh_pay()))
            grossSal += basicSalary.getRh_pay();
        if(has(basicSalary.getRh_ot_pay()))
            grossSal += basicSalary.getRh_ot_pay();
        if(has(basicSalary.getRh_nightdiff_pay()))
            grossSal += basicSalary.getRh_nightdiff_pay();
        if(has(basicSalary.getSh_pay()))
            grossSal += basicSalary.getSh_pay();
        if(has(basicSalary.getSh_ot_pay()))
            grossSal += basicSalary.getSh_ot_pay();
        if(has(basicSalary.getSh_nightdiff_pay()))
            grossSal += basicSalary.getSh_nightdiff_pay();
        // extra pay
        if(has(basicSalary.getExtra_pay())) {
            grossSal += basicSalary.getExtra_pay();
        }

        employeePayroll.setGrossSalary(grossSal);
        if(has(salaryDeduction.getTotalDeductions())) {
            employeePayroll.setTotalNetPay(employeePayroll.getGrossSalary() - salaryDeduction.getTotalDeductions());
        } else {
            employeePayroll.setTotalNetPay(employeePayroll.getGrossSalary());
        }
        return employeePayroll;
    }

    public BasicSalary generateBasicSalary(BasicSalary basicSalary, EmployeePayroll employeePayroll) {
        Double hourlyWage = 0.0;

        if(has(basicSalary.getDailyWage()) && basicSalary.getDailyWage() > 0) {
            // already has daily wage
        } else {
            // get wage from Detachment/Location level
            Double rate = has(employeePayroll.getDetachmentPayroll().getDetachment().getLocation().getRateSG()) ? employeePayroll.getDetachmentPayroll().getDetachment().getLocation().getRateSG() : 0.0;
            basicSalary.setDailyWage(rate);
        }
        if(has(basicSalary.getDailyWage()) && basicSalary.getDailyWage() > 0)
            hourlyWage = basicSalary.getDailyWage() / 8;
        basicSalary.setHourlyWage(hourlyWage);

        if(has(basicSalary.getTotalDaysWorked())) {
            basicSalary.setBasicHoursWorked(basicSalary.getTotalDaysWorked() * 8);
        } else {
            if(has(basicSalary.getBasicHoursWorked())) {
                basicSalary.setTotalDaysWorked(basicSalary.getBasicHoursWorked() / 8);
            }
        }
        basicSalary.setBasicSalary(basicSalary.getDailyWage() * basicSalary.getTotalDaysWorked()); // compute basic by numdays * daily wage

        if(has(basicSalary.getOt_no_days())) {
            basicSalary.setOt_total_hours(basicSalary.getOt_no_days() * 4);
        } else {
            if(has(basicSalary.getOt_total_hours())) {
                basicSalary.setOt_no_days(basicSalary.getOt_total_hours() / 4);
            }
        }

        if(has(basicSalary.getNd_no_days())) {
            basicSalary.setNd_total_hours(basicSalary.getNd_no_days() * 8);
        } else {
            if(has(basicSalary.getNd_total_hours())) {
                basicSalary.setNd_no_days(basicSalary.getNd_total_hours() / 8);
            }
        }

        if(has(basicSalary.getOt_rate())) {
            basicSalary.setOt_pay(basicSalary.getOt_rate() * basicSalary.getOt_total_hours());
        }

        if(has(basicSalary.getNd_hourly_rate())) {
            basicSalary.setNd_pay(basicSalary.getNd_hourly_rate() * basicSalary.getNd_total_hours());
        }

        if(has(basicSalary.getRh_no_days())) {
            basicSalary.setRh_pay(basicSalary.getHourlyWage() * (basicSalary.getRh_no_days() * 8));
            if(has(basicSalary.getRh_ot_hrs())) {
                basicSalary.setRh_ot_pay(basicSalary.getRh_ot_rate() * basicSalary.getRh_ot_hrs());
            }
            if(basicSalary.getRh_ot_hrs() == 0 && has(basicSalary.getRh_excess_ot_hrs()) && basicSalary.getRh_excess_ot_hrs() > 0) {
                basicSalary.setRh_ot_pay(basicSalary.getRh_ot_rate() * basicSalary.getRh_excess_ot_hrs());
            }
            if(has(basicSalary.getRh_nightdiff_hrs())) {
                basicSalary.setRh_nightdiff_pay(basicSalary.getRh_nightdiff_rate() * basicSalary.getRh_nightdiff_hrs());
            }
        }

        if(has(basicSalary.getSh_no_days())) {
            basicSalary.setSh_pay(basicSalary.getHourlyWage() * (basicSalary.getSh_no_days() * 8));
            if(has(basicSalary.getSh_ot_hrs())) {
                basicSalary.setSh_ot_pay(basicSalary.getSh_ot_rate() * basicSalary.getSh_ot_hrs());
            }
            if(basicSalary.getSh_ot_hrs() == 0 && has(basicSalary.getSh_excess_ot_hrs()) && basicSalary.getSh_excess_ot_hrs() > 0) {
                basicSalary.setSh_ot_pay(basicSalary.getSh_ot_rate() * basicSalary.getSh_excess_ot_hrs());
            }
            if(has(basicSalary.getSh_nightdiff_hrs())) {
                basicSalary.setSh_nightdiff_pay(basicSalary.getSh_nightdiff_rate() * basicSalary.getSh_nightdiff_hrs());
            }
        }

        return basicSalary;
    }
}
