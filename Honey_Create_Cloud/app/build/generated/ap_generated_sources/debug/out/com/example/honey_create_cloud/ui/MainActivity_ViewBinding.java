// Generated code from Butter Knife. Do not modify!
package com.example.honey_create_cloud.ui;

import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.honey_create_cloud.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.mNewwebprogressbar = Utils.findRequiredViewAsType(source, R.id.newwebprogressbar, "field 'mNewwebprogressbar'", ProgressBar.class);
    target.mNewWeb = Utils.findRequiredViewAsType(source, R.id.new_Web, "field 'mNewWeb'", WebView.class);
    target.mLoadingPage = Utils.findRequiredView(source, R.id.loading_page, "field 'mLoadingPage'");
    target.mWebError = Utils.findRequiredView(source, R.id.web_error, "field 'mWebError'");
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mNewwebprogressbar = null;
    target.mNewWeb = null;
    target.mLoadingPage = null;
    target.mWebError = null;
  }
}
