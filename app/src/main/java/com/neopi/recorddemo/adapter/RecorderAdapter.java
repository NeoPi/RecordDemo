package com.neopi.recorddemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.audio.AudioFileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author    :  NeoPi
 * Date      :  2017/11/16
 * Describe  :
 */

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.RecorderViewHolder> {


    private Context context ;
    private ArrayList<File> mDatas ;

    public RecorderAdapter(Context context) {
        this.context = context;
        mDatas = new ArrayList<>();
    }

    public void updateData(List<File> files){
        if (files == null) {
            files = new ArrayList<>() ;
        }

        mDatas.clear();
        mDatas.addAll(files) ;
        notifyDataSetChanged();
    }
    @Override
    public RecorderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recorder_item,null) ;

        RecorderViewHolder holder = new RecorderViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecorderViewHolder holder, int position) {
        if (mDatas.size() == 0 || mDatas.size() <= position || position < 0) {
            return;
        }

        File file = mDatas.get(position);
        holder.bindData(file);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public File getItem(int position) {
        if (mDatas.size() == 0 || mDatas.size() <= position || position < 0) {
            return null;
        }
        return mDatas.get(position);
    }

    public static class RecorderViewHolder extends RecyclerView.ViewHolder {

        TextView title ;
        TextView desc ; // 230K   1017/11/16
        SimpleDateFormat sdf ;

        public RecorderViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title );
            desc = itemView.findViewById(R.id.item_desc) ;
            itemView.setLongClickable(true);
            sdf = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
//            itemView.setOnCreateContextMenuListener(this);
        }

        public void bindData(File file) {
            if (file != null) {
                title.setText(file.getName());
                desc.setText(AudioFileUtils.getSize(file.length())+"     " + sdf.format(new Date(file.lastModified())));
            }
        }

    }
}
