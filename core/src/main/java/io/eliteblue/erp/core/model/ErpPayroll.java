package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "ERP_PAYROLL")
public class ErpPayroll extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payroll_seq")
    @SequenceGenerator(name = "payroll_seq", sequenceName = "payroll_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "COVER_PERIOD_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date coverPeriodStart;

    @Column(name = "COVER_PERIOD_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date coverPeriodEnd;

    @OneToMany(mappedBy = "erpPayroll", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<DetachmentPayroll> detachmentPayrolls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCoverPeriodStart() {
        return coverPeriodStart;
    }

    public void setCoverPeriodStart(Date coverPeriodStart) {
        this.coverPeriodStart = coverPeriodStart;
    }

    public Date getCoverPeriodEnd() {
        return coverPeriodEnd;
    }

    public void setCoverPeriodEnd(Date coverPeriodEnd) {
        this.coverPeriodEnd = coverPeriodEnd;
    }

    public Set<DetachmentPayroll> getDetachmentPayrolls() {
        return detachmentPayrolls;
    }

    public void setDetachmentPayrolls(Set<DetachmentPayroll> detachmentPayrolls) {
        this.detachmentPayrolls = detachmentPayrolls;
    }
}
