package com.tfb.cbit.models.JticketFilter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tfb.cbit.models.MyJTicket.Contest;

import java.util.List;

public class Content {
    @SerializedName("Names")
    @Expose
    private List<Names> names = null;

    public List<Names> getNames() {
        return names;
    }

    public void setNames(List<Names> names) {
        this.names = names;
    }
}
