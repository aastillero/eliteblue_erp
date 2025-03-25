package io.eliteblue.erp.core.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import io.eliteblue.erp.core.model.ErpEmployee;
import io.eliteblue.erp.core.model.OperationsArea;
import io.eliteblue.erp.core.model.security.ErpOAuthUser;
import io.eliteblue.erp.core.model.security.ErpUser;
import io.eliteblue.erp.core.model.security.ErpUserDetails;
import io.eliteblue.erp.core.service.ErpEmployeeService;
import io.eliteblue.erp.core.service.ErpUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

@Named
@ViewScoped
public class SessionMB implements Serializable {

	private String currentUser;
	private String firstname;
	private String lastname;
	private String username;
	private List<OperationsArea> operationsAreas;
	private ErpEmployee erpEmployee;

	@Autowired
	private ErpEmployeeService erpEmployeeService;

	@PostConstruct
	public void init() {
		currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal instanceof ErpUserDetails) {
			ErpUserDetails erpUserDetails = (ErpUserDetails) principal;
			String username = erpUserDetails.getUsername();
			firstname = erpUserDetails.getFirstname();
			lastname = erpUserDetails.getLastname();
			operationsAreas = erpUserDetails.getOperationsAreas();
			List<ErpEmployee> erpEmployees = erpEmployeeService.findByFirstnameAndLastname(firstname, lastname);
			if(erpEmployees != null && erpEmployees.size() > 0) {
				erpEmployee = erpEmployees.get(0);
			}
		}
		else if(principal instanceof ErpOAuthUser) {
			ErpOAuthUser erpOAuthUser = (ErpOAuthUser) principal;
			String username = erpOAuthUser.getUsername();
			firstname = erpOAuthUser.getFirstname();
			lastname = erpOAuthUser.getLastname();
			operationsAreas = erpOAuthUser.getOperationsAreas();
			List<ErpEmployee> erpEmployees = erpEmployeeService.findByFirstnameAndLastname(firstname, lastname);
			if(erpEmployees != null && erpEmployees.size() > 0) {
				erpEmployee = erpEmployees.get(0);
			}
		}
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
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

	public List<OperationsArea> getOperationsAreas() {
		return operationsAreas;
	}

	public void setOperationsAreas(List<OperationsArea> operationsAreas) {
		this.operationsAreas = operationsAreas;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ErpEmployee getErpEmployee() {
		return erpEmployee;
	}

	public void setErpEmployee(ErpEmployee erpEmployee) {
		this.erpEmployee = erpEmployee;
	}
}
