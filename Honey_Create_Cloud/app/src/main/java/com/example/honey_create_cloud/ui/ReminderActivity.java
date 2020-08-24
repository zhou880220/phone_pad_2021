package com.example.honey_create_cloud.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.webclient.WebViewSetting;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener {
    @InjectView(R.id.Reminder_web)
    WebView mWebView;
    @InjectView(R.id.back_image)
    ImageView mBackImage;
    @InjectView(R.id.title_text)
    TextView mTitleText;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type.equals("1")) {
            webview("file:///android_asset/reminder.html", "用户协议");
        } else if (type.equals("2")) {
            webview("file:///android_asset/policy.html", "隐私政策");
        }
        initview();
    }

    private void initview() {
        mBackImage.setOnClickListener(this);
    }

    private void webview(String fileurl, String titletext) {
        mTitleText.setText(titletext);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings settings = mWebView.getSettings();
        if (settings != null) {
            WebViewSetting.initweb(settings);
        }
        mWebView.loadUrl(fileurl);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.back_image://返回温馨提示
            {
                finish();
            }
            break;
        }
    }

    /**
     * 修改顶部状态栏字体颜色
     *
     * @param activity
     * @param dark
     */
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }


}
