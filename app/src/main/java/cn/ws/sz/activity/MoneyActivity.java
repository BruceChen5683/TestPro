package cn.ws.sz.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessPhotoAdapter;
import cn.ws.sz.bean.ClassifyStatus;
import cn.ws.sz.bean.UploadStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.ImageItem;
import cn.ws.sz.utils.StringUtils;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.utils.WSApp;
import cn.ws.sz.view.MyGridView;
import third.volley.PostUploadRequest;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

import static cn.ws.sz.utils.Constant.CODE_ACTION_IMAGE_CAPTURE;
import static cn.ws.sz.utils.Constant.CODE_IMAGE_SELECT_ACTIVITY;

public class MoneyActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = MoneyActivity.class.getSimpleName();
    private MyGridView postPhoto;
    private BusinessPhotoAdapter adapter;
    private String randomFileName;
    private boolean isShowDelete = false;

    private TextView tvTitle;
    private LinearLayout llReturnBack;

    private Button submitBtn;

    private Dialog dialog;
    private int dialogHeight;

    private RelativeLayout llRecommendEnter,rlAli,rlWeChat;
    private ImageView ivAliConfim,ivWeChatConfim;

    private Map<String,String> params = new HashMap<>();

    private Map<String,String[]> paramsImg = new HashMap<>();//key parm || value是长度为2的字符串数组， 下标0存放的是文件的本地路径，下标1存放的是传送到服务器中的文件名


    public static ArrayList<ImageItem> SelectedImages = new ArrayList<ImageItem>();


    public static final int MSG_PAY_SUCCESS = 1;
    public static final int MSG_PAY_FAIL = 2;

    private Gson gson;

    private EditText tvSettledName2,etDetailAddress,tvSettledTel2,mainProducts,ad;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MSG_PAY_SUCCESS:
                    Intent intent = new Intent();
                    intent.setClass(MoneyActivity.this,AfterPayActivity.class);
                    startActivity(intent);
                    break;
                case MSG_PAY_FAIL:
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        gson = new Gson();
        initView();

        initDialog();
    }

    private void initView() {


        tvTitle= (TextView) findViewById(R.id.title_value);
        tvTitle.setText("入驻万商");
        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);
        llReturnBack.setOnClickListener(this);


        postPhoto = (MyGridView) findViewById(R.id.gvPostPhoto);
        postPhoto.setSelector(new ColorDrawable(Color.TRANSPARENT));

        adapter = new BusinessPhotoAdapter(this);
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
        etDetailAddress = (EditText) findViewById(R.id.etDetailAddress);
        tvSettledTel2 = (EditText) findViewById(R.id.tvSettledTel2);
        mainProducts = (EditText) findViewById(R.id.mainProducts);
        ad = (EditText) findViewById(R.id.ad);


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
                    startActivityForResult(intent, CODE_IMAGE_SELECT_ACTIVITY);
                }
            }
        }).create();

        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == CODE_IMAGE_SELECT_ACTIVITY){
                ArrayList<ImageItem> selectData = (ArrayList<ImageItem>) data.getSerializableExtra("data");
                for (int i = 0; i < selectData.size(); i++) {
                    Bitmap bitmap = CommonUtils.getScaleBitmap(selectData.get(i).getImagePath(), 200, 200);
                    selectData.get(i).setBitmap(bitmap);
                }
                SelectedImages.addAll(selectData);
                adapter.notifyDataSetChanged();
            }else if(requestCode == CODE_ACTION_IMAGE_CAPTURE){
                Log.d(TAG, "onActivityResult: CODE_ACTION_IMAGE_CAPTURE");
                String picPath = Environment.getExternalStorageDirectory() + File.separator + randomFileName;
                ImageItem imageItem = new ImageItem();
                imageItem.setImagePath(picPath);
                Bitmap bitmap = CommonUtils.getScaleBitmap(picPath, 200, 200);
                imageItem.setBitmap(bitmap);
                SelectedImages.add(imageItem);
                adapter.notifyDataSetChanged();
                randomFileName = "";
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
                break;
            case R.id.rlWeChat:
                changPayConfim(false);
                break;
            case R.id.rlAli:
                changPayConfim(true);
                break;
            default:
                break;
        }
    }

    private void hideDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    private void payAction() {

        uploadImage();
        Log.d(TAG, "payAction: ");

        //check
        if(TextUtils.isEmpty(tvSettledName2.getText())){
            ToastUtil.showLong(this,"请输入商家名称");
            hideDialog();
            CommonUtils.showSoftInputFromWindow(this,tvSettledName2);
            return;
        }

        if(TextUtils.isEmpty(etDetailAddress.getText())){
            ToastUtil.showLong(this,"请输入详细地址");
            hideDialog();
            CommonUtils.showSoftInputFromWindow(this,etDetailAddress);
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


//        params.put("name","上传测试");
//        params.put("region","110101");
//        params.put("categoryId","63");
//        params.put("cellphone","13912345678");
//        params.put("mainProducts","主营业务测试中，游戏代理");
//        params.put("adWord","自定义广告奥");
//        params.put("phone","01012345");
//        params.put("lng","163.78965");
//        params.put("lat","38.98765");
//        params.put("address","测试江苏延迟");

        params.put("name",tvSettledName2.getText().toString());
        params.put("region","110101");
        params.put("categoryId","63");
        params.put("cellphone",tvSettledTel2.getText().toString());
        params.put("mainProducts",mainProducts.getText().toString());
        params.put("adWord",ad.getText().toString());
        params.put("phone","01012345");
        params.put("lng","163.78965");
        params.put("lat","38.98765");
        params.put("address",etDetailAddress.getText().toString());

        Log.d(TAG, "payAction: --------uploadBusinessInfo");
        uploadBusinessInfo(params);

//        handler.sendEmptyMessageDelayed(MSG_PAY_SUCCESS,1000);
    }

    private void uploadImage() {


        for (int i = 0; i < SelectedImages.size(); i++) {

            Log.d(TAG, "uploadImage: " + SelectedImages.get(i).getImagePath());
            paramsImg.clear();
            paramsImg.put("pic", new String[]{SelectedImages.get(i).getImagePath(), SelectedImages.get(i).getImagePath()});

            //发起请求

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
                            if (status.getErrcode() == 0) {
                                ToastUtil.showShort(MoneyActivity.this, "商家信息上传成功");
                            } else {
                                ToastUtil.showShort(MoneyActivity.this, "商家信息上传失败");
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
                        }else {
                            ToastUtil.showShort(MoneyActivity.this,"商家信息上传失败");
                        }


                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
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
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
        lp.height = getResources().getDisplayMetrics().heightPixels/4;
        dialogHeight = lp.height;
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
    }
}
