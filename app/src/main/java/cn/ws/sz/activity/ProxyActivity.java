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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.ws.sz.R;
import cn.ws.sz.adater.BusinessPhotoAdapter;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.ImageItem;
import cn.ws.sz.utils.StringUtils;
import cn.ws.sz.view.MyGridView;

import static cn.ws.sz.utils.Constant.CODE_ACTION_IMAGE_CAPTURE;
import static cn.ws.sz.utils.Constant.CODE_IMAGE_SELECT_ACTIVITY;

public class ProxyActivity extends AppCompatActivity {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        initView();



        uploadInfo();
    }

    private void initView(){
        tvTitle = (TextView) findViewById(R.id.title_value);
        tvTitle.setText("申请成为地区代理");

        llReturnBack = (LinearLayout) findViewById(R.id.returnBack);
        llReturnBack.setVisibility(View.VISIBLE);

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
}
