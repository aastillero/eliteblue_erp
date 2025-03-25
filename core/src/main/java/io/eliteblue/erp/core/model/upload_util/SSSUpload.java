package io.eliteblue.erp.core.model.upload_util;

import io.eliteblue.erp.core.model.ErpEmployee;

import java.util.Date;

public class SSSUpload {

    private String bgColor;

    private String fontColor;

    private String employeeName;

    private String sheetName;

    private String post;

    private String remarks;

    private String firstName;

    private String lastName;

    private Double loanAmount;

    private Double amortization;

    private Double balance;

    private Double totalDeducted;

    private Date loanDate;

    private Boolean employeeMatched;

    private Boolean selectedRecord;

    private Boolean existingLoan;

    private ErpEmployee erpEmployee;

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getAmortization() {
        return amortization;
    }

    public void setAmortization(Double amortization) {
        this.amortization = amortization;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getTotalDeducted() {
        return totalDeducted;
    }

    public void setTotalDeducted(Double totalDeducted) {
        this.totalDeducted = totalDeducted;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ErpEmployee getErpEmployee() {
        return erpEmployee;
    }

    public void setErpEmployee(ErpEmployee erpEmployee) {
        this.erpEmployee = erpEmployee;
    }

    public Boolean getEmployeeMatched() {
        return employeeMatched;
    }

    public void setEmployeeMatched(Boolean employeeMatched) {
        this.employeeMatched = employeeMatched;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Boolean getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(Boolean selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Boolean getExistingLoan() {
        return existingLoan;
    }

    public void setExistingLoan(Boolean existingLoan) {
        this.existingLoan = existingLoan;
    }
}
