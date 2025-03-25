package io.eliteblue.erp.core.model.security;

import io.eliteblue.erp.core.model.CoreEntity;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ERP_USER")
public class ErpUser extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1, initialValue = 10)
    private Long id;

    @Column(name = "USERNAME", length = 50, unique = true)
    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @Column(name = "PASSWORD", length = 100)
    @NotNull
    @Size(max = 100)
    private String password;

    @Column(name = "FIRSTNAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String firstname;

    @Column(name = "LASTNAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String lastname;

    @Column(name = "EMAIL", length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_AREA",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AREA_ID", referencedColumnName = "ID")})
    private Set<OperationsArea> locations;

    @Column(name = "ENABLED")
    @NotNull
    private Boolean enabled;

    @Column(name = "LASTPASSWORDRESETDATE")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date lastPasswordResetDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities;

    //@ManyToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "erp_detachment_id")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_DETACHMENT",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "DETACHMENT_ID", referencedColumnName = "ID")})
    private Set<ErpDetachment> erpDetachments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "RELIEVER_DETACHMENT",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "DETACHMENT_ID", referencedColumnName = "ID")})
    private Set<ErpDetachment> relieverDetachments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public Set<OperationsArea> getLocations() {
        return locations;
    }

    public void setLocations(Set<OperationsArea> locations) {
        this.locations = locations;
    }

    public Set<ErpDetachment> getErpDetachments() {
        return erpDetachments;
    }

    public void setErpDetachments(Set<ErpDetachment> erpDetachments) {
        this.erpDetachments = erpDetachments;
    }

    public Set<ErpDetachment> getRelieverDetachments() {
        return relieverDetachments;
    }

    public void setRelieverDetachments(Set<ErpDetachment> relieverDetachments) {
        this.relieverDetachments = relieverDetachments;
    }
}