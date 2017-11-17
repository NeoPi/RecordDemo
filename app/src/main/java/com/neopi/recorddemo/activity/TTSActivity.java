package com.neopi.recorddemo.activity;

import android.app.ProgressDialog;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.ResourceObserver;

public class TTSActivity extends AppCompatActivity {


    private EditText editText ;
    private Button btn ;
    private ProgressDialog progressDialog ;
    private CompositeDisposable disposable ;
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

        disposable = new CompositeDisposable() ;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍等");
    }

    private void toTTS(String text) {

        ResourceObserver<BaseResult> ttsObserver = new ResourceObserver<BaseResult>() {
            @Override
            public void onNext(BaseResult baseResult) {
                Log.e("111",baseResult.toString()) ;
                if (baseResult.code == 0 && baseResult.data instanceof String) {
                    String data = (String) baseResult.data;
                    if (!TextUtils.isEmpty(data)) {
                        AudioFileUtils.playMedia(TTSActivity.this,data);
                    }
                }
                btn.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                progressDialog.dismiss();
                btn.setEnabled(true);
            }

            @Override
            public void onComplete() {

            }
        } ;
        btn.setEnabled(false);
        progressDialog.show();
        DeviceApi.ttsTest(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ttsObserver);
        disposable.add(ttsObserver) ;
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
