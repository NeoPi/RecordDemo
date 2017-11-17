package com.neopi.recorddemo.api;

import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by wq on 2017/2/15.
 */

public class mFunction<Data> implements Function<ResponseBody, BaseResult<Data>> {
    private TypeToken type;
    private boolean gzip;
    private int BUFFERSIZE = 1024;

    public mFunction(TypeToken type) {
        this.type = type;
    }

    public mFunction(TypeToken type, boolean gzip) {
        this.gzip = gzip;
        this.type = type;
    }

    @Override
    public BaseResult apply(ResponseBody responseBody) throws Exception {
        String result = "";
        BaseResult baseResult = null;
        try {
            if (gzip) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = responseBody.byteStream();
                GZIPInputStream gzipInputStream = new GZIPInputStream(in);
                byte[] buffer = new byte[BUFFERSIZE];
                int n = 0;
                while ((n = gzipInputStream.read(buffer, 0, buffer.length)) > 0) {
                    out.write(buffer, 0, n);
                }
                gzipInputStream.close();
                in.close();
                out.close();
                result = out.toString("utf-8");
            } else {
                result = responseBody.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }
        try {
            baseResult = ApiManager.gson.fromJson(result, type.getType());
        } catch (Exception e) {
            e.printStackTrace();
            baseResult = ApiManager.gson.fromJson(result, new TypeToken<BaseResult>() {
            }.getType());
        }
        return baseResult;
    }
}
