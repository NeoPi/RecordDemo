package com.neopi.recorddemo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.en_us.speechmodels;
import com.microsoft.msrmt.quicksandlibrary.SimpleTranslator;
import com.microsoft.msrmt.quicksandlibrary.TranslatorConfig;
import com.microsoft.msttsengine.MsttsException;
import com.microsoft.msttsengine.SpeechSynthesizer;
import com.microsoft.msttsengine.SpeechSynthesizerConfig;
import com.neopi.recorddemo.model.EventModel;
import com.neopi.recorddemo.model.HistoryInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Future;

import static com.microsoft.msttsengine.MsttsException.TTSERR_UNIT_NAME_NOT_FOUND;

/**
 * Author    :  NeoPi
 * Date      :  2019/01/09
 * Describe  :
 */
public class OfflineManager {

    private static final String TAG = "OfflineManager";
    private static OfflineManager instance = null;
    private String modelPath = Environment.getExternalStorageDirectory().toString() + "/models/";
    private String EN_CH = "zh-HANS";
    private String EN_US = "en-US";
    private String ZH_CN = "zh-CN";
    private String EN_TO_CN = "EN-CH";
    private String CN_TO_EN = "CH-EN";

    /**
     * 语音合成
     */
    private SpeechSynthesizer synthesizer = null;
    /**
     * 翻译引擎
     */
    private SimpleTranslator translator = null;
    /**
     * 语音识别
     */
    private SpeechRecognizer recognizer = null;

    // 语音识别参数
    private SpeechConfig speechRecognizerConfig = null;
    // 翻译参数
    private TranslatorConfig translatorConfig = null;
    // 语音合成参数
    private SpeechSynthesizerConfig speechSynthesizerConfig = null;

    /**
     * 翻译引擎默认翻译语言
     */
    private String selectSpeechLanguage = CN_TO_EN;
    private String speechRecognizerLanguage = null;
    private String speechSynthesizerLanguage = null;
    private String speechTranslatorLanguage = null;
    // 语音翻译引擎
    private String speechTranslatorEngine = null;

    private OfflineManager(Context context) {
        setTranslationConfiguration(CN_TO_EN);
        speechmodels.deployLanguageModels(context.getAssets(), EN_US);
        speechmodels.deployLanguageModels(context.getAssets(), ZH_CN);
        speechmodels.deployLanguageModels(context.getAssets(), EN_CH);
        createSpeechObjects();
    }

    public static OfflineManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OfflineManager.class) {
                if (instance == null) {
                    instance = new OfflineManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 设置识别 语音合成 与翻译的语言
     *
     * @param direction
     */
    private void setTranslationConfiguration(String direction) {
        if (direction.equalsIgnoreCase(CN_TO_EN)) {
            speechRecognizerLanguage = ZH_CN;
            speechSynthesizerLanguage = EN_US;
            speechTranslatorLanguage = EN_CH;
            speechTranslatorEngine = "chs.enu.quicksand_mobile";
        } else if (direction.equalsIgnoreCase(EN_TO_CN)) {
            speechRecognizerLanguage = EN_US;
            speechSynthesizerLanguage = ZH_CN;
            speechTranslatorLanguage = EN_CH;
            speechTranslatorEngine = "enu.chs.quicksand_mobile";
        }
    }

    /**
     * 开始进行语音识别 ，Mic收声
     */
    public void startRecognizer() {
        try {
            final Future<Void> task = recognizer.startContinuousRecognitionAsync();
//            setOnTaskCompletedListener(task, result -> {
//
//            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 停止识别
     */
    public void stopRecognizer() {
        if (recognizer != null) {
            final Future<Void> task = recognizer.stopContinuousRecognitionAsync();
//            setOnTaskCompletedListener(task, result -> {
//
//            });
        }
    }

    private void createSpeechObjects() {
        try {
            speechRecognizerConfig = SpeechConfig.fromLocalModels(GetModelPathSR(), speechRecognizerLanguage);
            translatorConfig = TranslatorConfig.fromLocalModels(GetModelPathMT(), speechTranslatorEngine);
            speechSynthesizerConfig = SpeechSynthesizerConfig.fromLocalModels(GetModelPathTTS(), speechSynthesizerLanguage);
            recognizer = new SpeechRecognizer(speechRecognizerConfig);
            recognizer.recognizing.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(TAG, "Intermediate result received: " + s);
            });
            recognizer.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(TAG, "Final result received: " + s);
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                translator.startTranslationAsync(s);
                String translatedTextContent = translator.getTranslationResult();
                synthesizer.StartSpeechSynthesisPlaybackSync(translatedTextContent);

                HistoryInfo info = new HistoryInfo();
                info.fromText = s;
                info.toText = translatedTextContent ;
                info.offline = true ;
                EventBus.getDefault().post(new EventModel.OffLineMessageEvent(info));
            });

            translator = new SimpleTranslator(translatorConfig);
            try {
                synthesizer = new SpeechSynthesizer(speechSynthesizerConfig);
            } catch (MsttsException e) {
                if (e.getErrorCode() == TTSERR_UNIT_NAME_NOT_FOUND) {
                    ShowSynthesizerVoiceAlert();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Log.i("Exception", ex.toString());
        }
    }

    /**
     *
     */
    private void changeTranslationEngine() {
        if (selectSpeechLanguage.equalsIgnoreCase(EN_TO_CN)) {
            selectSpeechLanguage = CN_TO_EN;
        } else if (selectSpeechLanguage.equalsIgnoreCase(CN_TO_EN)) {
            selectSpeechLanguage = EN_TO_CN;
        }
        setTranslationConfiguration(selectSpeechLanguage);
        createSpeechObjects();
    }

    private void ShowSynthesizerVoiceAlert() {
//        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
//        dlgAlert.setMessage("Please copy the voice font to /sdcard/tts folder");
//        dlgAlert.setTitle("No voice is installed");
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
    }

    private String GetModelPathSR() {
        return modelPath + "SR/" /*+ speechRecognizerLanguage + "/"*/;
    }

    private String GetModelPathMT() {
        return modelPath + "MT/" + speechTranslatorLanguage; // + "/";
    }

    private String GetModelPathTTS() {
        return modelPath + "TTS" /*+ speechSynthesizerLanguage + "/"*/;
    }

}
