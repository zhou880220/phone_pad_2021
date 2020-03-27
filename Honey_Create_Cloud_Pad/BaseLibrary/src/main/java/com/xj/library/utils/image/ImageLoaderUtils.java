package com.xj.library.utils.image;

/**
 * @ explain:
 * @ author：xujun on 2016/9/17 17:34
 * @ email：gdutxiaoxu@163.com
 */
public class ImageLoaderUtils {
/*
    *//**
     * 指定大小加载图片
     *
     * @param mContext   上下文
     * @param url       图片路径
     * @param width      宽
     * @param height     高
     * @param mImageView 控件
     *//*
    public static void loadImageViewSize(Context mContext, String url, int width, int height,
                                     ImageView mImageView) {
        Picasso.with(mContext).load(url).resize(width, height).centerCrop().into(mImageView);
//        Glide.with(mContext).load(url).into(mImageView);
    }

    public static void loadImageView(Context mContext, String url, ImageView mImageView) {
//        Picasso.with(mContext).load(url).into(mImageView);
        Glide.with(mContext).load(url).into(mImageView);
    }

    *//**
     * 加载有默认图片
     *
     * @param mContext   上下文
     * @param path       图片路径
     * @param resId      默认图片资源
     * @param mImageView 控件
     *//*
    public static void loadImageViewHolder(Context mContext, String path, int resId, ImageView
            mImageView) {
        Picasso.with(mContext).load(path).fit().placeholder(resId).into(mImageView);
    }

    *//**
     * 裁剪图片
     *
     * @param mContext   上下文
     * @param path       图片路径
     * @param mImageView 控件
     *//*
    public static void loadImageViewCrop(Context mContext, String path, ImageView mImageView) {
        Picasso.with(mContext).load(path).transform(new CropImageView()).into(mImageView);
    }

    public static void display(Context context, ImageView imageView,String url,Object tag){
        Picasso.with(context).load(url).tag(tag).fit().
                error(R.drawable.ic_error).placeholder(R.drawable.ic_progress).into(imageView);
    }

    public static void display(Context context, ImageView imageView,String url){
        Picasso.with(context).load(url).fit().
                error(R.drawable.ic_error).placeholder(R.drawable.ic_progress).into(imageView);
    }

    public static void cancel(Context context){
        Picasso picasso = Picasso.with(context);


    }

    *//**
     * 自定义图片裁剪
     *//*
    public static class CropImageView implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap newBitmap = Bitmap.createBitmap(source, x, y, size, size);

            if (newBitmap != null) {
                //内存回收
                source.recycle();
            }
            return newBitmap;
        }

        @Override
        public String key() {

            return "lgl";
        }
    }*/
}