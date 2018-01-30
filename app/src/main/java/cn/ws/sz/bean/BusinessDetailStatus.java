package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/13.
 */

public class BusinessDetailStatus {
    private String errcode;
    private BusinessBean data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public BusinessBean getData() {
        return data;
    }

    public void setData(BusinessBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BusinessDetailStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
