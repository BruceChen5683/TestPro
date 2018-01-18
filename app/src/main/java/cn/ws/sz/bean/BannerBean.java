package cn.ws.sz.bean;

import java.io.Serializable;

/**
 * Created by chenjianliang on 2018/1/18.
 */

public class BannerBean implements Serializable{

    private static final long serialVersionUID = -9030714864508635156L;
    /**
     * id : 1
     * isNewRecord : false
     * bannerUrl : http://img.zcool.cn/community/01e9ef5947dd9da8012193a31fc82f.jpg@2o.jpg
     * region : 110101
     * redictType : 1
     * redictUrl : 1234
     * merchantId : 1
     */

    private int id;
    private boolean isNewRecord;
    private String bannerUrl;
    private String region;
    private int redictType;
    private String redictUrl;
    private int merchantId;

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

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getRedictType() {
        return redictType;
    }

    public void setRedictType(int redictType) {
        this.redictType = redictType;
    }

    public String getRedictUrl() {
        return redictUrl;
    }

    public void setRedictUrl(String redictUrl) {
        this.redictUrl = redictUrl;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "id=" + id +
                ", isNewRecord=" + isNewRecord +
                ", bannerUrl='" + bannerUrl + '\'' +
                ", region='" + region + '\'' +
                ", redictType=" + redictType +
                ", redictUrl='" + redictUrl + '\'' +
                ", merchantId=" + merchantId +
                '}';
    }
}
