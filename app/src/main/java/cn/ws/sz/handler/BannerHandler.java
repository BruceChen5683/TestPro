package cn.ws.sz.handler;

import android.content.Context;
import android.os.Handler;

/**
 * Created by chenjianliang on 2018/1/7.
 */

public class BannerHandler extends Handler{
    private Context context;

    public BannerHandler(Context context) {
        this.context = context;
    }
}
