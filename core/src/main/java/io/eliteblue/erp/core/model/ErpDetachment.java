package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.Shift;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "DETACHMENT")
public class ErpDetachment extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detachment_seq")
    @SequenceGenerator(name = "detachment_seq", sequenceName = "detachment_seq", allocationSize = 1, initialValue = 10)
    private Long id;

    @Column(name = "NAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Column(name = "PHIC_SG")
    private Double phicSG;

    @Column(name = "PHIC_SIC")
    private Double phicSIC;

    @Column(name = "PHIC_DC")
    private Double phicDC;

    @Column(name = "EXCESS_ENABLED")
    private Boolean excessOT;

    @OneToOne
    @JoinColumn(name = "AREA_ID", referencedColumnName = "id")
    private OperationsArea location;

    @OneToMany(mappedBy = "erpDetachment", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpPost> posts;

    @OneToMany(mappedBy = "erpDetachment", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpTimeSchedule> erpTimeSchedules;

    @ManyToOne
    @JoinColumn(name = "erp_client_id", nullable = false)
    private ErpClient erpClient;

    @OneToMany(mappedBy = "erpDetachment", cascade = CascadeType.ALL)
    private Set<ErpEmployee> assignedEmployees;

    @OneToMany(mappedBy = "erpDetachment", cascade = CascadeType.ALL)
    private Set<TODClient> todClients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationsArea getLocation() {
        return location;
    }

    public void setLocation(OperationsArea location) {
        this.location = location;
    }

    public Set<ErpPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<ErpPost> posts) {
        this.posts = posts;
    }

    public Set<ErpTimeSchedule> getErpTimeSchedules() {
        return erpTimeSchedules;
    }

    public void setErpTimeSchedules(Set<ErpTimeSchedule> erpTimeSchedules) {
        this.erpTimeSchedules = erpTimeSchedules;
    }

    public ErpClient getErpClient() {
        return erpClient;
    }

    public void setErpClient(ErpClient erpClient) {
        this.erpClient = erpClient;
    }

    public Set<ErpEmployee> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void setAssignedEmployees(Set<ErpEmployee> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }

    public Double getPhicSG() {
        return phicSG;
    }

    public void setPhicSG(Double phicSG) {
        this.phicSG = phicSG;
    }

    public Double getPhicSIC() {
        return phicSIC;
    }

    public void setPhicSIC(Double phicSIC) {
        this.phicSIC = phicSIC;
    }

    public Double getPhicDC() {
        return phicDC;
    }

    public void setPhicDC(Double phicDC) {
        this.phicDC = phicDC;
    }

    public Boolean getExcessOT() {
        return excessOT;
    }

    public void setExcessOT(Boolean excessOT) {
        this.excessOT = excessOT;
    }

    public Set<TODClient> getTodClients() {
        return todClients;
    }

    public void setTodClients(Set<TODClient> todClients) {
        this.todClients = todClients;
    }
}
