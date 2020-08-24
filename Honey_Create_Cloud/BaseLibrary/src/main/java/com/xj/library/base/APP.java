package com.xj.library.base;

import android.app.Application;

public class APP extends Application {

    private static APP mAPP;

  /*  private RefWatcher refWatcher;*/


    @Override
    public void onCreate() {
        super.onCreate();
        mAPP=this;

    /*    if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        mAPP=this;
        WriteLogUtil.init(this);*/
    }

    public static APP getInstance(){
        return mAPP;
    }
/*

    public static RefWatcher getRefWatcher(Context context) {
        APP application = (APP) context.getApplicationContext();
        return application.refWatcher;
    }

*/

}
