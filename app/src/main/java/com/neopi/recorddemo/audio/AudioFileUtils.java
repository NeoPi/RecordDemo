package com.neopi.recorddemo.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author    :  NeoPi
 * Date      :  17-11-16
 * Describe  :
 */

public class AudioFileUtils {

    private static String rootPath = "bananaTech";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASEPATH = "/" + rootPath +"/aimoyu/pcm/";
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASEPATH = "/" + rootPath + "/wav/";

    private static void setRootPath(String rootPath) {
        AudioFileUtils.rootPath = rootPath;
    }

    public static String getPcmFileAbsolutePath(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }
        String mAudioRawPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".pcm")) {
                fileName = fileName + ".pcm";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioRawPath = fileBasePath + fileName;
        }

        return mAudioRawPath;
    }

    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }

        String mAudioWavPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".wav")) {
                fileName = fileName + ".wav";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                file.mkdirs();
            }
            mAudioWavPath = fileBasePath + fileName;
        }
        return mAudioWavPath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取全部pcm文件列表
     *
     * @return
     */
    public static List<File> getPcmFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {

            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }
        }
        return list;
    }

    /**
     * 获取全部wav文件列表
     *
     * @return
     */
    public static List<File> getWavFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }
        }
        return list;
    }


    public static String getSize(long size) {

        String label;

        DecimalFormat df = new DecimalFormat("#.00");
        if (size < 1024) {
            label = size + "B";
        } else if (size >= 1024 && size < 1024 * 1024) {
            label = df.format(size * 1f / 1024) + "K";
        } else {
            label = df.format(size * 1f / 1024 / 1024) + "M";
        }

        return label;
    }

    private static MediaPlayer mediaPlayer;

    public static void playMedia(Context context, File file) {
        if (file == null || !file.isFile()) {
            return;
        }
        String absolutePath = file.getAbsolutePath();
        playMedia(context, absolutePath);
    }

    public static void playMedia(Context context, String url) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    return;
                }
                mediaPlayer.reset();
            }

            Uri mediaUri = Uri.parse(url);
            mediaPlayer = MediaPlayer.create(context, mediaUri);
            mediaPlayer.start();


            mediaPlayer = new MediaPlayer();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}