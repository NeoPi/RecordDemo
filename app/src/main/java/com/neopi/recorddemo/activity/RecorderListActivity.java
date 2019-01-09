package com.neopi.recorddemo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.neopi.recorddemo.Constants;
import com.neopi.recorddemo.R;
import com.neopi.recorddemo.adapter.ImpContextMenuRecyclerView;
import com.neopi.recorddemo.adapter.RecorderAdapter;
import com.neopi.recorddemo.api.BaseResult;
import com.neopi.recorddemo.api.DeviceApi;
import com.neopi.recorddemo.audio.AudioFileUtils;
import com.neopi.recorddemo.dialog.SeekbarDialog;
import com.neopi.recorddemo.model.HistoryInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.ResourceObserver;

public class RecorderListActivity extends AppCompatActivity {


    private ImpContextMenuRecyclerView recyclerView;
    private RecorderAdapter adapter;
    private RxPermissions rxPermissions;

    private CompositeDisposable disposable;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder_list_layout);


        adapter = new RecorderAdapter(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setOnCreateContextMenuListener(this);
        rxPermissions = new RxPermissions(this);

        readFileList();

        disposable = new CompositeDisposable();
        dialog = new ProgressDialog(this);
        dialog.setMessage("转换中,请稍等");
    }

    private void readFileList() {

        Disposable disposable = rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = getIntent();
                        String stringExtra = intent.getStringExtra(Constants.EXTRA_FILE_DIR);
                        Log.e("111", stringExtra);
                        List<File> wavFiles = new ArrayList<>();
                        if (TextUtils.isEmpty(stringExtra)) {
                            wavFiles.addAll(AudioFileUtils.getWavFiles());
                        } else {
                            File file = new File(stringExtra);
                            if (file.exists() && file.isDirectory()) {
                                File[] files = file.listFiles();
                                wavFiles.addAll(Arrays.asList(files));
                            } else {
                                wavFiles.addAll(AudioFileUtils.getWavFiles());
                            }
                        }
                        adapter.updateData(wavFiles);
                    } else {
                        Toast.makeText(this, "请先开启读取文件权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        menu.add(0, R.id.asr, 0, "转换成文字");
        menu.add(0, R.id.translate_to_word, 0, "翻译");
        menu.add(0, R.id.play, 0, "播放");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.translate_to_word:
                startTranslate(menuInfo.position);
                break;
            case R.id.play:
                playFile(menuInfo.position);
                break;
            case R.id.asr:
                toAsrTest(menuInfo.position);
                break;

        }
        return super.onContextItemSelected(item);
    }

    /**
     * @param position
     */
    private void playFile(int position) {
        File file = adapter.getItem(position);
        if (file == null) {
            return;
        }
        String name = file.getName();
        if (name.endsWith(".pcm")) {
            AudioFileUtils.playPcm(getApplicationContext(), file);
        } else {
            AudioFileUtils.playMedia(getApplicationContext(), file);
        }
    }

    private void toAsrTest(int position) {
        File file = adapter.getItem(position);
        if (file == null) {
            return;
        }

        ResourceObserver<BaseResult> asrObserver = new ResourceObserver<BaseResult>() {
            @Override
            public void onNext(BaseResult baseResult) {
                Log.e("111", "asr:" + baseResult.toString());
                dialog.dismiss();
                Intent intent = new Intent(RecorderListActivity.this, TextActivity.class);
                if (baseResult.code == 0) {
                    intent.putExtra("extra", (String) baseResult.data);
                } else {
                    intent.putExtra("extra", baseResult.info);
                }
                startActivity(intent);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dialog.dismiss();
            }

            @Override
            public void onComplete() {

            }
        };
        dialog.show();
        DeviceApi.upload(file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(asrObserver);

        disposable.add(asrObserver);
    }


    long startTime = 0;
    /**
     * 翻译
     *
     * @param position
     */
    private void startTranslate(int position) {
        File file = adapter.getItem(position);
        if (file == null) {
            return;
        }

        ResourceObserver<BaseResult> observer = new ResourceObserver<BaseResult>() {
            @Override
            protected void onStart() {
                super.onStart();
                startTime = System.currentTimeMillis() ;
            }

            @Override
            public void onNext(BaseResult baseResult) {
                dialog.dismiss();
                if (baseResult.code == 0 && baseResult.data instanceof HistoryInfo) {
                    HistoryInfo data = (HistoryInfo) baseResult.data;
                    Log.e("111", data.toString());
                    showSeekDialog(data.url);
//                    AudioFileUtils.playMedia(RecorderListActivity.this, data.url);
                } else {
                    if (baseResult.data instanceof String) {
                        AudioFileUtils.playMedia(RecorderListActivity.this, (String) baseResult.data);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dialog.dismiss();
            }

            @Override
            public void onComplete() {
                long l = System.currentTimeMillis();
                Log.e("111","start - end:" + (l-startTime) ) ;
            }
        };
        dialog.show();
        DeviceApi.uploadFile(file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        disposable.add(observer);
    }

    /**
     * 显示 播放进度的dialog
     *
     * @param data
     */
    private void showSeekDialog(String data) {
        SeekbarDialog seekbarDialog = new SeekbarDialog();
        Bundle bundle = new Bundle() ;
        bundle.putString(SeekbarDialog.EXTRA_MEDIA_URL,data);
        seekbarDialog.setArguments(bundle);
        seekbarDialog.show(getSupportFragmentManager(),"SeebarDialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (disposable != null) {
            disposable.clear();
            disposable.dispose();
        }
    }
}
