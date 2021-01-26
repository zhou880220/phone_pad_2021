package com.example.honey_create_cloud.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.honey_create_cloud.Constant;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by wangpan on 2020/6/20
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("_TAG", "wx share "+baseResp.errCode);
//        finish();
        switch (baseResp.errCode) {

            case BaseResp.ErrCode.ERR_OK:
                //分享成功
//                Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                Log.e("_TAG", "onResp: ok ");
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
//                Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
                Log.e("_TAG", "onResp:cancel ");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
//                Toast.makeText(this, "分享拒绝", Toast.LENGTH_SHORT).show();
                Log.e("_TAG", "onResp:reject ");
                finish();
                break;
        }
    }
}
