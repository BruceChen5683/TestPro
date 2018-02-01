package cn.ws.sz.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessPhotoAdapter;
import cn.ws.sz.bean.UploadStatus;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.Eyes;
import cn.ws.sz.utils.ImageItem;
import cn.ws.sz.utils.StringUtils;
import cn.ws.sz.utils.ToastUtil;
import cn.ws.sz.view.MyGridView;
import third.volley.VolleyListenerInterface;
import third.volley.VolleyRequestUtil;

import static cn.ws.sz.utils.Constant.CODE_ACTION_IMAGE_CAPTURE;
import static cn.ws.sz.utils.Constant.CODE_IMAGE_SELECT_ACTIVITY;

public class ProxyActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ProxyActivity.class.getSimpleName();
    private TextView tvTitle;
    private LinearLayout llReturnBack;
    private Map<String, String[]> fileMap = new HashMap<>();
    private Map<String,String> parmMap = new HashMap<>();

    private MyGridView postPhoto;
    private BusinessPhotoAdapter adapter;
    private String randomFileName;
    private boolean isShowDelete = false;
    public static ArrayList<ImageItem> SelectedImages = new ArrayList<ImageItem>();
    private Gson gson = new Gson();
    private Button submitMoney;

    private RelativeLayout rlProxyName,rlProxyPhone,rlIdCard,rlAli,rlWeChat;
    private EditText tvProxyName2,tvProxyPhone2,tvIdCard2,tvAli2,tvWeChat2;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
        Eyes.setStatusBarColor(this, ContextCompat.getColor(this,R.color.title_bg));

        initView();

    }

    private void initView(){
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        rlProxyName = (RelativeLayout) findViewById(R.id.rlProxyName);
        rlProxyPhone = (RelativeLayout) findViewById(R.id.rlProxyPhone);
        rlIdCard = (RelativeLayout) findViewById(R.id.rlIdCard);
        rlAli = (RelativeLayout) findViewById(R.id.rlAli);
        rlWeChat = (RelativeLayout) findViewById(R.id.rlWeChat);
        rlProxyName.setOnClickListener(this);
        rlProxyPhone.setOnClickListener(this);
        rlIdCard.setOnClickListener(this);
        rlAli.setOnClickListener(this);
        rlWeChat.setOnClickListener(this);

        tvProxyName2 = (EditText) findViewById(R.id.tvProxyName2);
        tvProxyPhone2 = (EditText) findViewById(R.id.tvProxyPhone2);
        tvIdCard2 = (EditText) findViewById(R.id.tvIdCard2);
        tvAli2 = (EditText) findViewById(R.id.tvAli2);
        tvWeChat2 = (EditText) findViewById(R.id.tvWeChat2);



        submitMoney = (Button) findViewById(R.id.submitMoney);
        submitMoney.setOnClickListener(this);

        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("申请成为地区代理");

        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);
        llReturnBack.setOnClickListener(this);

        postPhoto = (MyGridView) findViewById(R.id.gvPostPhoto);
        postPhoto.setSelector(new ColorDrawable(Color.TRANSPARENT));

        adapter = new BusinessPhotoAdapter(this, Constant.PHOTO_TYPE_IDCARD);
        postPhoto.setAdapter(adapter);
        postPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == SelectedImages.size()){
//                    Log.d(TAG, "onItemClick: ");
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
    }

    private void uploadInfo() {

//        private EditText tvProxyName2,tvProxyPhone2,tvIdCard2,tvAli2,tvWeChat2;

        if(TextUtils.isEmpty(tvProxyName2.getText())){
            ToastUtil.showLong(this,tvProxyName2.getHint());
            CommonUtils.showSoftInputFromWindow(this,tvProxyName2);
            Log.d(TAG, "checkEditText: return");
            return;
        }
        if(TextUtils.isEmpty(tvProxyPhone2.getText())){
            ToastUtil.showLong(this,tvProxyPhone2.getHint());
            CommonUtils.showSoftInputFromWindow(this,tvProxyPhone2);
            Log.d(TAG, "checkEditText: return");
            return;
        }
        if(TextUtils.isEmpty(tvIdCard2.getText())){
            ToastUtil.showLong(this,tvIdCard2.getHint());
            CommonUtils.showSoftInputFromWindow(this,tvIdCard2);
            Log.d(TAG, "checkEditText: return");
            return;
        }
        if(TextUtils.isEmpty(tvAli2.getText())){
            ToastUtil.showLong(this,tvAli2.getHint());
            CommonUtils.showSoftInputFromWindow(this,tvAli2);
            Log.d(TAG, "checkEditText: return");
            return;
        }
        if(TextUtils.isEmpty(tvWeChat2.getText())){
            ToastUtil.showLong(this,tvWeChat2.getHint());
            CommonUtils.showSoftInputFromWindow(this,tvWeChat2);
            Log.d(TAG, "checkEditText: return");
            return;
        }
        if(SelectedImages.size() == 0){
            ToastUtil.showLong(this,"请上传身份证");
            showGetPhotoDialog();
            return;
        }


        fileMap.clear();
        parmMap.clear();
        fileMap.put("image", new String[]{SelectedImages.get(0).getImagePath(), SelectedImages.get(0).getImagePath()});
        parmMap.put("name",tvProxyName2.getText().toString());
        parmMap.put("idCard",tvIdCard2.getText().toString());
        parmMap.put("cellphone",tvProxyPhone2.getText().toString());
        parmMap.put("alipayAccount",tvAli2.getText().toString());
        parmMap.put("weixinAccount",tvWeChat2.getText().toString());


        //发起请求

        VolleyRequestUtil.RequestPostFileParm(this,
                Constant.URL_UPLOAD_AGENT_INFO,
                Constant.TAG_AGENT_INFO_UPLOAD,
                fileMap,
                parmMap,
                new VolleyListenerInterface(this,
                        VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        Log.d(TAG, "onMySuccess: " + result);
                        UploadStatus status = gson.fromJson(result, UploadStatus.class);
                        if (status.getErrcode() == 0) {
                            ToastUtil.showShort(ProxyActivity.this, "代理信息上传成功");
                        } else {
                            ToastUtil.showShort(ProxyActivity.this, "代理信息上传失败");
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Log.d(TAG, "onMyError: ");
                    }
                },
                true);
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
                    Intent intent = new Intent(ProxyActivity.this, ImageSelectActivity.class);
                    intent.putExtra("isUploadNeeded", "false");
                    intent.putExtra("selectedNum", SelectedImages.size());
                    intent.putExtra("photoType",Constant.PHOTO_TYPE_IDCARD);
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
                uploadInfo();
                break;
            case R.id.rlProxyName:onClickEditTextParent((ViewGroup) v);break;
            case R.id.rlProxyPhone:onClickEditTextParent((ViewGroup) v);break;
            case R.id.rlIdCard:onClickEditTextParent((ViewGroup) v);break;
            case R.id.rlAli:onClickEditTextParent((ViewGroup) v);break;
            case R.id.rlWeChat:onClickEditTextParent((ViewGroup) v);break;
            case R.id.returnBack:this.finish();break;

            default:
                break;
        }
    }

    private void onClickEditTextParent(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for (int i = 0;i < count;i++){
            if (viewGroup.getChildAt(i) instanceof EditText){
                EditText et = (EditText) viewGroup.getChildAt(i);
                CommonUtils.showSoftInputFromWindow(this,et);
                return;
            }
        }
    }

    private void checkEditText(EditText editText) {
        if(TextUtils.isEmpty(editText.getText())){
            ToastUtil.showLong(this,editText.getHint()+"1111");
            CommonUtils.showSoftInputFromWindow(this,editText);
            Log.d(TAG, "checkEditText: return");
            return;
        }

    }
}
