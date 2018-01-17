package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/16.
 */

public class UploadStatus implements Serializable{

    private static final long serialVersionUID = 3264505904138225627L;
    /**
     * errcode : 0
     * data : 11
     */

    private int errcode;
    private int data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UploadStatus{" +
                "errcode=" + errcode +
                ", data=" + data +
                '}';
    }
}
