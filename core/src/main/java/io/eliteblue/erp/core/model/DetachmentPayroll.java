package io.eliteblue.erp.core.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "DETACHMENT_PAYROLL")
public class DetachmentPayroll extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dpayroll_seq")
    @SequenceGenerator(name = "dpayroll_seq", sequenceName = "dpayroll_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @OneToOne
    @JoinColumn(name = "DETACHMENT_ID", referencedColumnName = "id")
    @NotNull
    private ErpDetachment detachment;

    @ManyToOne
    @JoinColumn(name = "erp_payroll_id", nullable = false)
    private ErpPayroll erpPayroll;

    @OneToMany(mappedBy = "detachmentPayroll", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<EmployeePayroll> employeePayrolls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpDetachment getDetachment() {
        return detachment;
    }

    public void setDetachment(ErpDetachment detachment) {
        this.detachment = detachment;
    }

    public ErpPayroll getErpPayroll() {
        return erpPayroll;
    }

    public void setErpPayroll(ErpPayroll erpPayroll) {
        this.erpPayroll = erpPayroll;
    }

    public Set<EmployeePayroll> getEmployeePayrolls() {
        return employeePayrolls;
    }

    public void setEmployeePayrolls(Set<EmployeePayroll> employeePayrolls) {
        this.employeePayrolls = employeePayrolls;
    }
}
