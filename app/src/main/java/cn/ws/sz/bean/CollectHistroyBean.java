package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by BattleCall on 2018/1/30.
 */

public class CollectHistroyBean implements Serializable{
	private static final long serialVersionUID = -1491481102007652472L;
	private int id;
	private String name;

	public CollectHistroyBean(int id,String name){
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CollectHistroyBean{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		if(this.getId() == ((CollectHistroyBean)obj).getId() && this.getName().equals(((CollectHistroyBean) obj).getName())){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id;
	}
}
