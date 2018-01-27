package cn.ws.sz.utils;

/**
 * Created by chenjianliang on 2018/1/9.
 */

public class Constant {
    public static final String BASEURL = "http://39.106.208.193:8080/ws";

    public static final int MAX_BUSINESS_PHOTO = 5;
    public static final int MAX_IDCARD_PHOTO = 1;

    public static final int PHOTO_TYPE_BUSINESS = 1;
    public static final int PHOTO_TYPE_IDCARD = 2;


    public static final int CODE_IMAGE_SELECT_ACTIVITY = 1;
    public static final int CODE_ACTION_IMAGE_CAPTURE = 2;
    public static final int CODE_CITY_PICERK_ACTIVITY = 3;
    public static final int CODE_MODIFIER_ACTIVITY = 4;

    public static final String URL_CATEGORY = "/api/category/list/";//分类接口  +  分类id (一级分类0 二级分类使用一级分类id)

    public static final String URL_PROVINCE = "/api/region/getProvinces";
    public static final String URL_CITY = "/api/region/getCity/";// + 省份id
    public static final String URL_AREA = "/api/region/getArea/";// +城市id

    public static final String URL_AD = "/api/banner/list/";// +areaId  /api/banner/list/110101    110101 是区域id 获取banner图
    public static final String URL_BUSINESS_LIST = "/api/merchant/getListByCategory/";//  /api/merchant/getListByCategory/17/1 获取商户列表 17是二级分类的id 1是页码
    public static final String URL_HOT = "/api/merchant/getHotList/";// + aredId


    public static final String URL_UPLOAD_BUSINESS_INFO = "/api/merchant/addMerchant";//添加商户
    public static final String URL_UPLOAD_AGENT_INFO = "/api/agent/addAgent";//添加代理


    public static final String URL_MODIFIR_ADWORDS = "/api/merchant/changeAd";//修改广告词
    public static final String URL_MODIFIR_MAINPRODUCTS = "/api/merchant/changeMainProducts";//修改广告词


    public static final String URL_UPLOAD_PIC = "/api/merchant/uploadImage";//上传图片

    public static final String URL_QUERY_BUSINESS = "/api/merchant/query";

    public static final String URL_QUERY_FINDNUM = "/api/merchant/findNum";///api/merchant/findNum/13512345678


    public static final String TAG_CATEGROY = "categroy";
    public static final String TAG_BUSINESS_LIST = "business_list";//分类
    public static final String TAG_BUSINESS_LIST_SEARCH = "business_list_search";//搜索
    public static final String TAG_BUSINESS_LIST_SIMILAR = "business_list_similar";//同类
    public static final String TAG_BUSINESS_LIST_2 = "business_list_2";//分类
    public static final String TAG_BUSINESS_INFO_UPLOAD = "business_info_upload";
    public static final String TAG_AGENT_INFO_UPLOAD = "agent_info_upload";

    public static final String TAG_MODIFIER_ADWORDS = "modify_ad";
    public static final String TAG_MODIFIER_MAINPRODUCTS= "modify_main_product";

    public static final String TAG_PIC_UPLOAD = "pic_upload";
    public static final String TAG_AD = "area_ad";
	public static final String TAG_HOT = "business_hot";

    public static final String TAG_QUERY_BUSINESS = "query_business";

    public static final String TAG_QUERY_FINDNUM = "query_findnum";




    public static final int MODIFIER_AD_TYPE = 1;
    public static final int MODIFIER_MAIN_PRODUCTS_TYPE = 2;

    public static final int HOT_BUSINESS_NUM = 2;


    public static final String TEST_AD_URL = "http://news.bioon.com/article/6716755.html";




}
