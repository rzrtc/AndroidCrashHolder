package com.rz.crashreport;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkhttpUtils {
    private static OkhttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    public static final String USER_AGENT = "User-Agent";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String SDK_VERSION = "AndroidCrashReportSdkVersion/";
    private static final String SEPARATE = ";";
    public static final String LOG_PATH = "https://data-center-dev.duobeiyun.com/tosee/v1/crash";



    private OkhttpUtils() {
        mOkHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//允许失败重试
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static OkhttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkhttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkhttpUtils();
                }
            }
        }
        return mInstance;
    }

    public boolean postSynJson(String url, String json) {
        boolean success = false;
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .removeHeader(USER_AGENT).addHeader(USER_AGENT, getUserAgent())
                .url(url).post(body).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            success = response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean  postLog(String url, String logStr) {
        boolean success = false;
        final String key = "result";
        FormBody body = new FormBody.Builder().add(key, logStr).build();
        final Request request = new Request.Builder().removeHeader(USER_AGENT)
                .removeHeader(USER_AGENT).addHeader(USER_AGENT, getUserAgent())
                .url(url).post(body).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            success = response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private String getUserAgent() {
        StringBuilder builder = new StringBuilder();
        builder.append(SDK_VERSION).append(BuildConfig.VERSION_NAME);
        return builder.toString();
    }

}
