package cn.ws.sz.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.activity.BusinessDetailActivity;
import cn.ws.sz.activity.MainActivity;
import cn.ws.sz.activity.SplashActivity;
import cn.ws.sz.adater.BusinesssItem2Adapter;
import cn.ws.sz.bean.BannerBean;
import cn.ws.sz.bean.BannerStatus;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import cn.ws.sz.view.ViewFactory;
import cn.ws.sz.fragment.BannerFragment.ImageCycleViewListener;
import third.citypicker.PickCityActivity;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements View.OnClickListener{

    private final static String TAG = "HomeFragment";
    private  BannerFragment bannerFragment;
    private List<ImageView> views = new ArrayList<ImageView>();
    private List<ImageView> viewsBanner = new ArrayList<>();
    private int bannerSize = 0;
    private List<BannerBean> bannerList = new ArrayList<BannerBean>();
//    private int[] bannerImageViews = {R.drawable.banner,R.drawable.banner,R.drawable.banner};
    private ImageCycleViewListener imageCycleViewListener;
    private MainActivity activity;

    private TextView tvArea = null;
    private RelativeLayout rlSearch = null;
    private LinearLayout food,supermarket,entertainment,life,wholesale,business,education,car;

//    private LinearLayout llHot1;//热门商家

    private GridView gridView;
    private BusinesssItem2Adapter adapter;

    private Gson gson = new Gson();
    private int region = 110101;//default



    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: "+ getActivity());
        activity = (MainActivity) getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);

        loadData();

        return view;
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
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private void initView(View view) {


        Log.d(TAG, "initView: ");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Log.d(TAG, "initView: 1");
            bannerFragment = (BannerFragment) getChildFragmentManager().findFragmentById(R.id.fragment_banner_content);
//        } else {
//            Log.d(TAG, "initView: 2");
//            bannerFragment = (BannerFragment) getChildFragmentManager().findFragmentById(R.id.fragment_banner_content);
//        }

        if (bannerFragment != null) {// null, Version

            Log.d(TAG, "initView: 3");
            bannerFragment.setHeight();
//            loadBannerFragment();
        }

        tvArea = (TextView) view.findViewById(R.id.tv_area);
        rlSearch = (RelativeLayout) view.findViewById(R.id.rlSearch);
        tvArea.setOnClickListener(this);
        rlSearch.setOnClickListener(this);

        food = (LinearLayout) view.findViewById(R.id.food);
        supermarket = (LinearLayout) view.findViewById(R.id.supermarket);
        entertainment = (LinearLayout) view.findViewById(R.id.entertainment);
        life = (LinearLayout) view.findViewById(R.id.life);
        wholesale = (LinearLayout) view.findViewById(R.id.wholesale);
        business = (LinearLayout) view.findViewById(R.id.business);
        education = (LinearLayout) view.findViewById(R.id.education);
        car = (LinearLayout) view.findViewById(R.id.car);


        food.setOnClickListener(this);
        supermarket.setOnClickListener(this);
        entertainment.setOnClickListener(this);
        life.setOnClickListener(this);
        wholesale.setOnClickListener(this);
        business.setOnClickListener(this);
        education.setOnClickListener(this);
        car.setOnClickListener(this);

        gridView = (GridView) view.findViewById(R.id.gvHot);

        List<String> data = new ArrayList<String>();
        data.add("北京天安门");
        data.add("上海迪斯尼");
        data.add("51区");
        data.add("超长地址-----------------------------------------------------------------");
        data.add("北京天安门");
        adapter = new BusinesssItem2Adapter(getActivity(),data);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BusinessDetailActivity.class);
                startActivity(intent);
            }
        });


//        llHot1 = (LinearLayout) view.findViewById(R.id.hot1);
//        llHot1.setOnClickListener(this);

    }

    private void loadBannerFragment() {
//        views.add(ViewFactory.getImageView(getActivity(), bannerImageViews[2]));
//        for (int i = 0; i < 3; i++) {
//            views.add(ViewFactory.getImageView(getActivity(),
//                    bannerImageViews[i]));
//        }
//        // 将第一个ImageView添加进来
//        views.add(ViewFactory.getImageView(getActivity(), bannerImageViews[0]));
        // 设置循环，在调用setData方法前调用
        bannerFragment.setCycle(true);
        // 在加载数据前设置是否循环
        imageCycleViewListener = new ImageCycleViewListener() {

            @Override
            public void onImageClick(int postion, View imageView) {

            }
        };


        Log.d(TAG, "loadBannerFragment: "+views.size());
        for (int i = 0; i < views.size();i++){
            Log.d(TAG, "loadBannerFragment: "+views.get(i));
        }

        bannerFragment.setData(views, imageCycleViewListener);
        // 设置轮播
        bannerFragment.setWheel(true);
        // 设置轮播时间，默认5000ms
        bannerFragment.setTime(3000);
        // 设置圆点指示图标组居中显示，默认靠右
        bannerFragment.setIndicatorCenter();
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.tv_area:
                intent.setClass(getActivity(), PickCityActivity.class);
                startActivityForResult(intent, Constant.CODE_CITY_PICERK_ACTIVITY);
                break;

            case R.id.rlSearch:
                activity.changeFragment(R.id.tv_search);
                break;
            case R.id.food:
                WSApp.classifyId = 0;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.supermarket:
                WSApp.classifyId = 1;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.entertainment:
                WSApp.classifyId = 2;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.life:
                WSApp.classifyId = 3;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.wholesale:
                WSApp.classifyId = 4;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.business:
                WSApp.classifyId = 5;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.education:
                WSApp.classifyId = 6;
                activity.changeFragment(R.id.tv_classify);
                break;
            case R.id.car:
                WSApp.classifyId = 7;
                activity.changeFragment(R.id.tv_classify);
                break;

//            case R.id.hot1:
//                intent.setClass(getActivity(), BusinessDetailActivity.class);
//                startActivity(intent);
//                break;

            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if(resultCode == RESULT_OK){
             if(requestCode == Constant.CODE_CITY_PICERK_ACTIVITY){
                 Log.d(TAG, "onActivityResult: " + data.getStringExtra("city"));
                 String city = data.getStringExtra("city");
                 if (!TextUtils.isEmpty(city)){
                    tvArea.setText(data.getStringExtra("city"));
                 }
             }
        }
    }

    private void loadData(){
        VolleyRequestUtil.RequestGet(getActivity(),
                Constant.URL_AD + region,//广告区域id
                Constant.TAG_AD,
                new VolleyListenerInterface(getActivity(),
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        BannerStatus status = gson.fromJson(result,BannerStatus.class);
                        bannerList.clear();
                        if(status.getData() != null && status.getData().size() > 0){
                            bannerList.addAll(status.getData());
                        }
                        bannerSize = bannerList.size();

                        for (int i = 0;i < bannerSize;i++){
                            ImageView temp = new ImageView(getActivity());
                            viewsBanner.add(temp);
                            CommonUtils.setImageView(bannerList.get(i).getBannerUrl(),viewsBanner.get(i));
                        }

                        views.add(viewsBanner.get(bannerSize-1));
                        for (int i = 0;i < bannerSize;i++){
                            views.add(viewsBanner.get(i));
                        }
                        views.add(viewsBanner.get(0));

                        loadBannerFragment();

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
    }
}
