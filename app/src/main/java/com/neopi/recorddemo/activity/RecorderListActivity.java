package com.neopi.recorddemo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.adapter.ImpContextMenuRecyclerView;
import com.neopi.recorddemo.adapter.RecorderAdapter;
import com.neopi.recorddemo.api.BaseResult;
import com.neopi.recorddemo.api.DeviceApi;
import com.neopi.recorddemo.audio.AudioFileUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.ResourceObserver;

public class RecorderListActivity extends AppCompatActivity {


    private ImpContextMenuRecyclerView recyclerView ;
    private RecorderAdapter adapter ;
    private RxPermissions rxPermissions ;

    private CompositeDisposable disposable ;

    private ProgressDialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder_list_layout);


        adapter = new RecorderAdapter(this);
        recyclerView = findViewById(R.id.recycler_view) ;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setOnCreateContextMenuListener(this);
        rxPermissions = new RxPermissions(this) ;
        readFileList();

        disposable = new CompositeDisposable() ;
        dialog = new ProgressDialog(this) ;
        dialog.setMessage("转换中,请稍等");
    }

    private void readFileList() {

        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {
                        List<File> wavFiles = AudioFileUtils.getWavFiles();
                        adapter.updateData(wavFiles);
                    } else {
                        Toast.makeText(this,"请先开启读取文件权限",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,R.id.translate_to_word,0,"转换成文字");
        menu.add(0,R.id.play,0,"播放");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.translate_to_word:
                toAsrTest(menuInfo.position);
                break;
            case R.id.play:
                playFile(menuInfo.position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void playFile(int position) {
        File file = adapter.getItem(position);
        if (file == null) {
            return ;
        }
        AudioFileUtils.playMedia(getApplicationContext(),file);
    }

    private void toAsrTest(int position) {
        File file = adapter.getItem(position);
        if (file == null) {
            return ;
        }

        ResourceObserver<BaseResult> asrObserver = new ResourceObserver<BaseResult>() {
            @Override
            public void onNext(BaseResult baseResult) {
                Log.e("111","asr:" +baseResult.toString()) ;
                dialog.dismiss();
                Intent intent = new Intent(RecorderListActivity.this,TextActivity.class) ;
                if (baseResult.code == 0) {
                    intent.putExtra("extra",(String) baseResult.data);
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
        } ;
        dialog.show();
        DeviceApi.upload(file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(asrObserver);

        disposable.add(asrObserver) ;
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
