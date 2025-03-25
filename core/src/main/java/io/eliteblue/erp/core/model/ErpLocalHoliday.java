package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ERP_LOCAL_HOLIDAY")
public class ErpLocalHoliday extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lholiday_seq")
    @SequenceGenerator(name = "lholiday_seq", sequenceName = "lholiday_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "HOLIDAY_NAME", length = 50)
    private String name;

    @Column(name = "HOLIDAY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date holidayDate;

    @ManyToOne
    @JoinColumn(name = "operations_area_id", nullable = false)
    private OperationsArea operationsArea;

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

    public OperationsArea getOperationsArea() {
        return operationsArea;
    }

    public void setOperationsArea(OperationsArea operationsArea) {
        this.operationsArea = operationsArea;
    }
}
