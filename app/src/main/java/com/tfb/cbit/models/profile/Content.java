package com.tfb.cbit.models.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Content {
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("profile_image")
    @Expose
    private String profile_image;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("middelName")
    @Expose
    private String middelName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("pan_number")
    @Expose
    private String panNumber;
    @SerializedName("verify_pan")
    @Expose
    private int verify_pan;
    @SerializedName("bank_account")
    @Expose
    private List<BankAccount> bankAccount = null;
    @SerializedName("verify_email")
    @Expose
    private int verify_email;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public List<BankAccount> getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(List<BankAccount> bankAccount) {
        this.bankAccount = bankAccount;
    }

    public int getVerify_pan() {
        return verify_pan;
    }

    public int getVerify_email() {
        return verify_email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddelName() {
        return middelName;
    }

    public String getLastName() {
        return lastName;
    }
}
