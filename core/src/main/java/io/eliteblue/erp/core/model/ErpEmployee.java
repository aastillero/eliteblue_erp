package io.eliteblue.erp.core.model;

import io.eliteblue.erp.core.constants.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "ERP_EMPLOYEE")
public class ErpEmployee extends CoreEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "EMP_ID", length = 50, updatable = false, unique = true)
    private String employeeId;

    @Column(name = "FIRSTNAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String firstname;

    @Column(name = "LASTNAME", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String lastname;

    @Column(name = "MIDDLENAME", length = 50)
    private String middlename;

    @Column(name = "EXTNAME", length = 50)
    private String extname;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "JOINED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedDate;

    @Column(name = "RESIGNED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resignedDate;

    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @Column(name = "BIRTH_PLACE", length = 50)
    private String birthPlace;

    @Column(name = "STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @Column(name = "EMPLOYMENT", length = 20)
    @Enumerated(EnumType.STRING)
    private EmploymentType employment;

    @Column(name = "GENDER", length = 20)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "BLOOD_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column(name="WEIGHT_KG", precision = 5, scale = 2)
    private Double weightKilo;

    @Column(name="WEIGHT_LB", precision = 5, scale = 2)
    private Double weightPound;

    @Column(name = "HEIGHT_CM")
    private Double heightCentimeters;

    @Column(name = "MARKS", length = 50)
    private String marks;

    @Column(name = "COMPANY_POSITION", length = 50)
    private String position;

    @OneToOne
    @JoinColumn(name = "COMPANY_POS", referencedColumnName = "id")
    private CompanyPosition companyPosition;

    @Column(name = "BANK_NAME", length = 50)
    private String bankName;

    @Column(name = "BANK_ACC_NUM", length = 50)
    private String bankAccountNumber;

    @Column(name = "SALARY_RATE")
    private Double salaryRate;

    @Column(name = "ALLOWANCE")
    private Double allowance;

    @Column(name = "SIL")
    private Double sil;

    @Column(name = "REST_DAY_PAY_ENTITLED")
    private Boolean restDayPayEntitled;

    @Column(name = "SALARY_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;

    @Column(name = "CONTRI_STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    private ContributionType contributionType;

    @OneToOne
    @JoinColumn(name = "AREA_ID", referencedColumnName = "id")
    private OperationsArea assignedLocation;

    //---------- MARITAL HISTORY -----------
    @Column(name = "CIVIL_STATUS", length = 20)
    @Enumerated(EnumType.STRING)
    private CivilStatus civilStatus;

    @Column(name = "NAME_OF_SPOUSE", length = 50)
    private String nameOfSpouse;

    @Column(name = "DATE_OF_MARRIAGE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date marriageDate;

    @Column(name = "PLACE_OF_MARRIAGE", length = 50)
    private String marriagePlace;

    @Column(name = "CHILDREN_NAME1", length = 50)
    private String childrenName1;

    @Column(name = "CHILDREN_BIRTHDAY1")
    @Temporal(TemporalType.TIMESTAMP)
    private Date childrenBirthday1;

    @Column(name = "CHILDREN_ADDRESS1", length = 50)
    private String childrenAddress1;

    @Column(name = "CHILDREN_NAME2", length = 50)
    private String childrenName2;

    @Column(name = "CHILDREN_BIRTHDAY2")
    @Temporal(TemporalType.TIMESTAMP)
    private Date childrenBirthday2;

    @Column(name = "CHILDREN_ADDRESS2", length = 50)
    private String childrenAddress2;

    @Column(name = "CHILDREN_NAME3", length = 50)
    private String childrenName3;

    @Column(name = "CHILDREN_BIRTHDAY3")
    @Temporal(TemporalType.TIMESTAMP)
    private Date childrenBirthday3;

    @Column(name = "CHILDREN_ADDRESS3", length = 50)
    private String childrenAddress3;

    @Column(name = "CHILDREN_NAME4", length = 50)
    private String childrenName4;

    @Column(name = "CHILDREN_BIRTHDAY4")
    @Temporal(TemporalType.TIMESTAMP)
    private Date childrenBirthday4;

    @Column(name = "CHILDREN_ADDRESS4", length = 50)
    private String childrenAddress4;

    @Column(name = "CHILDREN_NAME5", length = 50)
    private String childrenName5;

    @Column(name = "CHILDREN_BIRTHDAY5")
    @Temporal(TemporalType.TIMESTAMP)
    private Date childrenBirthday5;

    @Column(name = "CHILDREN_ADDRESS5", length = 50)
    private String childrenAddress5;

    //--------------------------------------
    //---------- FAMILY HISTORY & INFORMATION -----------

    @Column(name = "NAME_OF_FATHER", length = 50)
    private String nameOfFather;

    @Column(name = "BIRTHDATE_OF_FATHER")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fatherBirthDate;

    @Column(name = "BIRTHPLACE_OF_FATHER", length = 50)
    private String fatherBirthPlace;

    @Column(name = "FATHER_OCCUPATION", length = 50)
    private String fatherOccupation;

    @Column(name = "FATHER_EMPLOY_PLACE", length = 50)
    private String fatherEmployPlace;

    @Column(name = "FATHER_CITIZENSHIP", length = 50)
    private String fatherCitizenship;

    @Column(name = "FATHER_CONTACTS", length = 50)
    private String fatherContacts;

    @Column(name = "NAME_OF_MOTHER", length = 50)
    private String nameOfMother;

    @Column(name = "BIRTHDATE_OF_MOTHER")
    @Temporal(TemporalType.TIMESTAMP)
    private Date motherBirthDate;

    @Column(name = "BIRTHPLACE_OF_MOTHER", length = 50)
    private String motherBirthPlace;

    @Column(name = "MOTHER_OCCUPATION", length = 50)
    private String motherOccupation;

    @Column(name = "MOTHER_EMPLOY_PLACE", length = 50)
    private String motherEmployPlace;

    @Column(name = "MOTHER_CITIZENSHIP", length = 50)
    private String motherCitizenship;

    @Column(name = "MOTHER_CONTACTS", length = 50)
    private String motherContacts;

    @Column(name = "NAME_OF_FATHERINLAW", length = 50)
    private String nameOfFatherInLaw;

    @Column(name = "BIRTHDATE_OF_FATHERINLAW")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fatherInLawBirthDate;

    @Column(name = "BIRTHPLACE_OF_FATHERINLAW", length = 50)
    private String fatherInLawBirthPlace;

    @Column(name = "FATHERINLAW_ADDRESS", length = 50)
    private String fatherInLawAddress;

    @Column(name = "FATHERINLAW_CITIZENSHIP", length = 50)
    private String fatherInLawCitizenship;

    @Column(name = "FATHERINLAW_CONTACTS", length = 50)
    private String fatherInLawContacts;

    @Column(name = "NAME_OF_MOTHERINLAW", length = 50)
    private String nameOfMotherInLaw;

    @Column(name = "BIRTHDATE_OF_MOTHERINLAW")
    @Temporal(TemporalType.TIMESTAMP)
    private Date motherInLawBirthDate;

    @Column(name = "BIRTHPLACE_OF_MOTHERINLAW", length = 50)
    private String motherInLawBirthPlace;

    @Column(name = "MOTHERINLAW_ADDRESS", length = 50)
    private String motherInLawAddress;

    @Column(name = "MOTHERINLAW_CITIZENSHIP", length = 50)
    private String motherInLawCitizenship;

    @Column(name = "MOTHERINLAW_CONTACTS", length = 50)
    private String motherInLawContacts;

    @Column(name = "STEP_PARENT_SISTER", length = 50)
    private String stepParentSister;

    @Column(name = "STEP_PARENT_SISTER_ADDRESS", length = 50)
    private String stepParentSisterAddress;

    @Column(name = "STEP_PARENT_SISTER_CITIZENSHIP", length = 50)
    private String stepParentSisterCitizenship;

    @Column(name = "STEP_PARENT_SISTER_CONTACTS", length = 50)
    private String stepParentSisterContacts;

    //--------------------------------------
    //---------- EDUCATIONAL BACKGROUND -----------

    @Column(name = "ELEMENTARY", length = 50)
    private String elementary;

    @Column(name = "ELEMENTARY_ADDRESS", length = 50)
    private String elementaryAddress;

    @Column(name = "ELEM_ATTENDED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date elementaryAttended;

    @Column(name = "ELEM_GRADUATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date elementaryGraduated;

    @Column(name = "HIGHSCHOOL", length = 50)
    private String highSchool;

    @Column(name = "HIGHSCHOOL_ADDRESS", length = 50)
    private String highSchoolAddress;

    @Column(name = "HS_ATTENDED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date highSchoolAttended;

    @Column(name = "HS_GRADUATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date highSchoolGraduated;

    @Column(name = "COLLEGE", length = 50)
    private String college;

    @Column(name = "COLLEGE_ADDRESS", length = 50)
    private String collegeAddress;

    @Column(name = "COLLEGE_ATTENDED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date collegeAttended;

    @Column(name = "COLLEGE_GRADUATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date collegeGraduated;

    @Column(name = "OTHER_SCHOOLS", length = 50)
    private String otherSchools;

    @Column(name = "OTHER_SCHOOLS_ADDRESS", length = 50)
    private String otherSchoolsAddress;

    @Column(name = "OTHER_SCHOOLS_ATTENDED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otherSchoolsAttended;

    @Column(name = "OTHER_SCHOOLS_GRADUATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otherSchoolsGraduated;

    @Column(name = "SEMINAR_QUALIFICATIONS", length = 50)
    private String seminarQualifications;

    @Column(name = "SEMINAR_QUALIFICATIONS_ADDRESS", length = 50)
    private String seminarQualificationsAddress;

    @Column(name = "SEMINAR_QUALIFICATIONS_ATTENDED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seminarQualificationsAttended;

    @Column(name = "SEMINAR_QUALIFICATIONS_GRADUATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seminarQualificationsGraduated;

    //--------------------------------------
    //---------- EMPLOYMENT HISTORY -----------

    @Column(name = "COMPANY_AGENCY1", length = 50)
    private String companyAgency1;

    @Column(name = "COMPANY1_HIRED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date company1Hired;

    @Column(name = "COMPANY1_RESIGNED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date company1Resigned;

    @Column(name = "COMPANY1_ADDRESS", length = 50)
    private String company1Address;

    @Column(name = "COMPANY1_REASON", length = 50)
    private String company1Reason;

    @Column(name = "COMPANY_AGENCY2", length = 50)
    private String companyAgency2;

    @Column(name = "COMPANY2_HIRED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date company2Hired;

    @Column(name = "COMPANY2_RESIGNED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date company2Resigned;

    @Column(name = "COMPANY2_ADDRESS", length = 50)
    private String company2Address;

    @Column(name = "COMPANY2_REASON", length = 50)
    private String company2Reason;

    @Column(name = "COMPANY_AGENCY3", length = 50)
    private String companyAgency3;

    @Column(name = "COMPANY3_HIRED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date company3Hired;

    @Column(name = "COMPANY3_RESIGNED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date company3Resigned;

    @Column(name = "COMPANY3_ADDRESS", length = 50)
    private String company3Address;

    @Column(name = "COMPANY3_REASON", length = 50)
    private String company3Reason;

    @Column(name = "IS_FORCED_RESIGNED")
    private Boolean forcedResigned;

    @Column(name = "FORCE_RES_REASON", length = 50)
    private String forceResignedReason;

    //--------------------------------------
    //---------- ARREST AND CONDUCT -----------

    @Column(name = "INVESTIGATED_ARRESTED", length = 50)
    private String investigatedArrested;

    @Column(name = "FAM_INVESTIGATED_ARRESTED", length = 50)
    private String famInvestigatedArrested;

    @Column(name = "ADMINISTRATIVE_CASE", length = 50)
    private String administrativeCase;

    @Column(name = "LIQUOR_NARCOTICS", length = 50)
    private String liquorNarcotics;

    //--------------------------------------
    //---------- CHARACTER REFERENCES -----------

    @Column(name = "CH_REFERENCES1", length = 50)
    private String characterRef1;

    @Column(name = "BUS_REFERENCES1", length = 50)
    private String busRef1;

    @Column(name = "CH_REFERENCES2", length = 50)
    private String characterRef2;

    @Column(name = "BUS_REFERENCES2", length = 50)
    private String busRef2;

    @Column(name = "CH_REFERENCES3", length = 50)
    private String characterRef3;

    @Column(name = "BUS_REFERENCES3", length = 50)
    private String busRef3;

    @Column(name = "CH_REFERENCES4", length = 50)
    private String characterRef4;

    @Column(name = "BUS_REFERENCES4", length = 50)
    private String busRef4;

    @Column(name = "CH_REFERENCES5", length = 50)
    private String characterRef5;

    @Column(name = "BUS_REFERENCES5", length = 50)
    private String busRef5;

    //--------------------------------------
    //---------- MISC -----------

    @Column(name = "HOBBIES", length = 50)
    private String hobbies;

    @Column(name = "LANGUAGE1", length = 50)
    private String language1;

    @Column(name = "SPEAK1", length = 50)
    private String speak1;

    @Column(name = "READ1", length = 50)
    private String read1;

    @Column(name = "WRITE1", length = 50)
    private String write1;

    @Column(name = "LANGUAGE2", length = 50)
    private String language2;

    @Column(name = "SPEAK2", length = 50)
    private String speak2;

    @Column(name = "READ2", length = 50)
    private String read2;

    @Column(name = "WRITE2", length = 50)
    private String write2;

    @Column(name = "LANGUAGE3", length = 50)
    private String language3;

    @Column(name = "SPEAK3", length = 50)
    private String speak3;

    @Column(name = "READ3", length = 50)
    private String read3;

    @Column(name = "WRITE3", length = 50)
    private String write3;

    @Column(name = "DETECTION_TEST")
    private Boolean detectionTest;

    @Column(name = "SIG_LINK", length = 50)
    private String signatureLink;

    @Column(name = "PIC_LINK", length = 50)
    private String pictureLink;

    //--------------------------------------

    @OneToMany(mappedBy = "employee", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<Address> addresses;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ContactInfo> contacts;

    @ManyToOne
    @JoinColumn(name = "erp_detachment_id")
    private ErpDetachment erpDetachment;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<ErpEmployeeID> erpEmployeeIDList;

    @OneToMany(mappedBy = "erpEmployee", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<EmploymentHistory> employmentHistory;

    public Long getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getExtname() {
        return extname;
    }

    public void setExtname(String extname) {
        this.extname = extname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Date getResignedDate() {
        return resignedDate;
    }

    public void setResignedDate(Date resignedDate) {
        this.resignedDate = resignedDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public EmploymentType getEmployment() {
        return employment;
    }

    public void setEmployment(EmploymentType employment) {
        this.employment = employment;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public CivilStatus getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(CivilStatus civilStatus) {
        this.civilStatus = civilStatus;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Double getWeightKilo() {
        return weightKilo;
    }

    public void setWeightKilo(Double weightKilo) {
        this.weightKilo = weightKilo;
    }

    public Double getWeightPound() {
        return weightPound;
    }

    public void setWeightPound(Double weightPound) {
        this.weightPound = weightPound;
    }

    public Double getHeightCentimeters() {
        return heightCentimeters;
    }

    public void setHeightCentimeters(Double heightCentimeters) {
        this.heightCentimeters = heightCentimeters;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<ContactInfo> getContacts() {
        return contacts;
    }

    public void setContacts(Set<ContactInfo> contacts) {
        this.contacts = contacts;
    }

    public ErpDetachment getErpDetachment() {
        return erpDetachment;
    }

    public void setErpDetachment(ErpDetachment erpDetachment) {
        this.erpDetachment = erpDetachment;
    }

    public OperationsArea getAssignedLocation() {
        return assignedLocation;
    }

    public void setAssignedLocation(OperationsArea assignedLocation) {
        this.assignedLocation = assignedLocation;
    }

    public Set<ErpEmployeeID> getErpEmployeeIDList() {
        return erpEmployeeIDList;
    }

    public void setErpEmployeeIDList(Set<ErpEmployeeID> erpEmployeeIDList) {
        this.erpEmployeeIDList = erpEmployeeIDList;
    }

    public Double getSalaryRate() {
        return salaryRate;
    }

    public void setSalaryRate(Double salaryRate) {
        this.salaryRate = salaryRate;
    }

    public Double getAllowance() {
        return allowance;
    }

    public void setAllowance(Double allowance) {
        this.allowance = allowance;
    }

    public SalaryType getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(SalaryType salaryType) {
        this.salaryType = salaryType;
    }

    public ContributionType getContributionType() {
        return contributionType;
    }

    public void setContributionType(ContributionType contributionType) {
        this.contributionType = contributionType;
    }

    public Double getSil() {
        return sil;
    }

    public void setSil(Double sil) {
        this.sil = sil;
    }

    public Boolean getRestDayPayEntitled() {
        return restDayPayEntitled;
    }

    public void setRestDayPayEntitled(Boolean restDayPayEntitled) {
        this.restDayPayEntitled = restDayPayEntitled;
    }

    public CompanyPosition getCompanyPosition() {
        return companyPosition;
    }

    public void setCompanyPosition(CompanyPosition companyPosition) {
        this.companyPosition = companyPosition;
    }

    public Set<EmploymentHistory> getEmploymentHistory() {
        return employmentHistory;
    }

    public void setEmploymentHistory(Set<EmploymentHistory> employmentHistory) {
        this.employmentHistory = employmentHistory;
    }

    public String getNameOfSpouse() {
        return nameOfSpouse;
    }

    public void setNameOfSpouse(String nameOfSpouse) {
        this.nameOfSpouse = nameOfSpouse;
    }

    public Date getMarriageDate() {
        return marriageDate;
    }

    public void setMarriageDate(Date marriageDate) {
        this.marriageDate = marriageDate;
    }

    public String getMarriagePlace() {
        return marriagePlace;
    }

    public void setMarriagePlace(String marriagePlace) {
        this.marriagePlace = marriagePlace;
    }

    public String getNameOfFather() {
        return nameOfFather;
    }

    public void setNameOfFather(String nameOfFather) {
        this.nameOfFather = nameOfFather;
    }

    public Date getFatherBirthDate() {
        return fatherBirthDate;
    }

    public void setFatherBirthDate(Date fatherBirthDate) {
        this.fatherBirthDate = fatherBirthDate;
    }

    public String getFatherBirthPlace() {
        return fatherBirthPlace;
    }

    public void setFatherBirthPlace(String fatherBirthPlace) {
        this.fatherBirthPlace = fatherBirthPlace;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getFatherEmployPlace() {
        return fatherEmployPlace;
    }

    public void setFatherEmployPlace(String fatherEmployPlace) {
        this.fatherEmployPlace = fatherEmployPlace;
    }

    public String getFatherCitizenship() {
        return fatherCitizenship;
    }

    public void setFatherCitizenship(String fatherCitizenship) {
        this.fatherCitizenship = fatherCitizenship;
    }

    public String getFatherContacts() {
        return fatherContacts;
    }

    public void setFatherContacts(String fatherContacts) {
        this.fatherContacts = fatherContacts;
    }

    public String getNameOfMother() {
        return nameOfMother;
    }

    public void setNameOfMother(String nameOfMother) {
        this.nameOfMother = nameOfMother;
    }

    public Date getMotherBirthDate() {
        return motherBirthDate;
    }

    public void setMotherBirthDate(Date motherBirthDate) {
        this.motherBirthDate = motherBirthDate;
    }

    public String getMotherBirthPlace() {
        return motherBirthPlace;
    }

    public void setMotherBirthPlace(String motherBirthPlace) {
        this.motherBirthPlace = motherBirthPlace;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public String getMotherEmployPlace() {
        return motherEmployPlace;
    }

    public void setMotherEmployPlace(String motherEmployPlace) {
        this.motherEmployPlace = motherEmployPlace;
    }

    public String getMotherCitizenship() {
        return motherCitizenship;
    }

    public void setMotherCitizenship(String motherCitizenship) {
        this.motherCitizenship = motherCitizenship;
    }

    public String getMotherContacts() {
        return motherContacts;
    }

    public void setMotherContacts(String motherContacts) {
        this.motherContacts = motherContacts;
    }

    public String getNameOfFatherInLaw() {
        return nameOfFatherInLaw;
    }

    public void setNameOfFatherInLaw(String nameOfFatherInLaw) {
        this.nameOfFatherInLaw = nameOfFatherInLaw;
    }

    public Date getFatherInLawBirthDate() {
        return fatherInLawBirthDate;
    }

    public void setFatherInLawBirthDate(Date fatherInLawBirthDate) {
        this.fatherInLawBirthDate = fatherInLawBirthDate;
    }

    public String getFatherInLawBirthPlace() {
        return fatherInLawBirthPlace;
    }

    public void setFatherInLawBirthPlace(String fatherInLawBirthPlace) {
        this.fatherInLawBirthPlace = fatherInLawBirthPlace;
    }

    public String getFatherInLawAddress() {
        return fatherInLawAddress;
    }

    public void setFatherInLawAddress(String fatherInLawAddress) {
        this.fatherInLawAddress = fatherInLawAddress;
    }

    public String getFatherInLawCitizenship() {
        return fatherInLawCitizenship;
    }

    public void setFatherInLawCitizenship(String fatherInLawCitizenship) {
        this.fatherInLawCitizenship = fatherInLawCitizenship;
    }

    public String getFatherInLawContacts() {
        return fatherInLawContacts;
    }

    public void setFatherInLawContacts(String fatherInLawContacts) {
        this.fatherInLawContacts = fatherInLawContacts;
    }

    public String getNameOfMotherInLaw() {
        return nameOfMotherInLaw;
    }

    public void setNameOfMotherInLaw(String nameOfMotherInLaw) {
        this.nameOfMotherInLaw = nameOfMotherInLaw;
    }

    public Date getMotherInLawBirthDate() {
        return motherInLawBirthDate;
    }

    public void setMotherInLawBirthDate(Date motherInLawBirthDate) {
        this.motherInLawBirthDate = motherInLawBirthDate;
    }

    public String getMotherInLawBirthPlace() {
        return motherInLawBirthPlace;
    }

    public void setMotherInLawBirthPlace(String motherInLawBirthPlace) {
        this.motherInLawBirthPlace = motherInLawBirthPlace;
    }

    public String getMotherInLawAddress() {
        return motherInLawAddress;
    }

    public void setMotherInLawAddress(String motherInLawAddress) {
        this.motherInLawAddress = motherInLawAddress;
    }

    public String getMotherInLawCitizenship() {
        return motherInLawCitizenship;
    }

    public void setMotherInLawCitizenship(String motherInLawCitizenship) {
        this.motherInLawCitizenship = motherInLawCitizenship;
    }

    public String getMotherInLawContacts() {
        return motherInLawContacts;
    }

    public void setMotherInLawContacts(String motherInLawContacts) {
        this.motherInLawContacts = motherInLawContacts;
    }

    public String getStepParentSister() {
        return stepParentSister;
    }

    public void setStepParentSister(String stepParentSister) {
        this.stepParentSister = stepParentSister;
    }

    public String getStepParentSisterAddress() {
        return stepParentSisterAddress;
    }

    public void setStepParentSisterAddress(String stepParentSisterAddress) {
        this.stepParentSisterAddress = stepParentSisterAddress;
    }

    public String getStepParentSisterCitizenship() {
        return stepParentSisterCitizenship;
    }

    public void setStepParentSisterCitizenship(String stepParentSisterCitizenship) {
        this.stepParentSisterCitizenship = stepParentSisterCitizenship;
    }

    public String getStepParentSisterContacts() {
        return stepParentSisterContacts;
    }

    public void setStepParentSisterContacts(String stepParentSisterContacts) {
        this.stepParentSisterContacts = stepParentSisterContacts;
    }

    public String getElementary() {
        return elementary;
    }

    public void setElementary(String elementary) {
        this.elementary = elementary;
    }

    public String getElementaryAddress() {
        return elementaryAddress;
    }

    public void setElementaryAddress(String elementaryAddress) {
        this.elementaryAddress = elementaryAddress;
    }

    public Date getElementaryAttended() {
        return elementaryAttended;
    }

    public void setElementaryAttended(Date elementaryAttended) {
        this.elementaryAttended = elementaryAttended;
    }

    public Date getElementaryGraduated() {
        return elementaryGraduated;
    }

    public void setElementaryGraduated(Date elementaryGraduated) {
        this.elementaryGraduated = elementaryGraduated;
    }

    public String getHighSchool() {
        return highSchool;
    }

    public void setHighSchool(String highSchool) {
        this.highSchool = highSchool;
    }

    public String getHighSchoolAddress() {
        return highSchoolAddress;
    }

    public void setHighSchoolAddress(String highSchoolAddress) {
        this.highSchoolAddress = highSchoolAddress;
    }

    public Date getHighSchoolAttended() {
        return highSchoolAttended;
    }

    public void setHighSchoolAttended(Date highSchoolAttended) {
        this.highSchoolAttended = highSchoolAttended;
    }

    public Date getHighSchoolGraduated() {
        return highSchoolGraduated;
    }

    public void setHighSchoolGraduated(Date highSchoolGraduated) {
        this.highSchoolGraduated = highSchoolGraduated;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCollegeAddress() {
        return collegeAddress;
    }

    public void setCollegeAddress(String collegeAddress) {
        this.collegeAddress = collegeAddress;
    }

    public Date getCollegeAttended() {
        return collegeAttended;
    }

    public void setCollegeAttended(Date collegeAttended) {
        this.collegeAttended = collegeAttended;
    }

    public Date getCollegeGraduated() {
        return collegeGraduated;
    }

    public void setCollegeGraduated(Date collegeGraduated) {
        this.collegeGraduated = collegeGraduated;
    }

    public String getOtherSchools() {
        return otherSchools;
    }

    public void setOtherSchools(String otherSchools) {
        this.otherSchools = otherSchools;
    }

    public String getOtherSchoolsAddress() {
        return otherSchoolsAddress;
    }

    public void setOtherSchoolsAddress(String otherSchoolsAddress) {
        this.otherSchoolsAddress = otherSchoolsAddress;
    }

    public Date getOtherSchoolsAttended() {
        return otherSchoolsAttended;
    }

    public void setOtherSchoolsAttended(Date otherSchoolsAttended) {
        this.otherSchoolsAttended = otherSchoolsAttended;
    }

    public Date getOtherSchoolsGraduated() {
        return otherSchoolsGraduated;
    }

    public void setOtherSchoolsGraduated(Date otherSchoolsGraduated) {
        this.otherSchoolsGraduated = otherSchoolsGraduated;
    }

    public String getSeminarQualifications() {
        return seminarQualifications;
    }

    public void setSeminarQualifications(String seminarQualifications) {
        this.seminarQualifications = seminarQualifications;
    }

    public String getSeminarQualificationsAddress() {
        return seminarQualificationsAddress;
    }

    public void setSeminarQualificationsAddress(String seminarQualificationsAddress) {
        this.seminarQualificationsAddress = seminarQualificationsAddress;
    }

    public Date getSeminarQualificationsAttended() {
        return seminarQualificationsAttended;
    }

    public void setSeminarQualificationsAttended(Date seminarQualificationsAttended) {
        this.seminarQualificationsAttended = seminarQualificationsAttended;
    }

    public Date getSeminarQualificationsGraduated() {
        return seminarQualificationsGraduated;
    }

    public void setSeminarQualificationsGraduated(Date seminarQualificationsGraduated) {
        this.seminarQualificationsGraduated = seminarQualificationsGraduated;
    }

    public String getCompanyAgency1() {
        return companyAgency1;
    }

    public void setCompanyAgency1(String companyAgency1) {
        this.companyAgency1 = companyAgency1;
    }

    public Date getCompany1Hired() {
        return company1Hired;
    }

    public void setCompany1Hired(Date company1Hired) {
        this.company1Hired = company1Hired;
    }

    public Date getCompany1Resigned() {
        return company1Resigned;
    }

    public void setCompany1Resigned(Date company1Resigned) {
        this.company1Resigned = company1Resigned;
    }

    public String getCompany1Address() {
        return company1Address;
    }

    public void setCompany1Address(String company1Address) {
        this.company1Address = company1Address;
    }

    public String getCompany1Reason() {
        return company1Reason;
    }

    public void setCompany1Reason(String company1Reason) {
        this.company1Reason = company1Reason;
    }

    public String getCompanyAgency2() {
        return companyAgency2;
    }

    public void setCompanyAgency2(String companyAgency2) {
        this.companyAgency2 = companyAgency2;
    }

    public Date getCompany2Hired() {
        return company2Hired;
    }

    public void setCompany2Hired(Date company2Hired) {
        this.company2Hired = company2Hired;
    }

    public Date getCompany2Resigned() {
        return company2Resigned;
    }

    public void setCompany2Resigned(Date company2Resigned) {
        this.company2Resigned = company2Resigned;
    }

    public String getCompany2Address() {
        return company2Address;
    }

    public void setCompany2Address(String company2Address) {
        this.company2Address = company2Address;
    }

    public String getCompany2Reason() {
        return company2Reason;
    }

    public void setCompany2Reason(String company2Reason) {
        this.company2Reason = company2Reason;
    }

    public String getCompanyAgency3() {
        return companyAgency3;
    }

    public void setCompanyAgency3(String companyAgency3) {
        this.companyAgency3 = companyAgency3;
    }

    public Date getCompany3Hired() {
        return company3Hired;
    }

    public void setCompany3Hired(Date company3Hired) {
        this.company3Hired = company3Hired;
    }

    public Date getCompany3Resigned() {
        return company3Resigned;
    }

    public void setCompany3Resigned(Date company3Resigned) {
        this.company3Resigned = company3Resigned;
    }

    public String getCompany3Address() {
        return company3Address;
    }

    public void setCompany3Address(String company3Address) {
        this.company3Address = company3Address;
    }

    public String getCompany3Reason() {
        return company3Reason;
    }

    public void setCompany3Reason(String company3Reason) {
        this.company3Reason = company3Reason;
    }

    public String getForceResignedReason() {
        return forceResignedReason;
    }

    public void setForceResignedReason(String forceResignedReason) {
        this.forceResignedReason = forceResignedReason;
    }

    public Boolean getForcedResigned() {
        return forcedResigned;
    }

    public void setForcedResigned(Boolean forcedResigned) {
        this.forcedResigned = forcedResigned;
    }

    public String getInvestigatedArrested() {
        return investigatedArrested;
    }

    public void setInvestigatedArrested(String investigatedArrested) {
        this.investigatedArrested = investigatedArrested;
    }

    public String getFamInvestigatedArrested() {
        return famInvestigatedArrested;
    }

    public void setFamInvestigatedArrested(String famInvestigatedArrested) {
        this.famInvestigatedArrested = famInvestigatedArrested;
    }

    public String getAdministrativeCase() {
        return administrativeCase;
    }

    public void setAdministrativeCase(String administrativeCase) {
        this.administrativeCase = administrativeCase;
    }

    public String getLiquorNarcotics() {
        return liquorNarcotics;
    }

    public void setLiquorNarcotics(String liquorNarcotics) {
        this.liquorNarcotics = liquorNarcotics;
    }

    public String getCharacterRef1() {
        return characterRef1;
    }

    public void setCharacterRef1(String characterRef1) {
        this.characterRef1 = characterRef1;
    }

    public String getBusRef1() {
        return busRef1;
    }

    public void setBusRef1(String busRef1) {
        this.busRef1 = busRef1;
    }

    public String getCharacterRef2() {
        return characterRef2;
    }

    public void setCharacterRef2(String characterRef2) {
        this.characterRef2 = characterRef2;
    }

    public String getBusRef2() {
        return busRef2;
    }

    public void setBusRef2(String busRef2) {
        this.busRef2 = busRef2;
    }

    public String getCharacterRef3() {
        return characterRef3;
    }

    public void setCharacterRef3(String characterRef3) {
        this.characterRef3 = characterRef3;
    }

    public String getBusRef3() {
        return busRef3;
    }

    public void setBusRef3(String busRef3) {
        this.busRef3 = busRef3;
    }

    public String getCharacterRef4() {
        return characterRef4;
    }

    public void setCharacterRef4(String characterRef4) {
        this.characterRef4 = characterRef4;
    }

    public String getBusRef4() {
        return busRef4;
    }

    public void setBusRef4(String busRef4) {
        this.busRef4 = busRef4;
    }

    public String getCharacterRef5() {
        return characterRef5;
    }

    public void setCharacterRef5(String characterRef5) {
        this.characterRef5 = characterRef5;
    }

    public String getBusRef5() {
        return busRef5;
    }

    public void setBusRef5(String busRef5) {
        this.busRef5 = busRef5;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getLanguage1() {
        return language1;
    }

    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    public String getSpeak1() {
        return speak1;
    }

    public void setSpeak1(String speak1) {
        this.speak1 = speak1;
    }

    public String getRead1() {
        return read1;
    }

    public void setRead1(String read1) {
        this.read1 = read1;
    }

    public String getWrite1() {
        return write1;
    }

    public void setWrite1(String write1) {
        this.write1 = write1;
    }

    public String getLanguage2() {
        return language2;
    }

    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    public String getSpeak2() {
        return speak2;
    }

    public void setSpeak2(String speak2) {
        this.speak2 = speak2;
    }

    public String getRead2() {
        return read2;
    }

    public void setRead2(String read2) {
        this.read2 = read2;
    }

    public String getWrite2() {
        return write2;
    }

    public void setWrite2(String write2) {
        this.write2 = write2;
    }

    public String getLanguage3() {
        return language3;
    }

    public void setLanguage3(String language3) {
        this.language3 = language3;
    }

    public String getSpeak3() {
        return speak3;
    }

    public void setSpeak3(String speak3) {
        this.speak3 = speak3;
    }

    public String getRead3() {
        return read3;
    }

    public void setRead3(String read3) {
        this.read3 = read3;
    }

    public String getWrite3() {
        return write3;
    }

    public void setWrite3(String write3) {
        this.write3 = write3;
    }

    public Boolean getDetectionTest() {
        return detectionTest;
    }

    public void setDetectionTest(Boolean detectionTest) {
        this.detectionTest = detectionTest;
    }

    public String getSignatureLink() {
        return signatureLink;
    }

    public void setSignatureLink(String signatureLink) {
        this.signatureLink = signatureLink;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getChildrenName1() {
        return childrenName1;
    }

    public void setChildrenName1(String childrenName1) {
        this.childrenName1 = childrenName1;
    }

    public Date getChildrenBirthday1() {
        return childrenBirthday1;
    }

    public void setChildrenBirthday1(Date childrenBirthday1) {
        this.childrenBirthday1 = childrenBirthday1;
    }

    public String getChildrenAddress1() {
        return childrenAddress1;
    }

    public void setChildrenAddress1(String childrenAddress1) {
        this.childrenAddress1 = childrenAddress1;
    }

    public String getChildrenName2() {
        return childrenName2;
    }

    public void setChildrenName2(String childrenName2) {
        this.childrenName2 = childrenName2;
    }

    public Date getChildrenBirthday2() {
        return childrenBirthday2;
    }

    public void setChildrenBirthday2(Date childrenBirthday2) {
        this.childrenBirthday2 = childrenBirthday2;
    }

    public String getChildrenAddress2() {
        return childrenAddress2;
    }

    public void setChildrenAddress2(String childrenAddress2) {
        this.childrenAddress2 = childrenAddress2;
    }

    public String getChildrenName3() {
        return childrenName3;
    }

    public void setChildrenName3(String childrenName3) {
        this.childrenName3 = childrenName3;
    }

    public Date getChildrenBirthday3() {
        return childrenBirthday3;
    }

    public void setChildrenBirthday3(Date childrenBirthday3) {
        this.childrenBirthday3 = childrenBirthday3;
    }

    public String getChildrenAddress3() {
        return childrenAddress3;
    }

    public void setChildrenAddress3(String childrenAddress3) {
        this.childrenAddress3 = childrenAddress3;
    }

    public String getChildrenName4() {
        return childrenName4;
    }

    public void setChildrenName4(String childrenName4) {
        this.childrenName4 = childrenName4;
    }

    public Date getChildrenBirthday4() {
        return childrenBirthday4;
    }

    public void setChildrenBirthday4(Date childrenBirthday4) {
        this.childrenBirthday4 = childrenBirthday4;
    }

    public String getChildrenAddress4() {
        return childrenAddress4;
    }

    public void setChildrenAddress4(String childrenAddress4) {
        this.childrenAddress4 = childrenAddress4;
    }

    public String getChildrenName5() {
        return childrenName5;
    }

    public void setChildrenName5(String childrenName5) {
        this.childrenName5 = childrenName5;
    }

    public Date getChildrenBirthday5() {
        return childrenBirthday5;
    }

    public void setChildrenBirthday5(Date childrenBirthday5) {
        this.childrenBirthday5 = childrenBirthday5;
    }

    public String getChildrenAddress5() {
        return childrenAddress5;
    }

    public void setChildrenAddress5(String childrenAddress5) {
        this.childrenAddress5 = childrenAddress5;
    }
}
