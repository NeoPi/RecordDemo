package com.neopi.recorddemo.api;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Author    :  NeoPi
 * Date      :  17-11-17
 * Describe  :
 */

public class ApiManager {

    static BaseApi baseApi;
    static Gson gson = new Gson();
    final static String HOST = "http://hardware.t.samjoy.com/";

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//    String userId = PreferenceUtil.getStringPref(BananaApp.getInstance(), Constants.Preference.KEY_USER_ID, null);
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
//                        .addInterceptor(new Interceptor() {
//                            @Override
//                            public okhttp3.Response intercept(Chain chain) throws IOException {
//                                String token = null;
////            String token = "MTAwMDIzNDVjMjA0MmFmNjM5ZWEzZTAzNThmMTczZDQxMDY1OGIxNDk4NTMwODI2" ;
//                                String cookie = token == null ? "" : "serviceToken=" + token;
//                                Request request = chain.request()
//                                        .newBuilder()
//                                        .addHeader("User-Agent", "Android_Tourist_")
//                                        .addHeader("cookie", cookie)
//                                        .build();
//                                return chain.proceed(request);
//                            }
//                        })
                        .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HOST)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        baseApi = retrofit.create(BaseApi.class);
    }


    static Observable<BaseResult> upload(String url, File file) {
        //MultipartBody.Builder builder = new MultipartBody.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

//        HashMap<String, String> params = new HashMap<>();
        builder.addFormDataPart("file", file.getName(), requestBody);

//        String content = null;
//        builder.addPart(
//                RequestBody.create(MediaType.parse("applicaiton/otcet-stream"), content));

        return baseApi.upload(url, builder.build())
                .compose(getComposer())
                .map(new mFunction(new TypeToken<BaseResult>() {
                }));
    }

    @NonNull
    static ObservableTransformer<Response<ResponseBody>, ResponseBody> getComposer() {
        return upstream -> upstream.subscribeOn(Schedulers.newThread())
                .map((Function<Response, Object>) Response::body)
                .cast(ResponseBody.class);
    }
}
