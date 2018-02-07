package cn.ws.sz.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.bean.ClassifyBean;

/**
 * Created by BattleCall on 2018/1/30.
 */

public class DataHelper {
	private static volatile DataHelper dataHelper;

	private List<ClassifyBean> firstCategroyList = new ArrayList<>();
	private Map<Integer,List<ClassifyBean>> secondCategroyMap = new HashMap<>();

	private String city = "东城区";//defaut
	private String areaId = "110101";//default

	public DataHelper(){

	}

	public static DataHelper getInstance(){
		if(dataHelper == null){
			synchronized (DataHelper.class){
				if(dataHelper == null){
					dataHelper = new DataHelper();
				}
			}
		}
		return dataHelper;
	}

	public List<ClassifyBean> getFirstCategroyList() {
		return firstCategroyList;
	}

	public void setFirstCategroyList(List<ClassifyBean> firstCategroyList) {
		this.firstCategroyList.clear();
		this.firstCategroyList.addAll(firstCategroyList);
	}

	public Map<Integer, List<ClassifyBean>> getSecondCategroyMap() {
		return secondCategroyMap;
	}

	public void putSecondCategroyMap(Integer integer,List<ClassifyBean> list) {
		this.secondCategroyMap.put(integer,list);
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
}
