package com.neopi.recorddemo.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.neopi.recorddemo.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

public class SeekbarDialog extends DialogFragment {


    public static final String EXTRA_MEDIA_URL = "extra_media_url_a" ;
    private SeekBar mSeekBar ;
    private View rootView ;
    private String mUrl ;
    private MediaPlayer mediaPlayer ;
    private CompositeDisposable mDisposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rootView = inflater.inflate(R.layout.dialog_seekbar_layout,container,false) ;
        mSeekBar = rootView.findViewById(R.id.seekBar) ;


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


        getDialog().getWindow().setLayout(-1,200);
        getDialog().getWindow().setGravity(Gravity.CENTER);
//        getDialog().getWindow().setWindowAnimations(R.style.ActionAlphaDialogAnimation);

        mDisposable = new CompositeDisposable() ;
        Bundle arguments = getArguments();
        if (arguments != null) {
            String string = arguments.getString(EXTRA_MEDIA_URL);
            if (TextUtils.isEmpty(string)) {
                dismissAllowingStateLoss();
            } else {
                playMedia(string);
            }
        }
    }

    private void playMedia(String url) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    return;
                }
                mediaPlayer.reset();
            }


            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mp.getDuration() ;
                int second = duration /1000 ;
                int offset = duration % 1000 ;
                if (offset > 500 || second == 0) {
                    second ++ ;
                }
                Log.e("111","max seek: "+ duration) ;
                mSeekBar.setMax(second);
                startSeekBarProgress(0);
            });
            mediaPlayer.start();



        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    private void startSeekBarProgress(int progress) {

        ResourceObserver<Long> resourceObserver = new ResourceObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                int position = mediaPlayer.getCurrentPosition();
                int second = position /1000 ;
                int offset = position % 1000 ;
                if (offset > 0) {
                    second ++ ;
                }

                Log.e("111",second+"    sds ssssss     "+position) ;
                mSeekBar.setProgress(second);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };

        Observable.intervalRange(0,mSeekBar.getMax(),1,1,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resourceObserver) ;

        mDisposable.add(resourceObserver) ;
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
        mDisposable.dispose();
    }
}
