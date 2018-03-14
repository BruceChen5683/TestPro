package cn.ws.sz.utils;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.mob.MobSDK;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.bean.AreaBean;
import cn.ws.sz.bean.CityBean;
import cn.ws.sz.bean.ProvinceBean;
import cn.ws.sz.service.LocationService;

/**
 * Created by chenjianliang on 2018/1/12.
 */

public class WSApp extends Application{
    public static RequestQueue queue;
    public static int classifyId = 0;

    public final static List<ProvinceBean> provinces = new ArrayList<>();//省
    public final static Map<Integer,List<CityBean>> citysMap = new HashMap<>();//省 市
    public final static Map<Integer,CityBean> citys = new HashMap<>();//市

    public final static Map<Integer,List<AreaBean>> areasMap = new HashMap<>();//市 区


    public LocationService locationService;
    public Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

		new Instabug.Builder(this, "10a5d2ad274edb62618b14df0128c6d5")
				.setInvocationEvent(InstabugInvocationEvent.SHAKE)
				.build();

		MobSDK.init(this);
        queue = Volley.newRequestQueue(getApplicationContext());
        initImageloader(getApplicationContext());

        locationService = new LocationService(getApplicationContext());
        vibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

        SharedPreferencesUtil.getInstance(getApplicationContext(),"ws_sp");


    }

    private void initImageloader(Context applicationContext) {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_ws)
                .showImageOnFail(R.drawable.default_ws)
                .showImageOnLoading(R.drawable.default_ws)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
                .displayer(new RoundedBitmapDisplayer(1))
                .displayer(new FadeInBitmapDisplayer(100))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(applicationContext);
        config.threadPriority(Thread.NORM_PRIORITY-2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(20*1024*1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(defaultOptions);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config.build());
    }

    public static RequestQueue getHttpRequestQueue(){
        return queue;
    }

}
