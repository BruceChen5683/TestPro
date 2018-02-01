package cn.ws.sz.activity;

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

import org.w3c.dom.Text;

import cn.ws.sz.R;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.bean.UploadStatus;
import cn.ws.sz.utils.CheckUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

public class MyRecommendActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout llReturnBack;
    private TextView tvTitle;
    private Button lookUp;
    private EditText etTel,etIcCard;
    private final static String TAG = "MyRecommendActivity";
    private Gson gson = new Gson();
    private TextView tvAllRecommend,tvTodayRecommend;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tvAllRecommend.setText(msg.arg1+"家");
            tvTodayRecommend.setText(msg.arg1/2+"家");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recommend);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));

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

        tvAllRecommend = (TextView) findViewById(R.id.tvAllRecommend);
        tvTodayRecommend = (TextView) findViewById(R.id.tvTodayRecommend);


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
            default:break;
        }
    }

    private void verify(){
//        if(TextUtils.isEmpty(etTel.getText())){
//            ToastUtil.showShort(this,"请输入手机号码");
//            return;
//        }
//
//        if(TextUtils.isEmpty(etIcCard.getText())){
//            ToastUtil.showShort(this,"请输入身份证号码");
//            return;
//        }
//
//        if(!CheckUtils.isMobile(etTel.getText().toString())){
//            ToastUtil.showShort(this,"请输入有效手机号码");
//            Log.d(TAG, "verify: "+etTel.getText().toString());
//            return;
//        }
//
//        if(!CheckUtils.checkIdcard(etIcCard.getText().toString())){
//            ToastUtil.showShort(this,"请输入有效身份证号码");
//            return;
//        }

        
        loadData();
        Log.d(TAG, "verify: -----");
    }

    private void loadData() {
        VolleyRequestUtil.RequestGet(this,
                Constant.URL_QUERY_FINDNUM + "/" + etTel.getText(),
                Constant.TAG_QUERY_FINDNUM,//不同一级分类tag
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        UploadStatus status = gson.fromJson(result,UploadStatus.class);
                        if(status != null && status.getErrcode() == 0){
                            Message msg = new Message();
                            msg.arg1 = status.getData();
                            handler.sendMessage(msg);
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: "+error.getMessage());
                        ToastUtil.showShort(MyRecommendActivity.this,"未查询到相关推荐数据");
                    }
                },
                true);
    }


}
