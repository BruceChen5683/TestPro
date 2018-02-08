package cn.ws.sz.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessItemAdapter;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.Eyes;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

public class SearchActivity extends AppCompatActivity {

    private String searchValue = "";
    private TextView tvSearch;

    private static final String TAG = "SearchActivity";

    private BusinessItemAdapter adapter;
    private List<BusinessBean> data = new ArrayList<BusinessBean>();
    private ListView listView;

    private int firstCategroy = 0;
    private int secondCategroy = 0;
    private int pageId = 0;//页码
    private int areaId = 110101;//区域
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));

        tvSearch = (TextView) findViewById(R.id.tvSearch);
        Bundle bundle = getIntent().getExtras();
        searchValue = (String) bundle.get("searchValue");

        if(!TextUtils.isEmpty(searchValue)){
            tvSearch.setText(searchValue);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                tvSearch.setTextColor(getResources().getColor(R.color.white,null));
//            }else {
//                tvSearch.setTextColor(getResources().getColor(R.color.white));
//            }
        }else {
            tvSearch.setText("搜你所需...");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                tvSearch.setTextColor(getResources().getColor(R.color.grayDark,null));
//            }else {
//                tvSearch.setTextColor(getResources().getColor(R.color.grayDark));
//            }
        }

        listView = (ListView) findViewById(R.id.lvSearchBusiness);
        gson = new Gson();
        adapter = new BusinessItemAdapter(this,data);
        listView.setAdapter(adapter);
        loadData();

    }

    private void loadData() {

		String temp = areaId +"";
		String url = temp.endsWith("00") ? Constant.URL_BUSINESS_LIST_BY_CITY : Constant.URL_BUSINESS_LIST_BY_AREA;
		Log.d(TAG, "loadSimilarData: url "+ url);

        Log.d(TAG, "loadData: " + url + secondCategroy + "/" + pageId + "/" + areaId + "/"+0);
        VolleyRequestUtil.RequestGet(this,
                url + secondCategroy + "/" + pageId + "/" + areaId + "/"+0,
                Constant.TAG_BUSINESS_LIST_SEARCH,//商家列表搜索tag
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: "+ result);
                        BusinessStatus status = gson.fromJson(result,BusinessStatus.class);
                        data.clear();
                        if(status.getData() != null && status.getData().size() > 0){
                            data.addAll(status.getData());
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
    }
}
