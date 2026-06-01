package com.example.retrofit.basic.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 单例提供 Retrofit 实例，统一 baseUrl 与 Gson 转换器。
 * 最佳实践：应用内复用同一实例，便于配置超时、拦截器等。
 */
public final class RetrofitClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static volatile Retrofit sRetrofit;

    private RetrofitClient() {
    }

    public static Retrofit getInstance() {
        if (sRetrofit == null) {
            synchronized (RetrofitClient.class) {
                if (sRetrofit == null) {
                    Gson gson = new GsonBuilder().setLenient().create();
                    sRetrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return sRetrofit;
    }

    public static JsonPlaceholderService getJsonPlaceholderService() {
        return getInstance().create(JsonPlaceholderService.class);
    }
}
