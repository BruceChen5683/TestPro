package cn.ws.sz.bean;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.ws.sz.utils.Constant;

/**
 * Created by BattleCall on 2018/1/30.
 */

public class CollectHistoryBeanCollections implements Serializable{

	private static final String TAG = CollectHistoryBeanCollections.class.getSimpleName();
	private static final long serialVersionUID = 5066845852441656541L;

	private List<CollectHistroyBean> collectHistroyBeans = new ArrayList<CollectHistroyBean>();

	public CollectHistoryBeanCollections setCollectHistoryBeanCollections(CollectHistoryBeanCollections collectHistoryBeanCollections){
		CollectHistoryBeanCollections copy = new CollectHistoryBeanCollections();
		copy.collectHistroyBeans.addAll(collectHistoryBeanCollections.getCollectHistroyBeans());
		return copy;
	}

	public List<CollectHistroyBean> getCollectHistroyBeans() {
		return collectHistroyBeans;
	}

	public void addOrRemoveCollectHistroyBean(CollectHistroyBean item){
		Log.d(TAG, "addOrRemoveCollectHistroyBean: ");
		if(this.collectHistroyBeans.contains(item)){
			Log.d(TAG, "addOrRemoveCollectHistroyBean: 3");
			this.collectHistroyBeans.remove(item);
		}else{
			Log.d(TAG, "addOrRemoveCollectHistroyBean: 4");
			if(this.collectHistroyBeans.size() == Constant.MAX_COLLECT){
				Log.d(TAG, "addOrRemoveCollectHistroyBean: ---5 remove");
				this.collectHistroyBeans.remove(0);
			}
			this.collectHistroyBeans.add(item);
		}
	}
}
