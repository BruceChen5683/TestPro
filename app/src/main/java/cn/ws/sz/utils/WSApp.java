package cn.ws.sz.utils;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.bean.ClassifyBean;
import cn.ws.sz.service.LocationService;

/**
 * Created by chenjianliang on 2018/1/12.
 */

public class WSApp extends Application{
    public static RequestQueue queue;
    public static List<ClassifyBean> firstCategroyList;
    public static Map<Integer,List<ClassifyBean>> secondCategroyMap;
    public static int classifyId = 0;

    public LocationService locationService;
    public Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
        firstCategroyList = new ArrayList<ClassifyBean>();
        secondCategroyMap = new HashMap<Integer, List<ClassifyBean>>();

        initImageloader(getApplicationContext());

        locationService = new LocationService(getApplicationContext());
        vibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);


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
