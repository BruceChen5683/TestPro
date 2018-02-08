package third.citypicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.bean.AreaBean;
import cn.ws.sz.bean.CityBean;
import cn.ws.sz.bean.CityStatus;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.service.LocationService;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DataHelper;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import me.yokeyword.indexablerv.EntityWrapper;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.indexablerv.SimpleHeaderAdapter;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;


/**
 * 选择城市
 * Created by YoKey on 16/10/7.
 */
public class PickCityActivity extends AppCompatActivity implements SearchFragment.SearchResultClickListent{

    private final static String TAG = "PickCityActivity";
    private List<CityEntity> mDatas;
	private List<CityEntity> mHotCitys = new ArrayList<>();
    private SearchFragment mSearchFragment;
    private SearchView mSearchView;
    private FrameLayout mProgressBar;
    private SimpleHeaderAdapter mHotCityAdapter;

    private LocationService locationService;
    private ImageView ivBack;

    private List<CityEntity> gpsCity = iniyGPSCityDatas();
    private SimpleHeaderAdapter gpsHeaderAdapter;
	private CityAdapter adapter;
    private String areaCode = "110101";

	private IndexableLayout indexableLayout;
	private Gson gson = new Gson();

	private final static int GPS = 1;
	private final static int HOT_DATA = 2;
	private final static int NO_HOT_DATA = 3;


    private Handler gpsHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GPS:
                	if(msg.obj != null){
						gpsCity.get(0).setName(msg.obj.toString());
						gpsHeaderAdapter.notifyDataSetChanged();
					}
                    break;
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);
//        getSupportActionBar().setTitle("选择城市");

        mSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
		mSearchFragment.setSearchResultClickListent(this);
		indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
        mSearchView = (SearchView) findViewById(R.id.searchview);
        mProgressBar = (FrameLayout) findViewById(R.id.progress);
        ivBack = (ImageView) findViewById(R.id.pic_city_back);

//        indexableLayout.setLayoutManager(new LinearLayoutManager(this));
        indexableLayout.setLayoutManager(new GridLayoutManager(this, 2));

        // 多音字处理
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)));

        // 添加自定义多音字词典
        Pinyin.init(Pinyin.newConfig()
                .with(new PinyinMapDict() {
                    @Override
                    public Map<String, String[]> mapping() {
                        HashMap<String, String[]> map = new HashMap<String, String[]>();
                        map.put("重庆",  new String[]{"CHONG", "QING"});
                        return map;
                    }
                }));


        // 快速排序。  排序规则设置为：只按首字母  （默认全拼音排序）  效率很高，是默认的10倍左右。  按需开启～
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST);
        // 自定义排序规则
//        indexableLayout.setComparator(new Comparator<EntityWrapper<CityEntity>>() {
//            @Override
//            public int compare(EntityWrapper<CityEntity> lhs, EntityWrapper<CityEntity> rhs) {
//                return lhs.getPinyin().compareTo(rhs.getPinyin());
//            }
//        });

        // setAdapter
		adapter = new CityAdapter(this);
		indexableLayout.setAdapter(adapter);
        // set Datas
        mDatas = initDatas();
		getHotCity();
        // 添加 HeaderView DefaultHeaderAdapter接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
        // 如果想自定义布局,点击事件, 可传入 new IndexableHeaderAdapter
        // 热门城市

        // 定位
        gpsHeaderAdapter = new SimpleHeaderAdapter<>(adapter, "定", "当前城市", gpsCity);
        indexableLayout.addHeaderAdapter(gpsHeaderAdapter);

        // 显示真实索引
//        indexableLayout.showAllLetter(false);

        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

				if(gpsHandler != null){
					gpsHandler.removeMessages(GPS);
				}

				locationService.unregisterListener(mListener);
				locationService.stop();

                Intent mIntent = new Intent();
                mIntent.putExtra("city",gpsCity.get(0).getName() );
                mIntent.putExtra("areaId",areaCode);
                // 设置结果，并进行传送
                PickCityActivity.this.setResult(RESULT_OK, mIntent);
                PickCityActivity.this.finish();
            }
        });

        // 搜索Demo
        initSearch();


        adapter.setDatas(mDatas, new IndexableAdapter.IndexCallback<CityEntity>() {
            @Override
            public void onFinished(List<EntityWrapper<CityEntity>> datas) {
                // 数据处理完成后回调
                mSearchFragment.bindDatas(mDatas);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        // set Center OverlayView
        indexableLayout.setOverlayStyle_Center();

        // set Listener
        adapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<CityEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
				ToastUtil.showShort(PickCityActivity.this,entity.getName());
				if(gpsHandler != null){
					gpsHandler.removeMessages(GPS);
				}
                if(!gpsCity.get(0).getName().equals(entity.getName())){
                    gpsCity.get(0).setName(entity.getName());
					areaCode = entity.getId()+"";
                    gpsHeaderAdapter.notifyDataSetChanged();
					PickCityActivity.this.finish();
                }
            }
        });

        locationService = ((WSApp)getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.start();
    }

	@Override
	protected void onPause() {
		super.onPause();
        Log.d(TAG, "onPause: "+areaCode);
        DataHelper.getInstance().setCity(gpsCity.get(0).getName());
		DataHelper.getInstance().setAreaId(areaCode);
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener);
        locationService.stop();
    }

    private List<CityEntity> initDatas() {
        List<CityEntity> list = new ArrayList<>();
        List<AreaBean> cityStrings = new ArrayList<>();


        for (Integer key : WSApp.areasMap.keySet()){
            cityStrings.addAll(WSApp.areasMap.get(key));
        }

        List<AreaBean> cityToArea = new ArrayList<>();
        for (Integer key:WSApp.citys.keySet()){
			CityBean cityBean = WSApp.citys.get(key);
			Log.d(TAG, "initDatas: "+ cityBean.getCity());
			AreaBean areaBean = new AreaBean();
			areaBean.setId(cityBean.getId());
			areaBean.setArea(cityBean.getCity());
			areaBean.setCityid(cityBean.getId());
			areaBean.setIsNewRecord(cityBean.isIsNewRecord());
			cityToArea.add(areaBean);
		}
		cityStrings.addAll(cityToArea);

        for (AreaBean item : cityStrings) {
            CityEntity cityEntity = new CityEntity();
//            if(item.getArea().equals("市辖区")){
//                cityEntity.setName(WSApp.citys.get(item.getCityid()).getCity());
//            }else {
                cityEntity.setName(item.getArea());
//            }
            cityEntity.setId(item.getId());
            list.add(cityEntity);
        }
        return list;
    }

    private List<CityEntity> iniyGPSCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("定位中..."));
        return list;
    }

    private void initSearch() {
        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "onQueryTextSubmit: "+query);
				return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
				Log.d(TAG, "onQueryTextChange: "+newText);
				if (newText.trim().length() > 0) {
                    if (mSearchFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().show(mSearchFragment).commit();
                    }
                } else {
                    if (!mSearchFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
                    }
                }
                mSearchFragment.bindQueryText(newText);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mSearchFragment.isHidden()) {
            // 隐藏 搜索
            mSearchView.setQuery(null, false);
            getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
            return;
        }
        super.onBackPressed();
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                areaCode = location.getAdCode();
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
                Message msg = new Message();
                msg.what = 1;
                msg.obj = location.getDistrict();
                gpsHandler.sendMessage(msg);
//
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append(poi.getName() + ";");
//                    }
//                }
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    sb.append("\ndescribe : ");
//                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//                }
            }
        }

    };

	@Override
	public void setSearhResult(CityEntity entity) {
		if(gpsHandler != null){
			gpsHandler.removeMessages(GPS);
		}
		areaCode = entity.getId()+"";
		gpsCity.get(0).setName(entity.getName());
		gpsHeaderAdapter.notifyDataSetChanged();
		this.finish();
	}

	private void getHotCity(){
		VolleyRequestUtil.RequestGet(this,
				Constant.URL_HOT_CITY,
				Constant.TAG_HOT_CITY,
				new VolleyListenerInterface(this,
						VolleyListenerInterface.mListener,
						VolleyListenerInterface.mErrorListener) {
					@Override
					public void onMySuccess(String result) {
						Log.d(TAG, "onMySuccess: "+result);
						CityStatus status = gson.fromJson(result,CityStatus.class);
							if(status.getData() != null && status.getData().size() > 0){{
								for (int i = 0;i < status.getData().size();i++){
									CityEntity entity = new CityEntity();
									entity.setName(status.getData().get(i).getCity());
									entity.setId(status.getData().get(i).getId());
									if(!mDatas.contains(entity)){
										mDatas.add(entity);
									}
									mHotCitys.add(entity);
								}
								mHotCityAdapter = new SimpleHeaderAdapter<>(adapter, "热", "热门城市", mHotCitys);
								indexableLayout.addHeaderAdapter(mHotCityAdapter);
							}
						}
					}

					@Override
					public void onMyError(VolleyError error) {
						Log.d(TAG, "onMyError: "+error.getMessage());
					}
				},
				true);


	}
}
