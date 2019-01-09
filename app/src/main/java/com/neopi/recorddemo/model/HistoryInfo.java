package com.neopi.recorddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HistoryInfo {

    @SerializedName("url") @Expose
    public String url = "" ;
    @SerializedName("from_text") @Expose
    public String fromText = "" ;
    @SerializedName("to_text") @Expose
    public String toText = "";

    public boolean offline = false ;


    @Override
    public String toString() {
        return "HistoryInfo{" +
                "url='" + url + '\'' +
                ", fromText='" + fromText + '\'' +
                ", toText='" + toText + '\'' +
                ", offline='" + offline + '\'' +
                '}';
    }
}
