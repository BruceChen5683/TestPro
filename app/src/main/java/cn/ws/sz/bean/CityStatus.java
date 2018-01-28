package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/18.
 */

public class CityStatus {
    private String errcode;
    private List<CityBean> data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public List<CityBean> getData() {
        return data;
    }

    public void setData(List<CityBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CityStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
