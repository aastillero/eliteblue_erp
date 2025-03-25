package io.eliteblue.erp.core.bean;

import io.eliteblue.erp.core.model.ClientStatus;
import io.eliteblue.erp.core.service.ClientStatusService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@ViewScoped
public class ClientStatusForm implements Serializable {

    @Autowired
    private ClientStatusService clientStatusService;

    private Long id;
    private ClientStatus clientStatus;

    public void init() {
        if(Faces.isAjaxRequest()) {
            return;
        }
        if(has(id)) {
            clientStatus = clientStatusService.findById(Long.valueOf(id));
        } else {
            clientStatus = new ClientStatus();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public void clear() {
        clientStatus = new ClientStatus();
        id = null;
    }

    public boolean isNew() {
        return clientStatus == null || clientStatus.getId() == null;
    }

    public void remove() throws Exception {
        if(has(clientStatus) && has(clientStatus.getId())) {
            String name = clientStatus.getName();
            clientStatusService.delete(clientStatus);
            addDetailMessage("CLIENT STATUS DELETED", name, FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("client-status.xhtml");
        }
    }

    public void save() throws Exception {
        if(clientStatus != null) {
            clientStatusService.save(clientStatus);
            addDetailMessage("CLIENT STATUS SAVED", clientStatus.getName(), FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().getExternalContext().redirect("client-status.xhtml");
        }
    }

    public void addDetailMessage(String message, String detail, FacesMessage.Severity severity) {
        FacesMessage msg = new FacesMessage(message, detail);
        msg.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
