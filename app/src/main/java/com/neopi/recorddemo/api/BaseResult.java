package com.neopi.recorddemo.api;

/**
 * 基础返回数据结构
 * Created by wq on 16/2/25.
 */
public class BaseResult<Data> {
  public int code = -1;
  public String info;
  public Data data;

  @Override
  public String toString() {
    return "BaseResult{" +
        "code=" + code +
        ", msg='" + info + '\'' +
        ", data=" + data +
        '}';
  }
}
