package com.neopi.recorddemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AudioActivity extends AppCompatActivity {


    private AudioRecorder.Status status ;
    private AudioRecorder audioRecorder ;

    private Button btnStart ;
    private Button btnStop ;
    private RxPermissions rxPermissions ;
    private SimpleDateFormat sdf ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        btnStart = findViewById(R.id.start);
        btnStop = findViewById(R.id.stop);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
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
                        String fileName = sdf.format(new Date(System.currentTimeMillis()));
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


        getMenuInflater().inflate(R.menu.home_more_menu,menu);
        return true;
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.wav_lis :
                toCheckList();
                break;
        }
        return true ;
    }

    private void toCheckList() {
        Intent intent = new Intent(this,RecorderListActivity.class) ;
        startActivity(intent);
    }
}
