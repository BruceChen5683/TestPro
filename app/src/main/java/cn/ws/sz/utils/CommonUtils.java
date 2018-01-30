package cn.ws.sz.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.ws.sz.activity.MainActivity;
import cn.ws.sz.activity.SplashActivity;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.view.ImageLayout;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

/**
 * Created by chenjianliang on 2018/1/10.
 */

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();
    public static Bitmap getScaleBitmap(String filePath, int width, int height) {

        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(filePath), null, options);

            // Decode bitmap with inSampleSize set
            int inSampleSize = 1;
            if (options.outHeight > height || options.outWidth > width) {
                int heightRatio = Math.round((float) options.outHeight / (float) height);
                int widthRatio = Math.round((float) options.outWidth / (float) width);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;

            bitmap =  BitmapFactory.decodeStream(new FileInputStream(filePath), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * scrollView嵌套listView
     * */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if(listItem == null){
                Log.d("", "setListViewHeightBasedOnChildren: null----------");
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static  void callByDefault(Context context,String phone) {
        Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
		intentPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ToastUtil.showShort(context,"请到设置中，允许万商获取拨打电话权限");
//            return;
//        }
        context.startActivity(intentPhone);
    }

    public static void setImageView2(String imageUrl, final ImageLayout imageView){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUrl, imageView,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    public static void setImageView(String imageUrl, final ImageView imageView){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUrl, imageView,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


//    public static void updateData(final Context context){
//        VolleyRequestUtil.RequestGet(context,
//                Constant.URL_CATEGORY + 0,
//                Constant.TAG_CATEGROY,//一级分类tag
//                new VolleyListenerInterface(context,
//                        VolleyListenerInterface.mListener,
//                        VolleyListenerInterface.mErrorListener) {
//                    @Override
//                    public void onMySuccess(String result) {
//                        Log.d(TAG, "onMySuccess: " + result);
//                        ClassifyStatus status = new Gson().fromJson(result,ClassifyStatus.class);
//                        WSApp.firstCategroyList.clear();
//                        WSApp.firstCategroyList.addAll(status.getData());
//                        for (int i = 0;i < WSApp.firstCategroyList.size();i++){
//                            CommonUtils.loadingSecondData(context,WSApp.firstCategroyList.get(i).getId());
//                        }
//                    }
//
//                    @Override
//                    public void onMyError(VolleyError error) {
//                        Log.d(TAG, "onMyError: "+error.getMessage());
//                    }
//                },
//                true);
//    }

//    public static void loadingSecondData(Context context,final int id){
//
//        VolleyRequestUtil.RequestGet(context,
//                Constant.URL_CATEGORY + id,
//                Constant.TAG_CATEGROY+id,//不同一级分类tag
//                new VolleyListenerInterface(context,
//                        VolleyListenerInterface.mListener,
//                        VolleyListenerInterface.mErrorListener) {
//                    @Override
//                    public void onMySuccess(String result) {
//                        Log.d(TAG, "onMySuccess: " + result);
//                        ClassifyStatus status = new Gson().fromJson(result,ClassifyStatus.class);
//                        WSApp.secondCategroyMap.put(id,status.getData());
//                    }
//
//                    @Override
//                    public void onMyError(VolleyError error) {
//                        Log.d(TAG, "onMyError: "+error.getMessage());
//                    }
//                },
//                true);
//    }

}
