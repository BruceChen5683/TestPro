package cn.ws.sz.view;

/**
 * Created by chenjianliang on 2018/1/8.
 */

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;

import cn.ws.sz.R;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     */
    public static ImageView getImageView(Context context, String url) {
        try {
            LayoutInflater mLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (mLayoutInflater != null) {
                ImageView imageView = (ImageView) mLayoutInflater.inflate(
                        R.layout.view_banner, null);
//                ImageLoader imageLoader = new ImageLoader(context);

//                imageLoader.DisplayImage(url, imageView);
                // ImageLoader.getInstance().displayImage(url, imageView);
                return imageView;
            } else {
                return null;
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.i("cjl", "ViewFactory---"+e);
            return null;
        }

    }

    public static ImageView getImageView(Context context, int id) {
        try {
            LayoutInflater mLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (mLayoutInflater != null) {
                ImageView imageView = (ImageView) mLayoutInflater.inflate(
                        R.layout.view_banner, null);

                InputStream is = context.getResources().openRawResource(id);

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inJustDecodeBounds = false;
                options.inSampleSize = 2;// 压缩图片

                Bitmap btp = BitmapFactory.decodeStream(is, null, options);

                imageView.setImageBitmap(btp);
                return imageView;
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.i("cjl", "ViewFactory---"+e);
            return null;
        }

    }

}