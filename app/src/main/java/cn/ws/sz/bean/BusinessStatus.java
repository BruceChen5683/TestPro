package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/13.
 */

public class BusinessStatus {
    private String errcode;
    private List<BusinessBean> data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public List<BusinessBean> getData() {
        return data;
    }

    public void setData(List<BusinessBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BusinessStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
