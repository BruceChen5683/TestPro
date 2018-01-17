package cn.ws.sz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.bean.ClassifyBean;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.utils.Constant;
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

        gson = new Gson();

        loadingData();


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
//                        Log.d(TAG, "onMySuccess: " + result);
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
                                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
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
//                        Log.d(TAG, "onMySuccess: " + result);
                        ClassifyStatus status = gson.fromJson(result,ClassifyStatus.class);
                        WSApp.secondCategroyMap.put(id,status.getData());

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: "+error.getMessage());
                    }
                },
                true);
    }




}
