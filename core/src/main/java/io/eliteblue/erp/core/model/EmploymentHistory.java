package io.eliteblue.erp.core.model;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYMENT_HISTORY")
public class EmploymentHistory extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employment_hist_seq")
    @SequenceGenerator(name = "employment_hist_seq", sequenceName = "employment_hist_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private ErpEmployee erpEmployee;

    @Column(name = "CHANGES", columnDefinition = "TEXT")
    private String changes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpEmployee getEmployee() {
        return erpEmployee;
    }

    public void setEmployee(ErpEmployee erpEmployee) {
        this.erpEmployee = erpEmployee;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }
}
