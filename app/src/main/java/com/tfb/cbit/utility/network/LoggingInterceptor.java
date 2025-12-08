package com.tfb.cbit.utility.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoggingInterceptor implements Interceptor {

	private static final HttpLoggingInterceptor.Level LOG_LEVEL_DEBUG = HttpLoggingInterceptor.Level.BODY;
	private static final HttpLoggingInterceptor.Level LOG_LEVEL_RELEASE = HttpLoggingInterceptor.Level.NONE;

	private final HttpLoggingInterceptor httpLoggingInterceptor;

	public LoggingInterceptor(boolean debugEnabled) {
		httpLoggingInterceptor = new HttpLoggingInterceptor();
		httpLoggingInterceptor.setLevel(debugEnabled ? LOG_LEVEL_DEBUG : LOG_LEVEL_RELEASE);
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		return httpLoggingInterceptor.intercept(chain);
	}

}