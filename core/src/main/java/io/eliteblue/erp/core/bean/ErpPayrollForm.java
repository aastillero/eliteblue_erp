package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.lazy.LazyDetachmentPayrollModel;
import io.eliteblue.erp.core.lazy.LazyErpDetachmentModel;
import io.eliteblue.erp.core.model.*;
import io.eliteblue.erp.core.service.*;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.LangUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpPayrollForm implements Serializable {

    @Autowired
    private ErpPayrollService erpPayrollService;

    @Autowired
    private ErpConfigService configService;

    @Autowired
    private EmployeePayrollService employeePayrollService;

    @Autowired
    private BasicSalaryService basicSalaryService;

    @Autowired
    private SalaryDeductionsService salaryDeductionsService;

    @Autowired
    private DetachmentPayrollService detachmentPayrollService;

    @Autowired
    private ErpDTRService erpDTRService;

    private Long id;
    private ErpPayroll erpPayroll;

    private LazyDataModel<DetachmentPayroll> lazyPayrollDetachments;
    private List<DetachmentPayroll> filteredPayrollDetachments;
    private List<DetachmentPayroll> payrollDetachments;
    private DetachmentPayroll selectedPayrollDetachment;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            erpPayroll = erpPayrollService.findById(Long.valueOf(id));
            payrollDetachments = new ArrayList<DetachmentPayroll>(erpPayroll.getDetachmentPayrolls());
            lazyPayrollDetachments = new LazyDetachmentPayrollModel(payrollDetachments);
        } else {
            erpPayroll = new ErpPayroll();
        }
    }

    public LazyDataModel<DetachmentPayroll> getLazyPayrollDetachments() {
        return lazyPayrollDetachments;
    }

    public void setLazyPayrollDetachments(LazyDataModel<DetachmentPayroll> lazyPayrollDetachments) {
        this.lazyPayrollDetachments = lazyPayrollDetachments;
    }

    public List<DetachmentPayroll> getFilteredPayrollDetachments() {
        return filteredPayrollDetachments;
    }

    public void setFilteredPayrollDetachments(List<DetachmentPayroll> filteredPayrollDetachments) {
        this.filteredPayrollDetachments = filteredPayrollDetachments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpPayroll getErpPayroll() {
        return erpPayroll;
    }

    public void setErpPayroll(ErpPayroll erpPayroll) {
        this.erpPayroll = erpPayroll;
    }

    public List<DetachmentPayroll> getPayrollDetachments() {
        return payrollDetachments;
    }

    public void setPayrollDetachments(List<DetachmentPayroll> payrollDetachments) {
        this.payrollDetachments = payrollDetachments;
    }

    public DetachmentPayroll getSelectedPayrollDetachment() {
        return selectedPayrollDetachment;
    }

    public void setSelectedPayrollDetachment(DetachmentPayroll selectedPayrollDetachment) {
        this.selectedPayrollDetachment = selectedPayrollDetachment;
    }

    public void clear() {
        erpPayroll = new ErpPayroll();
        id = null;
    }

    public boolean isNew() {
        return erpPayroll == null || erpPayroll.getId() == null;
    }

    public String newDetachmentPressed() {
        return "detachment-form?clientId="+id+"&faces-redirect=true&includeViewParams=true";
    }

    public void onRowSelect(SelectEvent<ErpDetachment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-detachment-form.xhtml?id="+ selectedPayrollDetachment.getId());
    }

    public void onRowUnselect(UnselectEvent<ErpDetachment> event) throws Exception {
        FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-detachment-form.xhtml?id="+ selectedPayrollDetachment.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }
        int filterInt = getInteger(filterText);

        ErpDetachment erpDetachment = (ErpDetachment) value;
        return erpDetachment.getName().toLowerCase().contains(filterText);
    }

    private int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    public void remove() throws Exception {
        if(has(erpPayroll) && has(erpPayroll.getId())) {
            //String name = erpPayroll.getName();
            erpPayrollService.delete(erpPayroll);
            addDetailMessage("PAYROLL DELETED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-list.xhtml");
        }
    }

    public void save() throws Exception {
        if(erpPayroll != null) {
            // Get Detachments within the cover period
            List<ErpDTR> erpDTRs = erpDTRService.getAllFilteredStartAndEndDate(erpPayroll.getCoverPeriodStart(), erpPayroll.getCoverPeriodEnd());
            List<DetachmentPayroll> detachmentPayrolls = new ArrayList<>();
            List<EmployeePayroll> employeePayrolls = new ArrayList<>();
            List<BasicSalary> basicSalaries = new ArrayList<>();
            List<SalaryDeductions> salaryDeductions = new ArrayList<>();
            for(ErpDTR edtr: erpDTRs) {
                DetachmentPayroll dp = new DetachmentPayroll();
                dp.setDetachment(edtr.getErpDetachment());
                List<ErpDTRAssignment> assignments = new ArrayList<>(edtr.getAssignments());
                // loop employees in detachment
                for(ErpDTRAssignment assn: assignments) {
                    EmployeePayroll employeePayroll = new EmployeePayroll();
                    employeePayroll.setCoverPeriodStart(erpPayroll.getCoverPeriodStart());
                    employeePayroll.setCoverPeriodEnd(erpPayroll.getCoverPeriodEnd());
                    employeePayroll.setEmployeePayroll(assn.getEmployeeAssigned());
                    employeePayroll.setDetachmentPayroll(dp);
                    BasicSalary basicSalary = generateBasicSalary(assn, edtr);
                    SalaryDeductions salaryDeduction = generateDeductions(assn, edtr);
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

                    employeePayroll.setGrossSalary(grossSal);
                    if(has(salaryDeduction.getTotalDeductions())) {
                        employeePayroll.setTotalNetPay(employeePayroll.getGrossSalary() - salaryDeduction.getTotalDeductions());
                    } else {
                        employeePayroll.setTotalNetPay(employeePayroll.getGrossSalary());
                    }
                    basicSalaryService.save(basicSalary);
                    salaryDeductionsService.save(salaryDeduction);
                    employeePayroll.setBasicSalary(basicSalary);
                    basicSalaries.add(basicSalary);
                    employeePayroll.setSalaryDeductions(salaryDeduction);
                    salaryDeductions.add(salaryDeduction);

                    employeePayrolls.add(employeePayroll);
                }
                // dp.setEmployeePayrolls();
                dp.setErpPayroll(erpPayroll);
                detachmentPayrolls.add(dp);
            }
            erpPayrollService.save(erpPayroll);
            for(DetachmentPayroll dp: detachmentPayrolls) {
                detachmentPayrollService.save(dp);
            }
            for(EmployeePayroll ep: employeePayrolls) {
                employeePayrollService.save(ep);
            }
            /*for(BasicSalary bs: basicSalaries) {
                basicSalaryService.save(bs);
            }
            for(SalaryDeductions sd: salaryDeductions) {
                salaryDeductionsService.save(sd);
            }*/
            addDetailMessage("PAYROLL SAVED", "", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-list.xhtml");
        }
    }

    public BasicSalary generateBasicSalary(ErpDTRAssignment assn, ErpDTR erpDTR) {
        //ErpConfig ot = configService.findByName("");
        BasicSalary basicSalary = new BasicSalary();
        // get hours night shift

        if(has(assn.getEmployeeAssigned().getSalaryRate())) {
            basicSalary.setDailyWage(assn.getEmployeeAssigned().getSalaryRate());
        } else {
            // get wage from Detachment/Location level
            Double rate = has(erpDTR.getErpDetachment().getLocation().getRateSG()) ? erpDTR.getErpDetachment().getLocation().getRateSG() : 0.0;
            basicSalary.setDailyWage(rate);
        }
        Double hourlyWage = basicSalary.getDailyWage() / 8;
        basicSalary.setHourlyWage(hourlyWage);
        if(has(assn.getTotalWorkDays())) {
            basicSalary.setTotalDaysWorked((double) assn.getTotalWorkDays());
        }
        else {
            if(has(assn.getTotalWorkHours()))
                basicSalary.setTotalDaysWorked((double) (assn.getTotalWorkHours() / 12));
        }
        if(has(assn.getTotalWorkHours())) {
            basicSalary.setBasicHoursWorked((double) assn.getTotalWorkHours());
        } else {
            if(has(assn.getTotalWorkDays()))
                basicSalary.setBasicHoursWorked((double) assn.getTotalWorkDays() * 12);
        }

        if(basicSalary.getTotalDaysWorked() < 0) {
            basicSalary.setTotalDaysWorked(basicSalary.getTotalDaysWorked() * -1);
        }
        if(basicSalary.getBasicHoursWorked() < 0) {
            basicSalary.setBasicHoursWorked(basicSalary.getBasicHoursWorked() * -1);
        }
        //basicSalary.setTotalDaysOT(); // compute number of days OT
        //basicSalary.setTotalDaysND(); // compute number of days Night
        basicSalary.setBasicSalary(basicSalary.getDailyWage() * basicSalary.getTotalDaysWorked()); // compute basic by numdays * daily wage

        //basicSalary.setOt_no_days();
        //basicSalary.setOt_no_excess_4hours();
        //basicSalary.setOt_hours_per_day();
        //basicSalary.setOt_total_hours();
        if(has(basicSalary.getHourlyWage()))
            basicSalary.setOt_rate(basicSalary.getHourlyWage() * 1.25); // hourly wage * 1.25
        //basicSalary.setOt_pay();

        //basicSalary.setNd_no_days();
        if(has(basicSalary.getHourlyWage()))
            basicSalary.setNd_hourly_rate(basicSalary.getHourlyWage() * 0.8);
        //basicSalary.setNd_pay();

        //basicSalary.setRd_eight_hrs();
        //basicSalary.setRd_twelve_hrs();
        if(has(basicSalary.getDailyWage())) {
            basicSalary.setRd_reg_day_ot((basicSalary.getDailyWage() * 0.3) + basicSalary.getDailyWage());
            basicSalary.setRd_eight_hrs_rate((basicSalary.getDailyWage() * 1.3) - basicSalary.getDailyWage());
            //basicSalary.setRd_twelve_hrs_rate(basicSalary.getDailyWage() * (1.3/8) * 1.3 * (4-(basicSalary.getRd_reg_day_ot()/8*4)) + basicSalary.getRd_eight_hrs_rate());
        }
        //basicSalary.setRd_pay();

        //basicSalary.setRh_eight_hrs();
        //basicSalary.setRh_twelve_hrs();
        //basicSalary.setRh_excess_ot_hrs();
        //basicSalary.setRh_excess_8hrs();
        //basicSalary.setRh_pay();
        if(has(basicSalary.getHourlyWage())) {
            basicSalary.setRh_ot_rate(basicSalary.getHourlyWage() * 2.6);
            basicSalary.setRh_nightdiff_rate(basicSalary.getHourlyWage() * 1.6);
        }
        //basicSalary.setRh_ot_pay();
        //basicSalary.setRh_nightdiff_days();
        //basicSalary.setRh_nightdiff_pay();

        //basicSalary.setSh_eight_hrs();
        //basicSalary.setSh_twelve_hrs();
        //basicSalary.setSh_excess_ot_hrs();
        //basicSalary.setSh_excess_8hrs();
        //basicSalary.setSh_pay();
        if(has(basicSalary.getHourlyWage())) {
            basicSalary.setSh_ot_rate(basicSalary.getHourlyWage() * 1.69);
            basicSalary.setSh_nightdiff_rate(basicSalary.getHourlyWage() * 1.04);
        }
        //basicSalary.setSh_ot_pay();
        //basicSalary.setSh_nightdiff_days();
        //basicSalary.setSh_nightdiff_pay();
        return  basicSalary;
    }

    public SalaryDeductions generateDeductions(ErpDTRAssignment assn, ErpDTR erpDTR) {
        SalaryDeductions deductions = new SalaryDeductions();
        //deductions.setSssPremium();
        //deductions.setHdmfPremium();
        //deductions.setPhicPremium();
        return deductions;
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
