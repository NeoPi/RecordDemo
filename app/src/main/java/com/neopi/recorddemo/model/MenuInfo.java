package com.neopi.recorddemo.model;

/**
 * Author    :  NeoPi
 * Date      :  2019/01/09
 * Describe  :
 */
public class MenuInfo {

    public String title ;
    public boolean isSelect = false;

    public MenuInfo() {
    }

    public MenuInfo(String title, boolean isSelect) {
        this.title = title;
        this.isSelect = isSelect;
    }

    @Override
    public String toString() {
        return "MenuInfo{" +
                "title='" + title + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }
}
