package com.example.honey_create_cloud.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.util.ClipView;
import com.example.honey_create_cloud.util.ClipViewLayout;
import com.xj.library.fragment.ActivityResultHelper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ClipImageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ClipImageActivity";

    public static final String TYPE = "type";
    public static final int REQ_CLIP_AVATAR = 50;

    private ClipViewLayout mClipViewLayout;
    private ImageView mBack;
    private TextView mBtnCancel;
    private TextView mBtnOk;

    private int mType;

    /**
     * @param activity
     * @param uri
     * @param callback
     */
    public static void goToClipActivity(FragmentActivity activity, Uri uri,
                                        ActivityResultHelper.Callback callback) {
        if (uri == null) {
            return;
        }
        Intent intent = getClipIntent(activity, uri);
        ActivityResultHelper.init(activity).startActivityForResult(intent, callback);
    }

    public static void goToClipActivity(FragmentActivity activity, Uri uri) {
        Intent clipIntent = getClipIntent(activity, uri);
        activity.startActivityForResult(clipIntent, REQ_CLIP_AVATAR);
    }

    @NonNull
    public static Intent getClipIntent(FragmentActivity activity, Uri uri) {
        Intent intent = new Intent();
        intent.setClass(activity, ClipImageActivity.class);
        intent.putExtra(TYPE, ClipView.TYPE_PALACE);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_clip_image);
        mType = getIntent().getIntExtra(TYPE, ClipView.TYPE_ROUND);
        Log.i(TAG, "onCreate: mType =" + mType);
        initView();
        mClipViewLayout.setClipType(mType);
    }

    /**
     * 初始化组件
     */
    public void initView() {
        mClipViewLayout = findViewById(R.id.clipViewLayout);
        mBack = findViewById(R.id.iv_back);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnOk = findViewById(R.id.bt_ok);
        //设置点击事件监听器
        mBack.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClipViewLayout.setVisibility(View.VISIBLE);
        mClipViewLayout.setClipType(mType);

        //设置图片资源
        mClipViewLayout.setImageSrc(getIntent().getData());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.bt_ok:
                generateUriAndReturn();
                break;
        }
    }

    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap = mClipViewLayout.clip();

        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent();
            intent.setData(mSaveUri);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
