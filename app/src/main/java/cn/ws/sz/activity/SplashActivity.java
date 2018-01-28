package cn.ws.sz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;

import com.android.volley.VolleyError;
import com.google.gson.Gson;


import cn.ws.sz.R;
import cn.ws.sz.bean.AreaStatus;
import cn.ws.sz.bean.CityStatus;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.bean.ProvinceStatus;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;


public class SplashActivity extends Activity {

    private static final int AUTO_HIDE_DELAY_MILLIS = 500;

    private final Handler mHideHandler = new Handler();

    private static final String TAG = SplashActivity.class.getSimpleName();

    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));

        gson = new Gson();

        loadingData();

        loadingProviceData();


    }

    private void loadingProviceData() {

        VolleyRequestUtil.RequestGet(this,
                Constant.URL_PROVINCE,
                Constant.TAG_PROVINCE,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "loadingProviceData onMySuccess: " + result);
                        ProvinceStatus status = gson.fromJson(result,ProvinceStatus.class);
                        WSApp.provinces.clear();
                        WSApp.provinces.addAll(status.getData());
                        for (int i = 0;i < WSApp.provinces.size();i++){
                            loadingCityData(WSApp.provinces.get(i).getId());
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
                    }
                },
                true);
    }

    private void loadingCityData(final int id) {
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_CITY + "/" + id,
                Constant.TAG_CITY+id,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "loadingCityData onMySuccess: " + "*****"+id + "******"+result);
                        CityStatus status = gson.fromJson(result,CityStatus.class);
                        WSApp.citysMap.put(id,status.getData());

                        if (status.getData() != null && status.getData().size() > 0){
                            for (int i = 0;i < status.getData().size();i++){
                                WSApp.citys.put(status.getData().get(i).getId(),status.getData().get(i));
                                loadAreaData(status.getData().get(i).getId());
                            }
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
                    }
                },
                true);
    }

    private void loadAreaData(final int id) {
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_AREA + "/" + id,
                Constant.TAG_AREA+id,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "loadAreaData onMySuccess: " + result);
                        AreaStatus status = gson.fromJson(result,AreaStatus.class);
                        if(status.getData() != null){
                            WSApp.areasMap.put(id,status.getData());
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");
                    }
                },
                true);
    }

    private void loadingData(){

        Log.d(TAG, "loadingData: ");
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_CATEGORY + 0,
                Constant.TAG_CATEGROY,//一级分类tag
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        ClassifyStatus status = gson.fromJson(result,ClassifyStatus.class);

                            WSApp.firstCategroyList.clear();
                            WSApp.firstCategroyList.addAll(status.getData());
//                            Log.d(TAG, "onMySuccess: WSApp.firstCategroyList  "+ WSApp.firstCategroyList);
                        for (int i = 0;i < WSApp.firstCategroyList.size();i++){
                            loadingSecondData(WSApp.firstCategroyList.get(i).getId());
                        }


                        mHideHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                SplashActivity.this.finish();
                            }
                        },AUTO_HIDE_DELAY_MILLIS);


                    }

                    @Override
                    public void onMyError(VolleyError error) {

                        ToastUtil.showLong(SplashActivity.this,"加载数据失败，请检查网络");

                    }
                },
                true);
    }

    private void loadingSecondData(final int id){

        Log.d(TAG, "loadingData: ");
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_CATEGORY + id,
                Constant.TAG_CATEGROY+id,//不同一级分类tag
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        ClassifyStatus status = gson.fromJson(result,ClassifyStatus.class);
                        WSApp.secondCategroyMap.put(id,status.getData());

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: "+error.getMessage());
//                   TODO     WSApp.firstCategroyList.remove(i);
                    }
                },
                true);
    }




}
