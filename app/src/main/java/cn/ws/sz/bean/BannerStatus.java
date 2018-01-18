package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/18.
 */

public class BannerStatus {
    private String errcode;
    private List<BannerBean> data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public List<BannerBean> getData() {
        return data;
    }

    public void setData(List<BannerBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BannerStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
