package cn.ws.sz.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;

/**
 * Created by chenjianliang on 2018/1/31.
 */

public class Authority {
    /**
     * 相机
     *  返回true 表示可以使用  返回false表示不可以使用
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

//    /**
//     * 华为的权限管理页面
//     */
//    private static void gotoHuaweiPermission() {
//        try {
//            Intent intent = new Intent();
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
//            intent.setComponent(comp);
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            startActivity(getAppDetailSettingIntent());
//        }
//
//    }

    /**
     * 跳转到miui的权限管理页面
     */
//    private void gotoMiuiPermission() {
//        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//        i.setComponent(componentName);
//        i.putExtra("extra_pkgname", getPackageName());
//        try {
//            startActivity(i);
//        } catch (Exception e) {
//            e.printStackTrace();
//            gotoMeizuPermission();
//        }
//    }

//    /**
//     * 跳转到魅族的权限管理系统
//     */
//    private void gotoMeizuPermission() {
//        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            gotoHuaweiPermission();
//        }
//    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(context), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName(context));
        }
        return localIntent;
    }

    private static String getPackageName(Context context) {
        PackageManager pm = context.getPackageManager();

        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.packageName;
    }

}