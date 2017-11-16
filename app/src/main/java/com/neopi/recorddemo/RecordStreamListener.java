package com.neopi.recorddemo;

/**
 * Author    :  NeoPi
 * Date      :  17-11-16
 * Describe  :  获取录音的音频流,用于拓展的处理
 */

public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);
}
