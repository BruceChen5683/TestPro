package gps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.LinkedList;

import cn.ws.sz.R;
import cn.ws.sz.service.LocationService;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;

import static cn.ws.sz.utils.Constant.DISPLAY_GPS;

/***
 * 定位滤波demo，实际定位场景中，可能会存在很多的位置抖动，此示例展示了一种对定位结果进行的平滑优化处理
 * 实际测试下，该平滑策略在市区步行场景下，有明显平滑效果，有效减少了部分抖动，开放算法逻辑，希望能够对开发者提供帮助
 * 注意：该示例场景仅用于对定位结果优化处理的演示，里边相关的策略或算法并不一定适用于您的使用场景，请注意！！！
 * 
 * @author baidu
 * 
 */
public class LocationFilter extends AppCompatActivity {
	private static final String TAG = LocationFilter.class.getSimpleName();
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private Button sure,reset;
	private LocationService locService;
	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果

	private LatLng mLatLng;
	private int mode;

	private GeoCoder mSearch;
	private final static int GEO_SUCCESS = 1;
	private final static int GEO_FAILED = 2;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case GEO_SUCCESS:
					// 构建Marker图标
					LatLng tLatLng = (LatLng)msg.obj;
					BitmapDescriptor bitmap = null;
					bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
					// 构建MarkerOption，用于在地图上添加Marker
					OverlayOptions option = new MarkerOptions().position(tLatLng).icon(bitmap);
					// 在地图上添加Marker，并显示
					mBaiduMap.clear();
					mBaiduMap.addOverlay(option);
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(tLatLng));
					break;
				case GEO_FAILED:
					break;
				default:
					break;
			}

		}
	};


	private OnGetGeoCoderResultListener geoListener = new OnGetGeoCoderResultListener() {
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
			if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
				//没有检索到结果
				Log.d(TAG, "onGetGeoCodeResult: null");
				ToastUtil.showShort(LocationFilter.this,"地理位置编码结果异常");
				return;
			}
			if(geoCodeResult.getLocation() != null){
				Message msg = new Message();
				msg.what = GEO_SUCCESS;
				msg.obj = geoCodeResult.getLocation();
				mHandler.sendMessage(msg);
			}else {
				ToastUtil.showShort(LocationFilter.this,"地理位置编码结果坐标异常");
			}

			//获取地理编码结果
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		mode = bundle.getInt("mode");

		Log.d(TAG, "onCreate: ");
		setContentView(R.layout.locationfilter);
		mMapView = (MapView) findViewById(R.id.bmapView);
		sure = (Button) findViewById(R.id.sure);
		reset = (Button) findViewById(R.id.reset);

		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
		mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				Log.d(TAG, "onMapClick: "+latLng);
				mLatLng = new LatLng(latLng.latitude,latLng.longitude);
				LatLng point = new LatLng(latLng.latitude, latLng.longitude);
				Log.d(TAG, "handleMessage: "+point);
				// 构建Marker图标
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
				// 构建MarkerOption，用于在地图上添加Marker
				OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
				// 在地图上添加Marker，并显示
				mBaiduMap.clear();
				mBaiduMap.addOverlay(option);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

			}

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				Log.d(TAG, "onMapPoiClick: "+mapPoi.getPosition());
				return false;
			}
		});
		locService = ((WSApp) getApplication()).locationService;
		LocationClientOption mOption = locService.getDefaultLocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
		mOption.setCoorType("bd09ll");
		locService.setLocationOption(mOption);

		if(mode == DISPLAY_GPS){
//			mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));

			sure.setVisibility(View.GONE);
			reset.setVisibility(View.GONE);
			double tLat,tLong;
			LatLng point = new LatLng(39.87601941962116,116.3946533203125);//默认北京
			if(bundle.get("latitude") != null){//优先坐标
				tLat = Double.valueOf(bundle.get("latitude").toString());
				tLong = Double.valueOf(bundle.get("longitude").toString());
				Log.d(TAG, "onCreate: tLat "+tLat);
				Log.d(TAG, "onCreate: tLong "+tLong);
				point = new LatLng(tLat,tLong);
			}else {//其次地址
				Log.d(TAG, "onCreate: "+bundle.get("address").toString());
				if(bundle.get("address") != null){
					mSearch = GeoCoder.newInstance();
					mSearch.setOnGetGeoCodeResultListener(geoListener);
					mSearch.geocode(new GeoCodeOption()
							.city(bundle.get("city").toString())
					.address(bundle.get("address").toString()));
					mSearch.destroy();
				}
			}
			BitmapDescriptor bitmap = null;
			bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
			// 构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
			// 在地图上添加Marker，并显示
			mBaiduMap.clear();
			mBaiduMap.addOverlay(option);
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
		}else{
			locService.registerListener(listener);
			locService.start();
		}

	}

	/***
	 * 定位结果回调，在此方法中处理定位结果
	 */
	BDAbstractLocationListener listener = new BDAbstractLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d(TAG, "onReceiveLocation: "+location.getLatitude()+"----------"+location.getLongitude());
			mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
			if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
				Message locMsg = locHander.obtainMessage();
				Bundle locData;
				locData = Algorithm(location);
				if (locData != null) {
					locData.putParcelable("loc", location);
					locMsg.setData(locData);
					locHander.sendMessage(locMsg);
				}
			}
		}

	};

	/***
	 * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
	 * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
	 * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
	 *
	 * @param location
	 * @return Bundle
	 */
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntity temp = new LocationEntity();
			temp.location = location;
			temp.time = System.currentTimeMillis();
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
						locationList.get(i).location.getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
//				double distance = DistanceUtil.getDistance(lastPoint, curPoint);
//				curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
				score += curSpeed * Utils.EARTH_WEIGHT[i];
			}
			if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
				location.setLongitude(
						(locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
								/ 2);
				location.setLatitude(
						(locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
								/ 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			LocationEntity newLocation = new LocationEntity();
			newLocation.location = location;
			newLocation.time = System.currentTimeMillis();
			locationList.add(newLocation);

		}
		return locData;
	}

	/***
	 * 接收定位结果消息，并显示在地图上
	 */
	private Handler locHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				BDLocation location = msg.getData().getParcelable("loc");
				if (location != null) {
					LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
					Log.d(TAG, "handleMessage: "+point);
					// 构建Marker图标
					BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
					// 构建MarkerOption，用于在地图上添加Marker
					OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
					// 在地图上添加Marker，并显示
					mBaiduMap.clear();
					mBaiduMap.addOverlay(option);
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		locService.unregisterListener(listener);
		locService.stop();
		mMapView.onDestroy();
//		if(mSearch != null){
//			mSearch.destroy();
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				if(mLatLng == null){
					LocationFilter.this.finish();
					return;
				}
				mIntent.putExtra("latitude",mLatLng.latitude+"" );
				mIntent.putExtra("longitude",mLatLng.longitude+"" );
				// 设置结果，并进行传送
				LocationFilter.this.setResult(RESULT_OK, mIntent);

				LocationFilter.this.finish();
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locService.start();
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();

	}

	/**
	 * 封装定位结果和时间的实体类
	 * 
	 * @author baidu
	 *
	 */
	class LocationEntity {
		BDLocation location;
		long time;
	}
}
