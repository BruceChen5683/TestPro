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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessPhotoAdapter;
import cn.ws.sz.adater.ChooseClassifyAdapter;
import cn.ws.sz.bean.ClassifyBean;
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
import third.wheelviewchoose.OnWheelChangedListener;
import third.wheelviewchoose.WheelView;

import static cn.ws.sz.utils.Constant.CODE_ACTION_IMAGE_CAPTURE;
import static cn.ws.sz.utils.Constant.CODE_IMAGE_SELECT_ACTIVITY;

public class MoneyActivity extends AppCompatActivity implements View.OnClickListener,OnWheelChangedListener{

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

    /*
     *  choose
     * */
    private PopupWindow mPopupWindow;
    private WheelView firstClassifyView;
    private WheelView secondClassifyView;
    private List<ClassifyBean> firstClassifyDatas = new ArrayList<>();
    private List<ClassifyBean> secondClassifyDatas = new ArrayList<>();
    private String mCurrentFirstClassify,mCurrentSecondClassify;
    private int mCurrentFirstClassifyItem,mCurrentSecondClassifyItem;
    private TextView btn_myinfo_sure,btn_myinfo_cancel;
    private ChooseClassifyAdapter firstClassifyAdapter,secondClassifyAdapter;
    private final int TEXTSIZE=17;//选择器的字体大小
    private View rootView;
    private RelativeLayout rlSettledClassify;//选择分类
    private TextView tvSettledClassify2;
    private int categoryId = -1;

    private RelativeLayout rlSettledName,rlSettledAddres,rlSettledAddres2,rlSettledCoordinate,rlSettledPhone,rlSettledTel;
    private LinearLayout llMainPoructs,llAd;


    private ScrollView scrollView;




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

    public MoneyActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = LayoutInflater.from(this).inflate(R.layout.activity_money,null);
        setContentView(rootView);

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

        rlSettledName.setOnClickListener(this);
        rlSettledAddres.setOnClickListener(this);
        rlSettledAddres2.setOnClickListener(this);
        rlSettledCoordinate.setOnClickListener(this);
        rlSettledPhone.setOnClickListener(this);
        rlSettledTel.setOnClickListener(this);
        llMainPoructs.setOnClickListener(this);
        llAd.setOnClickListener(this);




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
        etDetailAddress = (EditText) findViewById(R.id.etDetailAddress);
        tvSettledTel2 = (EditText) findViewById(R.id.tvSettledTel2);
        mainProducts = (EditText) findViewById(R.id.mainProducts);
        ad = (EditText) findViewById(R.id.ad);

        rlSettledClassify = (RelativeLayout) findViewById(R.id.rlSettledClassify);
        rlSettledClassify.setOnClickListener(this);
        tvSettledClassify2 = (TextView) findViewById(R.id.tvSettledClassify2);


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
            case R.id.btn_myinfo_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.btn_myinfo_sure:
                tvSettledClassify2.setText(mCurrentFirstClassify+"-"+mCurrentSecondClassify);
                mPopupWindow.dismiss();
                break;
            case R.id.rlSettledClassify:
                initPopupWindow();
                mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
                break;

            case R.id.rlSettledName:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.rlSettledAddres:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.rlSettledAddres2:
                onClickEditTextParent((ViewGroup) v);
                break;
            case R.id.rlSettledCoordinate:
                onClickEditTextParent((ViewGroup) v);
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

        uploadImage();
        Log.d(TAG, "payAction: ");

        //check

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
        params.put("categoryId",String.valueOf(categoryId));
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

    private void initpopData() {


        firstClassifyDatas = WSApp.firstCategroyList;

        mCurrentFirstClassify = firstClassifyDatas.get(0).getName();
        firstClassifyAdapter = new ChooseClassifyAdapter(this,firstClassifyDatas);
        firstClassifyAdapter.setTextSize(TEXTSIZE);
        firstClassifyView.setViewAdapter(firstClassifyAdapter);

        if(mCurrentFirstClassifyItem != 0){
            firstClassifyView.setCurrentItem(mCurrentFirstClassifyItem);
        }

        updateSecondClassifyDate();
    }

    private void updateSecondClassifyDate() {
        int pCurrent = firstClassifyView.getCurrentItem();

        ToastUtil.showLong(this,firstClassifyDatas.size()+"");
        if(firstClassifyDatas.size() > 0){
            secondClassifyDatas = WSApp.secondCategroyMap.get(firstClassifyDatas.get(pCurrent).getId());
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
                categoryId = secondClassifyDatas.get(mCurrentSecondClassifyItem).getId();
                secondClassifyView.setCurrentItem(mCurrentSecondClassifyItem);
            }else {
                mCurrentSecondClassify = secondClassifyDatas.get(secondClassifyLenght-1).getName();
                categoryId = secondClassifyDatas.get(secondClassifyLenght-1).getId();
                secondClassifyView.setCurrentItem(secondClassifyLenght-1);
            }
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
        }
    }
}
