package io.eliteblue.erp.core.service;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.repository.AuthorityRepository;
import io.eliteblue.erp.core.repository.ErpUserRepository;
import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.security.Authority;
import io.eliteblue.erp.core.model.security.AuthorityName;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.model.security.ErpUserDetails;
import io.eliteblue.erp.core.repository.OperationsAreaRepository;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class ErpUserService implements Serializable {

    private final static String USERNAME_TAKEN = "user with username %s is taken";

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ErpUserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OperationsAreaRepository operationsAreaRepository;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    public List<ErpUser> getAllUsers() {
        return repository.findAll();
    }

    public List<OperationsArea> getAllAreas() { return operationsAreaRepository.findAll(); }

    public OperationsArea findAreaByLocation(String location) {
        return operationsAreaRepository.findByLocation(location);
    }

    public ErpUser findById(Long id) {
        return repository.getOne(id);
    }

    public void delete(ErpUser t) {
        repository.delete(t);
    }

    public Authority findAuthorityByName(AuthorityName name) {
        return authorityRepository.findByName(name);
    }

    /*public Long savePassword(ErpUser user) throws Exception {

    }*/

    public ErpUser getCurrentUser() {
        String username = CurrentUserUtil.getUsername();
        ErpUser currentUser = repository.findByUsername(username);
        return currentUser;
    }

    public Long changePassword(ErpUser user, String password) throws Exception {
        //System.out.println("PWD: "+password);
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        //System.out.println("ENCODED: "+encodedPassword);
        user.setPassword(encodedPassword);
        user.setLastPasswordResetDate(new Date());
        return repository.save(user).getId();
    }

    public Long save(ErpUser user) throws Exception {
        if(user != null) {
            Set<Authority> authorityList = new HashSet<>();
            Set<OperationsArea> areaList = new HashSet<>();
            ErpUser dbUser = repository.findByUsername(user.getUsername());

            /*System.out.println("USERS PASSWORD: "+user.getPassword());
            if(user.getPassword() != null && !(user.getPassword().isEmpty())) {
                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                user.setLastPasswordResetDate(new Date());
                user.setPassword(encodedPassword);
            } else {
                //System.out.println("USERS PASSWORD is empty");
                if(dbUser != null) {
                    user.setPassword(dbUser.getPassword());
                }
            }*/

            if(user.getId() == null) {
                boolean userExists = dbUser != null;

                if(userExists) {
                    throw new IllegalStateException(String.format(USERNAME_TAKEN, user.getUsername()));
                }

                if(user.getPassword() != null && !(user.getPassword().isEmpty())) {
                    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                    user.setLastPasswordResetDate(new Date());
                    user.setPassword(encodedPassword);
                }

                user.setOperation(DataOperation.CREATED.name());
                user.setDateCreated(new Date());
                user.setLastUpdate(new Date());
                if(user.getEnabled() == null) {
                    user.setEnabled(true);
                }
            }
            else {
                user.setOperation(DataOperation.UPDATED.name());
                user.setLastUpdate(new Date());
            }
            if(user.getAuthorities() != null) {
                for(Authority a: user.getAuthorities()) {
                    if(a.getId() == null) {
                        a = saveAuthorityName(a.getName());
                    }
                    authorityList.add(a);
                }
                user.setAuthorities(authorityList);
            }
            else {
                Authority authority = authorityRepository.findByName(AuthorityName.ROLE_USER);
                if(authority == null) {
                    authority = saveAuthorityName(AuthorityName.ROLE_USER);
                }
                authorityList.add(authority);
                user.setAuthorities(authorityList);
            }
            if(user.getLocations() != null) {
                for(OperationsArea l: user.getLocations()) {
                    if(l.getId() == null) {
                        l = saveUserArea(l.getLocation());
                    }
                    areaList.add(l);
                }
            }
            else {
                OperationsArea area = operationsAreaRepository.findByLocation("FIELD OFFICE");
                if(area == null) {
                    area = saveUserArea("FIELD OFFICE");
                }
                areaList.add(area);
                user.setLocations(areaList);
            }
            user.setLastUpdate(new Date());
            return repository.save(user).getId();
        }
        return null;
    }

    public Authority saveAuthorityName(AuthorityName name) {
        Authority retVal = new Authority();
        retVal.setName(name);
        retVal.setDateCreated(new Date());
        retVal.setLastUpdate(new Date());
        retVal.setOperation(DataOperation.CREATED.name());
        return authorityRepository.save(retVal);
    }

    public OperationsArea saveUserArea(String location) {
        OperationsArea retVal = new OperationsArea();
        retVal.setLocation(location);
        retVal.setLastUpdate(new Date());
        retVal.setDateCreated(new Date());
        retVal.setOperation(DataOperation.CREATED.name());
        return operationsAreaRepository.save(retVal);
    }

    public ErpUser findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public ErpUserDetails getUserDetails(String username) {
        ErpUser user = repository.findByUsername(username);
        List<Authority> authorities = (user.getAuthorities() != null) ? new ArrayList<>(user.getAuthorities()) : null;
        List<OperationsArea> operationsAreas = (user.getLocations() != null) ? new ArrayList<>(user.getLocations()) : null;
        List<ErpDetachment> detachments = (user.getErpDetachments() != null) ? new ArrayList<>(user.getErpDetachments()) : null;
        List<ErpDetachment> relieverDetachments = (user.getRelieverDetachments() != null) ? new ArrayList<>(user.getRelieverDetachments()) : null;

        if(user == null)
            return null;

        return new ErpUserDetails(
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                new Date(),
                false,
                user.getEnabled(),
                operationsAreas,
                detachments,
                relieverDetachments
        );
    }

    public ErpUser getErpUser(String username) {
        //System.out.println("getErpUser() METHOD: "+username);
        ErpUser user = repository.findByUsername(username);
        return user;
    }
}
