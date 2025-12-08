package com.tfb.cbit.event;

public class FbRegister {
    private String socialid;
    private String socialtype;
    private String email;
    private String fname;

    public FbRegister(String socialid, String socialtype, String email, String fname) {
        this.socialid = socialid;
        this.socialtype = socialtype;
        this.email = email;
        this.fname = fname;
    }

    public String getSocialid() {
        return socialid;
    }

    public String getSocialtype() {
        return socialtype;
    }

    public String getEmail() {
        return email;
    }

    public String getFname() {
        return fname;
    }
}
