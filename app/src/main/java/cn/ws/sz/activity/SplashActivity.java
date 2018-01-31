package cn.ws.sz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;

import com.android.volley.VolleyError;
import com.google.gson.Gson;


import cn.ws.sz.R;
import cn.ws.sz.bean.AreaStatus;
import cn.ws.sz.bean.CityStatus;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.bean.ProvinceStatus;
import cn.ws.sz.utils.Authority;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DataHelper;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.PermissionsChecker;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import third.ACache;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;


public class SplashActivity extends Activity {

    private static final int AUTO_HIDE_DELAY_MILLIS = 500;

    private final Handler mHideHandler = new Handler();

    private static final String TAG = SplashActivity.class.getSimpleName();

    private Gson gson;
    private ACache mCache;


    private boolean isRequireCheck; // 是否需要系统权限检测
    private PermissionsChecker mPermissionsChecker;//检查权限
    //危险权限（运行时权限）
    static final String[] PERMISSIONS = new String[]{
			Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int PERMISSION_REQUEST_CODE = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));

        mPermissionsChecker = new PermissionsChecker(this);
        isRequireCheck = true;
        gson = new Gson();
        mCache = ACache.get(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 6.0权限
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //权限没有授权，进入授权界面
            if(mPermissionsChecker.judgePermissions(PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }else{
				isRequireCheck = true;
                loadData();
            }
        }else {
			isRequireCheck = true;
			loadData();
		}
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            loadData();
        } else {
            isRequireCheck = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                showPermissionDialog();
            }
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted( int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "hasAllPermissionsGranted: "+grantResult+"");
                return false;
            }
        }
        return true;
    }
    /**
     * 提示对话框
     */
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
//                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    // 启动应用的设置
    private void startAppSettings() {
        Intent appDetailSettingIntent = Authority.getAppDetailSettingIntent(this);
        startActivity(appDetailSettingIntent);

    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadingData();
                loadingProviceData();
            }
        },"loadingAreasAndCategroys").start();
    }

    private void loadingProviceData() {
        String provinces = mCache.getAsString(Constant.CACHE_PROVINCES);
        if(provinces == null){
            VolleyRequestUtil.RequestGet(this,
                    Constant.URL_PROVINCE,
                    Constant.TAG_PROVINCE,
                    new VolleyListenerInterface(this,
                            VolleyListenerInterface.mListener,
                            VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            Log.d(TAG, "loadingProviceData onMySuccess: ");

                            parseProvinces(result,true);
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Log.e(TAG, "onMyError: "+error.getMessage());

                            ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
                        }
                    },
                    true);
        }else{
            parseProvinces(provinces,false);
        }
    }

    private void parseProvinces(String result,boolean save) {
        Log.d(TAG, "parseProvinces " + result);
        ProvinceStatus status = gson.fromJson(result,ProvinceStatus.class);
        WSApp.provinces.clear();
        if(status.getData().size() != 0){
            if(save){
                mCache.put(Constant.CACHE_PROVINCES,result, Constant.PROVINCES_CITY_DISTRICT_TIME);
            }
            WSApp.provinces.addAll(status.getData());
            for (int i = 0;i < WSApp.provinces.size();i++){
                loadingCityData(WSApp.provinces.get(i).getId());
            }

        }else{
            ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
        }
    }

    private void loadingCityData(final int id) {

        String citys = mCache.getAsString(Constant.CACHE_PROVINCE_CITY+id);
        if(citys == null){
            VolleyRequestUtil.RequestGet(this,
                    Constant.URL_CITY + "/" + id,
                    Constant.TAG_CITY+id,
                    new VolleyListenerInterface(this,
                            VolleyListenerInterface.mListener,
                            VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            Log.d(TAG, "loadingCityData onMySuccess: ");

                            parseCitys(result, id,true);
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Log.e(TAG, "onMyError: "+error.getMessage());
                            ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
                        }
                    },
                    true);
        }else{
            parseCitys(citys, id,false);
        }

    }

    private void parseCitys(String result, int id,boolean save) {
//        Log.d(TAG, "parseCitys " + "*****"+id + "******"+result);
        CityStatus status = gson.fromJson(result,CityStatus.class);
        WSApp.citysMap.put(id,status.getData());
        if(save){
            mCache.put(Constant.CACHE_PROVINCE_CITY+id,result,Constant.PROVINCES_CITY_DISTRICT_TIME);
        }

        if (status.getData() != null && status.getData().size() > 0){
            for (int i = 0;i < status.getData().size();i++){
                WSApp.citys.put(status.getData().get(i).getId(),status.getData().get(i));
                loadAreaData(status.getData().get(i).getId());
            }
        }
    }

    private void loadAreaData(final int id) {

        String areas = mCache.getAsString(Constant.CACHE_CITY_AREA+id);
        if(areas == null){
            VolleyRequestUtil.RequestGet(this,
                    Constant.URL_AREA + "/" + id,
                    Constant.TAG_AREA+id,
                    new VolleyListenerInterface(this,
                            VolleyListenerInterface.mListener,
                            VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            Log.d(TAG, "loadAreaData onMySuccess: ");
                            parseAreas(result, id,true);
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Log.e(TAG, "onMyError: "+error.getMessage());

                            ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
                        }
                    },
                    true);
        }else {
            parseAreas(areas,id,false);
        }


    }

    private void parseAreas(String result, int id,boolean save) {
//        Log.d(TAG, "parseAreas : " + result);

        AreaStatus status = gson.fromJson(result,AreaStatus.class);
        if(status.getData() != null){
            if(save){
                mCache.put(Constant.CACHE_CITY_AREA+id,result,Constant.PROVINCES_CITY_DISTRICT_TIME);
            }
            WSApp.areasMap.put(id,status.getData());
        }
    }

    private void loadingData(){

        String firsCategroys = mCache.getAsString(Constant.CACHE_FIRST_CATEGROYS);
        if(firsCategroys == null){
            Log.d(TAG, "loadingData: ");
            VolleyRequestUtil.RequestGet(this,
                    Constant.URL_CATEGORY + 0,
                    Constant.TAG_CATEGROY,//一级分类tag
                    new VolleyListenerInterface(this,
                            VolleyListenerInterface.mListener,
                            VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            parseFirstCategroy(result,true);

                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Log.e(TAG, "onMyError: "+error.getMessage());
                            ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");

                        }
                    },
                    true);
        }else {
            parseFirstCategroy(firsCategroys,false);
        }
    }

    private void loadingSecondData(final int id){

        String secondCategroys = mCache.getAsString(Constant.CACHE_SECOND_CATEGROYS+id);
        if(secondCategroys == null){
            Log.d(TAG, "loadingSecondData: ");
            VolleyRequestUtil.RequestGet(this,
                    Constant.URL_CATEGORY + id,
                    Constant.TAG_CATEGROY+id,//不同一级分类tag
                    new VolleyListenerInterface(this,
                            VolleyListenerInterface.mListener,
                            VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            parseSecondCategroy(result, id,true);
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Log.d(TAG, "onMyError: "+error.getMessage());
                            //TODO     WSApp.firstCategroyList.remove(i);
                        }
                    },
                    true);
        }else{
            parseSecondCategroy(secondCategroys,id,false);
        }

    }


    private void parseFirstCategroy(String result,boolean save) {
        ClassifyStatus status = gson.fromJson(result,ClassifyStatus.class);
        if(status.getData() != null){
            if(save){
                mCache.put(Constant.CACHE_FIRST_CATEGROYS,result,Constant.CATEGROYS_TIME);
            }
        }

        Log.d(TAG, "parseFirstCategroy: "+status.getData().size());

        Log.d(TAG, "parseFirstCategroy: clear");
		DataHelper.getInstance().setFirstCategroyList(status.getData());

        for (int i = 0;i < DataHelper.getInstance().getFirstCategroyList().size();i++){
            loadingSecondData(DataHelper.getInstance().getFirstCategroyList().get(i).getId());
        }
        if(isRequireCheck){
            startMainActivity();
        }
    }

    private void parseSecondCategroy(String result, int id,boolean save) {
        ClassifyStatus status = gson.fromJson(result,ClassifyStatus.class);
        if(save){
            mCache.put(Constant.CACHE_SECOND_CATEGROYS+id,result,Constant.CATEGROYS_TIME);
        }
		DataHelper.getInstance().putSecondCategroyMap(id,status.getData());
    }


    private void startMainActivity(){
        mHideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        },AUTO_HIDE_DELAY_MILLIS);
    }

}
