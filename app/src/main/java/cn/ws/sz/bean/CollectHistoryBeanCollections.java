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

	public boolean containsCollectHistroyBean(CollectHistroyBean item){
		if(this.collectHistroyBeans.contains(item)){
			return true;
		}else {
			return false;
		}
	}

	public void addHistroyBean(CollectHistroyBean item){
		if(this.collectHistroyBeans.contains(item)){
			return;
		}
		if(this.collectHistroyBeans.size() == Constant.MAX_HISTROY){
			this.collectHistroyBeans.remove(0);
		}
		this.collectHistroyBeans.add(item);
	}

	public void addOrRemoveCollectBean(CollectHistroyBean item){
		if(this.collectHistroyBeans.contains(item)){
			this.collectHistroyBeans.remove(item);
		}else{
			if(this.collectHistroyBeans.size() == Constant.MAX_COLLECT){
				this.collectHistroyBeans.remove(0);
			}
			this.collectHistroyBeans.add(item);
		}
	}
}
