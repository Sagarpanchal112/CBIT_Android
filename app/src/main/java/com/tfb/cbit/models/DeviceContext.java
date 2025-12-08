package com.tfb.cbit.models;

import java.io.Serializable;

public class DeviceContext implements Serializable {
   public String deviceOs;

   public String getDeviceOs() {
      return deviceOs;
   }

   public void setDeviceOs(String deviceOs) {
      this.deviceOs = deviceOs;
   }
}
