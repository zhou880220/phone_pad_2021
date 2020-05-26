package com.example.honey_create_cloud.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.audio.ProgressTextUtils;
import com.example.honey_create_cloud.audio.RecordManager;

import java.io.File;



public class SoundTextView extends AppCompatTextView {

    private Context mContext;

    private Dialog recordIndicator;

    private TextView mVoiceTime;

//    private MediaRecorder recorder;
//    private File myRecAudioFile;

    //依次为开始录音时刻，按下录音时刻，松开录音按钮时刻
    private long startTime, time1, time2;

    // private  Conversation conversation=null;

    // private  String mp3Path="";//转码后的地址
    // String fileDir;

    private File file;
    private String type = "1";//默认开始录音 type=2，录音完毕
    RecordManager recordManager;
    File fileto;
    int level;
    private long downT;
    String sountime;


    public SoundTextView(Context context) {
        super(context);
        init();
    }

    public SoundTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public SoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {

        recordIndicator = new Dialog(getContext(), R.style.jmui_record_voice_dialog);
        recordIndicator.setContentView(R.layout.jmui_dialog_record_voice);
        mVoiceTime = (TextView) recordIndicator.findViewById(R.id.voice_time);


        file = new File(Environment.getExternalStorageDirectory() + "/recoder.amr");

        fileto = new File(Environment.getExternalStorageDirectory() + "/recoder.mp3");
        recordManager = new RecordManager(
                (Activity) mContext,
                String.valueOf(file),
                String.valueOf(fileto));
        recordManager.setOnAudioStatusUpdateListener(new RecordManager.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db) {
                //得到分贝

                if (null != recordIndicator) {
                    level = (int) db;
                    handler.sendEmptyMessage(0x111);

                }

            }
        });


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {

                case 0x111:



                    sountime = ProgressTextUtils.getSecsProgress(System.currentTimeMillis() - downT);

                    Log.e("-hus-", "sountime=: "+sountime );


                    long time = System.currentTimeMillis() - downT;

                    mVoiceTime.setText(ProgressTextUtils.getProgressText(time));
                    //判断时间
                    judetime(Integer.parseInt(sountime));
                    // }


                    break;
            }


        }
    };


    public void judetime(int time) {

        if (time > 14) {
            //结束录制
            Toast.makeText(mContext,"录音不能超过十五秒",Toast.LENGTH_SHORT).show();
            recordManager.stop_mp3();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    recordManager.saveData();
                    finishRecord(fileto.getPath(), sountime);
                }
            }.start();

            recordIndicator.dismiss();
            type = "2";
            // tvtime.setText("当前录音" + sountime + "''");
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:


                if (type.equals("1")) {
                    //开始发送时间
                    downT = System.currentTimeMillis();
                    recordManager.start_mp3();
                    recordIndicator.show();
                    Log.e("-hus-", "1111111 " );

                } else {

                    Log.e("-shy-", "您已经录制完毕: ");
                }


                return true;
            case MotionEvent.ACTION_UP:

                    if (type.equals("1")) {
                        try {
                            if (Integer.parseInt(sountime) > 2) {
                                recordManager.stop_mp3();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        recordManager.saveData();
                                        finishRecord(fileto.getPath(), sountime);
                                    }
                                }.start();

                                if (recordIndicator.isShowing()) {
                                    recordIndicator.dismiss();
                                }

                                type = "2";

                            } else {
                                recordManager.stop_mp3();
                                if (recordIndicator.isShowing()) {
                                    recordIndicator.dismiss();
                                }
                                sountime = null;
                                Toast.makeText(mContext,"录音时间少于3秒，请重新录制",Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {

                            recordManager.stop_mp3();
                            if (recordIndicator.isShowing()) {
                                recordIndicator.dismiss();
                            }
                            sountime = null;
                            Toast.makeText(mContext,"录音时间少于3秒，请重新录制",Toast.LENGTH_SHORT).show();


                        }

                    }



                break;
            case MotionEvent.ACTION_CANCEL:


                    if (recordIndicator.isShowing()) {
                        recordIndicator.dismiss();
                    }




                break;
        }

        return super.onTouchEvent(event);
    }


    //录音完毕加载 ListView item
    private void finishRecord(String path, String time) {

        if (onRecordFinishedListener != null) {
            onRecordFinishedListener.newMessage(path, Integer.parseInt(time));
            type = "1";
        }
        //发送语音
        // Toasts.toast(getContext(),"您已经录完了一条语音"+myRecAudioFile);
    }

    private OnRecordFinishedListener onRecordFinishedListener;

    public void setOnRecordFinishedListener(OnRecordFinishedListener onRecordFinishedListener) {
        this.onRecordFinishedListener = onRecordFinishedListener;
    }

    public interface OnRecordFinishedListener {
        void newMessage(String path, int duration);
    }


}
