package com.example.honey_create_cloud.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangpan on 2020/3/23
 */
public class OKHttpUitls {
    private OKHttpGetListener onOKHttpGetListener;
    private MyHandler myHandler = new MyHandler();
    private static final String TAG = "OKHttpUitls";

    // /get
    public void get(String url){
        OkHttpClient client = new OkHttpClient();
        //创建请求对象
        Request request = new Request.Builder().url(url).build();
        //创建Call请求队列
        //请求都是放到一个队列里面的
        Call call = client.newCall(request);

        Log.d(TAG, "get() returned: " + call+"------------");
        //开始请求
        call.enqueue(new Callback() {
            //       失败，成功的方法都是在子线程里面，不能直接更新UI
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = myHandler.obtainMessage();
                message.obj = "请求失败";
                message.what = 0;
                myHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = myHandler.obtainMessage();
                String json = response.body().string();
                message.obj = json;
                message.what = 1;
                myHandler.sendMessage(message);
            }
        });
    }

    //使用接口回到，将数据返回
    public interface OKHttpGetListener{
        void error(String error);
        void success(String json);
    }
    //给外部调用的方法
    public void setOnOKHttpGetListener(OKHttpGetListener onOKHttpGetListener){
        this.onOKHttpGetListener = onOKHttpGetListener;
    }
    //使用Handler，将数据在主线程返回
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int w = msg.what;
            Log.d(TAG, "handleMessage() returned: " +msg );
            if (w ==0){
                String error = (String) msg.obj;
                onOKHttpGetListener.error(error);
            }
            if (w==1){
                String json = (String) msg.obj;
                onOKHttpGetListener.success(json);
            }
        }
    }
}
