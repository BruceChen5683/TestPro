package cn.ws.sz.bean;

import java.util.List;

/**
 * Created by chenjianliang on 2018/1/13.
 */

public class ClassifyStatus {
    private String errcode;
    private List<ClassifyBean> data;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public List<ClassifyBean> getData() {
        return data;
    }

    public void setData(List<ClassifyBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ClassifyStatus{" +
                "errcode='" + errcode + '\'' +
                ", data=" + data +
                '}';
    }
}
