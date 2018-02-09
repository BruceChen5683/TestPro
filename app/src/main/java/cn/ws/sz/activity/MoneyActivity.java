package cn.ws.sz.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessPhotoAdapter;
import cn.ws.sz.adater.ChooseAreaAdapter;
import cn.ws.sz.adater.ChooseCityAdapter;
import cn.ws.sz.adater.ChooseClassifyAdapter;
import cn.ws.sz.adater.ChooseProvinceAdapter;
import cn.ws.sz.bean.AreaBean;
import cn.ws.sz.bean.CityBean;
import cn.ws.sz.bean.ClassifyBean;
import cn.ws.sz.bean.ProvinceBean;
import cn.ws.sz.bean.UploadStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DataHelper;
import cn.ws.sz.utils.DeviceUtils;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.ImageItem;
import cn.ws.sz.utils.PayResult;
import cn.ws.sz.utils.StringUtils;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import cn.ws.sz.view.MyGridView;
import gps.LocationFilter;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;
import third.wheelviewchoose.OnWheelChangedListener;
import third.wheelviewchoose.WheelView;

import static cn.ws.sz.utils.Constant.CODE_ACTION_IMAGE_CAPTURE;
import static cn.ws.sz.utils.Constant.CODE_IMAGE_SELECT_ACTIVITY;
import static cn.ws.sz.utils.Constant.CODE_LOCATIONFILTER_ACTIVITY;

public class MoneyActivity extends AppCompatActivity implements View.OnClickListener,OnWheelChangedListener{

    private final static String TAG = MoneyActivity.class.getSimpleName();
    private MyGridView postPhoto;
    private BusinessPhotoAdapter adapter;
    private String randomFileName;
    private boolean isShowDelete = false;
	private final static int WINDOW_CLASSIFY = 1;
	private final static int WINDOW_AREA = 2;
	private int windowType = 0;
	private int areaId;

    private TextView tvTitle;
    private LinearLayout llReturnBack;

    private Button submitBtn;
	private RadioButton rgSettledUserNormal,rgSettledUserVip;

    private Dialog dialog;
    private int dialogHeight;

    private RelativeLayout llRecommendEnter,rlAli,rlWeChat;
    private ImageView ivAliConfim,ivWeChatConfim;

    private Map<String,String> params = new HashMap<>();

    private Map<String,String[]> paramsImg = new HashMap<>();//key parm || value是长度为2的字符串数组， 下标0存放的是文件的本地路径，下标1存放的是传送到服务器中的文件名


    public static ArrayList<ImageItem> SelectedImages = new ArrayList<ImageItem>();

	private TextView tvSettledCoordinate2;
	private String lat = "",lng = "";


    public static final int MSG_PAY_SUCCESS = 1;
    public static final int MSG_PAY_FAIL = 2;

    private int mImageSize = 0;

    private Gson gson;

    private EditText tvSettledName2,etDetailAddress,tvSettledTel2,mainProducts,ad,tvSettledPhone2;
    private StringBuilder imageIdStr = new StringBuilder();

    private static final int UPLOAD_PIC_SUCCESS = 200;


    /*
     *  choose
     * */
    private PopupWindow mPopupWindow;
    private WheelView firstClassifyView;
    private WheelView secondClassifyView;
	private WheelView provinceView,cityView,areaView;
	private List<ClassifyBean> firstClassifyDatas = new ArrayList<>();
    private List<ClassifyBean> secondClassifyDatas = new ArrayList<>();
	private List<ProvinceBean> provinceBeanList = new ArrayList<>();
	private List<CityBean> cityBeanList = new ArrayList<>();
	private List<AreaBean> areaBeanList = new ArrayList<>();

    private String mCurrentFirstClassify,mCurrentSecondClassify,mCurrentProvince,mCurrentCity,mCurrentArea;
    private int mCurrentFirstClassifyItem,mCurrentSecondClassifyItem,mCurrentProvinceItem,mCurrentCityItem,mCurrentAreaItem;
    private TextView btn_myinfo_sure,btn_myinfo_cancel;
    private ChooseClassifyAdapter firstClassifyAdapter,secondClassifyAdapter;
	private ChooseProvinceAdapter chooseProvinceAdapter;
	private ChooseCityAdapter chooseCityAdapter;
	private ChooseAreaAdapter chooseAreaAdapter;
    private final int TEXTSIZE=17;//选择器的字体大小
    private View rootView;
    private RelativeLayout rlSettledClassify;//选择分类,商家地址
    private TextView tvSettledClassify2,tvSettledAddresss2;
    private int categoryId = -1;

    private RelativeLayout rlSettledName,rlSettledAddres,rlSettledAddres2,rlSettledCoordinate,rlSettledPhone,rlSettledTel;
    private LinearLayout llMainPoructs,llAd;


    private ScrollView scrollView;

	private int merchantId;

	/*
	 * alipay
	 * */
	private String orderInfo;
	private static final int SDK_PAY_FLAG = 1;



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPLOAD_PIC_SUCCESS:
                    Log.d(TAG, "handleMessage: "+mImageSize);
                    if(mImageSize == SelectedImages.size()){
                        ToastUtil.showShort(MoneyActivity.this, "商家图片信息上传成功");

                        addMerchant();
                    }else {
                        paramsImg.clear();
                        // path fileName
                        paramsImg.put("pic", new String[]{SelectedImages.get(mImageSize).getImagePath(),SelectedImages.get(mImageSize).getImagePath()});
                        //发起请求
                        uploadImage(mImageSize,paramsImg);
                    }
                    break;
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MoneyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(MoneyActivity.this,AfterPayActivity.class);
                        startActivity(intent);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_money,null);
        setContentView(rootView);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));
        gson = new Gson();
        initView();
        initDialog();
    }

    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        rlSettledName = (RelativeLayout) findViewById(R.id.rlSettledName);
        rlSettledAddres = (RelativeLayout) findViewById(R.id.rlSettledAddres);
        rlSettledAddres2 = (RelativeLayout) findViewById(R.id.rlSettledAddres2);
        rlSettledCoordinate = (RelativeLayout) findViewById(R.id.rlSettledCoordinate);
        rlSettledPhone = (RelativeLayout) findViewById(R.id.rlSettledPhone);
        rlSettledTel = (RelativeLayout) findViewById(R.id.rlSettledTel);

        llMainPoructs = (LinearLayout) findViewById(R.id.llMainPoructs);
        llAd = (LinearLayout) findViewById(R.id.llAd);

		rgSettledUserNormal = (RadioButton) findViewById(R.id.rgSettledUserNormal);
		rgSettledUserVip = (RadioButton) findViewById(R.id.rgSettledUserVip);


		rlSettledName.setOnClickListener(this);
        rlSettledAddres.setOnClickListener(this);
        rlSettledAddres2.setOnClickListener(this);
        rlSettledCoordinate.setOnClickListener(this);
        rlSettledPhone.setOnClickListener(this);
        rlSettledTel.setOnClickListener(this);
        llMainPoructs.setOnClickListener(this);
        llAd.setOnClickListener(this);

		tvSettledCoordinate2 = (TextView) findViewById(R.id.tvSettledCoordinate2);




        tvTitle= (TextView) findViewById(R.id.title_value);
        tvTitle.setText("入驻万商");
        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);
        llReturnBack.setOnClickListener(this);


        postPhoto = (MyGridView) findViewById(R.id.gvPostPhoto);
        postPhoto.setSelector(new ColorDrawable(Color.TRANSPARENT));

        adapter = new BusinessPhotoAdapter(this,Constant.PHOTO_TYPE_BUSINESS);
        postPhoto.setAdapter(adapter);
        postPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == SelectedImages.size()){
                    Log.d(TAG, "onItemClick: ");
                    showGetPhotoDialog();
                }
                adapter.setIsShowDelete(false,position);
            }
        });
        postPhoto.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShowDelete) {
                    isShowDelete = false;
                } else {
                    isShowDelete = true;
                }

                adapter.setIsShowDelete(true, position);


                return true;
            }
        });

        adapter.setDeleteView(new BusinessPhotoAdapter.DeleteView() {
            @Override
            public void delete(int id) {
                SelectedImages.remove(id);
                adapter.notifyDataSetChanged();
            }
        });


        submitBtn = (Button) findViewById(R.id.submitMoney);
        submitBtn.setOnClickListener(this);


        tvSettledName2 = (EditText) findViewById(R.id.tvSettledName2);
		tvSettledPhone2 = (EditText)findViewById(R.id.tvSettledPhone2);
        etDetailAddress = (EditText) findViewById(R.id.etDetailAddress);
        tvSettledTel2 = (EditText) findViewById(R.id.tvSettledTel2);
        mainProducts = (EditText) findViewById(R.id.mainProducts);
        ad = (EditText) findViewById(R.id.ad);

        rlSettledClassify = (RelativeLayout) findViewById(R.id.rlSettledClassify);
		rlSettledAddres = (RelativeLayout) findViewById(R.id.rlSettledAddres);
        rlSettledClassify.setOnClickListener(this);
		rlSettledAddres.setOnClickListener(this);
        tvSettledClassify2 = (TextView) findViewById(R.id.tvSettledClassify2);
		tvSettledAddresss2 = (TextView) findViewById(R.id.tvSettledAddresss2);


    }

    private void showGetPhotoDialog() {
        final CharSequence[] items = { "拍照", "从相册选择", "取消" };

        AlertDialog alertDialog = new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                    String action = MediaStore.ACTION_IMAGE_CAPTURE;
                    // if (isKitKat) {
                    // action = MediaStore.ACTION_IMAGE_CAPTURE_SECURE;
                    // }
                    Intent intent = new Intent(action);
                    randomFileName = StringUtils.getRandomFileName("") + "wanshang.jpg";
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), randomFileName)));
                    startActivityForResult(intent, CODE_ACTION_IMAGE_CAPTURE);
                } else if (which == 1) {
                    // 从相册选择
                    Intent intent = new Intent(MoneyActivity.this, ImageSelectActivity.class);
                    intent.putExtra("isUploadNeeded", "false");
                    intent.putExtra("selectedNum", SelectedImages.size());
                    intent.putExtra("photoType",Constant.PHOTO_TYPE_BUSINESS);
                    startActivityForResult(intent, CODE_IMAGE_SELECT_ACTIVITY);
                }
            }
        }).create();

        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
			switch (requestCode){
				case CODE_IMAGE_SELECT_ACTIVITY:
					ArrayList<ImageItem> selectData = (ArrayList<ImageItem>) data.getSerializableExtra("data");
					for (int i = 0; i < selectData.size(); i++) {
						Bitmap bitmap = CommonUtils.getScaleBitmap(selectData.get(i).getImagePath(), 200, 200);
						selectData.get(i).setBitmap(bitmap);
					}
					SelectedImages.addAll(selectData);
					adapter.notifyDataSetChanged();
					break;
				case CODE_ACTION_IMAGE_CAPTURE:
					Log.d(TAG, "onActivityResult: CODE_ACTION_IMAGE_CAPTURE");
					String picPath = Environment.getExternalStorageDirectory() + File.separator + randomFileName;
					ImageItem imageItem = new ImageItem();
					imageItem.setImagePath(picPath);
					Bitmap bitmap = CommonUtils.getScaleBitmap(picPath, 200, 200);
					imageItem.setBitmap(bitmap);
					SelectedImages.add(imageItem);
					adapter.notifyDataSetChanged();
					randomFileName = "";
					break;
				case CODE_LOCATIONFILTER_ACTIVITY:
//					data.getStringExtra("latitude");
//					data.getStringExtra("longitude");
					lat = data.getStringExtra("latitude");
					lng = data.getStringExtra("longitude");

					tvSettledCoordinate2.setText(lat+","+lng);
					break;
				default:
					Log.d(TAG, "onActivityResult: requestCode "+requestCode);
					break;
			}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitMoney:
                showPayDialog();
                break;
            case R.id.returnBack:
                this.finish();
                break;
            case R.id.llRecommendEnter://submit
                payAction();
//				startAliPay();
                break;
            case R.id.rlWeChat:
                changPayConfim(false);
                break;
            case R.id.rlAli:
                changPayConfim(true);
                break;
            case R.id.btn_myinfo_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.btn_myinfo_sure:
            	if(windowType == WINDOW_CLASSIFY){
                	tvSettledClassify2.setText(mCurrentFirstClassify+"-"+mCurrentSecondClassify);
            	}else if(windowType == WINDOW_AREA){
					tvSettledAddresss2.setText(mCurrentProvince+mCurrentCity+mCurrentArea);
				}
                mPopupWindow.dismiss();
                break;
            case R.id.rlSettledClassify:
				windowType = WINDOW_CLASSIFY;
                initPopupWindow();
                mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
                break;
			case R.id.rlSettledAddres:
				windowType = WINDOW_AREA;
				initAreaPopupWindow();
				mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
				break;

            case R.id.rlSettledName:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.rlSettledAddres2:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.rlSettledCoordinate:
				Log.d(TAG, "onClick: ");
//                onClickEditTextParent((ViewGroup) v);
                Intent intent = new Intent();
                intent.setClass(MoneyActivity.this, LocationFilter.class);
				intent.putExtra("mode",Constant.SET_GPS);
				startActivityForResult(intent,CODE_LOCATIONFILTER_ACTIVITY);
                break;
            case R.id.rlSettledPhone:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.rlSettledTel:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.llMainPoructs:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.llAd:
                onClickEditTextParent((ViewGroup) v);
                break;

            default:
                break;
        }
    }

    private void onClickEditTextParent(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for (int i = 0;i < count;i++){
            if (viewGroup.getChildAt(i) instanceof  EditText){
                EditText et = (EditText) viewGroup.getChildAt(i);
                CommonUtils.showSoftInputFromWindow(this,et);
                return;
            }
        }
    }

    private void hideDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    private void payAction() {

		if(!rgSettledUserVip.isChecked() && !rgSettledUserNormal.isChecked()){
			ToastUtil.showLong(this,"请选择商家类型,默认普通商家");
			rgSettledUserNormal.setChecked(true);
			return;
		}

        uploadImage();
        Log.d(TAG, "payAction: ");

//        addMerchant();

    }

    private void addMerchant() {
        if(categoryId == -1){
            ToastUtil.showLong(this,"请选择商家所属的分类");
            hideDialog();
            scrollView.smoothScrollTo(0,0);
            initPopupWindow();
            mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
            return;
        }
        if(TextUtils.isEmpty(tvSettledName2.getText())){
            ToastUtil.showLong(this,"请输入商家名称");
            hideDialog();
            CommonUtils.showSoftInputFromWindow(this,tvSettledName2);
            return;
        }

        if(TextUtils.isEmpty(tvSettledAddresss2.getText())){
            ToastUtil.showLong(this,"请输入商家的地址");
			hideDialog();
			scrollView.smoothScrollTo(0,0);
			initAreaPopupWindow();
			mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
            return;
        }

        if(TextUtils.isEmpty(tvSettledTel2.getText())){
            ToastUtil.showLong(this,"请输入手机号码");
            hideDialog();
            CommonUtils.showSoftInputFromWindow(this,tvSettledTel2);
            return;
        }

        if(TextUtils.isEmpty(mainProducts.getText())){
            ToastUtil.showLong(this,"请输入主营内容");
            CommonUtils.showSoftInputFromWindow(this,mainProducts);
            return;
        }

        if(TextUtils.isEmpty(ad.getText())){
            ToastUtil.showLong(this,"请输入广告内容");
            hideDialog();
            CommonUtils.showSoftInputFromWindow(this,ad);
            return;
        }

        params.put("name",tvSettledName2.getText().toString());
        params.put("region",areaId+"");
        params.put("categoryId",String.valueOf(categoryId));
        params.put("cellphone",tvSettledTel2.getText().toString());
        params.put("mainProducts",mainProducts.getText().toString());
        params.put("adWord",ad.getText().toString());
		if(!TextUtils.isEmpty(tvSettledPhone2.getText().toString())){
			params.put("phone",tvSettledPhone2.getText().toString());
		}
		if(!TextUtils.isEmpty(lng) && !TextUtils.isEmpty(lat)){
			params.put("lng",lng);
			params.put("lat",lat);
		}

        params.put("address",tvSettledAddresss2.getText() + etDetailAddress.getText().toString());
        imageIdStr.deleteCharAt(imageIdStr.length()-1);
        params.put("imageIdStr",imageIdStr.toString());
		if(rgSettledUserNormal.isChecked()){
			params.put("type","normal");
		}else {
			params.put("type","vip");
		}

        Log.d(TAG, "payAction: --------uploadBusinessInfo");
        uploadBusinessInfo(params);
    }

    private void uploadImage() {

		if(SelectedImages.size() == 0){
			ToastUtil.showLong(this,"请添加商户图片");
			hideDialog();
			return;
		}
        mImageSize = 0;
//        for (int i = 0; i < SelectedImages.size(); i++) {

//            Log.d(TAG, "uploadImage: " + SelectedImages.get(i).getImagePath());
            paramsImg.clear();
            // path fileName
            paramsImg.put("pic", new String[]{SelectedImages.get(0).getImagePath(),SelectedImages.get(0).getImagePath()});
            //发起请求
            uploadImage(0,paramsImg);
//        }
    }

    private void uploadImage(final int i,final Map<String,String[]> paramsImg){
        VolleyRequestUtil.RequestPostFile(this,
                Constant.URL_UPLOAD_PIC,
                Constant.TAG_PIC_UPLOAD + i,
                paramsImg,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        UploadStatus status = gson.fromJson(result, UploadStatus.class);

                        mImageSize++;
                        if(mHandler != null){
                            mHandler.removeMessages(UPLOAD_PIC_SUCCESS);
                            mHandler.sendEmptyMessage(UPLOAD_PIC_SUCCESS);
                        }

                        if (status.getErrcode() == 0) {
                            imageIdStr.append(status.getData());
                            imageIdStr.append(",");
                        } else {
                            ToastUtil.showShort(MoneyActivity.this, "商家图片上传失败");
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        mImageSize++;
						ToastUtil.showShort(MoneyActivity.this, "商家图片上传失败");
                        Log.d(TAG, "onMyError: "+error.getMessage());
                    }
                },
                true);
    }


    private void changPayConfim(boolean ali){
        if(ali){
            ivAliConfim.setVisibility(View.VISIBLE);
            ivWeChatConfim.setVisibility(View.GONE);
        }else {
            ivAliConfim.setVisibility(View.GONE);
            ivWeChatConfim.setVisibility(View.VISIBLE);
        }
    }
    private void showPayDialog() {
        if(dialog != null && !dialog.isShowing()){


//            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                            //布局位于状态栏下方
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                            //全屏
////                            View.SYSTEM_UI_FLAG_FULLSCREEN |
//                            //隐藏导航栏
//                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//                    if (Build.VERSION.SDK_INT >= 19) {
//                        uiOptions |= 0x00001000;
//                    } else {
//                        uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
//                    }
//                    dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
//                }
//            });


            dialog.show();
        }
    }

    private void uploadBusinessInfo(Map<String,String> parmMap){

        VolleyRequestUtil.RequestPost(this,
                Constant.URL_UPLOAD_BUSINESS_INFO,
                Constant.TAG_BUSINESS_INFO_UPLOAD,
                parmMap,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        UploadStatus status = gson.fromJson(result,UploadStatus.class);
                        if(status.getErrcode() == 0){
                            ToastUtil.showShort(MoneyActivity.this,"商家信息上传成功");
							merchantId = status.getData();
							startAliPay();
                        }else {
                            ToastUtil.showShort(MoneyActivity.this,"商家信息上传失败");
                        }


                    }

                    @Override
                    public void onMyError(VolleyError error) {
						ToastUtil.showShort(MoneyActivity.this,"商家信息上传失败");
                        Log.d(TAG, "onMyError: "+error.getMessage());
                    }
                },
                true);
    }


    private void initDialog() {
        dialog = new Dialog(this, R.style.recommend_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.money_pay, null);
        llRecommendEnter = (RelativeLayout) root.findViewById(R.id.llRecommendEnter);
        rlAli = (RelativeLayout) root.findViewById(R.id.rlAli);
        rlWeChat = (RelativeLayout) root.findViewById(R.id.rlWeChat);

        ivAliConfim = (ImageView) root.findViewById(R.id.ivAliConfim);
        ivWeChatConfim = (ImageView) root.findViewById(R.id.ivWeChatConfim);


        llRecommendEnter.setOnClickListener(this);
        rlAli.setOnClickListener(this);
        rlWeChat.setOnClickListener(this);

        dialog.setContentView(root);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogAnimation); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        if(CommonUtils.hasNavBar(MoneyActivity.this)){
            lp.y = 0 + CommonUtils.getNavigationBarHeight(MoneyActivity.this); // 新位置Y坐标
        }else{
            lp.y = 0; // 新位置Y坐标
        }
        lp.width = DeviceUtils.getDeviceScreeWidth(this); // 宽度
//        lp.height = (int) getResources().getDimension(R.dimen.dp_127); 去除微信支付gone
        lp.height = (int) getResources().getDimension(R.dimen.dp_85);
        dialogHeight = lp.height;
        lp.alpha = 1.0f; // 透明度
        dialogWindow.setAttributes(lp);
    }


    public void initPopupWindow(){
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_classifychoose, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.popup_choose_bottom);
        firstClassifyView = (WheelView)popupView.findViewById(R.id.provinceView);
        secondClassifyView = (WheelView)popupView.findViewById(R.id.cityView);
        //确定或者取消
        btn_myinfo_sure = (TextView)popupView.findViewById(R.id.btn_myinfo_sure);
        btn_myinfo_cancel = (TextView) popupView.findViewById(R.id.btn_myinfo_cancel);
        btn_myinfo_cancel.setOnClickListener(this);
        btn_myinfo_sure.setOnClickListener(this);
        // 设置可见条目数量
        firstClassifyView.setVisibleItems(7);
        secondClassifyView.setVisibleItems(7);
        // 添加change事件
        firstClassifyView.addChangingListener(this);
        secondClassifyView.addChangingListener(this);
        initpopData();
    }

    public void initAreaPopupWindow(){
		View popupView = LayoutInflater.from(this).inflate(R.layout.popup_city_choose, null);
		mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.popup_choose_bottom);

		provinceView = (WheelView)popupView.findViewById(R.id.provinceView);
		cityView = (WheelView)popupView.findViewById(R.id.cityView);
		areaView = (WheelView)popupView.findViewById(R.id.areaView);

		//确定或者取消
		btn_myinfo_sure = (TextView)popupView.findViewById(R.id.btn_myinfo_sure);
		btn_myinfo_cancel = (TextView) popupView.findViewById(R.id.btn_myinfo_cancel);
		btn_myinfo_cancel.setOnClickListener(this);
		btn_myinfo_sure.setOnClickListener(this);
		// 设置可见条目数量
		provinceView.setVisibleItems(7);
		cityView.setVisibleItems(7);
		areaView.setVisibleItems(7);
		// 添加change事件
		provinceView.addChangingListener(this);
		cityView.addChangingListener(this);
		areaView.addChangingListener(this);
		initAreaData();
	}

    private void initpopData() {
        firstClassifyDatas.clear();
		firstClassifyDatas.addAll(DataHelper.getInstance().getFirstCategroyList());
        mCurrentFirstClassify = firstClassifyDatas.get(0).getName();
        firstClassifyAdapter = new ChooseClassifyAdapter(this,firstClassifyDatas);
        firstClassifyAdapter.setTextSize(TEXTSIZE);
        firstClassifyView.setViewAdapter(firstClassifyAdapter);
        if(mCurrentFirstClassifyItem != 0){
            firstClassifyView.setCurrentItem(mCurrentFirstClassifyItem);
        }
        updateSecondClassifyDate();
    }

    private void initAreaData(){
		provinceBeanList.clear();
		provinceBeanList.addAll(WSApp.provinces);
		mCurrentProvince = provinceBeanList.get(0).getProvince();
		chooseProvinceAdapter = new ChooseProvinceAdapter(this,provinceBeanList);
		chooseProvinceAdapter.setTextSize(TEXTSIZE);
		provinceView.setViewAdapter(chooseProvinceAdapter);
		if(mCurrentProvinceItem != 0){
			provinceView.setCurrentItem(mCurrentProvinceItem);
		}
		updateCityData();
	}

	private void updateCityData(){
		int pCurrent = provinceView.getCurrentItem();
		Log.d(TAG, "updateCityData: pCurrent "+pCurrent);
		if(provinceBeanList.size() > 0){
			cityBeanList =  WSApp.citysMap.get(provinceBeanList.get(pCurrent).getId());// DataHelper.getInstance().getSecondCategroyMap().get(firstClassifyDatas.get(pCurrent).getId());
		}else {
			cityBeanList.clear();
		}

		if(cityBeanList != null){

			chooseCityAdapter = new ChooseCityAdapter(this,cityBeanList);
			chooseCityAdapter.setTextSize(TEXTSIZE);
			cityView.setViewAdapter(chooseCityAdapter);

			int cityLength = cityBeanList.size();
			if(cityLength > 0){
				if(cityLength > mCurrentCityItem){
					mCurrentCity = cityBeanList.get(mCurrentCityItem).getCity();
					cityView.setCurrentItem(mCurrentCityItem);
				}else {
					mCurrentCity = cityBeanList.get(cityLength-1).getCity();
					cityView.setCurrentItem(cityLength-1);
				}
			}else {
//			mCurrentCity = "";
			}

			updateAreaData();
		}


	}

	private void updateAreaData(){
		int pCurrent = cityView.getCurrentItem();
		if(cityBeanList.size() > 0){
			areaBeanList =  WSApp.areasMap.get(cityBeanList.get(pCurrent).getId());// DataHelper.getInstance().getSecondCategroyMap().get(firstClassifyDatas.get(pCurrent).getId());
		}else {
			areaBeanList.clear();
		}
		chooseAreaAdapter = new ChooseAreaAdapter(this,areaBeanList);
		chooseAreaAdapter.setTextSize(TEXTSIZE);
		areaView.setViewAdapter(chooseAreaAdapter);

		int areaLength = areaBeanList.size();
		if(areaLength > 0){
			if(areaLength > mCurrentAreaItem){
				mCurrentArea = areaBeanList.get(mCurrentAreaItem).getArea();
				areaId = areaBeanList.get(mCurrentAreaItem).getId();
				areaView.setCurrentItem(mCurrentAreaItem);
			}else {
				mCurrentArea = areaBeanList.get(areaLength-1).getArea();
				areaId = areaBeanList.get(areaLength-1).getId();
				areaView.setCurrentItem(areaLength-1);
			}
		}else {
//			mCurrentCity = "";
		}
	}

    private void updateSecondClassifyDate() {
		int pCurrent = firstClassifyView.getCurrentItem();

		if(firstClassifyDatas.size() > 0){
			secondClassifyDatas =  DataHelper.getInstance().getSecondCategroyMap().get(firstClassifyDatas.get(pCurrent).getId());
		}else {
			secondClassifyDatas.clear();
		}

		secondClassifyAdapter = new ChooseClassifyAdapter(this,secondClassifyDatas);
		secondClassifyAdapter.setTextSize(TEXTSIZE);
		secondClassifyView.setViewAdapter(secondClassifyAdapter);



		int secondClassifyLenght = secondClassifyDatas.size();
		if(secondClassifyLenght > 0){
			if(secondClassifyLenght > mCurrentSecondClassifyItem){
				mCurrentSecondClassify = secondClassifyDatas.get(mCurrentSecondClassifyItem).getName();
                Log.d(TAG, "updateSecondClassifyDate: 1");
                categoryId = secondClassifyDatas.get(mCurrentSecondClassifyItem).getId();
				secondClassifyView.setCurrentItem(mCurrentSecondClassifyItem);
			}else {
				mCurrentSecondClassify = secondClassifyDatas.get(secondClassifyLenght-1).getName();
                Log.d(TAG, "updateSecondClassifyDate: 2");
                categoryId = secondClassifyDatas.get(secondClassifyLenght-1).getId();
				secondClassifyView.setCurrentItem(secondClassifyLenght-1);
			}
            Log.d(TAG, "updateSecondClassifyDate: categoryId "+categoryId);
        }else {
			mCurrentSecondClassify = "";
		}
	}

    /**
     * Callback method to be invoked when current item changed
     *
     * @param wheel    the wheel view whose state has changed
     * @param oldValue the old value of current item
     * @param newValue the new value of current item
     */
    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if(wheel == firstClassifyView){
            mCurrentFirstClassify = firstClassifyDatas.get(newValue).getName();
            updateSecondClassifyDate();
            mCurrentFirstClassifyItem = newValue;
        }
        if(wheel == secondClassifyView){
            mCurrentSecondClassify = secondClassifyDatas.get(newValue).getName();
            mCurrentSecondClassifyItem = newValue;
            categoryId = secondClassifyDatas.get(mCurrentSecondClassifyItem).getId();
            Log.d(TAG, "onChanged: categoryId "+categoryId);
        }

		if(wheel == provinceView){
			mCurrentProvince = provinceBeanList.get(newValue).getProvince();
			updateCityData();
			mCurrentProvinceItem = newValue;
		}

		if(wheel == cityView){
			mCurrentCity = cityBeanList.get(newValue).getCity();
			updateAreaData();
			mCurrentCityItem = newValue;
		}

		if(wheel == areaView){
			mCurrentArea = areaBeanList.get(newValue).getArea();
			areaId = areaBeanList.get(newValue).getId();
			mCurrentAreaItem = newValue;
		}
    }


    private void startAliPay(){
		if(merchantId <= 0){
			ToastUtil.showShort(this,"请上传商户信息");
			return;
		}
		doPay(merchantId);
	}

	private void doPay(int merchantId) {
		VolleyRequestUtil.RequestGet(this,
				Constant.PRE_PAY + "/" + merchantId,
				Constant.TAG_ALIPAY,//商家列表tag
				new VolleyListenerInterface(this,
						VolleyListenerInterface.mListener,
						VolleyListenerInterface.mErrorListener) {
					@Override
					public void onMySuccess(String result) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(result);
							int code = jsonObject.getInt("errcode");
							if (code == 0) {
								orderInfo = jsonObject.getString("data");
								Runnable payRunnable = new Runnable() {
									@Override
									public void run() {
										PayTask alipay = new PayTask(MoneyActivity.this);
										Map<String, String> result = alipay.payV2(orderInfo, true);
										Log.i("msp", result.toString());

										Message msg = new Message();
										msg.what = SDK_PAY_FLAG;
										msg.obj = result;
										mHandler.sendMessage(msg);
									}
								};
								Thread payThread = new Thread(payRunnable);
								payThread.start();
							} else {
								ToastUtil.showShort(MoneyActivity.this, jsonObject.getString("data"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onMyError(VolleyError error) {
						Log.d(TAG, "onMyError: "+error.getMessage());
					}
				},
				true);
	}
}
