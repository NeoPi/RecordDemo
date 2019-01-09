package com.neopi.recorddemo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.model.MenuInfo;

import java.util.ArrayList;

/**
 * Author    :  NeoPi
 * Date      :  2019/01/09
 * Describe  :
 */
public class MenuAdapter extends BaseAdapter {

    private ArrayList<MenuInfo> menuInfos = new ArrayList<>();
    private Context mContext;

    public MenuAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return menuInfos.size();
    }

    @Override
    public MenuInfo getItem(int position) {
        return menuInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_trans_model_layout, null, false);
            holder = new Holder();
            holder.radioButton = convertView.findViewById(R.id.menuRadio);
            holder.tvTitle = convertView.findViewById(R.id.menuTitle);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        MenuInfo item = getItem(position);
        holder.radioButton.setChecked(item.isSelect);
        holder.tvTitle.setText(item.title);
        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public void updateMenus(ArrayList<MenuInfo> infos) {
        menuInfos.clear();
        menuInfos.addAll(infos);
    }

    public void updateItem(MenuInfo info, int position) {
        menuInfos.set(position, info);
        notifyDataSetChanged();
    }

    public void addMenu(MenuInfo info) {
        menuInfos.add(info);
        notifyDataSetChanged();
    }

    public void selectItem(int position) {
        for (int i = 0; i < getCount(); i++) {
            MenuInfo item = getItem(i);
            item.isSelect = i == position;
        }
    }

    static class Holder {
        AppCompatTextView tvTitle;
        RadioButton radioButton;
    }
}
