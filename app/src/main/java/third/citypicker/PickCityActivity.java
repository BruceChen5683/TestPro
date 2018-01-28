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

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.bean.AreaBean;
import cn.ws.sz.service.LocationService;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import me.yokeyword.indexablerv.EntityWrapper;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.indexablerv.SimpleHeaderAdapter;


/**
 * 选择城市
 * Created by YoKey on 16/10/7.
 */
public class PickCityActivity extends AppCompatActivity {

    private final static String TAG = "PickCityActivity";
    private List<CityEntity> mDatas;
    private SearchFragment mSearchFragment;
//    private SearchView mSearchView;
    private FrameLayout mProgressBar;
    private SimpleHeaderAdapter mHotCityAdapter;


    private ImageView ivBack;

    private List<CityEntity> gpsCity = iniyGPSCityDatas();
    private SimpleHeaderAdapter gpsHeaderAdapter;


    private Handler gpsHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    gpsCity.get(0).setName(msg.obj.toString());
                    gpsHeaderAdapter.notifyDataSetChanged();
                    break;
                default:
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
        IndexableLayout indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
//        mSearchView = (SearchView) findViewById(R.id.searchview);
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
        CityAdapter adapter = new CityAdapter(this);
        indexableLayout.setAdapter(adapter);
        // set Datas
        mDatas = initDatas();

        // 添加 HeaderView DefaultHeaderAdapter接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
        // 如果想自定义布局,点击事件, 可传入 new IndexableHeaderAdapter

        mHotCityAdapter = new SimpleHeaderAdapter<>(adapter, "热", "热门城市", iniyHotCityDatas());
        // 热门城市
        indexableLayout.addHeaderAdapter(mHotCityAdapter);


        // 定位

        gpsHeaderAdapter = new SimpleHeaderAdapter<>(adapter, "定", "当前城市", gpsCity);
        indexableLayout.addHeaderAdapter(gpsHeaderAdapter);

        // 显示真实索引
//        indexableLayout.showAllLetter(false);

        // 模拟定位
//        indexableLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gpsCity.get(0).setName("芜湖市");
//                gpsHeaderAdapter.notifyDataSetChanged();
//            }
//        }, 500);

        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra("city",gpsCity.get(0).getName() );
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
                if (originalPosition >= 0) {
                    ToastUtil.showShort(PickCityActivity.this, "选中:" + entity.getName() + "  当前位置:" + currentPosition + "  原始所在数组位置:" + originalPosition);
                } else {
                    ToastUtil.showShort(PickCityActivity.this, "选中Header:" + entity.getName() + "  当前位置:" + currentPosition);
                }
                if(!gpsCity.get(0).getName().equals(entity.getName())){
                    gpsCity.get(0).setName(entity.getName());
                    gpsHeaderAdapter.notifyDataSetChanged();
                }

            }
        });

        adapter.setOnItemTitleClickListener(new IndexableAdapter.OnItemTitleClickListener() {
            @Override
            public void onItemClick(View v, int currentPosition, String indexTitle) {

                ToastUtil.showShort(PickCityActivity.this, "选中:" + indexTitle + "  当前位置:" + currentPosition);

            }
        });



//        locationService = ((WSApp)getApplication()).locationService;
//        locationService.registerListener(mListener);


//        startGetLocation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "onDestroy: ");


//        locationService.unregisterListener(mListener);

        Intent intent;
        intent = new Intent();
        intent.putExtra("city", gpsCity.get(0).toString());
        setResult(RESULT_OK, intent);
        
        super.onDestroy();

    }

    // 更新数据点击事件
//    public void update(View view) {
//        List<CityEntity> list = new ArrayList<>();
//        list.add(new CityEntity("杭州市"));
//        list.add(new CityEntity("北京市"));
//        list.add(new CityEntity("上海市"));
//        list.add(new CityEntity("广州市"));
//        mHotCityAdapter.addDatas(list);
//        Toast.makeText(this, "更新数据", Toast.LENGTH_SHORT).show();
//    }

    private List<CityEntity> initDatas() {
        List<CityEntity> list = new ArrayList<>();
        List<AreaBean> cityStrings = new ArrayList<>();


        for (Integer key : WSApp.areasMap.keySet()){
            cityStrings.addAll(WSApp.areasMap.get(key));
        }

        for (AreaBean item : cityStrings) {
            CityEntity cityEntity = new CityEntity();
            if(item.getArea().equals("市辖区")){
                cityEntity.setName(WSApp.citys.get(item.getCityid()).getCity());
            }else {
                cityEntity.setName(item.getArea());
            }
            cityEntity.setId(item.getId());
            list.add(cityEntity);
        }
        return list;
    }

    private List<CityEntity> iniyHotCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("杭州市"));
        list.add(new CityEntity("北京市"));
        list.add(new CityEntity("上海市"));
        list.add(new CityEntity("广州市"));
        return list;
    }

    private List<CityEntity> iniyGPSCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("定位中..."));
        return list;
    }

    private void initSearch() {
        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();

//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.trim().length() > 0) {
//                    if (mSearchFragment.isHidden()) {
//                        getSupportFragmentManager().beginTransaction().show(mSearchFragment).commit();
//                    }
//                } else {
//                    if (!mSearchFragment.isHidden()) {
//                        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
//                    }
//                }
//
//                mSearchFragment.bindQueryText(newText);
//                return false;
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (!mSearchFragment.isHidden()) {
            // 隐藏 搜索
//            mSearchView.setQuery(null, false);
            getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
            return;
        }
        super.onBackPressed();
    }

}
