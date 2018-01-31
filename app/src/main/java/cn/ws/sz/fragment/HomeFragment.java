package cn.ws.sz.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.activity.BannerActivity;
import cn.ws.sz.activity.BusinessDetailActivity;
import cn.ws.sz.activity.MainActivity;
import cn.ws.sz.activity.SplashActivity;
import cn.ws.sz.adater.BusinesssItem2Adapter;
import cn.ws.sz.bean.BannerBean;
import cn.ws.sz.bean.BannerStatus;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DataHelper;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import cn.ws.sz.view.MarqueeTextView;
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
//    private List<ImageView> viewsBanner = new ArrayList<>();
    private int bannerSize = 0;
    private List<BannerBean> bannerList = new ArrayList<>();
//    private int[] bannerImageViews = {R.drawable.banner,R.drawable.banner,R.drawable.banner};
    private ImageCycleViewListener imageCycleViewListener;//广告点击监听
    private MainActivity activity;

    private TextView tvArea = null;
    private RelativeLayout rlSearch = null;
    private LinearLayout food,supermarket,entertainment,life,wholesale,business,education,car;

    private GridView gridView;
    private BusinesssItem2Adapter hotAdapter;

	private MarqueeTextView tvNotice;

    private Gson gson = new Gson();
    private int region = 110101;//default
	private String city = "东城区";
	private List<BusinessBean> hotList = new ArrayList<>();
	private RelativeLayout rl_left_titlel;
	private RelativeLayout.LayoutParams lp;

	private StringBuilder adAll = new StringBuilder();
	private static final int UPDATE_AD_NOTICE = 1;


	private Handler adHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case UPDATE_AD_NOTICE:
					tvNotice.setText(adAll.toString());
					break;
				default:
					break;
			}
		}
	};

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(){
        return new HomeFragment();
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
    public void onResume() {
        super.onResume();
		if(TextUtils.isEmpty(DataHelper.getInstance().getCity()) || TextUtils.isEmpty(DataHelper.getInstance().getAreaId())){
			return;
		}
		city = DataHelper.getInstance().getCity();
		region = Integer.valueOf( DataHelper.getInstance().getAreaId());

		Log.d(TAG, "onResume: city "+city+"    region  "+region);

		int cityLength = city.length();
		lp = (RelativeLayout.LayoutParams) rl_left_titlel.getLayoutParams();
		if(cityLength > 6){
			cityLength = 6;
		}
		lp.width = cityLength * ((int) getResources().getDimension(R.dimen.dp_32));
		rl_left_titlel.setLayoutParams(lp);
		tvArea.setText(city);


		loadData();
	}


    private void initView(View view) {
        Log.d(TAG, "initView: ");
		rl_left_titlel = (RelativeLayout) view.findViewById(R.id.rl_left_title);

		bannerFragment = (BannerFragment) getChildFragmentManager().findFragmentById(R.id.fragment_banner_content);

        if (bannerFragment != null) {// null, Version
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

		tvNotice = (MarqueeTextView) view.findViewById(R.id.tvNotice);
		tvNotice.setMarqueeEnable(true);


        food.setOnClickListener(this);
        supermarket.setOnClickListener(this);
        entertainment.setOnClickListener(this);
        life.setOnClickListener(this);
        wholesale.setOnClickListener(this);
        business.setOnClickListener(this);
        education.setOnClickListener(this);
        car.setOnClickListener(this);

        gridView = (GridView) view.findViewById(R.id.gvHot);

		hotAdapter = new BusinesssItem2Adapter(getActivity(),hotList);
        gridView.setAdapter(hotAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constant.KEY_EXTRA_MERCHANT_ID,hotList.get(position).getId());
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
                Log.d(TAG, "onImageClick: "+postion);
                Intent intent = new Intent();
                intent.setClass(getActivity(), BannerActivity.class);
                intent.putExtra("redict_url",bannerList.get(postion-1).getRedictUrl());
                getActivity().startActivity(intent);

            }
        };

        bannerFragment.setData(views, imageCycleViewListener);
        // 设置轮播
        bannerFragment.setWheel(true);
        // 设置轮播时间，默认5000ms
        bannerFragment.setTime(3000);
        // 设置圆点指示图标组居中显示，默认靠右
        bannerFragment.setIndicatorCenter();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.tv_area:
                intent.setClass(getActivity(), PickCityActivity.class);
                getActivity().startActivityForResult(intent, Constant.CODE_CITY_PICERK_ACTIVITY);
                break;

            case R.id.rlSearch:
                activity.changeFragment(R.id.rlSearch);
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
        Log.d(TAG, "onActivityResult: resultCode "+resultCode);
        if(resultCode == RESULT_OK){
             if(requestCode == Constant.CODE_CITY_PICERK_ACTIVITY){
//                 Log.d(TAG, "onActivityResult: " + data.getStringExtra("city"));
//                 String city = data.getStringExtra("city");
//                 region = Integer.valueOf(data.getStringExtra("areaId"));
//                 Log.d(TAG, "onActivityResult: region"+region);
//                 if (!TextUtils.isEmpty(city)){
//                    tvArea.setText(data.getStringExtra("city"));
//                 }
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

                        //加载广告
                        if(bannerSize > 0){
                            views.add(ViewFactory.getImageView(getActivity(),bannerList.get(bannerSize-1).getBannerUrl()));
                            for (int i = 0;i < bannerSize;i++){
                                views.add(ViewFactory.getImageView(getActivity(),bannerList.get(i).getBannerUrl()));
                            }
                            views.add(ViewFactory.getImageView(getActivity(),bannerList.get(0).getBannerUrl()));

                            loadBannerFragment();
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: "+error.getMessage());
                    }
                },
                true);


		VolleyRequestUtil.RequestGet(getActivity(),
				Constant.URL_HOT + region,//广告区域id
				Constant.TAG_HOT,
				new VolleyListenerInterface(getActivity(),
						VolleyListenerInterface.mListener,
						VolleyListenerInterface.mErrorListener) {
					@Override
					public void onMySuccess(String result) {
						Log.d(TAG, "onMySuccess: " + result);
						BusinessStatus status = gson.fromJson(result,BusinessStatus.class);
						hotList.clear();
						if(status.getData() != null && status.getData().size() > 0){
							hotList.addAll(status.getData());
						}
						hotAdapter.notifyDataSetChanged();

						for (int i = 0;i <hotList.size();i++){
							if(hotList.get(i).getIsHot() == 1){
								if(!TextUtils.isEmpty(hotList.get(i).getAdWord())){
									adAll.append("商家"+ hotList.get(i).getName() + ":" + hotList.get(i).getAdWord() + "     ");
								}
							}
						}
						adHandler.sendEmptyMessage(UPDATE_AD_NOTICE);

					}

					@Override
					public void onMyError(VolleyError error) {
						Log.d(TAG, "onMyError: ");
					}
				},
				true);

    }

}
