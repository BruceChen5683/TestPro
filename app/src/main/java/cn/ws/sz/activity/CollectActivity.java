package cn.ws.sz.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.adater.CollectAdapter;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.CollectHistoryBeanCollections;
import cn.ws.sz.bean.CollectHistroyBean;
import cn.ws.sz.utils.Eyes;
import third.ACache;

public class CollectActivity extends AppCompatActivity {

    private LinearLayout llReturnBack;
    private TextView tvTitle;

    private ListView collect;
    private CollectAdapter adapter;
    private List<CollectHistroyBean> data = new ArrayList<>();
	private ACache mACache;
	private CollectHistoryBeanCollections collectHistoryBeanCollections = new CollectHistoryBeanCollections();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));

        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        collect = (ListView) findViewById(R.id.collect);
        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("我的收藏");
		mACache = ACache.get(this);

		if(mACache.getAsObject("collect") != null){
			collectHistoryBeanCollections.setCollectHistoryBeanCollections((CollectHistoryBeanCollections) mACache.getAsObject("collect"));
		}

		for (int i = 0; i < collectHistoryBeanCollections.getCollectHistroyBeans().size();i++){
			data.add(collectHistoryBeanCollections.getCollectHistroyBeans().get(i));
		}
        adapter = new CollectAdapter(this,data);
        collect.setAdapter(adapter);
    }
}
