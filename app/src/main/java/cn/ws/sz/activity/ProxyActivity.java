package cn.ws.sz.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.utils.CommonUtils;

public class ProxyActivity extends AppCompatActivity {
    private TextView tvTitle;
    private LinearLayout llReturnBack;
    private Map<String, String[]> fileMap = new HashMap<>();
    private Map<String,String> parmMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("申请成为地区代理");

        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);


        uploadInfo();
    }

    private void uploadInfo() {

    }
}
