package com.neopi.recorddemo.activity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.neopi.recorddemo.R;

public class StreamTransActivity extends AppCompatActivity {


    boolean isRecording = false;
    static final int frequency = 16000;
    static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int recBufSize, playBufSize;
    AudioRecord audioRecord;
    AudioTrack audioTrack;
    Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_trans_layout);


        recordButton = findViewById(R.id.btStart);

        //用getMinBufferSize()方法得到采集数据所需要的最小缓冲区的大小
        recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        playBufSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        //实例化AudioRecord(声音来源，采样率，声道设置，采样声音编码，缓存大小）
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);
//        recordButton.setOnClickListener(new ClickEvent());

        // 设置声音大小
//        audioTrack.setStereoVolume(0.7f, 0.7f);

        recordButton.setOnTouchListener((View v, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTrans();
                    recordButton.setBackgroundResource(R.color.colorPrimary);
                    Log.e("111","action down");
                    break;
                case MotionEvent.ACTION_UP:
                    isRecording = false ;
                    recordButton.setBackgroundResource(R.color.colorPrimaryDark);
                    Log.e("111","action up");
                    break;
            }
            return true;
        });
    }

    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == recordButton) {
                isRecording = true;
                new RecordPlayThread().start();
            }
        }
    }

    class RecordPlayThread extends Thread {
        public void run() {
            try {
                //byte 文件来存储声音
                byte[] buffer = new byte[recBufSize];
                //开始采集声音
                audioRecord.startRecording();
                //播放声音
                audioTrack.play();
                while (isRecording) {
                    //从MIC存储到缓存区
                    int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);
                    byte[] tmpBuf = new byte[bufferReadResult];
                    System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
                    //播放缓存区的数据
                    audioTrack.write(tmpBuf, 0, tmpBuf.length);
                }
                audioTrack.stop();
                audioRecord.stop();
            } catch (Throwable t) {
                Toast.makeText(StreamTransActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 开始传输数据
     */
    private void startTrans() {
        isRecording = true ;
        new RecordPlayThread().start();
    }
}
