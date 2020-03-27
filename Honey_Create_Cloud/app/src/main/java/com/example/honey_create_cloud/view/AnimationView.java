package com.example.honey_create_cloud.view;

import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by wangpan on 2020/1/14
 */
public class AnimationView {
    private AlphaAnimation mHideAnimation;

    /**
     * View渐隐动画效果
     */
    public void getHideAnimation(View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }

        if (null != mHideAnimation) {
            mHideAnimation.cancel();
        }
        // 监听动画结束的操作
        mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        view.startAnimation(mHideAnimation);
    }
}
