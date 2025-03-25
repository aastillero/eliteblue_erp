package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.ApprovalStatus;
import io.eliteblue.erp.core.constants.EmployeeLoanType;
import io.eliteblue.erp.core.constants.SalaryType;
import io.eliteblue.erp.core.lazy.LazyDetachmentPayrollModel;
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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpPayrollForm implements Serializable {

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Autowired
    private ErpPayrollService erpPayrollService;

    @Autowired
    private ErpConfigService configService;

    @Autowired
    private EmployeePayrollService employeePayrollService;

    @Autowired
    private DetachmentPayrollService detachmentPayrollService;

    @Autowired
    private BasicSalaryService basicSalaryService;

    @Autowired
    private SalaryDeductionsService salaryDeductionsService;

    @Autowired
    private ErpDTRService erpDTRService;

    @Autowired
    private ErpSSSContributionService erpSSSContributionService;

    @Autowired
    private TimeScheduleService timeScheduleService;

    @Autowired
    private ErpConfigService erpConfigService;

    @Autowired
    private GovernmentLoanService governmentLoanService;

    @Autowired
    private GovernmentDeductionService governmentDeductionService;

    @Autowired
    private GovernmentLoanHistoryService governmentLoanHistoryService;

    @Autowired
    private HeadOfficeLoanService headOfficeLoanService;

    @Autowired
    private HeadOfficeLoanHistoryService headOfficeLoanHistoryService;

    @Autowired
    private HeadOfficeDeductionsService headOfficeDeductionsService;

    @Autowired
    private ScoutLoanService scoutLoanService;

    @Autowired
    private ScoutDeductionService scoutDeductionService;

    @Autowired
    private ScoutLoanHistoryService scoutLoanHistoryService;

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
            Comparator<DetachmentPayroll> comparator = Comparator.comparing(eq -> Optional.ofNullable(eq.getDetachment()).map(ErpDetachment::getName).orElse(""), Comparator.nullsFirst(String::compareTo));
            payrollDetachments.sort(comparator);
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

    public String dtrPending() {
        Date coverStart = erpPayroll.getCoverPeriodStart();
        Date coverEnd = erpPayroll.getCoverPeriodEnd();
        return "dtr-list?status=PENDING&coverStart="+format.format(coverStart)+"&coverEnd="+format.format(coverEnd)+"&faces-redirect=true&includeViewParams=true";
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
            // remove all related deductions
            removeDeductions();
            // delete erpPayroll
            erpPayrollService.delete(erpPayroll);
            FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-list.xhtml");
            addDetailMessage("PAYROLL DELETED", "", FacesMessage.SEVERITY_INFO);
        }
    }

    public void removeDeductions() {
        List<DetachmentPayroll> dps = new ArrayList<>(erpPayroll.getDetachmentPayrolls());
        dps.sort(Comparator.comparing((DetachmentPayroll ewa) -> ewa.getDetachment().getName()));
        for(DetachmentPayroll dp: dps) {
            DetachmentPayroll tempDp = detachmentPayrollService.findById(dp.getId());
            List<EmployeePayroll> eps = new ArrayList<>(tempDp.getEmployeePayrolls());
            eps.sort(Comparator.comparing((EmployeePayroll ewa) -> ewa.getEmployeePayroll().getLastname()));
            for(EmployeePayroll ep: eps) {
                SalaryDeductions sd = ep.getSalaryDeductions();
                if(sd.getTotalDeductions() > 0) {
                    //System.out.println("EMPLOYEE: "+ep.getEmployeePayroll().getFirstname()+" "+ep.getEmployeePayroll().getLastname());
                    if(has(sd) && has(sd.getGovernmentDeductions())) {
                        //System.out.println("GOVERNMENT DEDUCTIONS: " + sd.getGovernmentDeductions().size());
                        for(GovernmentDeductions gds: sd.getGovernmentDeductions()) {
                            // get head office Loan
                            GovernmentLoan hLoan = gds.getGovernmentLoan();
                            //System.out.println("HEAD OFFICE LOAN: "+hLoan.getLoanType().name()+" "+hLoan.getLoanAmount());
                            List<GovernmentLoanHistory> history = new ArrayList<>(hLoan.getLoanHistory());
                            //System.out.println("LOAN HISTORY: "+history.size());
                            Collections.sort(history, Collections.reverseOrder(Comparator.comparing((GovernmentLoanHistory ewa) -> ewa.getLastUpdate())));
                            //history.stream().map(tmp -> tmp.getLastUpdate()).forEach(System.out::println);
                            // get the first history entry
                            //System.out.println(history.get(0).getChanges()+history.get(0).getLastUpdate());
                            // revert loan changes
                            //System.out.println("LOAN PAID AMOUNT: "+hLoan.getPaidAmount());
                            //System.out.println("PREV PAID AMOUNT: "+history.get(0).getPrevPaidAmount());
                            Double paidDiff = history.get(0).getUpdatedPaidAmount() - history.get(0).getPrevPaidAmount();
                            Double paidAdjusted = hLoan.getPaidAmount() - paidDiff;
                            hLoan.setPaidAmount(paidAdjusted);
                            //System.out.println(history.get(1).getChanges()+history.get(1).getLastUpdate());
                            //System.out.println("LOAN BALANCE AMOUNT: "+hLoan.getBalanceAmount());
                            //System.out.println("PREV BALANCE AMOUNT: "+history.get(1).getPrevBalanceAmount());
                            Double balanceDiff = history.get(1).getPrevBalanceAmount() - history.get(1).getUpdatedBalanceAmount();
                            Double balanceAdjusted = hLoan.getBalanceAmount() + balanceDiff;
                            hLoan.setBalanceAmount(balanceAdjusted);
                            governmentLoanHistoryService.delete(history.get(0));
                            governmentLoanHistoryService.delete(history.get(1));
                            governmentDeductionService.delete(gds);
                        }
                    }
                    if(has(sd) && has(sd.getHeadOfficeDeductions())) {
                        //System.out.println("HEAD OFFICE DEDUCTIONS: " + sd.getHeadOfficeDeductions().size());
                        for(HeadOfficeDeductions hds: sd.getHeadOfficeDeductions()) {
                            // get head office Loan
                            HeadOfficeLoan hLoan = hds.getHeadOfficeLoan();
                            //System.out.println("HEAD OFFICE LOAN: "+hLoan.getLoanType().name()+" "+hLoan.getLoanAmount());
                            List<HeadOfficeLoanHistory> history = new ArrayList<>(hLoan.getLoanHistory());
                            //System.out.println("LOAN HISTORY: "+history.size());
                            Collections.sort(history, Collections.reverseOrder(Comparator.comparing((HeadOfficeLoanHistory ewa) -> ewa.getLastUpdate())));
                            //history.stream().map(tmp -> tmp.getLastUpdate()).forEach(System.out::println);
                            // get the first history entry
                            //System.out.println(history.get(0).getChanges()+history.get(0).getLastUpdate());
                            // revert loan changes
                            //System.out.println("LOAN PAID AMOUNT: "+hLoan.getPaidAmount());
                            //System.out.println("PREV PAID AMOUNT: "+history.get(0).getPrevPaidAmount());
                            //System.out.println("UPDATED PAID AMOUNT: "+history.get(0).getUpdatedPaidAmount());
                            Double paidDiff = history.get(0).getUpdatedPaidAmount() - history.get(0).getPrevPaidAmount();
                            Double paidAdjusted = hLoan.getPaidAmount() - paidDiff;
                            hLoan.setPaidAmount(paidAdjusted);
                            //System.out.println(history.get(1).getChanges()+history.get(1).getLastUpdate());
                            //System.out.println("LOAN BALANCE AMOUNT: "+hLoan.getBalanceAmount());
                            //System.out.println("PREV BALANCE AMOUNT: "+history.get(1).getPrevBalanceAmount());
                            Double balanceDiff = history.get(1).getPrevBalanceAmount() - history.get(1).getUpdatedBalanceAmount();
                            Double balanceAdjusted = hLoan.getBalanceAmount() + balanceDiff;
                            hLoan.setBalanceAmount(balanceAdjusted);
                            headOfficeLoanHistoryService.delete(history.get(0));
                            headOfficeLoanHistoryService.delete(history.get(1));
                            headOfficeDeductionsService.delete(hds);
                        }
                    }
                    if(has(sd) && has(sd.getScoutDeductions())) {
                        //System.out.println("SCOUT DEDUCTIONS: " + sd.getScoutDeductions().size());
                        for(ScoutDeductions sds: sd.getScoutDeductions()) {
                            // get head office Loan
                            ScoutLoan hLoan = sds.getScoutLoan();
                            //System.out.println("HEAD OFFICE LOAN: "+hLoan.getLoanType().name()+" "+hLoan.getLoanAmount());
                            List<ScoutLoanHistory> history = new ArrayList<>(hLoan.getLoanHistory());
                            //System.out.println("LOAN HISTORY: "+history.size());
                            Collections.sort(history, Collections.reverseOrder(Comparator.comparing((ScoutLoanHistory ewa) -> ewa.getLastUpdate())));
                            //history.stream().map(tmp -> tmp.getLastUpdate()).forEach(System.out::println);
                            // get the first history entry
                            //System.out.println(history.get(0).getChanges()+history.get(0).getLastUpdate());
                            // revert loan changes
                            //System.out.println("LOAN PAID AMOUNT: "+hLoan.getPaidAmount());
                            //System.out.println("PREV PAID AMOUNT: "+history.get(0).getPrevPaidAmount());
                            Double paidDiff = history.get(0).getUpdatedPaidAmount() - history.get(0).getPrevPaidAmount();
                            Double paidAdjusted = hLoan.getPaidAmount() - paidDiff;
                            hLoan.setPaidAmount(paidAdjusted);
                            //System.out.println(history.get(1).getChanges()+history.get(1).getLastUpdate());
                            //System.out.println("LOAN BALANCE AMOUNT: "+hLoan.getBalanceAmount());
                            //System.out.println("PREV BALANCE AMOUNT: "+history.get(1).getPrevBalanceAmount());
                            Double balanceDiff = history.get(1).getPrevBalanceAmount() - history.get(1).getUpdatedBalanceAmount();
                            Double balanceAdjusted = hLoan.getBalanceAmount() + balanceDiff;
                            hLoan.setBalanceAmount(balanceAdjusted);
                            scoutLoanHistoryService.delete(history.get(0));
                            scoutLoanHistoryService.delete(history.get(1));
                            scoutDeductionService.delete(sds);
                        }
                    }
                }
            }
        }
    }

    public Boolean containsID(Long detachmentId) {
        for(DetachmentPayroll dp: payrollDetachments) {
            if(dp.getDetachment().getId() == detachmentId) {
                return true;
            }
        }
        return false;
    }

    public DetachmentPayroll getDetachMentPayroll(Long detachmentId) {
        for(DetachmentPayroll dp: payrollDetachments) {
            if(dp.getDetachment().getId() == detachmentId) {
                //System.out.println("["+dp.getDetachment().getName()+"] getDetachmentPayroll DP: "+dp);
                return dp;
            }
        }
        return null;
    }

    public void generatePayroll(List<ErpDTR> erpDTRs, Boolean isUpdate) throws Exception {
        List<DetachmentPayroll> detachmentPayrolls = new ArrayList<>();
        List<EmployeePayroll> employeePayrolls = new ArrayList<>();
        List<BasicSalary> basicSalaries = new ArrayList<>();
        //List<SalaryDeductions> salaryDeductions = new ArrayList<>();

        for(ErpDTR edtr: erpDTRs) {
            boolean updateCheck = false;
            if(!edtr.getStatus().equals(ApprovalStatus.APPROVED)) {
                //System.out.println("SKIPPED: "+edtr.getErpDetachment().getName());
                continue;
            }

            DetachmentPayroll dp = new DetachmentPayroll();
            List<ErpDTRAssignment> assignments = new ArrayList<>(edtr.getAssignments());
            if(isUpdate && payrollDetachments.size() > 0 && containsID(edtr.getErpDetachment().getId())) {
                //continue;
                dp = getDetachMentPayroll(edtr.getErpDetachment().getId());
                if(dp != null)
                    updateCheck = true;
            } else {
                dp.setDetachment(edtr.getErpDetachment());
            }

            // loop employees in detachment
            for(ErpDTRAssignment assn: assignments) {
                EmployeePayroll employeePayroll = new EmployeePayroll();
                if(updateCheck) {
                    //System.out.println("["+dp.getDetachment().getName()+"] dpID: "+dp.getId()+" assn:"+assn.getEmployeeAssigned().getLastname());
                    employeePayroll = employeePayrollService.findByEmployeeAndDetachmentPayroll(assn.getEmployeeAssigned(), dp);
                } else {
                    employeePayroll.setCoverPeriodStart(erpPayroll.getCoverPeriodStart());
                    employeePayroll.setCoverPeriodEnd(erpPayroll.getCoverPeriodEnd());
                    employeePayroll.setEmployeePayroll(assn.getEmployeeAssigned());
                    employeePayroll.setDetachmentPayroll(dp);
                }
                BasicSalary basicSalary = generateBasicSalary(assn, edtr, employeePayroll, updateCheck);
                employeePayroll.setSil(computeSIL(assn, basicSalary));
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
                // for rdp extra pay incentive
                if(has(basicSalary.getExtra_pay())) {
                    grossSal += basicSalary.getExtra_pay();
                }
                // allowance from employee management
                if(has(assn.getEmployeeAssigned().getAllowance())) {
                    employeePayroll.setAllowance(assn.getEmployeeAssigned().getAllowance());
                    grossSal += assn.getEmployeeAssigned().getAllowance();
                }
                // for SIL of employee
                if(has(employeePayroll.getSil())) {
                    grossSal += employeePayroll.getSil();
                }

                SalaryDeductions salaryDeduction = (!updateCheck) ? generateDeductions(assn, edtr, grossSal, basicSalary.getBasicSalary(), employeePayroll) : employeePayroll.getSalaryDeductions();
                employeePayroll.setGrossSalary(grossSal);
                if(has(salaryDeduction.getTotalDeductions())) {
                    employeePayroll.setTotalNetPay(employeePayroll.getGrossSalary() - salaryDeduction.getTotalDeductions());
                } else {
                    employeePayroll.setTotalNetPay(employeePayroll.getGrossSalary());
                }

                basicSalaryService.save(basicSalary);
                employeePayroll.setBasicSalary(basicSalary);
                basicSalaries.add(basicSalary);
                if(!updateCheck) {
                    employeePayroll.setSalaryDeductions(salaryDeduction);
                    //salaryDeductions.add(salaryDeduction);
                }

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
            //System.out.println("saving EP ["+ep.getDetachmentPayroll().getDetachment().getName()+"] ep: "+ep.getEmployeePayroll().getLastname());
            employeePayrollService.save(ep);
        }
        addDetailMessage("PAYROLL SAVED", "", FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().getExternalContext().redirect("payroll-list.xhtml");
    }

    public void update() throws Exception {
        List<ErpDTR> erpDTRs = erpDTRService.getAllFilteredStartAndEndDate(erpPayroll.getCoverPeriodStart(), erpPayroll.getCoverPeriodEnd());
        generatePayroll(erpDTRs, true);
    }

    public void save() throws Exception {
        if(erpPayroll != null) {
            // check if there is existing cover period
            //System.out.println("COVER START - END: "+erpPayroll.getCoverPeriodStart()+" "+erpPayroll.getCoverPeriodEnd());
            List<ErpPayroll> epsTemp = erpPayrollService.getAllFilteredStartAndEndDate(erpPayroll.getCoverPeriodStart(), erpPayroll.getCoverPeriodEnd());
            //System.out.println("LIST: "+epsTemp.size());
            if(has(epsTemp) && epsTemp.size() > 0) {
                addDetailMessage("FAILED GENERATION", "Cover Period ["+format.format(erpPayroll.getCoverPeriodStart())+" to "+format.format(erpPayroll.getCoverPeriodEnd())+"] already Generated", FacesMessage.SEVERITY_ERROR);
            } else {
                // Get Detachments within the cover period
                List<ErpDTR> erpDTRs = erpDTRService.getAllFilteredStartAndEndDate(erpPayroll.getCoverPeriodStart(), erpPayroll.getCoverPeriodEnd());
                generatePayroll(erpDTRs, false);
            }
        }
    }

    public double roundOffDecimals(double val) {
        //return Math.round(val*100) / 100;
        return val;
    }

    public Double computeSIL(ErpDTRAssignment assn, BasicSalary basicSalary) {
        Double retVal = 0.0;
        if(has(assn.getTotalSIL()) && has(basicSalary.getDailyWage())) {
            retVal = basicSalary.getDailyWage() * assn.getTotalSIL();
        }
        return retVal;
    }

    public boolean isTwelveHour(ErpTimeSchedule schedule) {
        LocalTime startTime = schedule.getStartTime().toLocalTime();
        LocalTime endTime = schedule.getEndTime().toLocalTime();
        long totalHours = Duration.between(startTime, endTime).toHours();
        //System.out.println("(isTwelveHour) Total hours: " + totalHours);
        return totalHours >= 12;
    }

    public BasicSalary generateBasicSalary(ErpDTRAssignment assn, ErpDTR erpDTR, EmployeePayroll ep, boolean updateCheck) {
        //ErpConfig ot = configService.findByName("");
        List<ErpTimeSchedule> schedules = timeScheduleService.findByDetachment(erpDTR.getErpDetachment());
        //schedules.forEach(schedule -> System.out.println("START:"+schedule.getStartTime()+" STOP:"+schedule.getEndTime()));
        BasicSalary basicSalary = new BasicSalary();
        if(updateCheck) {
            basicSalary = ep.getBasicSalary();
        }
        Double hourlyWage = 0.0;
        boolean isFixed = (assn.getEmployeeAssigned().getSalaryType() != null &&
                assn.getEmployeeAssigned().getSalaryType().equals(SalaryType.FIXED));
        // get hours night shift

        if(!isFixed) {
            if(has(assn.getEmployeeAssigned().getSalaryRate())) {
                basicSalary.setDailyWage(assn.getEmployeeAssigned().getSalaryRate());
            } else {
                // get wage from Detachment/Location level
                Double rate = has(erpDTR.getErpDetachment().getLocation().getRateSG()) ? erpDTR.getErpDetachment().getLocation().getRateSG() : 0.0;
                basicSalary.setDailyWage(rate);
            }
        } else {
            basicSalary.setDailyWage(0.0);
        }

        if(has(basicSalary.getDailyWage()) && basicSalary.getDailyWage() > 0)
            hourlyWage = basicSalary.getDailyWage() / 8;
        if(!isFixed)
            basicSalary.setHourlyWage(roundOffDecimals(hourlyWage));
        else
            basicSalary.setHourlyWage(0.0);

        if(has(assn.getTotalWorkDays())) {
            basicSalary.setTotalDaysWorked((double) assn.getTotalWorkDays());
            basicSalary.setBasicHoursWorked(((double) assn.getTotalWorkDays()) * 8);
            basicSalary.setTotalHoursWorked(basicSalary.getBasicHoursWorked() + ((double) assn.getTotalOTHours()));
        }
        else {
            if(has(assn.getTotalWorkHours())) {
                basicSalary.setTotalDaysWorked((double) (assn.getTotalWorkHours() / 8));
                basicSalary.setBasicHoursWorked((double) assn.getTotalWorkHours());
                basicSalary.setTotalHoursWorked((double) (assn.getTotalBasicHours() + assn.getTotalOTHours()));
            }
        }
        if(has(assn.getTotalOTHours()) && assn.getTotalOTHours() > 0)
            basicSalary.setTotalDaysOT(Math.floor(assn.getTotalOTHours() / 4)); // compute number of days OT
        if(has(assn.getTotalNDHours()) && assn.getTotalNDHours() > 0)
            basicSalary.setTotalDaysND(Math.floor(assn.getTotalNDHours() / 8)); // compute number of days Night

        if(!isFixed)
            basicSalary.setBasicSalary(roundOffDecimals(basicSalary.getDailyWage() * basicSalary.getTotalDaysWorked())); // compute basic by numdays * daily wage
        else
            basicSalary.setBasicSalary(assn.getEmployeeAssigned().getSalaryRate());

        basicSalary.setOt_no_days(Math.floor(assn.getTotalBasicOTHours() / 4));
        basicSalary.setOt_no_excess_4hours(assn.getTotalBasicOTExcessHours().doubleValue());
        //basicSalary.setOt_hours_per_day(assn.getTotalOTHours().doubleValue());
        basicSalary.setOt_total_hours(assn.getTotalBasicOTHours().doubleValue());
        if(has(basicSalary.getHourlyWage())) {
            if(!isFixed) {
                basicSalary.setOt_rate(roundOffDecimals(basicSalary.getHourlyWage() * 1.25)); // hourly wage * 1.25
                basicSalary.setOt_pay(roundOffDecimals(basicSalary.getOt_rate() * basicSalary.getOt_total_hours()));
            } else {
                basicSalary.setOt_rate(0.0);
                basicSalary.setOt_pay(0.0);
            }
        }

        if(has(assn.getTotalBasicNDHours()) && assn.getTotalBasicNDHours() > 0)
            basicSalary.setNd_no_days((assn.getTotalBasicNDHours().doubleValue() / 8));
        basicSalary.setNd_total_hours(assn.getTotalBasicNDHours().doubleValue());
        if(has(basicSalary.getHourlyWage())) {
            if(!isFixed) {
                basicSalary.setNd_hourly_rate(roundOffDecimals(basicSalary.getHourlyWage() * 0.1));
                basicSalary.setNd_pay(roundOffDecimals(basicSalary.getNd_hourly_rate() * assn.getTotalBasicNDHours()));
            } else {
                basicSalary.setNd_hourly_rate(0.0);
                basicSalary.setNd_pay(0.0);
            }
        }

        //basicSalary.setRd_eight_hrs();
        //basicSalary.setRd_twelve_hrs();
        /*if(has(basicSalary.getDailyWage())) {
            basicSalary.setRd_reg_day_ot((basicSalary.getDailyWage() * 0.3) + basicSalary.getDailyWage());
            basicSalary.setRd_eight_hrs_rate((basicSalary.getDailyWage() * 1.3) - basicSalary.getDailyWage());
            //basicSalary.setRd_twelve_hrs_rate(basicSalary.getDailyWage() * (1.3/8) * 1.3 * (4-(basicSalary.getRd_reg_day_ot()/8*4)) + basicSalary.getRd_eight_hrs_rate());
        }*/
        //basicSalary.setRd_pay();
        basicSalary.setRd_no_days(assn.getTotalRdp().doubleValue());

        //basicSalary.setRh_eight_hrs();
        //basicSalary.setRh_twelve_hrs();
        if(has(assn.getTotalRegularHolidayHrs()) && assn.getTotalRegularHolidayHrs() > 0)
            basicSalary.setRh_no_days(assn.getTotalRegularHolidayHrs().doubleValue() / 8);
        basicSalary.setRh_excess_ot_hrs(assn.getTotalOTExcessRegularHolidayHrs().doubleValue());
        basicSalary.setRh_ot_hrs(assn.getTotalOTRegularHolidayHrs().doubleValue());
        basicSalary.setRh_nightdiff_hrs(assn.getTotalNDRegularHolidayHrs().doubleValue());
        if(has(basicSalary.getHourlyWage())) {
            if(!isFixed) {
                basicSalary.setRh_ot_rate(roundOffDecimals(basicSalary.getHourlyWage() * 2.6));
                basicSalary.setRh_nightdiff_rate(roundOffDecimals(basicSalary.getHourlyWage() * 0.2));

                basicSalary.setRh_pay(basicSalary.getHourlyWage() * assn.getTotalRegularHolidayHrs());
                if (has(assn.getTotalOTRegularHolidayHrs()) && assn.getTotalOTRegularHolidayHrs() > 0)
                    basicSalary.setRh_ot_pay(roundOffDecimals(basicSalary.getRh_ot_rate() * assn.getTotalOTRegularHolidayHrs()));
                else if (has(assn.getTotalOTExcessRegularHolidayHrs()) && assn.getTotalOTExcessRegularHolidayHrs() > 0)
                    basicSalary.setRh_ot_pay(roundOffDecimals(basicSalary.getRh_ot_rate() * assn.getTotalOTExcessRegularHolidayHrs()));

                if (has(assn.getTotalNDRegularHolidayHrs()) && assn.getTotalNDRegularHolidayHrs() > 0) {
                    basicSalary.setRh_nightdiff_days(assn.getTotalNDRegularHolidayHrs().doubleValue() / 8);
                }
                basicSalary.setRh_nightdiff_pay(roundOffDecimals(basicSalary.getRh_nightdiff_rate() * assn.getTotalNDRegularHolidayHrs()));
            } else {
                basicSalary.setRh_ot_rate(0.0);
                basicSalary.setRh_nightdiff_rate(0.0);
                basicSalary.setRh_pay(0.0);
                basicSalary.setRh_ot_pay(0.0);
                basicSalary.setRh_nightdiff_days(0.0);
            }
        }

        //basicSalary.setSh_eight_hrs();
        //basicSalary.setSh_twelve_hrs();
        if(has(assn.getTotalSpecialHolidayHrs()) && assn.getTotalSpecialHolidayHrs() > 0)
            basicSalary.setSh_no_days(assn.getTotalSpecialHolidayHrs().doubleValue() / 8);
        basicSalary.setSh_excess_ot_hrs(assn.getTotalOTExcessSpecialHolidayHrs().doubleValue());
        basicSalary.setSh_ot_hrs(assn.getTotalOTSpecialHolidayHrs().doubleValue());
        basicSalary.setSh_nightdiff_hrs(assn.getTotalNDSpecialHolidayHrs().doubleValue());
        if(has(basicSalary.getHourlyWage())) {
            if(!isFixed) {
                basicSalary.setSh_ot_rate(roundOffDecimals(basicSalary.getHourlyWage() * 1.69));
                basicSalary.setSh_nightdiff_rate(roundOffDecimals(basicSalary.getHourlyWage() * 1.3 * 0.1));

                basicSalary.setSh_pay(roundOffDecimals((basicSalary.getHourlyWage() * 0.3) * assn.getTotalSpecialHolidayHrs()));
                if (has(assn.getTotalOTSpecialHolidayHrs()) && assn.getTotalOTSpecialHolidayHrs() > 0)
                    basicSalary.setSh_ot_pay(roundOffDecimals(basicSalary.getSh_ot_rate() * assn.getTotalOTSpecialHolidayHrs()));
                else if (has(assn.getTotalOTExcessSpecialHolidayHrs()) && assn.getTotalOTExcessSpecialHolidayHrs() > 0)
                    basicSalary.setSh_ot_pay(roundOffDecimals(basicSalary.getSh_ot_rate() * assn.getTotalOTExcessRegularHolidayHrs()));

                if (has(assn.getTotalNDSpecialHolidayHrs()) && assn.getTotalNDSpecialHolidayHrs() > 0)
                    basicSalary.setSh_nightdiff_days(assn.getTotalNDSpecialHolidayHrs().doubleValue() / 8);
                basicSalary.setSh_nightdiff_pay(roundOffDecimals(basicSalary.getSh_nightdiff_rate() * assn.getTotalNDSpecialHolidayHrs()));
            } else {
                basicSalary.setSh_ot_rate(0.0);
                basicSalary.setSh_nightdiff_rate(0.0);
                basicSalary.setSh_pay(0.0);
                basicSalary.setSh_ot_pay(0.0);
                basicSalary.setSh_nightdiff_days(0.0);
                basicSalary.setSh_nightdiff_pay(0.0);
            }
        }

        if(assn.getHasRdp()) {
            boolean isTwelveHour = (schedules.size() > 0 && isTwelveHour(schedules.get(0)));
            //System.out.println("IS TWELVEHOUR: "+isTwelveHour);
            // add 30% from basic
            int computedDays = assn.getTotalRdp();
            Double basicSal;
            if(!isFixed) {
                basicSal = basicSalary.getDailyWage();
            } else {
                basicSal = basicSalary.getBasicSalary();
            }
            Double eightHoursRate = (basicSal * 1.3) - basicSal;
            if(!isTwelveHour) {
                basicSalary.setExtra_pay(computedDays * eightHoursRate);
            }
            else {
                Double regularWithOTRate = (basicSal * 0.3) + basicSal;
                //System.out.println(assn.getEmployeeAssigned().getFirstname()+" "+assn.getEmployeeAssigned().getLastname()+" regularWithOTRate: "+regularWithOTRate);
                Double twelveHourRate = basicSal * 1.3 / 8 * 1.3 * 4-regularWithOTRate/8*4 + eightHoursRate;
                //System.out.println("computedDays: "+computedDays+" twelveHourRate: "+twelveHourRate);
                basicSalary.setExtra_pay(computedDays * twelveHourRate);
            }

            //basicSalary.setRd_reg_day_ot((basicSalary.getDailyWage() * 0.3) + basicSalary.getDailyWage());
            //basicSalary.setRd_eight_hrs_rate((basicSalary.getDailyWage() * 1.3) - basicSalary.getDailyWage());
            //basicSalary.setRd_twelve_hrs_rate(basicSalary.getDailyWage() * (1.3/8) * 1.3 * (4-(basicSalary.getRd_reg_day_ot()/8*4)) + basicSalary.getRd_eight_hrs_rate());
        }

        return  basicSalary;
    }

    public SalaryDeductions generateDeductions(ErpDTRAssignment assn, ErpDTR erpDTR, Double grossSal, Double basicSal, EmployeePayroll ep) {
        SalaryDeductions deductions = new SalaryDeductions();
        // take home pay is 20% of Gross pay
        Double takeHomePay = grossSal * 0.2;
        Double total = 0.0;
        // initialize
        deductions.setSssPremium(0.0);
        deductions.setHdmfPremium(0.0);
        deductions.setPhicPremium(0.0);

        // check cover period is 1-15
        if(isOneToFifteen(erpDTR.getCutoffStart(), erpDTR.getCutoffEnd())) {
            List<GovernmentLoan> governmentLoans = governmentLoanService.getLoansByEmployee(ep.getEmployeePayroll());
            total = governmentLoans.stream().mapToDouble(itm -> itm.getDeductionAmount()).sum();
            deductions.setTotalDeductions(total);
            salaryDeductionsService.save(deductions);
            // deduct govt loans only
            if(has(governmentLoans) && governmentLoans.size() > 0) {
                governmentLoans = governmentLoans.stream().filter(itm -> itm.getBalanceAmount() > 0).collect(Collectors.toList());
                for(GovernmentLoan gl: governmentLoans) {
                    // No need for checking, deduct automatically
                    generateGovernmentLoanDeduction(gl, deductions);
                }
            }
        } else {
            // deduct contributions only
            //deductions.setSssPremium(0.00);
            // generate SSS deduction
            Double sssContribution = generateSSSContribution(assn.getEmployeeAssigned(), grossSal);
            //System.out.println("SSS CONTRIBUTION: ["+sssContribution+"]");
            deductions.setSssPremium(sssContribution);
            Double phicContribution = generatePhicContribution(assn.getEmployeeAssigned(), basicSal, getRateCard(erpDTR.getErpDetachment(), assn.getEmployeeAssigned()));
            ErpConfig hdmfConfig = erpConfigService.findByName("Pagibig Contribution");
            if(phicContribution > 0) {
                //System.out.println("["+assn.getEmployeeAssigned().getLastname().concat("_").concat(assn.getEmployeeAssigned().getFirstname())+"] HDMF CONTRIBUTION: ["+phicContribution+"]");
                deductions.setPhicPremium(phicContribution);
            } else {
                deductions.setPhicPremium(200.0);
            }
            if(has(hdmfConfig)) {
                deductions.setHdmfPremium(hdmfConfig.getNumValue());
            } else {
                deductions.setHdmfPremium(100.0);
            }
            // Total Amount Deduction for Government Loan, Eliteblue Loan and Scout Loan
            total = deductions.getSssPremium() + deductions.getHdmfPremium() + deductions.getPhicPremium();
            deductions.setTotalDeductions(total);
            salaryDeductionsService.save(deductions);
        }

        // Eliteblue Loan Deductions
        List<HeadOfficeLoan> headOfficeLoans = headOfficeLoanService.getLoansByEmployee(ep.getEmployeePayroll());
        if(has(headOfficeLoans) && headOfficeLoans.size() > 0) {
            headOfficeLoans = headOfficeLoans.stream().filter(itm->itm.getBalanceAmount() > 0).collect(Collectors.toList());
            for(HeadOfficeLoan hl: headOfficeLoans) {
                // Compute totals
                Double computedTotal = total + hl.getDeductionAmount();
                Double computedNetPay = grossSal - computedTotal;
                if(computedNetPay >= takeHomePay){
                    generateHeadOfficeLoanDeduction(hl, deductions);
                    total += hl.getDeductionAmount();
                } else {
                    break;
                }
            }
        }

        // Scout Loan Deductions
        List<ScoutLoan> scoutLoans = scoutLoanService.getLoansByEmployee(ep.getEmployeePayroll());
        if(has(scoutLoans) && scoutLoans.size() > 0) {
            //System.out.println("["+ep.getEmployeePayroll().getLastname()+"] ScoutLoans: "+scoutLoans.size());
            scoutLoans = scoutLoans.stream().filter(itm->itm.getBalanceAmount() > 0).collect(Collectors.toList());
            for(ScoutLoan sl: scoutLoans) {
                // Compute totals
                Double computedTotal = total + sl.getDeductionAmount();
                Double computedNetPay = grossSal - computedTotal;
                //System.out.println("["+sl.getLoanType().name()+"] deductAmount: "+sl.getDeductionAmount()+" computedTotal: "+computedTotal+" computedNetPay: "+computedNetPay+" takeHomePay: "+takeHomePay);
                if(computedNetPay >= takeHomePay){
                    generateScoutLoanDeduction(sl, deductions);
                    total += sl.getDeductionAmount();
                } else {
                    break;
                }
            }
        }

        deductions.setTotalDeductions(total);
        salaryDeductionsService.save(deductions);
        return deductions;
    }

    public Double getRateCard(ErpDetachment detachment, ErpEmployee employee) {
        Double retVal = 0.0;
        if(employee.getCompanyPosition() == null) {
            return retVal;
        }
        if(employee.getCompanyPosition().getName().equals("SECURITY IN CHARGE")) {
            return detachment.getPhicSIC();
        } else if(employee.getCompanyPosition().getName().equals("DETACHMENT COMMANDER")) {
            return detachment.getPhicDC();
        } else if(employee.getCompanyPosition().getName().equals("SECURITY GUARD")) {
            return detachment.getPhicSG();
        }
        return retVal;
    }

    public void generateGovernmentLoanDeduction(GovernmentLoan governmentLoan, SalaryDeductions deductions) {
        GovernmentDeductions hod = new GovernmentDeductions();
        GovernmentLoanHistory histBalance = new GovernmentLoanHistory();
        GovernmentLoanHistory histPaid = new GovernmentLoanHistory();
        hod.setDeductionAmount(governmentLoan.getDeductionAmount());
        hod.setDeductionDescription(governmentLoan.getLoanType().name());
        hod.setSalaryDeductions(deductions);
        hod.setGovernmentLoan(governmentLoan);
        histBalance.setGovernmentLoan(governmentLoan);
        histBalance.setReason("System Generated from Payroll");
        histPaid.setGovernmentLoan(governmentLoan);
        histPaid.setReason("System Generated from Payroll");
        Double computedBalance = governmentLoan.getBalanceAmount() - governmentLoan.getDeductionAmount();
        Double computedPaid = governmentLoan.getPaidAmount() + governmentLoan.getDeductionAmount();
        histBalance.setChanges("Balance changed from "+ governmentLoan.getBalanceAmount()+" to "+computedBalance);
        histBalance.setPrevBalanceAmount(governmentLoan.getBalanceAmount());
        histBalance.setUpdatedBalanceAmount(computedBalance);
        histPaid.setChanges("Paid Amount changed from "+(governmentLoan.getPaidAmount())+" to "+computedPaid);
        histPaid.setPrevPaidAmount(governmentLoan.getPaidAmount());
        histPaid.setUpdatedPaidAmount(computedPaid);
        histPaid.setUpdatedBalanceAmount(computedPaid);
        governmentLoan.setBalanceAmount(computedBalance);
        governmentLoan.setPaidAmount(computedPaid);
        governmentLoanHistoryService.save(histBalance);
        governmentLoanHistoryService.save(histPaid);
        governmentLoanService.save(governmentLoan);
        governmentDeductionService.save(hod);
        if(has(deductions.getGovernmentDeductions())) {
            deductions.getGovernmentDeductions().add(hod);
        } else {
            Set<GovernmentDeductions> gds = new HashSet<>();
            gds.add(hod);
            deductions.setGovernmentDeductions(gds);
        }
    }

    public void generateHeadOfficeLoanDeduction(HeadOfficeLoan headOfficeLoan, SalaryDeductions deductions) {
        HeadOfficeDeductions hod = new HeadOfficeDeductions();
        HeadOfficeLoanHistory histBalance = new HeadOfficeLoanHistory();
        HeadOfficeLoanHistory histPaid = new HeadOfficeLoanHistory();
        hod.setDeductionAmount(headOfficeLoan.getDeductionAmount());
        if(headOfficeLoan.getLoanType().equals(EmployeeLoanType.PARAPHERNALIA)) {
            hod.setDeductionDescription(headOfficeLoan.getLoanType().name()+" - "+headOfficeLoan.getLoanDescription());
        } else {
            hod.setDeductionDescription(headOfficeLoan.getLoanType().name());
        }
        hod.setSalaryDeductions(deductions);
        hod.setHeadOfficeLoan(headOfficeLoan);
        histBalance.setOfficeLoan(headOfficeLoan);
        histBalance.setReason("System Generated from Payroll");
        histPaid.setOfficeLoan(headOfficeLoan);
        histPaid.setReason("System Generated from Payroll");
        Double computedBalance = headOfficeLoan.getBalanceAmount() - headOfficeLoan.getDeductionAmount();
        Double computedPaid = headOfficeLoan.getPaidAmount() + headOfficeLoan.getDeductionAmount();
        histBalance.setChanges("Balance changed from "+headOfficeLoan.getBalanceAmount()+" to "+computedBalance);
        histBalance.setPrevBalanceAmount(headOfficeLoan.getBalanceAmount());
        histBalance.setUpdatedBalanceAmount(computedBalance);
        histPaid.setChanges("Paid Amount changed from "+(headOfficeLoan.getPaidAmount())+" to "+computedPaid);
        histPaid.setPrevPaidAmount(headOfficeLoan.getPaidAmount());
        histPaid.setUpdatedPaidAmount(computedPaid);
        histPaid.setUpdatedBalanceAmount(computedPaid);
        headOfficeLoan.setBalanceAmount(computedBalance);
        headOfficeLoan.setPaidAmount(computedPaid);
        headOfficeLoanHistoryService.save(histBalance);
        headOfficeLoanHistoryService.save(histPaid);
        headOfficeLoanService.save(headOfficeLoan);
        headOfficeDeductionsService.save(hod);
        if(has(deductions.getHeadOfficeDeductions())) {
            deductions.getHeadOfficeDeductions().add(hod);
        } else {
            Set<HeadOfficeDeductions> hos = new HashSet<>();
            hos.add(hod);
            deductions.setHeadOfficeDeductions(hos);
        }
    }

    public void generateScoutLoanDeduction(ScoutLoan selectedScoutLoan, SalaryDeductions deductions) {
        ScoutDeductions hod = new ScoutDeductions();
        ScoutLoanHistory histBalance = new ScoutLoanHistory();
        ScoutLoanHistory histPaid = new ScoutLoanHistory();
        hod.setDeductionAmount(selectedScoutLoan.getDeductionAmount());
        if(selectedScoutLoan.getLoanType().equals(EmployeeLoanType.PARAPHERNALIA)) {
            hod.setDeductionDescription(selectedScoutLoan.getLoanType().name()+" - "+selectedScoutLoan.getLoanDescription());
        } else {
            hod.setDeductionDescription(selectedScoutLoan.getLoanType().name());
        }
        hod.setSalaryDeductions(deductions);
        hod.setScoutLoan(selectedScoutLoan);
        histBalance.setScoutLoan(selectedScoutLoan);
        histBalance.setReason("System Generated from Payroll");
        histPaid.setScoutLoan(selectedScoutLoan);
        histPaid.setReason("System Generated from Payroll");
        Double computedBalance = selectedScoutLoan.getBalanceAmount() - selectedScoutLoan.getDeductionAmount();
        Double computedPaid = selectedScoutLoan.getPaidAmount() + selectedScoutLoan.getDeductionAmount();
        histBalance.setChanges("Balance changed from "+selectedScoutLoan.getBalanceAmount()+" to "+computedBalance);
        histBalance.setPrevBalanceAmount(selectedScoutLoan.getBalanceAmount());
        histBalance.setUpdatedBalanceAmount(computedBalance);
        histPaid.setChanges("Paid Amount changed from "+(selectedScoutLoan.getPaidAmount())+" to "+computedPaid);
        histPaid.setPrevPaidAmount(selectedScoutLoan.getPaidAmount());
        histPaid.setUpdatedPaidAmount(computedPaid);
        histPaid.setUpdatedBalanceAmount(computedPaid);
        selectedScoutLoan.setBalanceAmount(computedBalance);
        selectedScoutLoan.setPaidAmount(computedPaid);
        scoutLoanHistoryService.save(histBalance);
        scoutLoanHistoryService.save(histPaid);
        scoutLoanService.save(selectedScoutLoan);
        scoutDeductionService.save(hod);
        if(has(deductions.getScoutDeductions())) {
            deductions.getScoutDeductions().add(hod);
        } else {
            Set<ScoutDeductions> sds = new HashSet<>();
            sds.add(hod);
            deductions.setScoutDeductions(sds);
        }
    }

    public Double generatePhicContribution(ErpEmployee emp, Double grossSal, Double rateCard) {
        Double retVal = 0.0;
        Double totalBasic = getTotalBasic(emp, grossSal);

        if(totalBasic > 0) {
            // compute 4% of the totalBasic divide by 2 (employee and employer)
            Double phicContribution = (totalBasic * 0.04) / 2;
            ErpConfig phicConfig = erpConfigService.findByName("Philhealth Contribution Minimum");
            if(has(phicConfig)) {
                if(phicContribution > phicConfig.getNumValue()) {
                    if(phicContribution >= rateCard) {
                        retVal = rateCard;
                    } else {
                        retVal = phicContribution;
                    }
                } else {
                    retVal = phicConfig.getNumValue();
                }
            } else {
                // phicConfig is not available
                //System.out.println("generatePhicContribution: cannot find phicConfig");
            }
        } else {
            // Total Gross is zero: reason is previous gross might not be available
            //System.out.println("generatePhicContribution: Total Gross [0]. Previous gross not available");
        }
        //System.out.println("Total Basic: "+totalBasic);
        return retVal;
    }

    public Double generateSSSContribution(ErpEmployee emp, Double grossSal) {
        Double retVal = 0.0;
        Double totalGross = getTotalGross(emp, grossSal);

        if(totalGross > 0) {
            List<ErpSSSContribution> contributions = erpSSSContributionService.getAllFromRange(totalGross);
            //System.out.println("CONTRIBUTIONS length: "+contributions.size());
            if (contributions.size() > 0) {
                retVal = contributions.get(0).getEe();
            }
        }
        return retVal;
    }

    public Double getTotalBasic(ErpEmployee emp, Double basicSal) {
        Double retVal = 0.0;
        LocalDate today = convertToLocalDateViaInstant(erpPayroll.getCoverPeriodStart());
        LocalDate cutOffStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
        LocalDate cutOffEnd = LocalDate.of(today.getYear(), today.getMonth(), 15);
        // get the previous Employee Payroll
        Date _start = convertToDateViaInstant(cutOffStart);
        Date _end = convertToDateViaInstant(cutOffEnd);
        //System.out.println("DATE START: "+_start+"DATE END: "+_end);
        List<EmployeePayroll> ep = employeePayrollService.findByEmployeeCutoff(emp, _start, _end);
        //System.out.println("EMPLOYEE PAYROLL ep LENGTH: "+ep.size()+" GROSS SAL: "+grossSal);
        if(ep.size() > 0 && has(basicSal)) {
            Double prevBasic = ep.get(0).getBasicSalary().getBasicSalary();
            Double totalBasic = prevBasic + basicSal;
            //System.out.println("totalGross: "+totalGross);
            retVal = totalBasic;
        } else {
            // no previous records
            //System.out.println("HAS NO PREVIOUS RECORDS.");
        }
        return retVal;
    }

    public Double getTotalGross(ErpEmployee emp, Double grossSal) {
        Double retVal = 0.0;
        LocalDate today = convertToLocalDateViaInstant(erpPayroll.getCoverPeriodStart());
        LocalDate cutOffStart = LocalDate.of(today.getYear(), today.getMonth(), 1);
        LocalDate cutOffEnd = LocalDate.of(today.getYear(), today.getMonth(), 15);
        // get the previous Employee Payroll
        Date _start = convertToDateViaInstant(cutOffStart);
        Date _end = convertToDateViaInstant(cutOffEnd);
        //System.out.println("DATE START: "+_start+"DATE END: "+_end);
        List<EmployeePayroll> ep = employeePayrollService.findByEmployeeCutoff(emp, _start, _end);
        //System.out.println("EMPLOYEE PAYROLL ep LENGTH: "+ep.size()+" GROSS SAL: "+grossSal);
        if(ep.size() > 0 && has(grossSal)) {
            Double prevGross = ep.get(0).getGrossSalary();
            Double totalGross = prevGross + grossSal;
            //System.out.println("totalGross: "+totalGross);
            retVal = totalGross;
        } else {
            // no previous records
            //System.out.println("HAS NO PREVIOUS RECORDS.");
        }
        return retVal;
    }

    public boolean isOneToFifteen(Date start, Date stop) {
        LocalDate _start = convertToLocalDateViaInstant(start);
        LocalDate _stop = convertToLocalDateViaInstant(stop);
        if(_start.getDayOfMonth() == 1 && _stop.getDayOfMonth() <= 15) {
            return true;
        }
        return false;
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return Date
                .from(dateToConvert.atStartOfDay(ZoneId.systemDefault())
                        .toInstant());
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
