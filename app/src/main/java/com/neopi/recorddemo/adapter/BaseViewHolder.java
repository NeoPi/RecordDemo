package com.neopi.recorddemo.adapter ;

import android.content.Context ;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView ;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Author    :  NeoPi
 * Date      :  2017/11/28
 * Describe  :
 */
abstract class BaseViewHolder<D> extends RecyclerView.ViewHolder {

    protected Context mContext = null ;

    public BaseViewHolder (ViewGroup parent, @LayoutRes int layResId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layResId, parent, false));
    }


    abstract void onBindData(D data,int position);
}