package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/28.
 */

public class ProvinceBean implements Serializable{
    private static final long serialVersionUID = 5116998768560793101L;


    /**
     * id : 110000
     * isNewRecord : false
     * province : 北京市
     */

    private int id;
    private boolean isNewRecord;
    private String province;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsNewRecord() {
        return isNewRecord;
    }

    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "ProvinceBean{" +
                "id=" + id +
                ", isNewRecord=" + isNewRecord +
                ", province='" + province + '\'' +
                '}';
    }
}
