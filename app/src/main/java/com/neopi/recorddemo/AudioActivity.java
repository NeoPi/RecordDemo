package com.neopi.recorddemo;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;

public class AudioActivity extends AppCompatActivity {


    private AudioRecorder.Status status ;
    private AudioRecorder audioRecorder ;

    private Button btnStart ;
    private Button btnStop ;
    private RxPermissions rxPermissions ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        btnStart = findViewById(R.id.start);
        btnStop = findViewById(R.id.stop);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        rxPermissions = new RxPermissions(this) ;
        audioRecorder = AudioRecorder.getInstance() ;
        btnStart.setOnClickListener(view -> rxPermissions.request(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {
                        status = AudioRecorder.Status.STATUS_START;
                        btnStart.setEnabled(false);
                        btnStop.setEnabled(true);
                        String fileName = "temp";
                        audioRecorder.createDefaultAudio(fileName);
                        audioRecorder.startRecord((data, begin, end) -> Log.e("111",begin+"......."+end));
                    } else {
                        Toast.makeText(AudioActivity.this,"请开启录音和读写文件的权限",Toast.LENGTH_SHORT).show();
                    }
                }));

        btnStop.setOnClickListener(view -> {
            status = AudioRecorder.Status.STATUS_STOP;
            audioRecorder.stopRecord();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

}
