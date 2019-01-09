package com.neopi.recorddemo.model;

/**
 * Author    :  NeoPi
 * Date      :  2019/01/09
 * Describe  :
 */
public class EventModel {

    public static class OffLineMessageEvent {
        public HistoryInfo info ;

        public OffLineMessageEvent(HistoryInfo mInfo) {
            info = mInfo ;
        }
    }
}
