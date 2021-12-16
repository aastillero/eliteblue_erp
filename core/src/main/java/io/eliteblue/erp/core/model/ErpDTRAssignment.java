package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "ERP_DTR_ASSIGNMENT")
public class ErpDTRAssignment extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dtr_assn_seq")
    @SequenceGenerator(name = "dtr_assn_seq", sequenceName = "dtr_assn_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ASSN_EMP_ID", referencedColumnName = "id")
    @NotNull
    private ErpEmployee employeeAssigned;

    @ManyToOne
    @JoinColumn(name = "dtr_id", nullable = false)
    private ErpDTR erpDTR;

    @OneToMany(mappedBy = "erpDTRAssignment", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpDTRSchedule> schedules;

    @Column(name = "TOTAL_WORK_HOURS")
    private Integer totalWorkHours;

    @Column(name = "TOTAL_WORK_DAYS")
    private Integer totalWorkDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpEmployee getEmployeeAssigned() {
        return employeeAssigned;
    }

    public void setEmployeeAssigned(ErpEmployee employeeAssigned) {
        this.employeeAssigned = employeeAssigned;
    }

    public ErpDTR getErpDTR() {
        return erpDTR;
    }

    public void setErpDTR(ErpDTR erpDTR) {
        this.erpDTR = erpDTR;
    }

    public Set<ErpDTRSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<ErpDTRSchedule> schedules) {
        this.schedules = schedules;
    }

    public Integer getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(Integer totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public Integer getTotalWorkDays() {
        return totalWorkDays;
    }

    public void setTotalWorkDays(Integer totalWorkDays) {
        this.totalWorkDays = totalWorkDays;
    }
}
