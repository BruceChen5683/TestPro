package cn.ws.sz.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.Eyes;
import third.ACache;

public class CollectActivity extends AppCompatActivity {
	private final static String TAG = CollectActivity.class.getSimpleName();
    private LinearLayout llReturnBack;
    private TextView tvTitle;

    private ListView collect;
    private CollectAdapter adapter;
    private List<CollectHistroyBean> data = new ArrayList<>();
	private ACache mACache;
	private CollectHistoryBeanCollections collectHistoryBeanCollections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));
        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectActivity.this.finish();
            }
        });
        collect = (ListView) findViewById(R.id.collect);
        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("我的收藏");
		mACache = ACache.get(this);
        adapter = new CollectAdapter(this,data);
        collect.setAdapter(adapter);
		collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(CollectActivity.this,BusinessDetailActivity.class);
				intent.putExtra(Constant.KEY_EXTRA_MERCHANT_ID,adapter.getItem(position).getId());
				startActivity(intent);
			}
		});
    }

	@Override
	protected void onResume() {
		super.onResume();
		if(mACache.getAsObject(Constant.CACHE_COLLECT) == null){
			collectHistoryBeanCollections = new CollectHistoryBeanCollections();
		}else{
			collectHistoryBeanCollections = new CollectHistoryBeanCollections().setCollectHistoryBeanCollections((CollectHistoryBeanCollections) mACache.getAsObject(Constant.CACHE_COLLECT));
		}
		data.clear();
		for (int i = 0; i < collectHistoryBeanCollections.getCollectHistroyBeans().size();i++){
			data.add(collectHistoryBeanCollections.getCollectHistroyBeans().get(i));
		}
		adapter.notifyDataSetChanged();
	}
}
