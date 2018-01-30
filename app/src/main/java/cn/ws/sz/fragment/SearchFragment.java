package cn.ws.sz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.activity.BusinessDetailActivity;
import cn.ws.sz.activity.SearchActivity;
import cn.ws.sz.adater.BusinessItemAdapter;
import cn.ws.sz.adater.BusinesssItem3Adapter;
import cn.ws.sz.adater.WsSimpleAdater;
import cn.ws.sz.adater.WsSimpleAdater2;
import cn.ws.sz.adater.WsSimpleAdater2_Search;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.bean.ClassifyBean;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DataHelper;
import cn.ws.sz.utils.WSApp;
import third.PullToRefreshView;
import third.searchview.ICallBack;
import third.searchview.IChangeLayout;
import third.searchview.ISearchItemCallBack;
import third.searchview.SearchView;
import third.searchview.bCallBack;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;


public class SearchFragment extends Fragment implements View.OnClickListener,PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener{
    private final static String TAG = SearchFragment.class.getSimpleName();
    private SearchView searchView;


    private BusinessItemAdapter adapter;
    private List<BusinessBean> data = new ArrayList<BusinessBean>();
    private ListView listView;

    private String searchValue = "";
    private int firstCategroy = 0;
    private int secondCategroy = 0;
    private int pageId = 0;//页码
    private int region = 110101;//区域
    private Gson gson;

    private int areaId = 110101;//区域
    private boolean bLoadMore = false;

    private PullToRefreshView pullToRefreshView;
    private boolean actionDone = true;//下拉刷新是否结束

    private RelativeLayout rlClassify,rlSort;//选择
    private TextView tvClassify,tvSort;

    private LinearLayout llClassify,llSort,ll_no_search;//显示
    private ListView lvSortChoice;


    private ListView classifyFirstLV;
    private GridView classifySecondGV,classifySecondGVDetail;
    private WsSimpleAdater firstAdapter;
    private WsSimpleAdater2_Search secondAdapter;
    private BusinesssItem3Adapter classifySecondDetailAdapter;

    private List<ClassifyBean> tmpList = new ArrayList<ClassifyBean>();
    private List<String> secondData = new ArrayList<String>();
    private List<BusinessBean> classifySecondDetaildata = new ArrayList<BusinessBean>();



    private final static int TYPE_CLASSIFY = 1;
    private final static int TYPE_SORT = 2;
    private final static int TYPE_SEARCH = 3;
    private final static int TYPE_SEARCH_DONE = 4;


    private String queryText = "";
    private Map<String,String> quertyMap = new HashMap<>();


    private LinearLayout ll_classify_second;
    private TextView tv_classify_second_back,tv_classify_second_title;




    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance(){
        return new SearchFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchView = (SearchView) view.findViewById(R.id.search_view);



        listView = (ListView) view.findViewById(R.id.lvSearchBusiness);
        gson = new Gson();
        adapter = new BusinessItemAdapter(getActivity(),data);
        listView.setAdapter(adapter);
//        loadData();

        rlClassify = (RelativeLayout) view.findViewById(R.id.rlClassify);
        rlClassify.setOnClickListener(this);
        rlSort = (RelativeLayout) view.findViewById(R.id.rlSort);
        rlSort.setOnClickListener(this);

        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                queryText = string;
                searchView.hideScrollView(true);
                loadData();
//                Intent intent = new Intent();
//                intent.putExtra("searchValue",string);
//                intent.setClass(getActivity(), SearchActivity.class);
//                startActivity(intent);
            }
        });

        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                Log.d(TAG, "BackAciton: ");
//                getActivity().finish();
            }
        });

        searchView.setSearchItemCallBack(new ISearchItemCallBack() {
            @Override
            public void SearchHistroy(String string) {

                searchView.hideScrollView(true);
                queryText = string;
                loadData();
            }
        });

        searchView.setChangeLayoutCallBack(new IChangeLayout() {
            @Override
            public void change() {
                changeLayout(TYPE_SEARCH);
            }
        });


        initRlSort(view);


        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.classify_second_pull_refresh_view);
        pullToRefreshView.setOnHeaderRefreshListener(this);
        pullToRefreshView.setOnFooterRefreshListener(this);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        pullToRefreshView.onFooterRefreshComplete();

        classifySecondGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                secondCategroy = 17;
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

    public void hideSecondGridView(boolean flag,int position){
        if(flag){
            if(classifySecondGV != null){
                classifySecondGV.setVisibility(View.INVISIBLE);
            }
            if(ll_classify_second != null){
                loadData(bLoadMore,secondCategroy,pageId,areaId);
                ll_classify_second.setVisibility(View.VISIBLE);
                if(tv_classify_second_title != null){
                    tv_classify_second_title.setText(secondData.get(position));
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
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
    }

    private void initRlSort(View view) {

        llClassify = (LinearLayout) view.findViewById(R.id.llClassify);
        llSort = (LinearLayout) view.findViewById(R.id.llSort);
        ll_no_search = (LinearLayout) view.findViewById(R.id.ll_no_search);

        classifyFirstLV = (ListView) view.findViewById(R.id.classify_first);
        firstAdapter = new WsSimpleAdater(getActivity(),getFirstData());
        classifyFirstLV.setAdapter(firstAdapter);

        tvClassify = (TextView) view.findViewById(R.id.tvClassify);
        tvSort = (TextView) view.findViewById(R.id.tvSort);

        lvSortChoice = (ListView) view.findViewById(R.id.lvSortChoice);

        List<Map<String,String>> sortChoices = new ArrayList<>();
        Map map=new HashMap<String, Object>();
        map.put("name", "智能排序");
        sortChoices.add(map);
        map=new HashMap<String, Object>();
        map.put("name", "距离最近");
        sortChoices.add(map);
        map=new HashMap<String, Object>();
        map.put("name", "人气最高");
        sortChoices.add(map);

        lvSortChoice.setAdapter(new SimpleAdapter(getActivity(),
                sortChoices,
                android.R.layout.simple_list_item_1,
                new String[] { "name" },
                new int[] { android.R.id.text1 }));

        lvSortChoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeLayout(TYPE_SORT);

            }
        });


        classifyFirstLV.setSelection(2);
        firstAdapter.setSelectedPosition(2);
        firstAdapter.notifyDataSetChanged();


        classifySecondGV = (GridView) view.findViewById(R.id.classify_second);
        secondAdapter = new WsSimpleAdater2_Search(getActivity(),secondData);
        classifySecondGV.setAdapter(secondAdapter);

        updateSecondData(firstCategroy);


        classifyFirstLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                firstAdapter.setSelectedPosition(position);
                firstAdapter.notifyDataSetChanged();
                updateSecondData(position);
            }
        });

    }

    private List<String> getFirstData() {
        List<String> data = new ArrayList<String>();
        data.add("所有分类");
        for (int i = 0; i < DataHelper.getInstance().getFirstCategroyList().size(); i++){
            data.add(DataHelper.getInstance().getFirstCategroyList().get(i).getName());
        }
        return data;
    }

    public void updateSecondData(int id){
        if(id == 0){
            return;
        }
        tmpList =  DataHelper.getInstance().getSecondCategroyMap().get(DataHelper.getInstance().getFirstCategroyList().get(id-1).getId());
        secondData.clear();
        if(tmpList != null){
            secondData.add("所有"+DataHelper.getInstance().getFirstCategroyList().get(id-1).getName());
            for (int i =0;i < tmpList.size();i++){
                secondData.add(tmpList.get(i).getName());
            }
        }
        secondAdapter.notifyDataSetChanged();
    }

    private void loadData() {
        quertyMap.put("region",String.valueOf(region));
        quertyMap.put("queryText",queryText);
            VolleyRequestUtil.RequestPost(getActivity(),
                    Constant.URL_QUERY_BUSINESS,
                    Constant.TAG_QUERY_BUSINESS,//商家列表搜索tag
                    quertyMap,
                    new VolleyListenerInterface(getActivity(),
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
                            changeLayout(TYPE_SEARCH_DONE);
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Log.d(TAG, "onMyError: ");
                        }
                    },
                    true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlClassify:
                changeLayout(TYPE_CLASSIFY);
                break;
            case R.id.rlSort:
                changeLayout(TYPE_SORT);
                break;
            default:
                break;
        }
    }

    private void changeLayout(int type){
        Log.d(TAG, "changeLayout: type "+type);
        if(type == TYPE_CLASSIFY){

            if(llClassify.getVisibility() == View.VISIBLE){
                //            ll_no_search.setVisibility(View.VISIBLE);
                tvClassify.setTextColor(getResources().getColor(R.color.black));
                tvSort.setTextColor(getResources().getColor(R.color.black));
                llClassify.setVisibility(View.GONE);
                llSort.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }else{
                //            ll_no_search.setVisibility(View.VISIBLE);
                tvClassify.setTextColor(getResources().getColor(R.color.text_red));
                tvSort.setTextColor(getResources().getColor(R.color.black));
                llClassify.setVisibility(View.VISIBLE);
                llSort.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            }

        }else if(type == TYPE_SORT){
            if(llSort.getVisibility() == View.VISIBLE){
                //            ll_no_search.setVisibility(View.VISIBLE);
                tvClassify.setTextColor(getResources().getColor(R.color.black));
                tvSort.setTextColor(getResources().getColor(R.color.black));
                llClassify.setVisibility(View.GONE);
                llSort.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }else{
                //            ll_no_search.setVisibility(View.VISIBLE);
                tvClassify.setTextColor(getResources().getColor(R.color.black));
                tvSort.setTextColor(getResources().getColor(R.color.text_red));
                llClassify.setVisibility(View.GONE);
                llSort.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }

        }else if(type == TYPE_SEARCH){
            searchView.hideScrollView(false);
            if(ll_no_search.getVisibility() != View.GONE){
                ll_no_search.setVisibility(View.GONE);
//                searchView.hideScrollView(false);
//                llClassify.setVisibility(View.GONE);
//                llSort.setVisibility(View.GONE);
//                listView.setVisibility(View.GONE);
            }
        }else if(type == TYPE_SEARCH_DONE){
			searchView.hideScrollView(true);
            ll_no_search.setVisibility(View.VISIBLE);
            llClassify.setVisibility(View.GONE);
            llSort.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
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
