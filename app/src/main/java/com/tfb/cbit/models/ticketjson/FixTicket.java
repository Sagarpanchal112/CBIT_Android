package com.tfb.cbit.models.ticketjson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FixTicket {
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("slots")
    @Expose
    private List<Slots> slots;

    public FixTicket(String amount, List<Slots> slots){
        this.amount = amount;
        this.slots = slots;
    }

    public String getAmount() {
        return amount;
    }

    public List<Slots> getSlots() {
        return slots;
    }
}
