package cn.ws.sz.activity;

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
import cn.ws.sz.bean.ModifierStatus;
import cn.ws.sz.bean.UploadStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.ToastUtil;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

public class ModifierActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = ModifierActivity.class.getSimpleName();
    private TextView tvTitle,tvBusinessName,tvAddres,tvTel;
    private LinearLayout llReturnBack;
    private BusinessBean businessBean;
    private Button btnSure,btnCancel,btnVerity;
    private EditText etAd;
    private Map<String,String> parms = new HashMap<>();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);

        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("修改广告内容");
        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);

        tvBusinessName = (TextView) findViewById(R.id.business_name);
        tvAddres = (TextView) findViewById(R.id.tvAddres);
        tvTel = (TextView) findViewById(R.id.tvTel);

        etAd = (EditText) findViewById(R.id.etAd);

        btnSure = (Button) findViewById(R.id.btnSure);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnVerity = (Button) findViewById(R.id.yzm);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);




        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            businessBean = (BusinessBean) bundle.get("BusinessBean");
        }


        if(!TextUtils.isEmpty(businessBean.getName())){
            tvBusinessName.setText(businessBean.getName());
        }

        if(!TextUtils.isEmpty(businessBean.getAddress())){
            tvAddres.setText(businessBean.getAddress());
        }

        if(!TextUtils.isEmpty(businessBean.getPhone())){
            tvTel.setText(businessBean.getPhone());
        }




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSure:
                check();
                modifierAd();
                break;
            case R.id.btnCancel:

                break;

            case R.id.yzm:

                break;

            default:
                break;
        }
    }

    private void check() {
        if(TextUtils.isEmpty(etAd.getText())){
            ToastUtil.showShort(this,"请输入新广告内容");
            return;
        }

        checkYam();
        parms.put("adWord",etAd.getText().toString());
        parms.put("id",businessBean.getId()+"");

    }

    private void checkYam() {

    }

    private void modifierAd(){
        VolleyRequestUtil.RequestPost(this,
                Constant.URL_MODIFIR_ADWORDS,
                Constant.TAG_MODIFIER_ADWORDS,
                parms,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: "+result);
                        ModifierStatus status = gson.fromJson(result,ModifierStatus.class);
                        if(status.getErrcode() == 0){
                            ToastUtil.showShort(ModifierActivity.this,"广告修改成功");
                        }else {
                            ToastUtil.showShort(ModifierActivity.this,"广告修改失败"+"---"+status.getData());
                        }
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
    }
}
