package com.tfb.cbit.utility.network;


import com.tfb.cbit.utility.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Network {

	private static OkHttpClient okHttpClient;

	public static OkHttpClient getOkHttpClient() {
		if (okHttpClient == null) {
			okHttpClient = new OkHttpClient.Builder()
					.connectTimeout(Utils.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
					.readTimeout(Utils.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
					.writeTimeout(Utils.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
					.addInterceptor(new UserAgentInterceptor(Utils.USER_AGENT))
					.build();
		}
		return okHttpClient;
	}

}