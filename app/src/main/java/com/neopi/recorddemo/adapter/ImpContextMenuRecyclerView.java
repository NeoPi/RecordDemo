package com.neopi.recorddemo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;

/**
 * Author    :  NeoPi
 * Date      :  17-11-17
 * Describe  :
 */

public class ImpContextMenuRecyclerView extends RecyclerView {

    private AdapterView.AdapterContextMenuInfo contextMenuInfo;

    public ImpContextMenuRecyclerView(Context context) {
        super(context);
    }

    public ImpContextMenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImpContextMenuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean showContextMenuForChild(View originalView) {
        int position = getChildAdapterPosition(originalView);
        long longId = getChildItemId(originalView);
        contextMenuInfo = new AdapterView.AdapterContextMenuInfo(originalView, position, longId);
        return super.showContextMenuForChild(originalView);
    }


    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        if (contextMenuInfo != null) {
            return contextMenuInfo;
        }
        return super.getContextMenuInfo() ;
    }
}
