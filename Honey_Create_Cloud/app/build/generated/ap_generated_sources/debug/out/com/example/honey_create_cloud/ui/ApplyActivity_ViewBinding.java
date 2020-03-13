// Generated code from Butter Knife. Do not modify!
package com.example.honey_create_cloud.ui;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.honey_create_cloud.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ApplyActivity_ViewBinding implements Unbinder {
  private ApplyActivity target;

  private View view7f0800f6;

  private View view7f0800f5;

  private View view7f0800f7;

  private View view7f080068;

  @UiThread
  public ApplyActivity_ViewBinding(ApplyActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ApplyActivity_ViewBinding(final ApplyActivity target, View source) {
    this.target = target;

    View view;
    target.mNewwebprogressbar = Utils.findRequiredViewAsType(source, R.id.newwebprogressbar, "field 'mNewwebprogressbar'", ProgressBar.class);
    target.mNewWeb = Utils.findRequiredViewAsType(source, R.id.new_Web, "field 'mNewWeb'", WebView.class);
    target.mWebErrpr = Utils.findRequiredView(source, R.id.web_error, "field 'mWebErrpr'");
    target.mReloadTv = Utils.findRequiredViewAsType(source, R.id.reload_tv, "field 'mReloadTv'", TextView.class);
    target.mGridPopup = Utils.findRequiredViewAsType(source, R.id.grid_popup, "field 'mGridPopup'", RecyclerView.class);
    view = Utils.findRequiredView(source, R.id.tv_publish, "field 'mTvPublish' and method 'onClick'");
    target.mTvPublish = Utils.castView(view, R.id.tv_publish, "field 'mTvPublish'", TextView.class);
    view7f0800f6 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_myPublish, "field 'mTvMyPublish' and method 'onClick'");
    target.mTvMyPublish = Utils.castView(view, R.id.tv_myPublish, "field 'mTvMyPublish'", TextView.class);
    view7f0800f5 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_relation, "field 'mTvRelation' and method 'onClick'");
    target.mTvRelation = Utils.castView(view, R.id.tv_relation, "field 'mTvRelation'", TextView.class);
    view7f0800f7 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.mLlPopup = Utils.findRequiredViewAsType(source, R.id.ll_popup, "field 'mLlPopup'", LinearLayout.class);
    target.mIvCollectionMe = Utils.findRequiredViewAsType(source, R.id.iv_collection_me, "field 'mIvCollectionMe'", ImageView.class);
    target.mTtCourseNone = Utils.findRequiredViewAsType(source, R.id.tt_course_none, "field 'mTtCourseNone'", TextView.class);
    target.mLlCourseNone = Utils.findRequiredViewAsType(source, R.id.ll_course_none, "field 'mLlCourseNone'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.fab_more, "field 'mFabMore' and method 'onClick'");
    target.mFabMore = Utils.castView(view, R.id.fab_more, "field 'mFabMore'", ImageView.class);
    view7f080068 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ApplyActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mNewwebprogressbar = null;
    target.mNewWeb = null;
    target.mWebErrpr = null;
    target.mReloadTv = null;
    target.mGridPopup = null;
    target.mTvPublish = null;
    target.mTvMyPublish = null;
    target.mTvRelation = null;
    target.mLlPopup = null;
    target.mIvCollectionMe = null;
    target.mTtCourseNone = null;
    target.mLlCourseNone = null;
    target.mFabMore = null;

    view7f0800f6.setOnClickListener(null);
    view7f0800f6 = null;
    view7f0800f5.setOnClickListener(null);
    view7f0800f5 = null;
    view7f0800f7.setOnClickListener(null);
    view7f0800f7 = null;
    view7f080068.setOnClickListener(null);
    view7f080068 = null;
  }
}
