package com.example.honey_create_cloud_pad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by wangpan on 2020/10/20
 */
public class OPPOPushMessageActivity extends AppCompatActivity {
    public String type = "";
    public String url = "";
    public String innerUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOppoMessage();
    }

    private void getOppoMessage() {
        // 取参数值

        if (getIntent().getExtras() != null) {
            // 取参数值
            Bundle bundle = getIntent().getExtras();
            Set<String> set = bundle.keySet();
            JSONObject hm = new JSONObject();
            if (set != null) {
                for (String key : set) {
                    try {
                        hm.put(key, bundle.get(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.e("NPL", "hm的值是：" + hm.toString());
            if (hm.keys().hasNext()) {
                //解析当前的HashMap对象，可以获取具体的数据
                if (set.contains("appid")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("APP_NOTICE_LIST","消息");
                    intent.putExtra("pushContentMessage",hm.toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                //根据当前type类型去执行不同的操作
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getOppoMessage();
    }
}
