package cn.ws.sz.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
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
import java.lang.reflect.Method;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ws.sz.R;
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


    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }


    public static void showShare(Context context,String recommender){
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

		// title标题，微信、QQ和QQ空间等平台使用
		oks.setTitle(context.getResources().getString(R.string.app_name));
		// titleUrl QQ和QQ空间跳转链接
//		oks.setTitleUrl("http://www.sz-ws.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(recommender);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		TODO weChat Key
//		Bitmap logo = BitmapFactory.decodeResource(
//				context.getResources(), R.drawable.default_ws);
//		oks.setImageData(logo);
//		oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url在微信、微博，Facebook等平台中使用
//		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网使用
//		oks.setComment("我是测试评论文本");
		// 启动分享GUI
		oks.show(context);
	}
}
