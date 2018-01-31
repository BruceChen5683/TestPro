package cn.ws.sz.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessItemAdapter;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessDetailStatus;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.bean.CollectHistoryBeanCollections;
import cn.ws.sz.bean.CollectHistroyBean;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.DeviceUtils;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.SoftKeyBroadManager;
import cn.ws.sz.utils.SoftKeyBroadManager.SoftKeyboardStateListener;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.view.ImageLayout;
import third.ACache;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

import static cn.ws.sz.utils.Constant.MODIFIER_AD_TYPE;

public class BusinessDetailActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,SoftKeyboardStateListener {
    private RelativeLayout rlMainBusiness;
    private RelativeLayout rlAd;
    private final static String TAG = "BusinessDetailActivity";
    private RelativeLayout ivBack, ivCollect, rlFixedPhone, rlTel;//返回，收藏，手机，电话
    private String tel = "";

    private ListView lvSimilar;
    private BusinessItemAdapter adapter;
    private List<BusinessBean> data = new ArrayList<BusinessBean>();


    private TextView tvFixedPhone,tvTel;
	RelativeLayout rlModifierAd;
    /**
     *  BusinessBean
     * */
    private TextView tvBusinessName,tvVistor,tvAddres;
    private ImageView ivHot;

    private int secondCategroy = 0;//用于获取同类商家
    private int pageId = 0;//页码
    private int areaId = 110101;//区域
    private Gson gson;
    private BusinessBean businessBean = null;
	private int merchantId = 1;
    private  SoftKeyBroadManager mManager;
    private Dialog adDiaglog;

    private int dialogHeight;

    private TextView tvTivMainBusiness2,tvAd2,tvModifier;
    private ImageLayout rlLogo;

    private int type = -1;
    private ImageView ivLabel;

	private ACache mACache;
	private final static int UPDATE_MERCHANT = 1;

	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//根据bundle更新数据
			setBusinessBeanToUi();
		}
	};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mACache = ACache.get(this);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_business_detail,null);
        setContentView(rootView);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.monyey_text_color));

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
			merchantId = bundle.getInt(Constant.KEY_EXTRA_MERCHANT_ID,1);
        }

        gson = new Gson();

        initView();

		loadBusinessData();
        loadSimilarData();

        mManager =new SoftKeyBroadManager(rootView);
        mManager.addSoftKeyboardStateListener(this);

        initDialog();

    }

	private void loadBusinessData() {
		VolleyRequestUtil.RequestGet(this,
				Constant.URL_DETAIL + merchantId,
				Constant.TAG_DETAIL,//商家列表tag
				new VolleyListenerInterface(this,
						VolleyListenerInterface.mListener,
						VolleyListenerInterface.mErrorListener) {
					@Override
					public void onMySuccess(String result) {
						Log.d(TAG, "onMySuccess: "+result);
						BusinessDetailStatus status = gson.fromJson(result,BusinessDetailStatus.class);
						businessBean = status.getData();
						uiHandler.sendEmptyMessage(UPDATE_MERCHANT);
					}

					@Override
					public void onMyError(VolleyError error) {
						Log.d(TAG, "onMyError: ");
					}
				},
				true);
	}

	private void initDialog() {
        adDiaglog = new Dialog(this, R.style.recommend_dialog);
        adDiaglog.setCancelable(true);
        adDiaglog.setCanceledOnTouchOutside(true);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_ad, null);
		rlModifierAd = (RelativeLayout) root.findViewById(R.id.rlModifierAd);
        rlModifierAd.setOnClickListener(this);
        adDiaglog.setContentView(root);
        Window dialogWindow = adDiaglog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogAnimation); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = DeviceUtils.getDeviceScreeWidth(this); // 宽度
        lp.height = (int) getResources().getDimension(R.dimen.dp_198);
        dialogHeight = lp.height;
        lp.alpha = 1.0f; // 透明度
        dialogWindow.setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mManager.removeSoftKeyboardStateListener(this);
    }

    private void initView() {

        ivLabel = (ImageView) findViewById(R.id.ivLabel);

        rlMainBusiness = (RelativeLayout) findViewById(R.id.rlMainBusiness);
        rlAd = (RelativeLayout) findViewById(R.id.rlAd);

        ivBack = (RelativeLayout) findViewById(R.id.rlBack);
        ivCollect = (RelativeLayout) findViewById(R.id.rlCollect);
        rlFixedPhone = (RelativeLayout) findViewById(R.id.rlFixedPhone);
        rlTel = (RelativeLayout) findViewById(R.id.rlTel);

        tvFixedPhone = (TextView) findViewById(R.id.tvFixedPhone);
        tvTel = (TextView) findViewById(R.id.tvTel);


        tvBusinessName = (TextView) findViewById(R.id.business_name);
        ivHot = (ImageView) findViewById(R.id.ivHot);
        tvVistor = (TextView) findViewById(R.id.vistor);
        tvAddres = (TextView) findViewById(R.id.tvAddres);


        lvSimilar = (ListView) findViewById(R.id.lvSimilar);
        adapter = new BusinessItemAdapter(this,data);
        lvSimilar.setAdapter(adapter);
        CommonUtils.setListViewHeightBasedOnChildren(lvSimilar);

        tvTivMainBusiness2 = (TextView) findViewById(R.id.tvTivMainBusiness2);
        tvAd2 = (TextView) findViewById(R.id.tvAd2);

        rlLogo = (ImageLayout) findViewById(R.id.rlLogo);
        rlMainBusiness.setOnClickListener(this);
        rlAd.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivCollect.setOnClickListener(this);
        rlFixedPhone.setOnClickListener(this);
        rlTel.setOnClickListener(this);
        lvSimilar.setOnItemClickListener(this);
    }

    private void setBusinessBeanToUi() {

		CollectHistoryBeanCollections collectHistoryBeanCollections;
		if(mACache.getAsObject(Constant.CACHE_HISTROY) == null){
			collectHistoryBeanCollections = new CollectHistoryBeanCollections();
		}else{
			collectHistoryBeanCollections = new CollectHistoryBeanCollections().setCollectHistoryBeanCollections((CollectHistoryBeanCollections) mACache.getAsObject(Constant.CACHE_HISTROY));
		}
		CollectHistroyBean tempCHB = new CollectHistroyBean(businessBean.getId(),businessBean.getName());
		collectHistoryBeanCollections.addHistroyBean(tempCHB);
		mACache.put(Constant.CACHE_HISTROY,collectHistoryBeanCollections,ACache.TIME_DAY*10);

        if(businessBean != null){
            if(!TextUtils.isEmpty(businessBean.getName())){
                tvBusinessName.setText(businessBean.getName());
            }

            if(!TextUtils.isEmpty(businessBean.getType())){
                if(businessBean.getType().equals("vip")){
                    ivLabel.setVisibility(View.VISIBLE);
                }else {
                    ivLabel.setVisibility(View.GONE);
                }
            }

            if(businessBean.getIsHot() != 1){
                ivHot.setVisibility(View.GONE);
            }

            if(businessBean.getViewNum() > 0){
                tvVistor.setText("已浏览"+ businessBean.getViewNum() + "次");
            }else{
                tvVistor.setText("已浏览"+ 120 + "次");
            }

            if(!TextUtils.isEmpty(businessBean.getAddress())){
                tvAddres.setText(businessBean.getAddress());
            }

            if(!TextUtils.isEmpty(businessBean.getCellphone())){
                tvFixedPhone.setText(businessBean.getCellphone());
            }

            if(!TextUtils.isEmpty(businessBean.getPhone())){
                tvTel.setText(businessBean.getPhone());
            }

            if(!TextUtils.isEmpty(businessBean.getMainProducts())){
                tvTivMainBusiness2.setText(businessBean.getMainProducts());
            }

            if(!TextUtils.isEmpty(businessBean.getAdWord())){
                tvAd2.setText(businessBean.getAdWord());
            }

            if(!TextUtils.isEmpty(businessBean.getLogoUrl())){
				if(businessBean.getLogoUrl().startsWith("http")){//
					CommonUtils.setImageView2(businessBean.getLogoUrl(),rlLogo);
				}else{
					CommonUtils.setImageView2(Constant.BASEURL+businessBean.getLogoUrl(),rlLogo);
				}

            }

        }
    }

    private void loadSimilarData() {
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_BUSINESS_LIST + secondCategroy + "/" + pageId + "/" + areaId  + "/"+0,
                Constant.TAG_BUSINESS_LIST_SIMILAR,//商家列表tag
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        BusinessStatus status = gson.fromJson(result,BusinessStatus.class);
                        data.clear();
                        if(status.getData() != null && status.getData().size() > 0){
                            if(status.getData().size() > Constant.HOT_BUSINESS_NUM){//只显示前俩个同类商家
                                for (int i = 0; i < Constant.HOT_BUSINESS_NUM;i++){
                                    data.add(status.getData().get(i));
                                }
                            }else {
                                data.addAll(status.getData());
                            }
                        }
                        adapter.notifyDataSetChanged();
                        CommonUtils.setListViewHeightBasedOnChildren(lvSimilar);
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMainBusiness:
                type = Constant.MODIFIER_MAIN_PRODUCTS_TYPE;
                showBottomDialog(type);
                break;
            case R.id.rlAd:
                type = MODIFIER_AD_TYPE;
                showBottomDialog(type);
                break;
            case R.id.rlModifierAd:
                Intent intent = new Intent();
                intent.putExtra("BusinessBean",businessBean);
                intent.putExtra("type",type);
                intent.setClass(BusinessDetailActivity.this, ModifierActivity.class);
                startActivityForResult(intent,Constant.CODE_MODIFIER_ACTIVITY);
                break;
            case R.id.rlBack:
                this.finish();
                break;
            case R.id.rlCollect:
				CollectHistoryBeanCollections collectHistoryBeanCollections;
				if(mACache.getAsObject(Constant.CACHE_COLLECT) == null){
					collectHistoryBeanCollections = new CollectHistoryBeanCollections();
				}else{
					collectHistoryBeanCollections = new CollectHistoryBeanCollections().setCollectHistoryBeanCollections((CollectHistoryBeanCollections) mACache.getAsObject(Constant.CACHE_COLLECT));
				}
				CollectHistroyBean tempCHB = new CollectHistroyBean(businessBean.getId(),businessBean.getName());
				if(collectHistoryBeanCollections.containsCollectHistroyBean(tempCHB)){
					ToastUtil.showShort(this,"取消收藏");
				}else {
					ToastUtil.showShort(this,"收藏成功");
				}
				collectHistoryBeanCollections.addOrRemoveCollectBean(tempCHB);
				mACache.put(Constant.CACHE_COLLECT,collectHistoryBeanCollections,ACache.TIME_DAY*10);
                break;
            case R.id.rlFixedPhone:
                tel = (String) tvFixedPhone.getText();
                CommonUtils.callByDefault(this,tel);
                break;
            case R.id.rlTel:
                tel = (String) tvTel.getText();
                CommonUtils.callByDefault(this,tel);
                break;
            default:
                break;
        }
    }

    private void showBottomDialog(final int type) {
        if(adDiaglog != null && !adDiaglog.isShowing()){
            TextView dialogTitle = (TextView) adDiaglog.findViewById(R.id.dialogTitle);
            tvModifier = (TextView) adDiaglog.findViewById(R.id.tvModifier);
            TextView dialogEt = (TextView) adDiaglog.findViewById(R.id.dialogEt);

            if(type == Constant.MODIFIER_AD_TYPE){
				rlModifierAd.setVisibility(View.VISIBLE);
                dialogTitle.setText("广告");
                tvModifier.setText("修改广告内容");
                if(tvAd2 != null && !TextUtils.isEmpty(tvAd2.getText())){
                    dialogEt.setText(tvAd2.getText());
                }
            }else {
				rlModifierAd.setVisibility(View.INVISIBLE);
                dialogTitle.setText("主营");
//                tvModifier.setText("修改主营内容");
//                if(tvTivMainBusiness2 != null && !TextUtils.isEmpty(tvTivMainBusiness2.getText())){
//                    dialogEt.setText(tvTivMainBusiness2.getText());
//                }
            }

            adDiaglog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            adDiaglog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            //布局位于状态栏下方
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            //全屏
//                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            //隐藏导航栏
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    if (Build.VERSION.SDK_INT >= 19) {
                        uiOptions |= 0x00001000;
                    } else {
                        uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                    }
                    adDiaglog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                }
            });

            adDiaglog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_EXTRA_MERCHANT_ID,data.get(position).getId());
        intent.setClass(this, BusinessDetailActivity.class);
        startActivity(intent);

    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        if(adDiaglog != null && adDiaglog.isShowing()){
            WindowManager.LayoutParams lp = adDiaglog.getWindow().getAttributes();
            lp.height = lp.height +keyboardHeightInPx;
            adDiaglog.getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        if(adDiaglog != null && adDiaglog.isShowing()){
            WindowManager.LayoutParams lp = adDiaglog.getWindow().getAttributes();
            lp.height = dialogHeight;
            adDiaglog.getWindow().setAttributes(lp);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == Constant.CODE_MODIFIER_ACTIVITY){
                Log.d(TAG, "onActivityResult: " + data.getStringExtra("newValue"));
                String newValue = data.getStringExtra("newValue");
                hideDialog();
                if(!TextUtils.isEmpty(newValue)){
                    if (type == MODIFIER_AD_TYPE){
                        tvAd2.setText(newValue);
                    }else{
                        tvTivMainBusiness2.setText(newValue);
                    }
                }

            }
        }
    }

    private void hideDialog() {
        if(adDiaglog != null && adDiaglog.isShowing()) {
            adDiaglog.dismiss();
        }
    }
}
