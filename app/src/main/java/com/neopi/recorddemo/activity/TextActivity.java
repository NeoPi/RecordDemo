package com.neopi.recorddemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.neopi.recorddemo.R;

public class TextActivity extends AppCompatActivity {


    private TextView textView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_layout);

        textView = findViewById(R.id.tts_text) ;

        String extra = getIntent().getStringExtra("extra");
        textView.setText(extra);
    }
}
