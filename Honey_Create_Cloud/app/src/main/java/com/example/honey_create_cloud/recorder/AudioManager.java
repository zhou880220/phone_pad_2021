package com.example.honey_create_cloud.recorder;

import android.app.Activity;
import android.media.MediaRecorder;
import android.util.Log;

import com.pocketdigi.utils.FLameUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Created by Administrator on 2017/11/28.
 * <p>
 * 录音管理类
 */

public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    //录制成MP3格式..............................................
    /**构造时候需要的Activity，主要用于获取文件夹的路径*/
    private Activity activity;
    /**采样频率*/
    private static final int SAMPLE_RATE = 11025;
    /**文件路径*/
    private String fileName = null;
    private String rawPath = null;
    private String mp3Path = null;
    /**是否转换ok*/
    private boolean convertOk = false;

    private static AudioManager mInstance;

    private boolean isPrepared;


    public AudioManager(String dir) {
        mDir = dir;
    }

    ;

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }


    /**
     * 准备
     */
    public void prepareAudio() {
        Log.e(TAG, "startAudio: ");

        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData(){
        Log.e(TAG, "mCurrentFilePath: " +mCurrentFilePath);
        Log.e(TAG, "mp3Path: " +mCurrentFilePath.replace(".amr",".mp3"));
        FLameUtils lameUtils = new FLameUtils(1, SAMPLE_RATE, 96);
        convertOk = lameUtils.raw2mp3(mCurrentFilePath, mp3Path);
//        if (convertOk) {//删除源文件
//            if(mCurrentFilePath != null) {
//                File file = new File(mCurrentFilePath);
//                file.delete();
//                mCurrentFilePath = null;
//            }
//        }

    }



    //    生成UUID唯一标示符
//    算法的核心思想是结合机器的网卡、当地时间、一个随即数来生成GUID
//    .amr音频文件
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            //获得最大的振幅getMaxAmplitude() 1-32767
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    public void release() {
        try {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }catch (Exception e){

        }
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}

