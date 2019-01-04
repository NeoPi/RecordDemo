package com.neopi.recorddemo.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.adapter.HistoryAdapter;
import com.neopi.recorddemo.api.BaseResult;
import com.neopi.recorddemo.api.DeviceApi;
import com.neopi.recorddemo.audio.AudioFileUtils;
import com.neopi.recorddemo.audio.AudioRecorder;
import com.neopi.recorddemo.model.HistoryInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.ResourceObserver;

public class TranslateActivity extends AppCompatActivity {



    private HistoryAdapter mAdapter = null ;
    private RecyclerView mHistoryList ;
    private Button mRecordView = null;
    private AppCompatTextView tvLanguage = null ;
    private Drawable swapDrawable ;

    private ArrayList<HistoryInfo> mDatas = new ArrayList<HistoryInfo>() ;

    private AudioRecorder audioRecorder ;
    private RxPermissions rxPermissions ;
    private AudioRecorder.Status status ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_layout);

        audioRecorder = AudioRecorder.getInstance() ;
        initView();
    }

    private void initView() {
        mHistoryList = findViewById(R.id.historyList) ;
        mAdapter = new HistoryAdapter(this) ;
        mHistoryList.setAdapter(mAdapter);
        mHistoryList.setLayoutManager(new LinearLayoutManager(this));
        tvLanguage = findViewById(R.id.historyLanguage) ;
        initLanguageVie() ;


        rxPermissions = new RxPermissions(this) ;
        audioRecorder = AudioRecorder.getInstance() ;
        mRecordView = findViewById(R.id.historyRecord) ;
        mRecordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRecordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecord();
                        mRecordView.setText("松手即可翻译");
                        mRecordView.setBackgroundColor(ContextCompat.getColor(TranslateActivity.this,R.color.colorPrimary));
                        break;
                    case MotionEvent.ACTION_UP:
                        mRecordView.setText("长按录音");
                        mRecordView.setBackgroundColor(ContextCompat.getColor(TranslateActivity.this,R.color.charcoalGrey));
                        status = AudioRecorder.Status.STATUS_STOP;
                        audioRecorder.stopRecord();
                        startTranslate();
                        break;
                }
                return true;
            }
        });
    }

    private void initLanguageVie() {
        swapDrawable = getResources().getDrawable(R.drawable.ic_swap_horiz) ;
        swapDrawable.setBounds(0,0,50,60);
        SpannableString ss = new SpannableString("n") ;
        ss.setSpan(new ImageSpan(swapDrawable),0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder ssb = new SpannableStringBuilder() ;
        ssb.append("中文");
        ssb.append(ss);
        ssb.append("英文") ;
        tvLanguage.setText(ssb);
    }

    private void startRecord() {
        rxPermissions.request(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {
                        status = AudioRecorder.Status.STATUS_START;
                        String fileName = "output";
                        audioRecorder.createDefaultAudio(fileName);
                        audioRecorder.startRecord((data, begin, end) -> Log.e("111",begin+"......."+end));
                    } else {
                        Toast.makeText(TranslateActivity.this,"请开启录音和读写文件的权限",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startTranslate() {
        File file = new File(AudioFileUtils.getPcmFileAbsolutePath("output.pcm")) ;
        DeviceApi.uploadFile(file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResourceObserver<BaseResult>() {
                    @Override
                    public void onNext(BaseResult baseResult) {
                        if (baseResult.code == 0 && baseResult.data instanceof HistoryInfo) {
                            HistoryInfo data = (HistoryInfo) baseResult.data;
                            mAdapter.appendData(data);
                            Log.e("111",data.toString()) ;
                            AudioFileUtils.playMedia(TranslateActivity.this,data.url);
                        } else {
                            if (baseResult.data instanceof String) {
                                AudioFileUtils.playMedia(TranslateActivity.this,(String) baseResult.data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
