package cn.ws.sz.bean;

/**
 * Created by chenjianliang on 2018/1/9.
 */

public class ClassifyBean {
    /**
     * id : 1
     * isNewRecord : false
     * name : 餐饮美食
     * sort : 1
     */

    private int id;
    private boolean isNewRecord;
    private String name;
    private int sort;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "ClassifyBean{" +
                "id=" + id +
                ", isNewRecord=" + isNewRecord +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                '}';
    }
}
