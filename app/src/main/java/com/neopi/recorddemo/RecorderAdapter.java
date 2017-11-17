package com.neopi.recorddemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public static class RecorderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView title ;
        TextView desc ; // 230K   1017/11/16
        SimpleDateFormat sdf ;

        public RecorderViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title );
            desc = itemView.findViewById(R.id.item_desc) ;

            sdf = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bindData(File file) {
            if (file != null) {
                title.setText(file.getName());
                desc.setText(AudioFileUtils.getSize(file.length())+"     " + sdf.format(new Date(file.lastModified())));
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0,R.id.translate_to_word,0,"转换成文字");
            menu.add(0,R.id.play,0,"播放");
        }
    }
}
