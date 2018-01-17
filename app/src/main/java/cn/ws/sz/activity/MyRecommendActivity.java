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

import org.w3c.dom.Text;

import cn.ws.sz.R;
import cn.ws.sz.utils.CheckUtils;
import cn.ws.sz.utils.ToastUtil;

public class MyRecommendActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout llReturnBack;
    private TextView tvTitle;
    private Button lookUp;
    private EditText etTel,etIcCard;
    private final static String TAG = "MyRecommendActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recommend);

        initView();
    }

    private void initView() {

        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);
        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("查看我的推荐");

        lookUp = (Button) findViewById(R.id.lookup);
        etTel = (EditText) findViewById(R.id.etTel);
        etIcCard = (EditText) findViewById(R.id.etIcCard);

        llReturnBack.setOnClickListener(this);
        lookUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.returnBack:
                this.finish();
                break;
            case R.id.lookup:
                verify();
                break;
        }
    }

    private void verify(){
        if(TextUtils.isEmpty(etTel.getText())){
            ToastUtil.showShort(this,"请输入手机号码");
            return;
        }

        if(TextUtils.isEmpty(etIcCard.getText())){
            ToastUtil.showShort(this,"请输入身份证号码");
            return;
        }

        if(!CheckUtils.isMobile(etTel.getText().toString())){
            ToastUtil.showShort(this,"请输入有效手机号码");
            Log.d(TAG, "verify: "+etTel.getText().toString());
            return;
        }

        if(!CheckUtils.checkIdcard(etIcCard.getText().toString())){
            ToastUtil.showShort(this,"请输入有效身份证号码");
            return;
        }

        Log.d(TAG, "verify: -----");
    }
}
