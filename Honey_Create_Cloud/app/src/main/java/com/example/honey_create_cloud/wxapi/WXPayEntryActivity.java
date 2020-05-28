package com.example.honey_create_cloud.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler, View.OnClickListener {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    @InjectView(R.id.wechat_img)
    ImageView mWechatImg;
    @InjectView(R.id.wechat_title)
    TextView mWechatTitle;
    @InjectView(R.id.wechat_back)
    Button mWechatBack;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        ButterKnife.inject(this);
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
        api.handleIntent(getIntent(), this);
        mWechatBack.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) { //成功
                mWechatImg.setBackgroundResource(R.drawable.wechat_pay_success);
                mWechatTitle.setText(R.string.wechat_pay_success);
            }else if(resp.errCode == -1){  //错误
                mWechatImg.setBackgroundResource(R.drawable.wechat_pay_error);
                mWechatTitle.setText(R.string.wechat_pay_error);
            }else if(resp.errCode == -2){  //用户取消
                mWechatImg.setBackgroundResource(R.drawable.wechat_pay_error);
                mWechatTitle.setText(R.string.wechat_pay_cancel);
            }
        }
    }

    @Override
    public void onClick(View v) {  //用户点击button按钮返回通知收银台
        backNoticeCashier();
    }

    @Override
    public void onBackPressed() {  //用户点击物理按键返回通知收银台
        super.onBackPressed();
        backNoticeCashier();
    }

    private void backNoticeCashier() {
        String s = mWechatTitle.getText().toString();
        Log.e(TAG, "backNoticeCashier: 1"+s );
        if (s.equals("支付成功")){
            EventBus.getDefault().post("支付成功");
            finish();
            Log.e(TAG, "backNoticeCashier:2"+s );
        }else if(s.equals("支付失败")){
            EventBus.getDefault().post("支付失败");
            finish();
            Log.e(TAG, "backNoticeCashier:3 "+s );
        }else if(s.equals("取消支付")){
            finish();
            Log.e(TAG, "backNoticeCashier: "+s );
        }
    }
}