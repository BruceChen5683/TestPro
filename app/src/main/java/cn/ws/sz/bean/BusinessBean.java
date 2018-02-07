package cn.ws.sz.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by chenjianliang on 2018/1/13.
 */

public class BusinessBean implements Serializable{


    private static final long serialVersionUID = -5525259221150491451L;

    /**
     * id : 1
     * isNewRecord : false
     * name : ceshi 1
     * region : 110101
     * categoryId : 17
     * cellphone : 1234567890
     * logoUrl : http://img2.bdstatic.com/static/searchresult/widget/ui/controls/hoverBox/img/shadow_c288912.png
     * referee : 经常推荐人
     * refereeCellphone : 13512345678
     * attendDate : 2018-01-17 00:00:00
     * mainProducts : 游戏代理、网球
     * adWord : 寓教于乐
     * type : 1
     * phone : 01012345678
     * viewNum : 100
     * lng : 163.78965
     * lat : 38.9876
     * isHot : 1
     * sort : 1
     * address : 上海浦东测试
	 * isAuth:1
     */

    private int id;
    private boolean isNewRecord;
    private String name;
    private String region;
    private int categoryId;
    private String cellphone;
    private String logoUrl = "";
    private String referee;
    private String refereeCellphone;
    private String attendDate;
    private String mainProducts;
    private String adWord;
    private String type;
    private String phone;
    private int viewNum;
    private String lng;
    private String lat;
    private int isHot;
    private int sort;
    private String address;
	private int isAuth;
	private String[] images;

	public int getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(int isAuth) {
		this.isAuth = isAuth;
	}

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getReferee() {
        return referee;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public String getRefereeCellphone() {
        return refereeCellphone;
    }

    public void setRefereeCellphone(String refereeCellphone) {
        this.refereeCellphone = refereeCellphone;
    }

    public String getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(String attendDate) {
        this.attendDate = attendDate;
    }

    public String getMainProducts() {
        return mainProducts;
    }

    public void setMainProducts(String mainProducts) {
        this.mainProducts = mainProducts;
    }

    public String getAdWord() {
        return adWord;
    }

    public void setAdWord(String adWord) {
        this.adWord = adWord;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "BusinessBean{" +
				"id=" + id +
				", isNewRecord=" + isNewRecord +
				", name='" + name + '\'' +
				", region='" + region + '\'' +
				", categoryId=" + categoryId +
				", cellphone='" + cellphone + '\'' +
				", logoUrl='" + logoUrl + '\'' +
				", referee='" + referee + '\'' +
				", refereeCellphone='" + refereeCellphone + '\'' +
				", attendDate='" + attendDate + '\'' +
				", mainProducts='" + mainProducts + '\'' +
				", adWord='" + adWord + '\'' +
				", type='" + type + '\'' +
				", phone='" + phone + '\'' +
				", viewNum=" + viewNum +
				", lng='" + lng + '\'' +
				", lat='" + lat + '\'' +
				", isHot=" + isHot +
				", sort=" + sort +
				", address='" + address + '\'' +
				", isAuth=" + isAuth +
				", images=" + Arrays.toString(images) +
				'}';
	}
}
