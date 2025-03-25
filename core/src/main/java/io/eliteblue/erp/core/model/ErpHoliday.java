package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.HolidayType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ERP_HOLIDAY")
public class ErpHoliday extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holiday_seq")
    @SequenceGenerator(name = "holiday_seq", sequenceName = "holiday_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "HOLIDAY_NAME", length = 50)
    private String name;

    @Column(name = "HOLIDAY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date holidayDate;

    @Column(name = "HOLIDAY_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private HolidayType holidayType;

    @Column(name = "HOLIDAY_PERCENTAGE")
    private Double holidayPayPercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date date) {
        this.holidayDate = date;
    }

    public HolidayType getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(HolidayType holidayType) {
        this.holidayType = holidayType;
    }

    public Double getHolidayPayPercentage() {
        return holidayPayPercentage;
    }

    public void setHolidayPayPercentage(Double holidayPayPercentage) {
        this.holidayPayPercentage = holidayPayPercentage;
    }
}
