package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/28.
 */

public class CityBean implements Serializable{
    private static final long serialVersionUID = 8802132968650784190L;


    /**
     * id : 130100
     * isNewRecord : false
     * city : 石家庄市
     * provinceid : 130000
     */

    private int id;
    private boolean isNewRecord;
    private String city;
    private int provinceid;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "id=" + id +
                ", isNewRecord=" + isNewRecord +
                ", city='" + city + '\'' +
                ", provinceid=" + provinceid +
                '}';
    }
}
