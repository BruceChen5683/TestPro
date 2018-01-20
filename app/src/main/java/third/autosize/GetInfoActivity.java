package third.autosize;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ws.sz.R;

/**
 * 获取手机屏幕相关数据
 */
public class GetInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView show;
    private Button get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.ttt,null);
        setContentView(rootView);

                //setContentView(R.layout.activity_getinfo);

        View rl =  LayoutInflater.from(this).inflate(R.layout.activity_getinfo, (ViewGroup) rootView);


        Log.d("GetInfoActivity", "onCreate: height   "+rl.getWidth());
        Log.d("GetInfoActivity", "onCreate: width   "+rl.getHeight());

        Log.d("GetInfoActivity", "onCreate: height   "+rl.getLayoutParams().height);
        Log.d("GetInfoActivity", "onCreate: width   "+rl.getLayoutParams().width);
        rl.measure(0,0);
        Log.d("GetInfoActivity", "onCreate: rl.getMeasuredHeight()   "+rl.getMeasuredHeight());
        Log.d("GetInfoActivity", "onCreate: rl.getMeasuredWidth()   "+rl.getMeasuredWidth());


        get = (Button) rl.findViewById(R.id.get_btn);
        get.setOnClickListener(this);

        show = (TextView) rl.findViewById(R.id.show_tv);
        show.setText(getScreenParams());
        data();
    }

    private void data() {
        Log.v("Build.BOARD",""+ Build.BOARD);
        Log.v("Build.BOOTLOADER",""+ Build.BOOTLOADER);
        Log.v("Build.BRAND",""+ Build.BRAND);
        Log.v("Build.DEVICE",""+ Build.DEVICE);
        Log.v("Build.DISPLAY",""+ Build.DISPLAY);
        Log.v("Build.FINGERPRINT",""+ Build.FINGERPRINT);
        Log.v("Build.HARDWARE",""+ Build.HARDWARE);
        Log.v("Build.HOST",""+ Build.HOST);
        Log.v("Build.ID",""+ Build.ID);
        Log.v("Build.MANUFACTURER",""+ Build.MANUFACTURER);
        Log.v("Build.MODEL",""+ Build.MODEL);
        Log.v("Build.PRODUCT",""+ Build.PRODUCT);
        Log.v("Build.SERIAL",""+ Build.SERIAL);
        Log.v("Build.TAGS",""+ Build.TAGS);
        Log.v("Build.TYPE",""+ Build.TYPE);
        Log.v("Build.UNKNOWN",""+ Build.UNKNOWN);
        Log.v("Build.USER",""+ Build.USER);
        Log.v("Build.VERSION.SDK_INT",""+ Build.VERSION.SDK_INT);
        Log.v("Build.VERSION.CODENAME",""+ Build.VERSION.CODENAME);
        Log.v("Build.V.INCREMENTAL",""+ Build.VERSION.INCREMENTAL);
        Log.v("Build.VERSION.RELEASE",""+ Build.VERSION.RELEASE);
        Log.v("Build.VERSION_CODES.M",""+ Build.VERSION_CODES.M);
    }

    @Override
    public void onClick(View v) {
        show.setText(getScreenParams());
    }

    public String getScreenParams() {
        DisplayMetrics dm = new DisplayMetrics();
//        dm = getResources().getDisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightPixels = dm.heightPixels;
        int widthPixels = dm.widthPixels;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        int densityDpi = dm.densityDpi;
        float density = dm.density;
        float scaledDensity = dm.scaledDensity;
        float heightDP = heightPixels / density;
        float widthDP = widthPixels / density;
        String str = "heightPixels: " + heightPixels + "px";
        str += "\nwidthPixels: " + widthPixels + "px";
        str += "\nxdpi: " + xdpi + "dpi";
        str += "\nydpi: " + ydpi + "dpi";
        str += "\ndensityDpi: " + densityDpi + "dpi";
        str += "\ndensity: " + density;
        str += "\nscaledDensity: " + scaledDensity;
        str += "\nheightDP: " + heightDP + "dp";
        str += "\nwidthDP: " + widthDP + "dp";
        return str;
    }
}