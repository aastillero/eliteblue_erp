package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.security.Authority;
import io.eliteblue.erp.core.model.security.AuthorityName;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.ErpUserService;
import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.service.OperationsAreaService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ErpUserFormMB implements Serializable {

    @Autowired
    private ErpUserService erpUserService;

    @Autowired
    private OperationsAreaService operationsAreaService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    private Long id;
    private ErpUser user;
    private Map<String, Boolean> enabledValues;
    private List<String> userRoles;
    private List<String> selectedRoles;
    private List<String> locations;
    private List<String> selectedLocations;
    private Map<String, Long> detachments;
    private List<Long> selectedDetachments;
    private List<Long> relieverDetachments;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            user = erpUserService.findById(Long.valueOf(id));
        } else {
            user = new ErpUser();
            //detachment = new ErpDetachment();
        }
        enabledValues = new HashMap<>();
        enabledValues.put("YES", true);
        enabledValues.put("NO", false);

        List<OperationsArea> areas = operationsAreaService.getAll();
        locations = new ArrayList<>();
        for(OperationsArea o: areas) {
            locations.add(o.getLocation());
        }
        detachments = new HashMap<>();
        for(ErpDetachment edp: erpDetachmentService.getAll()) {
            detachments.put(edp.getName(), edp.getId());
        }

        userRoles = new ArrayList<>();
        for(AuthorityName a : AuthorityName.values()) {
            userRoles.add(a.name().replaceAll("ROLE_", ""));
        }
        if(user != null && user.getId() != null) {
            selectedRoles = new ArrayList<>();
            selectedLocations = new ArrayList<>();
            selectedDetachments = new ArrayList<>();
            relieverDetachments = new ArrayList<>();
            for(Authority a : user.getAuthorities()) {
                selectedRoles.add(a.getName().name().replaceAll("ROLE_", ""));
            }
            for(OperationsArea ar: user.getLocations()) {
                selectedLocations.add(ar.getLocation());
            }
            for(ErpDetachment dts: user.getErpDetachments()) {
                selectedDetachments.add(dts.getId());
            }
            for(ErpDetachment dts: user.getRelieverDetachments()) {
                relieverDetachments.add(dts.getId());
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ErpUser getUser() {
        return user;
    }

    public void setUser(ErpUser user) {
        this.user = user;
    }

    public Map<String, Boolean> getEnabledValues() {
        return enabledValues;
    }

    public void setEnabledValues(Map<String, Boolean> enabledValues) {
        this.enabledValues = enabledValues;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public List<String> getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(List<String> selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public List<String> getSelectedLocations() {
        return selectedLocations;
    }

    public void setSelectedLocations(List<String> selectedLocations) {
        this.selectedLocations = selectedLocations;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Map<String, Long> getDetachments() {
        return detachments;
    }

    public void setDetachments(Map<String, Long> detachments) {
        this.detachments = detachments;
    }

    public List<Long> getSelectedDetachments() {
        return selectedDetachments;
    }

    public void setSelectedDetachments(List<Long> selectedDetachments) {
        this.selectedDetachments = selectedDetachments;
    }

    public List<Long> getRelieverDetachments() {
        return relieverDetachments;
    }

    public void setRelieverDetachments(List<Long> relieverDetachments) {
        this.relieverDetachments = relieverDetachments;
    }

    public void clear() {
        user = new ErpUser();
        id = null;
    }

    public boolean isNew() {
        return user == null || user.getId() == null;
    }

    public String navigateChangepass() throws Exception {
        return "/user/changepass-form?id="+user.getUsername()+"&faces-redirect=true&includeViewParams=true";
    }

    public void remove() throws Exception {
        if(has(user) && has(user.getId())) {
            String userFullName = user.getFirstname()+ " " +user.getLastname();
            erpUserService.delete(user);
            addDetailMessage("USER DELETED", userFullName+".", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("users-management.xhtml");
        }
    }

    public void save() throws Exception {
        if(user != null) {
            //System.out.println("DETACHMENT: "+detachment.getId()+" "+detachment.getName());
            /*if(has(erpDetachments)) {
                //detachment = erpDetachmentService.findById(detachment.getId());
                //user.setErpDetachment(detachment);
            }
            else {
                erpDetachments = null;
                //user.setErpDetachment(detachment);
            }*/
            if(selectedRoles != null && selectedRoles.size() > 0) {
                user.setAuthorities(new HashSet<>());
                for(String r: selectedRoles) {
                    Authority au = erpUserService.findAuthorityByName(AuthorityName.valueOf("ROLE_"+r));
                    if(au == null) {
                        au = new Authority();
                        au.setName(AuthorityName.valueOf("ROLE_"+r));
                    }
                    user.getAuthorities().add(au);
                }
            }
            if(selectedLocations != null && selectedLocations.size() > 0) {
                user.setLocations(new HashSet<>());
                for(String l: selectedLocations) {
                    OperationsArea area = new OperationsArea();
                    area = erpUserService.findAreaByLocation(l);
                    if(area == null) {
                        area.setOperation(DataOperation.CREATED.name());
                        area.setLastUpdate(new Date());
                        area.setLocation(l);
                    }
                    user.getLocations().add(area);
                }
            }
            if(selectedDetachments != null && selectedDetachments.size() >0) {
                user.setErpDetachments(new HashSet<>());
                //System.out.println("SELECTED: "+selectedDetachments);
                for (Object d : selectedDetachments) {
                    Long detId = 0L;
                    if (d instanceof String) {
                        //System.out.println("STRING INSTANCE ["+d+"]");
                        detId = Long.parseLong((String) d);
                    } else {
                        //System.out.println("LONG INSTANCE ["+d+"]");
                        detId = (Long) d;
                    }
                    ErpDetachment ed = erpDetachmentService.findById(detId);
                    if (ed != null) {
                        user.getErpDetachments().add(ed);
                    }
                }
            } else {
                user.setErpDetachments(null);
            }
            if(relieverDetachments != null && relieverDetachments.size() >0) {
                user.setRelieverDetachments(new HashSet<>());
                for (Object d : relieverDetachments) {
                    Long detId = 0L;
                    if (d instanceof String) {
                        //System.out.println("STRING INSTANCE ["+d+"]");
                        detId = Long.parseLong((String) d);
                    } else {
                        //System.out.println("LONG INSTANCE ["+d+"]");
                        detId = (Long) d;
                    }
                    ErpDetachment ed = erpDetachmentService.findById(detId);
                    if (ed != null) {
                        user.getRelieverDetachments().add(ed);
                    }
                }
            } else {
                user.setRelieverDetachments(null);
            }
            erpUserService.save(user);
            String userFullName = user.getFirstname()+ " " +user.getLastname();
            addDetailMessage("USER SAVED", userFullName+".", FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("users-management.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
