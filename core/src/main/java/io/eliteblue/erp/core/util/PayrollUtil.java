package io.eliteblue.erp.core.util;

import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.SalaryDeductionsService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.adminfaces.template.util.Assert.has;

@Component
public class PayrollUtil {

    @Autowired
    private SalaryDeductionsService salaryDeductionsService;

    public EmployeePayroll calculateEmployeePayroll(EmployeePayroll payroll) {
        BasicSalary basicSalary = calculateBasicSalary(payroll);
        SalaryDeductions salaryDeduction = calculateSalaryDeductions(payroll);
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
        // generate adjustments
        if(has(payroll.getEcola()))
            grossSal += payroll.getEcola();
        if(has(payroll.getAllowance()))
            grossSal += payroll.getAllowance();
        if(has(payroll.getSil()))
            grossSal += payroll.getSil();
        if(has(payroll.getPaternityLeave()))
            grossSal += payroll.getPaternityLeave();
        if(has(payroll.getThirteenthMonth()))
            grossSal += payroll.getThirteenthMonth();
        if(has(payroll.getAdjustments()))
            grossSal += payroll.getAdjustments();
        if(has(payroll.getBasicSalary().getExtra_pay())) {
            grossSal += payroll.getBasicSalary().getExtra_pay();
        }

        payroll.setGrossSalary(grossSal);
        payroll.setBasicSalary(basicSalary);
        payroll.setSalaryDeductions(salaryDeduction);
        if(has(salaryDeduction.getTotalDeductions())) {
            payroll.setTotalNetPay(payroll.getGrossSalary() - salaryDeduction.getTotalDeductions());
        } else {
            payroll.setTotalNetPay(payroll.getGrossSalary());
        }
        return payroll;
    }

    public BasicSalary calculateBasicSalary(EmployeePayroll payroll) {
        BasicSalary basicSalary = payroll.getBasicSalary();

        if(!has(basicSalary.getHourlyWage())) {
            if(has(basicSalary.getDailyWage())) {
                Double hourlyWage = basicSalary.getDailyWage() / 8;
                basicSalary.setHourlyWage(hourlyWage);
            }
        }
        // compute basic
        if(has(basicSalary.getDailyWage()) && has(basicSalary.getTotalDaysWorked()))
            basicSalary.setBasicSalary(basicSalary.getDailyWage() * basicSalary.getTotalDaysWorked());

        if(!has(basicSalary.getNd_hourly_rate()) && has(basicSalary.getHourlyWage()))
            basicSalary.setNd_hourly_rate(basicSalary.getHourlyWage() * 0.8);

        if(has(basicSalary.getDailyWage())) {
            if(has(basicSalary.getRd_reg_day_ot()))
                basicSalary.setRd_reg_day_ot((basicSalary.getDailyWage() * 0.3) + basicSalary.getDailyWage());
            if(has(basicSalary.getRd_eight_hrs_rate()))
                basicSalary.setRd_eight_hrs_rate((basicSalary.getDailyWage() * 1.3) - basicSalary.getDailyWage());
            //if(has(basicSalary.getRd_twelve_hrs_rate()))
            //basicSalary.setRd_twelve_hrs_rate(basicSalary.getDailyWage() * (1.3/8) * 1.3 * (4-(basicSalary.getRd_reg_day_ot()/8*4)) + basicSalary.getRd_eight_hrs_rate());
        }

        if(has(basicSalary.getHourlyWage())) {
            if(has(basicSalary.getRh_ot_rate()))
                basicSalary.setRh_ot_rate(basicSalary.getHourlyWage() * 2.6);
            if(has(basicSalary.getRh_nightdiff_rate()))
                basicSalary.setRh_nightdiff_rate(basicSalary.getHourlyWage() * 1.6);
            if(has(basicSalary.getSh_ot_rate()))
                basicSalary.setSh_ot_rate(basicSalary.getHourlyWage() * 1.69);
            if(has(basicSalary.getSh_nightdiff_rate()))
                basicSalary.setSh_nightdiff_rate(basicSalary.getHourlyWage() * 1.04);
        }

        return basicSalary;
    }

    public SalaryDeductions calculateSalaryDeductions(EmployeePayroll payroll) {
        //SalaryDeductions deductions = salaryDeductionsService.findById(payroll.getSalaryDeductions().getId());
        SalaryDeductions deductions = payroll.getSalaryDeductions();
        //deductions.setSssPremium();
        //deductions.setHdmfPremium();
        //deductions.setPhicPremium();
        Double sssPrem = deductions.getSssPremium();
        Double hdmfPrem = deductions.getHdmfPremium();
        Double phicPrem = deductions.getPhicPremium();
        Double total = sssPrem + hdmfPrem + phicPrem;
        //System.out.println("PREMIUMS DEDUCTIONS: "+total);
        for(HeadOfficeDeductions ho: deductions.getHeadOfficeDeductions()) {
            total += ho.getDeductionAmount();
        }
        for(ScoutDeductions so: deductions.getScoutDeductions()) {
            total += so.getDeductionAmount();
        }
        for(GovernmentDeductions gd: deductions.getGovernmentDeductions()) {
            total += gd.getDeductionAmount();
        }
        //System.out.println("PAYROLL-UTIL TOTAL WITH DEDUCTIONS: "+total);
        deductions.setTotalDeductions(total);
        return deductions;
    }
}
