package com.neopi.recorddemo.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.model.HistoryInfo;

public class HistoryAdapter extends BaseRecyclerAdapter<HistoryInfo,HistoryHolder> {


    public HistoryAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryHolder(parent, R.layout.item_history_layout);
    }


}
