package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.constants.DataOperation;
import io.eliteblue.erp.core.model.ErpDetachment;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.security.Authority;
import io.eliteblue.erp.core.model.security.AuthorityName;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.service.ErpDetachmentService;
import io.eliteblue.erp.core.service.ErpUserService;
import io.eliteblue.erp.core.service.OperationsAreaService;
import io.eliteblue.erp.core.util.CurrentUserUtil;
import org.apache.xmlbeans.impl.store.Cur;
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
public class ErpChangepassForm implements Serializable {

    @Autowired
    private ErpUserService erpUserService;

    @Autowired
    private OperationsAreaService operationsAreaService;

    @Autowired
    private ErpDetachmentService erpDetachmentService;

    @Autowired
    private CurrentUserUtil currentUserUtil;

    private String id;
    private ErpUser user;
    private String passw;
    private String verifyPassw;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id) && CurrentUserUtil.isAdmin()) {
            user = erpUserService.getErpUser(id);
        } else {
            user = erpUserService.getCurrentUser();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ErpUser getUser() {
        return user;
    }

    public void setUser(ErpUser user) {
        this.user = user;
    }

    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public String getVerifyPassw() {
        return verifyPassw;
    }

    public void setVerifyPassw(String verifyPassw) {
        this.verifyPassw = verifyPassw;
    }

    public void clear() {
        passw = null;
        verifyPassw = null;
    }

    public void save() throws Exception {
        if(passw != null && !passw.isEmpty()) {
            if(verifyPassw != null && !verifyPassw.isEmpty()) {
                if(passw.equals(verifyPassw)) {
                    //user.setPassword(passw);
                    erpUserService.changePassword(user, passw);
                    String userFullName = user.getFirstname()+ " " +user.getLastname();
                    addDetailMessage("PASSWORD CHANGED", userFullName+".", FacesMessage.SEVERITY_INFO);
                    //FacesContext.getCurrentInstance().getExternalContext().redirect("changepass-form.xhtml");
                } else {
                    addDetailMessage("CHANGE PASSWORD FAILED", " Passwords did not match.", FacesMessage.SEVERITY_ERROR);
                }
            } else {
                addDetailMessage("CHANGE PASSWORD FAILED", " Verify Password is Empty.", FacesMessage.SEVERITY_ERROR);
            }
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
