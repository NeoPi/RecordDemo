package com.neopi.recorddemo.api;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;

import static com.neopi.recorddemo.api.ApiManager.HOST;
import static com.neopi.recorddemo.api.ApiManager.baseApi;
import static com.neopi.recorddemo.api.ApiManager.getComposer;

/**
 * Author    :  NeoPi
 * Date      :  17-11-17
 * Describe  :
 */

public class DeviceApi {

    public static Observable<BaseResult> ttsTest (String text) {
        String path = "device/ttsTest" ;

        Map<String, String> params = new TreeMap<>();
        params.put("text",text);
        params.put("language","zh");
        return baseApi.get(HOST+path,params)
                .compose(getComposer())
                .map(new mFunction(new TypeToken<BaseResult>() {
                }, false));
    }


    public static Observable<BaseResult> upload(File file) {
        String path = "device/asrTest";
        return ApiManager.upload(HOST + path, file);
    }
}
