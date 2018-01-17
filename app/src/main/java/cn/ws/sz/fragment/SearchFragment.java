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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.activity.SearchActivity;
import cn.ws.sz.adater.BusinessItemAdapter;
import cn.ws.sz.adater.WsSimpleAdater;
import cn.ws.sz.adater.WsSimpleAdater2;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.bean.ClassifyBean;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.WSApp;
import third.searchview.ICallBack;
import third.searchview.IChangeLayout;
import third.searchview.ISearchItemCallBack;
import third.searchview.SearchView;
import third.searchview.bCallBack;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;


public class SearchFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = SearchFragment.class.getSimpleName();
    private SearchView searchView;


    private BusinessItemAdapter adapter;
    private List<BusinessBean> data = new ArrayList<BusinessBean>();
    private ListView listView;

    private String searchValue = "";
    private int firstCategroy = 0;
    private int secondCategroy = 0;
    private int pageId = 0;//页码
    private int areaId = 110101;//区域
    private Gson gson;

    private RelativeLayout rlClassify,rlSort;//选择
    private TextView tvClassify,tvSort;

    private LinearLayout llClassify,llSort,ll_no_search;//显示
    private ListView lvSortChoice;


    private ListView classifyFirstLV;
    private GridView classifySecondGV;
    private WsSimpleAdater firstAdapter;
    private WsSimpleAdater2 secondAdapter;
    private List<ClassifyBean> tmpList = new ArrayList<ClassifyBean>();
    private List<String> secondData = new ArrayList<String>();



    private final static int TYPE_CLASSIFY = 1;
    private final static int TYPE_SORT = 2;
    private final static int TYPE_SEARCH = 3;
    private final static int TYPE_SEARCH_DONE = 4;




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
                Log.d(TAG, "SearchAciton: "+string);
                searchView.hideScrollView(true);
                loadData();
                changeLayout(TYPE_SEARCH_DONE);

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

                Log.d(TAG, "SearchHistroy: "+string);
//                Intent intent = new Intent();
//                intent.putExtra("searchValue",string);
//                intent.setClass(getActivity(), SearchActivity.class);
//                startActivity(intent);

                loadData();
                changeLayout(TYPE_SEARCH_DONE);

            }
        });

        searchView.setChangeLayoutCallBack(new IChangeLayout() {
            @Override
            public void change() {
                changeLayout(TYPE_SEARCH);
            }
        });


        initRlSort(view);
        
        
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


        classifyFirstLV.setSelection(2);
        firstAdapter.setSelectedPosition(2);
        firstAdapter.notifyDataSetChanged();


        classifySecondGV = (GridView) view.findViewById(R.id.classify_second);
        secondAdapter = new WsSimpleAdater2(getActivity(),secondData);
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
        for (int i = 0; i < WSApp.firstCategroyList.size(); i++){
            data.add(WSApp.firstCategroyList.get(i).getName());
        }
        return data;
    }

    public void updateSecondData(int id){
        if(id == 0){
            return;
        }
        tmpList = WSApp.secondCategroyMap.get(WSApp.firstCategroyList.get(id-1).getId());
        secondData.clear();
        if(tmpList != null){
            secondData.add("所有"+WSApp.firstCategroyList.get(id-1).getName());
            for (int i =0;i < tmpList.size();i++){
                secondData.add(tmpList.get(i).getName());
            }
        }
        secondAdapter.notifyDataSetChanged();
    }

    private void loadData() {
            Log.d(TAG, "loadData: " + Constant.URL_BUSINESS_LIST + secondCategroy + "/" + pageId + "/" + areaId);
            VolleyRequestUtil.RequestGet(getActivity(),
                    Constant.URL_BUSINESS_LIST + secondCategroy + "/" + pageId + "/" + areaId,
                    Constant.TAG_BUSINESS_LIST_SEARCH,//商家列表搜索tag
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
        if(type == TYPE_CLASSIFY){
//            ll_no_search.setVisibility(View.VISIBLE);
            tvClassify.setTextColor(getResources().getColor(R.color.text_red));
            tvSort.setTextColor(getResources().getColor(R.color.black));
            llClassify.setVisibility(View.VISIBLE);
            llSort.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }else if(type == TYPE_SORT){
//            ll_no_search.setVisibility(View.VISIBLE);
            tvClassify.setTextColor(getResources().getColor(R.color.black));
            tvSort.setTextColor(getResources().getColor(R.color.text_red));
            llClassify.setVisibility(View.GONE);
            llSort.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else if(type == TYPE_SEARCH){
            if(ll_no_search.getVisibility() != View.GONE){
                ll_no_search.setVisibility(View.GONE);
                searchView.hideScrollView(false);
//                llClassify.setVisibility(View.GONE);
//                llSort.setVisibility(View.GONE);
//                listView.setVisibility(View.GONE);
            }
        }else if(type == TYPE_SEARCH_DONE){
            ll_no_search.setVisibility(View.VISIBLE);
        }
    }
}
