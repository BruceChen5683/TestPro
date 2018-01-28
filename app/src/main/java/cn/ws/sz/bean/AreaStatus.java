package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/18.
 */

public class AreaStatus {
    private String errcode;
    private List<AreaBean> data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public List<AreaBean> getData() {
        return data;
    }

    public void setData(List<AreaBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AreaStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
