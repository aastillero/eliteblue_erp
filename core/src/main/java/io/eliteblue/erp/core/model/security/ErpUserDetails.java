package io.eliteblue.erp.core.model.security;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ErpUserDetails implements UserDetails, Serializable {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private List<Authority> authorities;
    private List<OperationsArea> operationsAreas;
    private Date lastLogged;
    private Boolean locked;
    private Boolean enabled;
    private List<ErpDetachment> detachments;
    private List<ErpDetachment> relieverDetachments;

    public ErpUserDetails(){}

    public ErpUserDetails(String username, String firstname, String lastname, String email, String password, List<Authority> authorities, Date lastLogged, Boolean locked, Boolean enabled, List<OperationsArea> areas, List<ErpDetachment> detachments, List<ErpDetachment> relieverDetachments) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.lastLogged = lastLogged;
        this.locked = locked;
        this.enabled = enabled;
        this.operationsAreas = areas;
        this.detachments = detachments;
        this.relieverDetachments = relieverDetachments;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        for(Authority a : authorities) {
            auths.add(new SimpleGrantedAuthority(a.getName().name()));
        }
        return auths;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public Date getLastLogged() {
        return lastLogged;
    }

    public void setLastLogged(Date lastLogged) {
        this.lastLogged = lastLogged;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<OperationsArea> getOperationsAreas() {
        return operationsAreas;
    }

    public void setOperationsAreas(List<OperationsArea> operationsAreas) {
        this.operationsAreas = operationsAreas;
    }

    public List<ErpDetachment> getDetachments() {
        return detachments;
    }

    public void setDetachments(List<ErpDetachment> detachments) {
        this.detachments = detachments;
    }

    public List<ErpDetachment> getRelieverDetachments() {
        return relieverDetachments;
    }

    public void setRelieverDetachments(List<ErpDetachment> relieverDetachments) {
        this.relieverDetachments = relieverDetachments;
    }
}
