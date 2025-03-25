package io.eliteblue.erp.core.model.view;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.ErpSil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

public class SilMonitoring implements Serializable {

    private final SimpleDateFormat format1 = new SimpleDateFormat("MMM dd-");

    private final SimpleDateFormat format2 = new SimpleDateFormat("dd, yyyy");

    private final SimpleDateFormat format3 = new SimpleDateFormat("MMM dd");

    private String name;

    private Date dateHired;

    private ErpDetachment detachment;

    private Date dateOfRegularity;

    private String dayOne;

    private String paymentDateOne;

    private String dayTwo;

    private String paymentDateTwo;

    private String dayThree;

    private String paymentDateThree;

    private String dayFour;

    private String paymentDateFour;

    private String dayFive;

    private String paymentDateFive;

    private Double totalAvailment;

    public SilMonitoring() {}

    public SilMonitoring(List<ErpSil> erpSil) {
        if(has(erpSil) && erpSil.size() > 0) {
            this.name = erpSil.get(0).getEmployee().getLastname() + ", " + erpSil.get(0).getEmployee().getFirstname();
            //System.out.println("EMPLOYEE: "+this.name);
            if(has(erpSil.get(0).getEmployee().getJoinedDate())) {
                this.dateHired = erpSil.get(0).getEmployee().getJoinedDate();
                // calculate one year from date hired
                this.dateOfRegularity = computeRegularization(this.getDateHired());
            }
            this.detachment = erpSil.get(0).getEmployee().getErpDetachment();
            // traverse the list
            erpSil.sort(Comparator.comparing(ErpSil::getDateAvailedStart).reversed());
            List<String> availedDates = new ArrayList<>();
            List<String> paymentDates = new ArrayList<>();
            List<Date> availedCheck = new ArrayList<>();
            for(ErpSil s: erpSil) {
                int availedDays = s.getNoDaysAvailed().intValue();
                Date _dateStart = s.getDateAvailedStart();
                if(!availedCheck.contains(_dateStart)) {
                    paymentDates.add(format1.format(s.getDateOfPaymentStart()) + "" + format2.format(s.getDateOfPaymentStop()));
                    availedDates.add(format3.format(_dateStart));
                    availedCheck.add(_dateStart);
                    this.totalAvailment = 1.0;
                    for (int x = 1; x < availedDays; x++) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(_dateStart);
                        cal.add(Calendar.DAY_OF_MONTH, x);
                        if(!availedCheck.contains(cal.getTime())) {
                            availedDates.add(format3.format(cal.getTime()));
                            availedCheck.add(cal.getTime());
                            paymentDates.add(format1.format(s.getDateOfPaymentStart()) + "" + format2.format(s.getDateOfPaymentStop()));
                            totalAvailment = (totalAvailment < 5) ? totalAvailment + 1 : totalAvailment + 0;
                        }
                    }
                }
            }
            if(availedDates.size() > 0) {
                this.dayOne = availedDates.get(0);
                this.paymentDateOne = paymentDates.get(0);
            }
            if(availedDates.size() > 1) {
                this.dayTwo = availedDates.get(1);
                this.paymentDateTwo = paymentDates.get(1);
            }
            if(availedDates.size() > 2) {
                this.dayThree = availedDates.get(2);
                this.paymentDateThree = paymentDates.get(2);
            }
            if(availedDates.size() > 3) {
                this.dayFour = availedDates.get(3);
                this.paymentDateFour = paymentDates.get(3);
            }
            if(availedDates.size() > 4) {
                this.dayFive = availedDates.get(4);
                this.paymentDateFive = paymentDates.get(4);
            }
        }
    }

    public Date computeRegularization(Date joinedDate) {
        Date retVal;
        Calendar cal = Calendar.getInstance();
        cal.setTime(joinedDate);
        //System.out.println("JOINED DATE: "+cal.getTime());
        cal.add(Calendar.YEAR, 1);
        retVal = cal.getTime();
        //System.out.println("CALCULATED: "+retVal);
        return retVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateHired() {
        return dateHired;
    }

    public void setDateHired(Date dateHired) {
        this.dateHired = dateHired;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public Date getDateOfRegularity() {
        return dateOfRegularity;
    }

    public void setDateOfRegularity(Date dateOfRegularity) {
        this.dateOfRegularity = dateOfRegularity;
    }

    public String getDayOne() {
        return dayOne;
    }

    public void setDayOne(String dayOne) {
        this.dayOne = dayOne;
    }

    public String getPaymentDateOne() {
        return paymentDateOne;
    }

    public void setPaymentDateOne(String paymentDateOne) {
        this.paymentDateOne = paymentDateOne;
    }

    public String getDayTwo() {
        return dayTwo;
    }

    public void setDayTwo(String dayTwo) {
        this.dayTwo = dayTwo;
    }

    public String getPaymentDateTwo() {
        return paymentDateTwo;
    }

    public void setPaymentDateTwo(String paymentDateTwo) {
        this.paymentDateTwo = paymentDateTwo;
    }

    public String getDayThree() {
        return dayThree;
    }

    public void setDayThree(String dayThree) {
        this.dayThree = dayThree;
    }

    public String getPaymentDateThree() {
        return paymentDateThree;
    }

    public void setPaymentDateThree(String paymentDateThree) {
        this.paymentDateThree = paymentDateThree;
    }

    public String getDayFour() {
        return dayFour;
    }

    public void setDayFour(String dayFour) {
        this.dayFour = dayFour;
    }

    public String getPaymentDateFour() {
        return paymentDateFour;
    }

    public void setPaymentDateFour(String paymentDateFour) {
        this.paymentDateFour = paymentDateFour;
    }

    public String getDayFive() {
        return dayFive;
    }

    public void setDayFive(String dayFive) {
        this.dayFive = dayFive;
    }

    public String getPaymentDateFive() {
        return paymentDateFive;
    }

    public void setPaymentDateFive(String paymentDateFive) {
        this.paymentDateFive = paymentDateFive;
    }

    public Double getTotalAvailment() {
        return totalAvailment;
    }

    public void setTotalAvailment(Double totalAvailment) {
        this.totalAvailment = totalAvailment;
    }
}
