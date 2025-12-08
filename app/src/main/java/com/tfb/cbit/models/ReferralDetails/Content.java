package com.tfb.cbit.models.ReferralDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.private_contest_detail.User;

import java.util.List;

public class Content {

    @SerializedName("ReferralAmount")
    @Expose
    private String ReferralAmount;

    @SerializedName("ReferralList")
    @Expose
    private List<UserDetails> ReferralList = null;

    @SerializedName("ReferralTotal")
    @Expose
    private String ReferralTotal;

    public String getReferralAmount() {
        return ReferralAmount;
    }

    public void setReferralAmount(String referralAmount) {
        ReferralAmount = referralAmount;
    }

    public List<UserDetails> getReferralList() {
        return ReferralList;
    }

    public void setReferralList(List<UserDetails> referralList) {
        ReferralList = referralList;
    }

    public String getReferralTotal() {
        return ReferralTotal;
    }

    public void setReferralTotal(String referralTotal) {
        ReferralTotal = referralTotal;
    }
}
