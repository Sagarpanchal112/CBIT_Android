package com.tfb.cbit.models.accounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("account_no")
    @Expose
    private String accountNo;
    @SerializedName("ifsc_code")
    @Expose
    private String ifscCode;
    @SerializedName("verify_bank")
    @Expose
    private int verifyBank;
    @SerializedName("bank_id")
    @Expose
    private String bankId;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public int getVerifyBank() {
        return verifyBank;
    }

    public void setVerifyBank(int verifyBank) {
        this.verifyBank = verifyBank;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
