package com.subra.taskman.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserModel {

    @SerializedName("UserID")
    @Expose
    private String userId;
    @SerializedName("LoginName")
    @Expose
    private String loginName;
    @SerializedName("EID")
    @Expose
    private String eId;
    @SerializedName("User_Level")
    @Expose
    private String userLevel;
    @SerializedName("UserFullName")
    @Expose
    private String userFullName;
    @SerializedName("BranchID")
    @Expose
    private String branchId;
    @SerializedName("OCode")
    @Expose
    private String oCode;
    @SerializedName("Company_Code")
    @Expose
    private String companyCode;
    @SerializedName("empPhoto")
    @Expose
    private String empPhoto;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("joiningDate")
    @Expose
    private String joiningDate;
    @SerializedName("subCompanyCount")
    @Expose
    private int subCompanyCount;
    @SerializedName("permissionList")
    @Expose
    private ArrayList<String> permissionList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String geteId() {
        return eId;
    }

    public void seteId(String eId) {
        this.eId = eId;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getoCode() {
        return oCode;
    }

    public void setoCode(String oCode) {
        this.oCode = oCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getEmpPhoto() {
        return empPhoto;
    }

    public void setEmpPhoto(String empPhoto) {
        this.empPhoto = empPhoto;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public int getSubCompanyCount() {
        return subCompanyCount;
    }

    public void setSubCompanyCount(int subCompanyCount) {
        this.subCompanyCount = subCompanyCount;
    }

    public ArrayList<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(ArrayList<String> permissionList) {
        this.permissionList = permissionList;
    }
}