package com.neopi.recorddemo.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.neopi.recorddemo.Constants;
import com.neopi.recorddemo.R;
import com.neopi.recorddemo.adapter.HistoryAdapter;
import com.neopi.recorddemo.adapter.MenuAdapter;
import com.neopi.recorddemo.api.BaseResult;
import com.neopi.recorddemo.api.DeviceApi;
import com.neopi.recorddemo.audio.AudioFileUtils;
import com.neopi.recorddemo.audio.AudioRecorder;
import com.neopi.recorddemo.model.EventModel;
import com.neopi.recorddemo.model.HistoryInfo;
import com.neopi.recorddemo.model.MenuInfo;
import com.neopi.recorddemo.utils.OfflineManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;

public class TranslateActivity extends AppCompatActivity {



    private HistoryAdapter mAdapter = null ;
    private RecyclerView mHistoryList ;
    private Button mRecordView = null;
    private AppCompatTextView tvLanguage = null ;
    private Drawable swapDrawable ;
    private ListPopupWindow popupWindow ;

    private ArrayList<HistoryInfo> mDatas = new ArrayList<HistoryInfo>() ;

    private AudioRecorder audioRecorder ;
    private RxPermissions rxPermissions ;
    private AudioRecorder.Status status ;

    private boolean modelOffline = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_layout);

        audioRecorder = AudioRecorder.getInstance() ;
        initView();

        requestPermission() ;
    }

    private void requestPermission() {
        Disposable permission = rxPermissions.request(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        finish();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
                Log.e("111",event.getAction()+"  :touch") ;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRecordView.setText("松手即可翻译");
                        mRecordView.setBackgroundColor(ContextCompat.getColor(TranslateActivity.this,R.color.colorPrimary));
                        if (modelOffline) {
                            OfflineManager.getInstance(TranslateActivity.this).startRecognizer();
                        } else {
                            startRecord();
                        }
                        break;
                    case  MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mRecordView.setText("长按录音");
                        mRecordView.setBackgroundColor(ContextCompat.getColor(TranslateActivity.this,R.color.charcoalGrey));
                        if (modelOffline) {
                            OfflineManager.getInstance(TranslateActivity.this).stopRecognizer();
                        } else {
                            status = AudioRecorder.Status.STATUS_STOP;
                            audioRecorder.stopRecord();
                            startTranslate();
                        }
                        break;
                }
                return true;
            }
        });

        findViewById(R.id.historyMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TranslateActivity.this,RecorderListActivity.class) ;
                intent.putExtra(Constants.EXTRA_FILE_DIR,Environment.getExternalStorageDirectory()+"/com.actions.voicebletest/pcm/");
//                intent.putExtra(Constants.EXTRA_FILE_DIR,Environment.getExternalStorageDirectory()+"/audiorecord/pcm/");
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
            }
        });

        View mMenuView = findViewById(R.id.historyMore);
        mMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreMenu (v) ;
            }
        });

        popupWindow = new ListPopupWindow(this);
        popupWindow.setModal(true);
        popupWindow.setWidth(540);
        popupWindow.setHeight(-2);
        MenuAdapter menuAdapter = new MenuAdapter(this) ;
        popupWindow.setAdapter(menuAdapter);
        menuAdapter.addMenu(new MenuInfo("离线翻译模式",true));
        menuAdapter.addMenu(new MenuInfo("在线翻译模式",false));
        popupWindow.setAnchorView(mMenuView);

        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuInfo item = menuAdapter.getItem(position);
                if (item.isSelect) {
                    return;
                }
                menuAdapter.selectItem(position);
                popupWindow.dismiss();
                modelOffline = position == 0 ;
            }
        });

    }

    /**
     * 显示更多菜单选项
     *
     */
    private void showMoreMenu(View view) {
        popupWindow.show();
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

    long startTime ;
    private void startTranslate() {
        File file = new File(AudioFileUtils.getPcmFileAbsolutePath("output.pcm")) ;
        DeviceApi.uploadFile(file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResourceObserver<BaseResult>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        startTime = System.currentTimeMillis() ;
                    }

                    @Override
                    public void onNext(BaseResult baseResult) {
                        if (baseResult.code == 0 && baseResult.data instanceof HistoryInfo) {
                            HistoryInfo data = (HistoryInfo) baseResult.data;
                            mAdapter.appendData(data);
                            mHistoryList.scrollToPosition(mAdapter.getItemCount() - 1);
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
                        long l = System.currentTimeMillis();
                        Log.e("111","start - end:" + (l-startTime) ) ;
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfflineEvent (EventModel.OffLineMessageEvent event) {
        if (event != null && event.info != null) {
            mAdapter.appendData(event.info);
            mHistoryList.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
