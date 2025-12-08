package com.tfb.cbit.models.ticketjson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlexiTicket {

    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("bracketSize")
    @Expose
    private String bracketSize;

    public FlexiTicket(String amount,String bracketSize){
        this.amount = amount;
        this.bracketSize = bracketSize;
    }

    public String getAmount() {
        return amount;
    }

    public String getBracketSize() {
        return bracketSize;
    }

}
