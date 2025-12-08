package com.tfb.cbit.models;

import java.io.Serializable;

public class PaymentInstrument  implements Serializable {
    public String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetApp() {
        return targetApp;
    }

    public void setTargetApp(String targetApp) {
        this.targetApp = targetApp;
    }

    public String targetApp;

}
