package third.citypicker;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.utils.Constant;

/**
 * 省市区解析辅助类
 * 作者：liji on 2017/11/4 10:09
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public class CityParseHelper {
    
    /**
     * 省份数据
     */
    private ArrayList<TProvinceBean> mProvinceBeanArrayList = new ArrayList<>();
    
    /**
     * 城市数据
     */
    private ArrayList<ArrayList<TCityBean>> mCityBeanArrayList;
    
    /**
     * 地区数据
     */
    private ArrayList<ArrayList<ArrayList<TDistrictBean>>> mDistrictBeanArrayList;
    
    private TProvinceBean[] mProvinceBeenArray;
    
    private TProvinceBean mProvinceBean;
    
    private TCityBean mCityBean;
    
    private TDistrictBean mDistrictBean;
    
    //    private CityConfig config;
    
    /**
     * key - 省 value - 市
     */
    private Map<String, TCityBean[]> mPro_CityMap = new HashMap<String, TCityBean[]>();
    
    /**
     * key - 市 values - 区
     */
    private Map<String, TDistrictBean[]> mCity_DisMap = new HashMap<String, TDistrictBean[]>();
    
    /**
     * key - 区 values - 邮编
     */
    private Map<String, TDistrictBean> mDisMap = new HashMap<String, TDistrictBean>();
    
    public ArrayList<TProvinceBean> getProvinceBeanArrayList() {
        return mProvinceBeanArrayList;
    }
    
    public void setProvinceBeanArrayList(ArrayList<TProvinceBean> provinceBeanArrayList) {
        mProvinceBeanArrayList = provinceBeanArrayList;
    }
    
    public ArrayList<ArrayList<TCityBean>> getCityBeanArrayList() {
        return mCityBeanArrayList;
    }
    
    public void setCityBeanArrayList(ArrayList<ArrayList<TCityBean>> cityBeanArrayList) {
        mCityBeanArrayList = cityBeanArrayList;
    }
    
    public ArrayList<ArrayList<ArrayList<TDistrictBean>>> getDistrictBeanArrayList() {
        return mDistrictBeanArrayList;
    }
    
    public void setDistrictBeanArrayList(ArrayList<ArrayList<ArrayList<TDistrictBean>>> districtBeanArrayList) {
        mDistrictBeanArrayList = districtBeanArrayList;
    }
    
    public TProvinceBean[] getProvinceBeenArray() {
        return mProvinceBeenArray;
    }
    
    public void setProvinceBeenArray(TProvinceBean[] provinceBeenArray) {
        mProvinceBeenArray = provinceBeenArray;
    }
    
    public TProvinceBean getProvinceBean() {
        return mProvinceBean;
    }
    
    public void setProvinceBean(TProvinceBean provinceBean) {
        mProvinceBean = provinceBean;
    }
    
    public TCityBean getCityBean() {
        return mCityBean;
    }
    
    public void setCityBean(TCityBean cityBean) {
        mCityBean = cityBean;
    }
    
    public TDistrictBean getDistrictBean() {
        return mDistrictBean;
    }
    
    public void setDistrictBean(TDistrictBean districtBean) {
        mDistrictBean = districtBean;
    }
    
    public Map<String, TCityBean[]> getPro_CityMap() {
        return mPro_CityMap;
    }
    
    public void setPro_CityMap(Map<String, TCityBean[]> pro_CityMap) {
        mPro_CityMap = pro_CityMap;
    }
    
    public Map<String, TDistrictBean[]> getCity_DisMap() {
        return mCity_DisMap;
    }
    
    public void setCity_DisMap(Map<String, TDistrictBean[]> city_DisMap) {
        mCity_DisMap = city_DisMap;
    }
    
    public Map<String, TDistrictBean> getDisMap() {
        return mDisMap;
    }
    
    public void setDisMap(Map<String, TDistrictBean> disMap) {
        mDisMap = disMap;
    }
    
    public CityParseHelper() {
        
    }
    
    /**
     * 初始化数据，解析json数据
     */
    public void initData(Context context) {
        
        String cityJson = JsonUtils.getJson(context, Constant.CITY_DATA);
        Type type = new TypeToken<ArrayList<TProvinceBean>>() {
        }.getType();
        
        mProvinceBeanArrayList = new Gson().fromJson(cityJson, type);
        
        if (mProvinceBeanArrayList == null || mProvinceBeanArrayList.isEmpty()) {
            return;
        }
        
        mCityBeanArrayList = new ArrayList<>(mProvinceBeanArrayList.size());
        mDistrictBeanArrayList = new ArrayList<>(mProvinceBeanArrayList.size());
        
        //*/ 初始化默认选中的省、市、区，默认选中第一个省份的第一个市区中的第一个区县
        if (mProvinceBeanArrayList != null && !mProvinceBeanArrayList.isEmpty()) {
            mProvinceBean = mProvinceBeanArrayList.get(0);
            List<TCityBean> cityList = mProvinceBean.getCityList();
            if (cityList != null && !cityList.isEmpty() && cityList.size() > 0) {
                mCityBean = cityList.get(0);
                List<TDistrictBean> districtList = mCityBean.getCityList();
                if (districtList != null && !districtList.isEmpty() && districtList.size() > 0) {
                    mDistrictBean = districtList.get(0);
                }
            }
        }
        
        //省份数据
        mProvinceBeenArray = new TProvinceBean[mProvinceBeanArrayList.size()];
        
        for (int p = 0; p < mProvinceBeanArrayList.size(); p++) {
            
            //遍历每个省份
            TProvinceBean itemProvince = mProvinceBeanArrayList.get(p);
            //当现实二级或者三级联动时，才会解析该数据
            //            if (config.getWheelType() == CityConfig.WheelType.PRO_CITY
            //                    || config.getWheelType() == CityConfig.WheelType.PRO_CITY_DIS) {
            
            //每个省份对应下面的市
            ArrayList<TCityBean> cityList = itemProvince.getCityList();
            
            //当前省份下面的所有城市
            TCityBean[] cityNames = new TCityBean[cityList.size()];
            
            //遍历当前省份下面城市的所有数据
            for (int j = 0; j < cityList.size(); j++) {
                cityNames[j] = cityList.get(j);
                
                //当前省份下面每个城市下面再次对应的区或者县
                List<TDistrictBean> districtList = cityList.get(j).getCityList();
                if (districtList == null) {
                    break;
                }
                TDistrictBean[] distrinctArray = new TDistrictBean[districtList.size()];
                
                for (int k = 0; k < districtList.size(); k++) {
                    
                    // 遍历市下面所有区/县的数据
                    TDistrictBean districtModel = districtList.get(k);
                    
                    //存放 省市区-区 数据
                    mDisMap.put(itemProvince.getName() + cityNames[j].getName() + districtList.get(k).getName(),
                            districtModel);
                    
                    distrinctArray[k] = districtModel;
                    
                }
                // 市-区/县的数据，保存到mDistrictDatasMap
                mCity_DisMap.put(itemProvince.getName() + cityNames[j].getName(), distrinctArray);
                
            }
            
            // 省-市的数据，保存到mCitisDatasMap
            mPro_CityMap.put(itemProvince.getName(), cityNames);
            
            mCityBeanArrayList.add(cityList);
            
            //只有显示三级联动，才会执行
            ArrayList<ArrayList<TDistrictBean>> array2DistrictLists = new ArrayList<>(cityList.size());
            
            for (int c = 0; c < cityList.size(); c++) {
                TCityBean cityBean = cityList.get(c);
                array2DistrictLists.add(cityBean.getCityList());
            }
            mDistrictBeanArrayList.add(array2DistrictLists);
            
            //            }
            //赋值所有省份的名称
            mProvinceBeenArray[p] = itemProvince;
            
        }
        
    }
    
}
