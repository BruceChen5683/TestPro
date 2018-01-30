package cn.ws.sz.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by chenjianliang on 2018/1/31.
 */

public class PermissionsChecker {

    private Context context;

    public PermissionsChecker(Context context){
        this.context = context.getApplicationContext();
    }

    /**
     * 判断权限
     */
    public boolean judgePermissions(String...permissions){
        for(String permission:permissions){
            if(deniedPermission(permission)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     * PackageManager.PERMISSION_GRANTED 授予权限
     * PackageManager.PERMISSION_DENIED 缺少权限
     *
     */
    private boolean deniedPermission(String permission){
        return ContextCompat.checkSelfPermission(context,permission)== PackageManager.PERMISSION_DENIED;
    }
}
