package com.neopi.recorddemo.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.model.HistoryInfo;

public class HistoryHolder extends BaseViewHolder<HistoryInfo> {

    private AppCompatTextView textFrom ;
    private AppCompatTextView textTo ;
    private View mContainer ;
    private AppCompatTextView textLanguage ;
    private Drawable arrowDrawable ;

    public HistoryHolder(ViewGroup parent,@LayoutRes int layoutId) {
        super(parent, layoutId);
        textFrom = itemView.findViewById(R.id.itemFrom);
        textTo = itemView.findViewById(R.id.itemTo);
        mContainer = itemView.findViewById(R.id.itemContainer);

        textLanguage = itemView.findViewById(R.id.itemLanguage);
        arrowDrawable = itemView.getResources().getDrawable(R.drawable.ic_arrow_forward) ;
        arrowDrawable.setBounds(0,0,30,30);
        SpannableString ss = new SpannableString("中#en") ;
        ss.setSpan(new ImageSpan(arrowDrawable),1,2,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textLanguage.setText(ss) ;
    }

    @Override
    public void onBindData(HistoryInfo data, int position) {
        textFrom.setText(data.fromText);
        textTo.setText(data.toText);
        if (data.offline) {
            mContainer.setBackgroundColor(Color.parseColor("#4A90E2"));
            textTo.setTextColor(Color.WHITE);
            textFrom.setTextColor(Color.WHITE);
        } else {
            mContainer.setBackgroundColor(Color.WHITE);
            textTo.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.charcoalGrey));
            textFrom.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.brownishGrey));
        }
    }
}
