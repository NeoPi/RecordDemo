package com.neopi.recorddemo.api;

import com.google.gson.reflect.TypeToken;
import com.neopi.recorddemo.model.HistoryInfo;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
        return baseApi.post(HOST+path,params)
                .compose(getComposer())
                .map(new mFunction(new TypeToken<BaseResult>() {
                }, false));
    }


    public static Observable<BaseResult> upload(File file) {
        String path = "device/asrTest";
        return ApiManager.upload(HOST + path, file);
    }

    /**
     * 上传文件并返回翻译结果
     *
     * @param file
     * @return
     */
    public static Observable<BaseResult> uploadFile (File file) {
        String path = "outer/translate/test" ;
        // file body
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file) ;

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM) ;

        builder.addFormDataPart("file", file.getName(), requestBody) ;
        builder.addFormDataPart("device_id","gh_81b9e4cf156a_26c47a7378e94aa2") ;
        builder.addFormDataPart("from","zh") ;
        builder.addFormDataPart("to","en") ;


        return baseApi.upload(HOST+path, builder.build())
                .compose(getComposer())
                .map(new mFunction( new TypeToken<BaseResult<HistoryInfo>>() {

        })) ;
    }


}
