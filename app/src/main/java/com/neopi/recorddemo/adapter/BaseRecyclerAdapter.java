package com.neopi.recorddemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.*;

/**
 * Author    :  NeoPi
 * Date      :  2017/11/28
 * Describe  :
 */
abstract class BaseRecyclerAdapter<D,H extends BaseViewHolder<D>>
    extends RecyclerView.Adapter<H> {


    protected ArrayList<D> mDatas = null;
    private OnItemClickListener<D> mOnItemClickListener = null;

    public BaseRecyclerAdapter (Context context) {
        mDatas = new ArrayList<D>();
    }


    @Override
    public void onBindViewHolder(H holder, int position) {
        D data = getItem(position);
        if (data != null) {
            holder.onBindData(data,position);
            holder.itemView.setOnClickListener(new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(data,position) ;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() ;
    }

    /**
     * 获取position对应的数据
     *
     */
    public D getItem(int position) {
        D data = null ;
        if (position >= 0 && position < mDatas.size()) {
            data = mDatas.get(position) ;
        }
        return data ;
    }

    /**
     * 更新item
     *
     */
    public void updateItem(int position,D data) {
        if (position >=0 && position < mDatas.size()) {
            return ;
        }

        mDatas.set(position,data);
        notifyDataSetChanged() ;
    }

    /**
     * 追加数据
     *
     */
    public void appendData (ArrayList<D> infos) {
        if (infos == null || infos.size() == 0) {
            return ;
        }
        mDatas.addAll(infos) ;
        notifyDataSetChanged() ;
    }

    /**
     * 追加一项
     */
    public void appendData (D info) {
        if (info == null) {
            return ;
        }
        mDatas.add(info) ;
        notifyDataSetChanged() ;
    }


    /**
     * 删除某一项
     */
    boolean removeData (int position) {
        if (position >= 0 && position < mDatas.size()) {
            mDatas.remove(position) ;
            notifyDataSetChanged() ;
            return true ;
        }

        return false ;
    }

    /**
     * 刷新数据
     *
     */
    public void updateData (ArrayList<D> infos) {
        if (infos == null && infos.size()> 0) {
            return ;
        }

        mDatas.clear() ;
        mDatas.addAll(infos) ;
        notifyDataSetChanged() ;
    }

    public void setOnItemClickListener (OnItemClickListener mListener) {
        this.mOnItemClickListener = mListener ;
    }

    public interface OnItemClickListener<D> {
        void onItemClick(D mData, int position) ;
    }
}