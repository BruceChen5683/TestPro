package cn.ws.sz.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.WSApp;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

public class BusinessListActivity extends AppCompatActivity {

    private TextView textView;
    private static final String TAG = "BusinessListActivity";
    private BusinessItemAdapter adapter;
    private List<BusinessBean> data = new ArrayList<BusinessBean>();
    private ListView listView;

    private int firstCategroy = 0;
    private int secondCategroy = 1;
    private int pageId = 0;//页码
    private int areaId = 110101;//区域
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_business_list);

        gson = new Gson();

        listView = (ListView) findViewById(R.id.lvBusiness);
        Bundle bundle = getIntent().getExtras();

        firstCategroy = bundle.getInt("firstCategroy");
        secondCategroy = bundle.getInt("secondCategroy");

        textView = (TextView) findViewById(R.id.title_value);
        textView.setText("商户详情");

        adapter = new BusinessItemAdapter(this,data);
        listView.setAdapter(adapter);


        loadData();



    }

    private void loadData() {

        Log.d(TAG, "loadData: "+Constant.URL_BUSINESS_LIST + secondCategroy + "/" + pageId + "/" + areaId);
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_BUSINESS_LIST + secondCategroy + "/" + pageId + "/" + areaId,
                Constant.TAG_BUSINESS_LIST,//商家列表tag
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
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
