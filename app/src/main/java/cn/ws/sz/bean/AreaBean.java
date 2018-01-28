package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/28.
 */

public class AreaBean implements Serializable{

    private static final long serialVersionUID = -7810832533709131885L;


    /**
     * id : 130102
     * isNewRecord : false
     * area : 长安区
     * cityid : 130100
     */

    private int id;
    private boolean isNewRecord;
    private String area;
    private int cityid;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    @Override
    public String toString() {
        return "AreaBean{" +
                "id=" + id +
                ", isNewRecord=" + isNewRecord +
                ", area='" + area + '\'' +
                ", cityid=" + cityid +
                '}';
    }
}
