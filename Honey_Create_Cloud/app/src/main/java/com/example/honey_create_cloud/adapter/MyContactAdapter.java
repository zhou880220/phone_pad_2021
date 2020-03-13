package com.example.honey_create_cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honey_create_cloud.R;
import com.example.honey_create_cloud.bean.ProductListBean;

import java.util.List;

/**
 * Created by wangpan on 2020/3/12
 */
public class MyContactAdapter extends RecyclerView.Adapter<MyContactAdapter.ViewHolder> {
    private List<ProductListBean> mContactList;
    private Context context;
    private OnClosePopupListener onClosePopupListener;

    public void setOnClosePopupListener(OnClosePopupListener onClosePopupListener) {
        this.onClosePopupListener = onClosePopupListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View contactView;//存储解析到的view
        ImageView imageView;
        LinearLayout recyclerWidth;

        public ViewHolder(View view) {
            super(view);
            contactView = view;
            imageView = view.findViewById(R.id.imgUrl);
            recyclerWidth = view.findViewById(R.id.recycler_width);
        }
    }

    public MyContactAdapter(List<ProductListBean> mContactList, Context context) {
        this.mContactList = mContactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gridview, parent, false);//解析layout
        final ViewHolder viewHolder = new ViewHolder(view);//新建一个viewHolder绑定解析到的view
//        //监听每一项的点击事件
//        viewHolder.contactView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = viewHolder.getAdapterPosition();
//                ProductListBean contact = mContactList.get(position);
//                Toast.makeText(view.getContext(), contact.getImgUrl(), Toast.LENGTH_SHORT).show();
//            }
//        });
        //监听每一项里的控件的点击事件，如点击了ImageView
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                ProductListBean contact = mContactList.get(position);
                if (onClosePopupListener != null){
                    onClosePopupListener.onClosePopupClick("关闭");
                }
                Toast.makeText(view.getContext(), contact.getProName(), Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductListBean contact = mContactList.get(position);
        holder.imageView.setImageResource(mContactList.get(position).getImgUrl());
    }


    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public interface OnClosePopupListener{
        void onClosePopupClick(String name);
    }

}
