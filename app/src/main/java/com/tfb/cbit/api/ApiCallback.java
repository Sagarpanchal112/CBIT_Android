package com.tfb.cbit.api;


public interface ApiCallback {
      void success(String responseData);
      void failure(String responseData);
}
