package com.example.honey_create_cloud.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.adapter.MyContactAdapter;
import com.example.honey_create_cloud.bean.ProductListBean;
import com.example.honey_create_cloud.webclient.MWebChromeClient;
import com.example.honey_create_cloud.webclient.MWebViewClient;
import com.example.honey_create_cloud.webclient.WebViewSetting;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyActivity extends AppCompatActivity {

    @BindView(R.id.newwebprogressbar)
    ProgressBar mNewwebprogressbar;
    @BindView(R.id.new_Web)
    WebView mNewWeb;
    @BindView(R.id.web_error)
    View mWebErrpr;
    @BindView(R.id.reload_tv)
    TextView mReloadTv;
    @BindView(R.id.grid_popup)
    RecyclerView mGridPopup;
    @BindView(R.id.tv_publish)
    TextView mTvPublish;
    @BindView(R.id.tv_myPublish)
    TextView mTvMyPublish;
    @BindView(R.id.tv_relation)
    TextView mTvRelation;
    @BindView(R.id.ll_popup)
    LinearLayout mLlPopup;
    @BindView(R.id.iv_collection_me)
    ImageView mIvCollectionMe;
    @BindView(R.id.tt_course_none)
    TextView mTtCourseNone;
    @BindView(R.id.ll_course_none)
    LinearLayout mLlCourseNone;
    @BindView(R.id.fab_more)
    ImageView mFabMore;

    private int[] icon = {R.mipmap.tabbar_contact_default, R.mipmap.tabbar_contact_pressed, R.mipmap.tabbar_sign_default};
    private String[] iconName = {"通讯录", "日历", "浏览器"};
    private List<ProductListBean> listDatas;
    private MyContactAdapter adapter;
    private boolean isShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView(url);
    }

    @OnClick({R.id.tv_publish, R.id.tv_myPublish, R.id.tv_relation, R.id.fab_more})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_publish:
                Toast.makeText(ApplyActivity.this, "1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_myPublish:
                Toast.makeText(ApplyActivity.this, "2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_relation:
                Toast.makeText(ApplyActivity.this, "3", Toast.LENGTH_SHORT).show();
                mGridPopup.setVisibility(View.VISIBLE);
                pagerView();
                adapter.setOnClosePopupListener(new MyContactAdapter.OnClosePopupListener() {
                    @Override
                    public void onClosePopupClick(String name) {
                        if (name.equals("关闭")) {
                            mGridPopup.setVisibility(View.GONE);
                        }
                    }
                });

                break;
            case R.id.fab_more:
                break;
        }
        switchPopup();
    }

    /**
     * 开合popup
     */
    private void switchPopup() {
        if (!isShow) {
            isShow = true;
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(fabMore, "rotation", 45f);
//            objectAnimator.setDuration(300);
//            objectAnimator.start();

            mLlPopup.setVisibility(View.VISIBLE);
            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(300);
            mLlPopup.startAnimation(scaleAnimation);

            if (mLlCourseNone.getVisibility() == View.VISIBLE) {
                float fY = mFabMore.getY();
                float tY = mTtCourseNone.getY();
                if (250 > fY - tY) {
                    mTtCourseNone.setText("");
                }
            }
        } else {
            isShow = false;
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(fabMore, "rotation", 0);
//            objectAnimator.setDuration(300);
//            objectAnimator.start();

            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(300);
            mLlPopup.startAnimation(scaleAnimation);
            mLlPopup.setVisibility(View.GONE);

            if (mLlCourseNone.getVisibility() == View.VISIBLE) {
                float fY = mFabMore.getY();
                float tY = mTtCourseNone.getY();
                if (250 > fY - tY) {
                    mTtCourseNone.setText("暂时没有内容哦");
                }
            }
        }
    }

    //页面卡片
    private void pagerView() {
        //初始化数据
        initViews();
        Log.i("listDatas", "" + listDatas.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(ApplyActivity.this);//添加布局管理器
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//设置为横向水平滚动，默认是垂直
        mGridPopup.setLayoutManager(layoutManager);//设置布局管理器
        adapter = new MyContactAdapter(listDatas, this);
        mGridPopup.setAdapter(adapter);
    }

    private void initViews() {
        listDatas = new ArrayList<>();
        for (int i = 0; i < iconName.length; i++) {
            listDatas.add(new ProductListBean(iconName[i], icon[i]));
        }
    }

    private void webView(String url) {
        if (Build.VERSION.SDK_INT >= 19) {
            mNewWeb.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mNewWeb.getSettings().setLoadsImagesAutomatically(false);
        }
        WebSettings webSettings = mNewWeb.getSettings();
        WebViewSetting.initweb(webSettings);
        mNewWeb.loadUrl(url);
        mNewWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mNewWeb != null && mNewWeb.canGoBack()) {
                        mNewWeb.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        wvClientSetting(mNewWeb);
    }

    private void wvClientSetting(WebView ead_web) {
        MWebViewClient mWebViewClient = new MWebViewClient(ead_web, this, mWebErrpr);
        ead_web.setWebViewClient(mWebViewClient);
        MWebChromeClient mWebChromeClient = new MWebChromeClient(this, mNewwebprogressbar);
        ead_web.setWebChromeClient(mWebChromeClient);
    }
}
