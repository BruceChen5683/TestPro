package cn.ws.sz.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.adater.CollectAdapter;

public class LookHistroryActivity extends AppCompatActivity {
    private LinearLayout llReturnBack;
    private TextView tvTitle;

    private ListView collect;
    private CollectAdapter adapter;
    private List<String> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_histrory);

        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        collect = (ListView) findViewById(R.id.collect);
        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("我的足迹");

        data.add("香格里拉大酒店");
        data.add("和平饭店");
        data.add("皇家娱乐KTV");
        data.add("孟非小面");
        data.add("望湘园");
        adapter = new CollectAdapter(this,data);
        collect.setAdapter(adapter);
    }

}
