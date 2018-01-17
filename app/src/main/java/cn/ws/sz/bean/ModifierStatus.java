package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/16.
 */

public class ModifierStatus implements Serializable{
    private static final long serialVersionUID = -3356407134449070401L;
    /**
     * errcode : 0
     * data : success
     */

    private int errcode;
    private String data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ModifierStatus{" +
                "errcode=" + errcode +
                ", data='" + data + '\'' +
                '}';
    }
}
