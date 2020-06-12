package com.example.honey_create_cloud;

import android.app.Application;
import android.webkit.WebView;

import com.tencent.tinker.entry.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;

/**
 * Created by wangpan on 2020/5/27
 */
public class MyApplication extends Application {
    private ApplicationLike tinkerPatchApplicationLike;
    @Override
    public void onCreate() {
        super.onCreate();
        initTinker();
    }
    private void initTinker() {
        // 我们可以从这里获得Tinker加载过程的信息
        tinkerPatchApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
        // 初始化TinkerPatch SDK
        TinkerPatch.init(tinkerPatchApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setFetchPatchIntervalByHours(3)
                .setPatchRestartOnSrceenOff(true);
        TinkerPatch.with().fetchPatchUpdate(true);
    }
}
