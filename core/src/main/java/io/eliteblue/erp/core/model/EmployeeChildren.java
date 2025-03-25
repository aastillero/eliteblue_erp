package io.eliteblue.erp.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "EMPLOYEE_CHILDREN")
public class EmployeeChildren extends CoreEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_children_seq")
    @SequenceGenerator(name = "employee_children_seq", sequenceName = "employee_children_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "COMPLETE_NAME", length = 50)
    private String completeName;

    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @Column(name = "ADDRESS_CONTACT", length = 50)
    private String addressContact;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private ErpEmployee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddressContact() {
        return addressContact;
    }

    public void setAddressContact(String addressContact) {
        this.addressContact = addressContact;
    }

    public ErpEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(ErpEmployee employee) {
        this.employee = employee;
    }
}
