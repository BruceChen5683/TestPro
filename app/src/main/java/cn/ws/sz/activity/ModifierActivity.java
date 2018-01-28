package cn.ws.sz.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.ws.sz.R;
import cn.ws.sz.bean.BusinessBean;
import cn.ws.sz.bean.BusinessStatus;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.bean.ModifierStatus;
import cn.ws.sz.bean.UploadStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import third.autosize.dp.Main;
import third.citypicker.PickCityActivity;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

public class ModifierActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = ModifierActivity.class.getSimpleName();
    private TextView tvTitle,tvBusinessName,tvAddres,tvTel;
    private LinearLayout llReturnBack;
    private BusinessBean businessBean;
    private Button btnSure,btnCancel,btnVerity;
    private EditText etAd,etVer;
    private Map<String,String> parms = new HashMap<>();
    private String phoneNum = "";
    private String yzm = "";
    private Gson gson = new Gson();
    private int type = Constant.MODIFIER_AD_TYPE;
    private boolean bGetYam = true;
    private static final int RE_GET_MSG = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RE_GET_MSG:
                    bGetYam = true;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));


        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);
        tvBusinessName = (TextView) findViewById(R.id.business_name);
        tvAddres = (TextView) findViewById(R.id.tvAddres);
        tvTel = (TextView) findViewById(R.id.tvTel);
        etAd = (EditText) findViewById(R.id.etAd);
        etVer = (EditText) findViewById(R.id.etVer);
        tvTitle = (TextView) findViewById(R.id.title_value);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            businessBean = (BusinessBean) bundle.get("BusinessBean");
            if(businessBean != null){
                if(!TextUtils.isEmpty(businessBean.getName())){
                    tvBusinessName.setText(businessBean.getName());
                }

                if(!TextUtils.isEmpty(businessBean.getAddress())){
                    tvAddres.setText(businessBean.getAddress());
                }

                if(!TextUtils.isEmpty(businessBean.getPhone())){
                    phoneNum = businessBean.getPhone();
                    tvTel.setText(phoneNum);
                }
            }else{//数据无法修改 返回

            }

            type = bundle.getInt("type");
        }

        if(type == Constant.MODIFIER_AD_TYPE){
            tvTitle.setText("修改广告内容");
            etAd.setHint("广告内容，最多200字");

        }else if(type == Constant.MODIFIER_MAIN_PRODUCTS_TYPE){
            tvTitle.setText("修改主营内容");
            etAd.setHint("主营内容，最多500字");

        }


        btnSure = (Button) findViewById(R.id.btnSure);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnVerity = (Button) findViewById(R.id.yzm);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnVerity.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSure:
//                check();
                modifierAd();
                break;
            case R.id.btnCancel:

                break;

            case R.id.yzm:
                if(bGetYam){
                    getYzm();
                }else {
                    ToastUtil.showShort(this,"请一分钟后重新获取");
                }
                break;

            default:
                break;
        }
    }

//    private void check() {
//
//
//    }

    private void checkYam() {

    }

    private void modifierAd(){

        if(TextUtils.isEmpty(etAd.getText())){
            if(type == Constant.MODIFIER_AD_TYPE){
                ToastUtil.showShort(this,"请输入新广告内容");
            }else if(type == Constant.MODIFIER_MAIN_PRODUCTS_TYPE){
                ToastUtil.showShort(this,"请输入新主营内容");
            }
            return;
        }


        if(TextUtils.isEmpty(etVer.getText())){
            ToastUtil.showShort(this,"请输入验证码");
            return;
        }

        Log.d(TAG, "modifierAd: "+yzm);
        Log.d(TAG, "modifierAd: "+etVer.getText().toString());

        if(!yzm.equals(etVer.getText().toString())){
            ToastUtil.showShort(this,"验证码输入错误");
            return;
        }

        if(type == Constant.MODIFIER_AD_TYPE){
            parms.put("adWord",etAd.getText().toString());
        }else if(type == Constant.MODIFIER_MAIN_PRODUCTS_TYPE){
            parms.put("mainProducts",etAd.getText().toString());
        }

        parms.put("id",businessBean.getId()+"");


        VolleyRequestUtil.RequestPost(this,
                type == Constant.MODIFIER_AD_TYPE ? Constant.URL_MODIFIR_ADWORDS : Constant.URL_MODIFIR_MAINPRODUCTS,
                type == Constant.MODIFIER_AD_TYPE ? Constant.TAG_MODIFIER_ADWORDS: Constant.TAG_MODIFIER_MAINPRODUCTS,
                parms,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: "+result);
                        ModifierStatus status = gson.fromJson(result,ModifierStatus.class);
                        if(status.getErrcode() == 0){
                            ToastUtil.showShort(ModifierActivity.this,"修改成功");

                            Intent mIntent = new Intent();
                            mIntent.putExtra("newValue",etAd.getText().toString() );
                            // 设置结果，并进行传送
                            ModifierActivity.this.setResult(RESULT_OK, mIntent);
                            ModifierActivity.this.finish();

                        }else {
                            ToastUtil.showShort(ModifierActivity.this,"修改失败"+"---"+status.getData());
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
    }

    private void getYzm(){

        VolleyRequestUtil.RequestGet(this,
                Constant.URL_SEND_MSG + "/" + businessBean.getPhone(),
                Constant.TAG_SEND_MSG,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        ModifierStatus status = gson.fromJson(result,ModifierStatus.class);
                        if(status.getErrcode() == 0){
                            ToastUtil.showShort(ModifierActivity.this,"验证码发送成功");
                            yzm = status.getData();
                            bGetYam = false;
                            mHandler.sendEmptyMessageDelayed(RE_GET_MSG,60000);
                        }else {
                            ToastUtil.showShort(ModifierActivity.this,"验证码获取失败,请勿连续获取");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        ToastUtil.showShort(ModifierActivity.this,"验证码获取失败,请检查网络");
                    }
                },
                true);
    }
}
