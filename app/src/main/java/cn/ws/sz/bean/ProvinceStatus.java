package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/18.
 */

public class ProvinceStatus {
    private String errcode;
    private List<ProvinceBean> data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public List<ProvinceBean> getData() {
        return data;
    }

    public void setData(List<ProvinceBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProvinceStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
