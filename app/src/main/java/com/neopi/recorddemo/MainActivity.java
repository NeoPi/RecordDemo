package com.neopi.recorddemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.to_audio).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AudioActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.to_media).setOnClickListener(view -> {

        });

    }
}
