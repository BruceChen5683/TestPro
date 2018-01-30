package cn.ws.sz.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.activity.BusinessDetailActivity;
import cn.ws.sz.activity.BusinessListActivity;
import cn.ws.sz.adater.BusinesssItem3Adapter;
import cn.ws.sz.adater.WsSimpleAdater;
import cn.ws.sz.adater.WsSimpleAdater2;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.bean.ClassifyBean;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DataHelper;
import cn.ws.sz.utils.WSApp;
import third.PullToRefreshView;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;


public class ClassifyFragment extends Fragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener{

    private final static String TAG = "ClassifyFragment";
    private ListView classifyFirstLV;
    private GridView classifySecondGV,classifySecondGVDetail;
    private List<ClassifyBean> secondData = new ArrayList<ClassifyBean>();
    private List<ClassifyBean> tmpList = new ArrayList<ClassifyBean>();

    private List<BusinessBean> classifySecondDetaildata = new ArrayList<BusinessBean>();

    private WsSimpleAdater firstAdapter;
    private WsSimpleAdater2 secondAdapter;
    private BusinesssItem3Adapter classifySecondDetailAdapter;

    private View rootView;

    private TextView tvTitle;
    private int firstCategroyId = 0;

    private LinearLayout ll_classify_second;
    private TextView tv_classify_second_back,tv_classify_second_title;

    private int secondCategroy = 0;
    private int pageId = 1;//页码  从1开始，0切勿使用
    private int areaId = 110101;//区域
    private boolean bLoadMore = false;
    private Gson gson;

    private PullToRefreshView pullToRefreshView;
    private boolean actionDone = true;//下拉刷新是否结束

    public ClassifyFragment() {
        // Required empty public constructor
    }


    public static ClassifyFragment newInstance(){
        return new ClassifyFragment();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        gson = new Gson();
        View view = inflater.inflate(R.layout.fragment_classify, container, false);
        firstCategroyId = WSApp.classifyId;

        Log.d(TAG, "onCreateView:firstCategroyId "+firstCategroyId);
        initView(view);
        rootView = view;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "onAttachFragment: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged: hidden "+hidden);
        if(!hidden){
            firstCategroyId = WSApp.classifyId;
            classifyFirstLV.setSelection(firstCategroyId);
            firstAdapter.setSelectedPosition(firstCategroyId);
            firstAdapter.notifyDataSetChanged();
            Log.d(TAG, "onHiddenChanged: 1");
            updateSecondData(firstCategroyId);
        }
    }

    private void initView(View view) {

        Log.d(TAG, "initView: ");
        tvTitle = (TextView) view.findViewById(R.id.title_value);
        tvTitle.setText("分类");

        classifyFirstLV = (ListView) view.findViewById(R.id.classify_first);
        firstAdapter = new WsSimpleAdater(getActivity(),getFirstData());
        classifyFirstLV.setAdapter(firstAdapter);

        classifyFirstLV.setSelection(firstCategroyId);
        firstAdapter.setSelectedPosition(firstCategroyId);
        firstAdapter.notifyDataSetChanged();


        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.classify_second_pull_refresh_view);
        pullToRefreshView.setOnHeaderRefreshListener(this);
        pullToRefreshView.setOnFooterRefreshListener(this);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        pullToRefreshView.onFooterRefreshComplete();

        classifySecondGV = (GridView) view.findViewById(R.id.classify_second);
        secondAdapter = new WsSimpleAdater2(getActivity(),secondData);
        classifySecondGV.setAdapter(secondAdapter);

        Log.d(TAG, "initView: 2");
        updateSecondData(firstCategroyId);

        classifyFirstLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                firstAdapter.setSelectedPosition(position);
                firstAdapter.notifyDataSetChanged();

                hideSecondGridView(false,-1);

                Log.d(TAG, "onItemClick: 3");
                updateSecondData(position);
            }
        });

        classifySecondGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                secondCategroy = secondAdapter.getItem(position).getId();
                Log.d(TAG, "onItemClick: secondCategroy "+secondCategroy);
                pageId = 1;
                bLoadMore = false;

                hideSecondGridView(true,position);
            }
        });

        ll_classify_second = (LinearLayout) view.findViewById(R.id.ll_classify_second);
        ll_classify_second.setVisibility(View.INVISIBLE);
        tv_classify_second_back = (TextView) ll_classify_second.findViewById(R.id.tv_classify_second_back);
        tv_classify_second_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSecondGridView(false,0);
            }
        });

        tv_classify_second_title = (TextView) ll_classify_second.findViewById(R.id.tv_classify_second_title);
        classifySecondGVDetail = (GridView) ll_classify_second.findViewById(R.id.classify_second_detail);

        classifySecondDetailAdapter = new BusinesssItem3Adapter(getActivity(),classifySecondDetaildata);
        classifySecondGVDetail.setAdapter(classifySecondDetailAdapter);
        classifySecondGVDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position "+position);
                if(classifySecondDetaildata.get(position) == null){
                    Log.e(TAG, "onItemClick: "+"eeee" );
                }
                Intent intent = new Intent();
                intent.putExtra(Constant.KEY_EXTRA_MERCHANT_ID,classifySecondDetaildata.get(position).getId());
                intent.setClass(getActivity(), BusinessDetailActivity.class);
                startActivity(intent);
            }
        });

    }

    public void updateSecondData(int id){
        tmpList =  DataHelper.getInstance().getSecondCategroyMap().get(DataHelper.getInstance().getFirstCategroyList().get(id).getId());
        secondData.clear();
        if(tmpList != null){
            secondData.addAll(tmpList);
        }
        secondAdapter.notifyDataSetChanged();
    }



    private List<String> getFirstData(){
        List<String> data = new ArrayList<String>();
            for (int i =0;i < DataHelper.getInstance().getFirstCategroyList().size();i++){
                data.add(DataHelper.getInstance().getFirstCategroyList().get(i).getName());
            }
        return data;

    }

    public void hideSecondGridView(boolean flag,int position){
        if(flag){
            if(classifySecondGV != null){
                classifySecondGV.setVisibility(View.INVISIBLE);
            }
            if(ll_classify_second != null){
                loadData(bLoadMore,secondCategroy,pageId,areaId);
                ll_classify_second.setVisibility(View.VISIBLE);
                if(tv_classify_second_title != null){
                    tv_classify_second_title.setText(secondData.get(position).getName());
                }
            }
        }else {
            if(classifySecondGV != null){
                classifySecondGV.setVisibility(View.VISIBLE);
            }
            if(ll_classify_second != null){
                ll_classify_second.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void loadData(final boolean loadMore,final int mSecondCategroy,final int mPageId,final int mAreaId) {

        Log.d(TAG, "loadData: "+Constant.URL_BUSINESS_LIST + mSecondCategroy + "/" + mPageId + "/" + mAreaId + "/"+0);
        VolleyRequestUtil.RequestGet(getActivity(),
                Constant.URL_BUSINESS_LIST + mSecondCategroy + "/" + mPageId + "/" + mAreaId + "/"+0,
                Constant.TAG_BUSINESS_LIST_2,//商家列表tag
                new VolleyListenerInterface(getActivity(),
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {


                        Log.d(TAG, "onMySuccess: "+result);

                        BusinessStatus status = gson.fromJson(result,BusinessStatus.class);
                        if(!loadMore){
                            classifySecondDetaildata.clear();
                        }
                        if(status.getData() != null && status.getData().size() > 0){
                            actionDone = true;
                            classifySecondDetaildata.addAll(status.getData());
                        }else{

                        }
                        classifySecondDetailAdapter.notifyDataSetChanged();
                        if(mPageId <= 1){
                            pullToRefreshView.onHeaderRefreshComplete("更新于:"+new Date().toLocaleString());
                        }else{
                            pullToRefreshView.onFooterRefreshComplete();
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: "+error.getMessage());
                    }
                },
                true);
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        Log.d(TAG, "onFooterRefresh: ");

        if(actionDone){
            pageId++;
            actionDone = false;
        }
        bLoadMore = true;
        loadData(bLoadMore,secondCategroy,pageId,areaId);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        Log.d(TAG, "onHeaderRefresh: ");
        pageId = 1;
        bLoadMore = false;
        loadData(bLoadMore,secondCategroy,pageId,areaId);
    }
}
