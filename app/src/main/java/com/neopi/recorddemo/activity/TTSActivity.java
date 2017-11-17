package com.neopi.recorddemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.neopi.recorddemo.R;
import com.neopi.recorddemo.api.BaseResult;
import com.neopi.recorddemo.api.DeviceApi;
import com.neopi.recorddemo.audio.AudioFileUtils;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;

public class TTSActivity extends AppCompatActivity {


    private EditText editText ;
    private Button btn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_layout);

        editText = findViewById(R.id.edit_text) ;
        btn = findViewById(R.id.tts_btn) ;

        btn.setOnClickListener(view -> {
            String text = editText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                return;
            }

            toTTS(text);
        });
    }

    private void toTTS(String text) {

        btn.setEnabled(false);
        DeviceApi.ttsTest(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BaseResult>() {
                    @Override
                    public void onNext(BaseResult baseResult) {
                        btn.setEnabled(true);
                        Log.e("111",baseResult.toString()) ;
                        if (baseResult.code == 0 && baseResult.data instanceof Map) {
                            String data = (String) baseResult.data;
                            if (!TextUtils.isEmpty(data)) {
                                AudioFileUtils.playMedia(TTSActivity.this,data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        btn.setEnabled(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
