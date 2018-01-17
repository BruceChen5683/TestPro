package cn.ws.sz.activity;

/**
 * Created by chenjianliang on 2018/1/10.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cn.ws.sz.R;
import cn.ws.sz.adater.ImageSelectAdapter;
import cn.ws.sz.utils.AlbumHelper;
import cn.ws.sz.utils.Constant;
import cn.ws.sz.utils.ImageItem;

/**
 * 这个是进入相册显示所有图片的界面
 *
 */
public class ImageSelectActivity extends AppCompatActivity implements OnClickListener {
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    //gridView的adapter
    private ImageSelectAdapter gridImageAdapter;
    private TextView titleName;
    private LinearLayout titleRightButton;
    private View bottomView;
    // 上传按钮
    private Button uploadButton;
    // 完成按钮
    private Button finishButton;
    // 上传目录
    private TextView uploadDir;
    private List<ImageItem> dataList;
    private AlbumHelper helper;
    public static Bitmap bitmap;
    // 上传种类
    private String type;
    // 上传至对应分组
    private String groupId;
    private String groupName;
    // 选择完图片后，是否需要直接上传
    private String isUploadNeeded;
    private int selectedNum;
    private ArrayList<ImageItem> selectedImages = new ArrayList<ImageItem>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        groupId = intent.getStringExtra("groupId");
        groupName = intent.getStringExtra("groupName");
        isUploadNeeded = intent.getStringExtra("isUploadNeeded");
        selectedNum = intent.getIntExtra("selectedNum", 0);
        // 初始化view
        initView();
    }

    // 初始化，给一些对象赋值
    private void initView() {
        titleName = (TextView) findViewById(R.id.title_value);
        titleName.setText("选择照片");

        titleRightButton = (LinearLayout)findViewById(R.id.right_button);
        ((Button)titleRightButton.getChildAt(0)).setText("取消");
        ((Button)titleRightButton.getChildAt(0)).setBackgroundResource(android.R.color.transparent);
        titleRightButton.setVisibility(View.VISIBLE);
        titleRightButton.setOnClickListener(this);

        bottomView = (View)findViewById(R.id.bottom_layout);
        // 上传按钮
        uploadButton = (Button)findViewById(R.id.upload);
        uploadButton.setOnClickListener(this);
        // 上传目录
        uploadDir = (TextView) findViewById(R.id.upload_dir);
        if (groupName != null && !"".equals(groupName)) {
            uploadDir.setText("上传至目录： " + groupName);
        }
        uploadDir.setOnClickListener(this);
        // 完成按钮
        finishButton = (Button)findViewById(R.id.finish);
        finishButton.setOnClickListener(this);

        helper = AlbumHelper.getInstance();
        helper.init(this);
        dataList = helper.getImageList();

        gridView = (GridView) findViewById(R.id.image_select_gridview);
        gridImageAdapter = new ImageSelectAdapter(this, dataList, selectedImages);
        gridView.setAdapter(gridImageAdapter);
        gridImageAdapter.setOnItemClickListener(new ImageSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ToggleButton toggleButton,
                                    int position, boolean isChecked,Button chooseBt) {
                if (isChecked) {
                    if ("false".equals(isUploadNeeded) && (selectedNum + selectedImages.size()) == Constant.MAX_BUSINESS_PHOTO) {
                        toggleButton.setChecked(false);
                        chooseBt.setVisibility(View.GONE);
                        Toast.makeText(ImageSelectActivity.this, "最多选择"+Constant.MAX_BUSINESS_PHOTO+"张照片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    chooseBt.setVisibility(View.VISIBLE);
                    selectedImages.add(dataList.get(position));
                } else {
                    chooseBt.setVisibility(View.GONE);
                    selectedImages.remove(dataList.get(position));
                }
                if (selectedImages.size() > 0) {
                    if ("false".equals(isUploadNeeded)) {
                        uploadDir.setVisibility(View.GONE);
                        uploadButton.setVisibility(View.GONE);
                        finishButton.setVisibility(View.VISIBLE);
                        finishButton.setText("完成(" + (selectedNum + selectedImages.size()) + "/" + Constant.MAX_BUSINESS_PHOTO + ")");
                    } else {
                        uploadButton.setText("上传(" + selectedImages.size() + ")");
                    }
                    bottomView.setVisibility(View.VISIBLE);
                } else {
                    bottomView.setVisibility(View.GONE);
                }
            }
        });

        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 取消
            case R.id.right_button:
                finish();
                break;
            // 上传
            case R.id.upload:
//                String[] filePaths = new String[selectedImages.size()];
//                for (int i = 0;i < selectedImages.size();i++) {
//                    filePaths[i] = selectedImages.get(i).getImagePath();
//                }
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("type", type);
//                params.put("groupId", groupId);
//                UploadAsyncTask mAsyncTask = new UploadAsyncTask(this, config.UPLOAD_URL, params, filePaths);
//                mAsyncTask.setCallback(new MyTaskCallback());
//                mAsyncTask.execute();
                break;
            case R.id.upload_dir:
//                Intent intent = new Intent(this, MoveToGroupActivity.class);
//                intent.putExtra("type", Integer.parseInt(type));
//                intent.putExtra("fromPage", "upload");
//                startActivityForResult(intent, 1);
                break;
            case R.id.finish:
                //SendHelpActivity.SelectedImages.addAll(selectedImages);
                Intent intent;
                intent = new Intent();
                intent.putExtra("data", selectedImages);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            groupId = data.getStringExtra("groupId");
            groupName = data.getStringExtra("groupName");
            uploadDir.setText("上传至目录： " + groupName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private class MyTaskCallback implements TaskCallback{
//
//        @Override
//        public void doback(String result) {
//            ImageSelectActivity.this.setResult(RESULT_OK);
//            ImageSelectActivity.this.finish();
//        }
//
//    }
}

