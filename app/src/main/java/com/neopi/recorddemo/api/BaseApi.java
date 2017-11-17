package com.neopi.recorddemo.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Author    :  NeoPi
 * Date      :  17-11-17
 * Describe  :
 */

public interface BaseApi {

    @GET
    Observable<Response<ResponseBody>> get(@Url String url,
                                           @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST
    Observable<Response<ResponseBody>> post(@Url String url,
                                                  @FieldMap Map<String, String> params);
    @POST
    Observable<Response<ResponseBody>> upload(@Url String url, @Body RequestBody requestBody);
}
