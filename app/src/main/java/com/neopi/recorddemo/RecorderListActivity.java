package com.neopi.recorddemo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class RecorderListActivity extends AppCompatActivity {


    private RecyclerView recyclerView ;
    private RecorderAdapter adapter ;
    private RxPermissions rxPermissions ;

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
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.translate_to_word:
                Toast.makeText(this,"开发中",Toast.LENGTH_SHORT).show();
                break;
            case R.id.play:

                break;
        }
        return super.onContextItemSelected(item);
    }

}
