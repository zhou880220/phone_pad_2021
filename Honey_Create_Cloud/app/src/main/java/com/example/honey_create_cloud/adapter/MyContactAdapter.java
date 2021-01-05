package com.example.honey_create_cloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.honey_create_cloud.Constant;
import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.RecentlyApps;
import com.example.honey_create_cloud.bean.Result;
import com.example.honey_create_cloud.http.CallBackUtil;
import com.example.honey_create_cloud.http.OkhttpUtil;
import com.example.honey_create_cloud.ui.ApplyFirstActivity;
import com.example.honey_create_cloud.ui.ApplySecondActivity;
import com.example.honey_create_cloud.ui.ApplyThirdActivity;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import okhttp3.Call;

/**
 * Created by wangpan on 2020/3/12
 */
public class MyContactAdapter extends RecyclerView.Adapter<MyContactAdapter.ViewHolder> {
    private List<RecentlyApps.DataBean> mContactList = new ArrayList<>();
    private Context context;
    private OnClosePopupListener onClosePopupListener;
    private String userid;
    private String token;
    private String url;

    public void setOnClosePopupListener(OnClosePopupListener onClosePopupListener) {
        this.onClosePopupListener = onClosePopupListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View contactView;//存储解析到的view
        ImageView imageView;
        LinearLayout recyclerWidth;
        TextView appNameView;


        public ViewHolder(View view) {
            super(view);
            contactView = view;
            imageView = view.findViewById(R.id.imgUrl);
            recyclerWidth = view.findViewById(R.id.recycler_width);
            appNameView = view.findViewById(R.id.appName);
        }
    }

    public MyContactAdapter(List<RecentlyApps.DataBean> mContactList, Context context,String userid, String token,String url) {
        this.mContactList = mContactList;
        this.context = context;
        this.userid = userid;
        this.token = token;
        this.url = url;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gridview, parent, false);//解析layout
        final ViewHolder viewHolder = new ViewHolder(view);//新建一个viewHolder绑定解析到的view
        //监听每一项里的控件的点击事件，如点击了ImageView
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                RecentlyApps.DataBean dataBean = mContactList.get(position);
                if (onClosePopupListener != null) {
                    onClosePopupListener.onClosePopupClick("关闭");
                }

                //权限判断
                int appId =  mContactList.get(position).getAppId();
                Map<String, String> paramsMap =  new HashMap<>();
                paramsMap.put("appId", appId+"");
                paramsMap.put("userId", userid);
                OkhttpUtil.okHttpGet(Constant.APP_AUTH_CHECK, paramsMap, new CallBackUtil.CallBackString() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        Log.e("MyContactAdapter", "onFailure: "+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("MyContactAdapter", "checkAuth onResponse: " + response);
                        Gson gson = new Gson();
                        Result result = gson.fromJson(response, Result.class);
                        if (result.getCode() == 200) {
                           if ((Boolean)result.getData()){
                               if (position == 0){
                                   Intent intent = new Intent(context, ApplyFirstActivity.class);
                                   intent.putExtra("userid",userid);
                                   intent.putExtra("token",token);
                                   intent.putExtra("url",mContactList.get(0).getAppInterfaceUrl());
                                   intent.putExtra("appId",mContactList.get(0).getAppId()+"");
                                   context.startActivity(intent);
                               }else if(position == 1){
                                   Intent intent = new Intent(context, ApplySecondActivity.class);
                                   intent.putExtra("userid",userid);
                                   intent.putExtra("token",token);
                                   intent.putExtra("url",mContactList.get(1).getAppInterfaceUrl());
                                   intent.putExtra("appId",mContactList.get(1).getAppId()+"");
                                   context.startActivity(intent);
                               }else if(position == 2){
                                   Intent intent = new Intent(context, ApplyThirdActivity.class);
                                   intent.putExtra("userid",userid);
                                   intent.putExtra("token",token);
                                   intent.putExtra("url",mContactList.get(2).getAppInterfaceUrl());
                                   intent.putExtra("appId",mContactList.get(2).getAppId()+"");
                                   context.startActivity(intent);
                               }
                           } else {
                               Log.e("MyContactAdapter", "no auth");
                               showToast(Constant.NO_AUTH_TIP);
                           }
                        } else {
                            showToast(Constant.ERROR_SERVER_TIP);
                        }
                    }
                });


            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mContactList.get(position).getLogoUrl() != null){
            //            Glide.with(context).load(beanList.get(position).getSP_LOGOURL()).apply((RequestOptions.bitmapTransform
//                    (new RoundedCorners(30)))).into((ImageView) viewHolder.studio_image);
            Glide.with(context).load(mContactList.get(position).getLogoUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).into(holder.imageView);
            holder.appNameView.setText(mContactList.get(position).getAppName());
        }
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public interface OnClosePopupListener{
        void onClosePopupClick(String name);
    }


    public void showToast(final String msg) {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //要执行的操作
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }, 30);
    }

}
