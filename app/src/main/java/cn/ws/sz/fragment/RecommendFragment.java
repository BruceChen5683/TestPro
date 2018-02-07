package cn.ws.sz.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ws.sz.R;
import cn.ws.sz.activity.MyRecommendActivity;
import cn.ws.sz.utils.CommonUtils;
import cn.ws.sz.utils.SoftKeyBroadManager;
import cn.ws.sz.utils.ToastUtil;


public class RecommendFragment extends Fragment implements View.OnClickListener,SoftKeyBroadManager.SoftKeyboardStateListener {

    private TextView tvTitle;
    private final static String TAG = "RecommendFragment";
    private RelativeLayout flEnter,flFriend,flMoney;
    private int recommendMode = 0;
    private final static int RECOMMEND_MODE_ENTER = 1;
    private final static int RECOMMEND_MODE_FRIEND = 2;


    private Dialog dialog;
	private EditText etRecommend,etTel;
    private int dialogHeight;
    private  SoftKeyBroadManager mManager;



    public RecommendFragment() {
        // Required empty public constructor
    }


    public static RecommendFragment newInstance(){
        return new RecommendFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        initView(view);
        mManager =new SoftKeyBroadManager(view);
        mManager.addSoftKeyboardStateListener(this);
        return view;
    }

    private void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.title_value);
        tvTitle.setText("推荐");

		flEnter = (RelativeLayout) view.findViewById(R.id.enter);
        flEnter.setOnClickListener(this);

        flFriend = (RelativeLayout) view.findViewById(R.id.friend);
        flFriend.setOnClickListener(this);

        flMoney = (RelativeLayout) view.findViewById(R.id.money);
        flMoney.setOnClickListener(this);

        initDialog();
    }

    private void initDialog() {
        dialog = new Dialog(getActivity(), R.style.recommend_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.recommend_enter, null);
        RelativeLayout llRecommendEnter = (RelativeLayout) root.findViewById(R.id.llRecommendEnter);
		etRecommend = (EditText) root.findViewById(R.id.etRecommend);
		etTel = (EditText) root.findViewById(R.id.etTel);
        llRecommendEnter.setOnClickListener(this);
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
        dialogHeight = lp.y;
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enter:
                recommendMode = RECOMMEND_MODE_ENTER;
                showRecommendEnterDialog();
                break;
            case R.id.friend:
                recommendMode = RECOMMEND_MODE_FRIEND;
                showRecommendFriendDialog();
            case R.id.llRecommendEnter:
				if(TextUtils.isEmpty(etRecommend.getText())){
					ToastUtil.showShort(getActivity(),"请输入姓名");
					return;
				}
				if(TextUtils.isEmpty(etTel.getText())){
					ToastUtil.showShort(getActivity(),"请输入手机号");
					return;
				}
                if(recommendMode == RECOMMEND_MODE_ENTER){
					CommonUtils.showShare(getActivity(),etRecommend.getText()+getResources().getString(R.string.recommend_enter));
                }else if(recommendMode == RECOMMEND_MODE_FRIEND){
					CommonUtils.showShare(getActivity(),etRecommend.getText()+getResources().getString(R.string.recommend_proxy));
                }
				etRecommend.setText("");
				etTel.setText("");
                Log.d(TAG, "onClick: llRecommendEnter");
                break;
            case R.id.money:
                Intent intent = new Intent();
                intent.setClass(getActivity(), MyRecommendActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void showRecommendEnterDialog() {
        if(dialog != null && !dialog.isShowing()){
            TextView tvEnterMoney = (TextView) dialog.findViewById(R.id.tvEnterMoney);
            tvEnterMoney.setText("好友入驻万商，\"推荐人\"填您的手机号，您将推荐得佣金20元");
            dialog.show();
        }
    }

    private void showRecommendFriendDialog() {
        if(dialog != null && !dialog.isShowing()){
            TextView tvEnterMoney = (TextView) dialog.findViewById(R.id.tvEnterMoney);
            tvEnterMoney.setText("推荐好友成为地区万商代理，服务商家，创造财富。");
            dialog.show();
        }
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        Log.d(TAG, "onSoftKeyboardOpened: ");
        if(dialog != null && dialog.isShowing()){
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            Log.d(TAG, "onSoftKeyboardOpened: "+lp.height);
            Log.d(TAG, "onSoftKeyboardOpened: "+keyboardHeightInPx);
            lp.y = keyboardHeightInPx+lp.y;
            dialog.getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        Log.d(TAG, "onSoftKeyboardClosed: ");
        if(dialog != null && dialog.isShowing()){
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.y = dialogHeight;
            dialog.getWindow().setAttributes(lp);
        }
    }
}
