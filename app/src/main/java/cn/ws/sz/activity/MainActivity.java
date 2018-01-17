package cn.ws.sz.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;

import cn.ws.sz.R;
import cn.ws.sz.fragment.ClassifyFragment;
import cn.ws.sz.fragment.HomeFragment;
import cn.ws.sz.fragment.PersonalFragment;
import cn.ws.sz.fragment.RecommendFragment;
import cn.ws.sz.fragment.SearchFragment;
import cn.ws.sz.service.LocationService;
import cn.ws.sz.utils.WSApp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvHome,tvClassify,tvSearch,tvPersonal,tvRecommand;
    public HomeFragment fragmentHome;
    public ClassifyFragment fragmentClassify;
    public SearchFragment fragmentSearch;
    private PersonalFragment fragmentPersonal;
    private RecommendFragment fragmentRecommand;
    private FrameLayout ly_content;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        bindView();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //UI组件初始化与事件绑定
    private void bindView() {
        tvHome = (TextView) findViewById(R.id.tv_home);
        tvClassify = (TextView) findViewById(R.id.tv_classify);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        tvPersonal = (TextView) findViewById(R.id.tv_personal);
        tvRecommand = (TextView) findViewById(R.id.tv_recommend);

        ly_content = (FrameLayout) findViewById(R.id.fragment_container);

        tvHome.setOnClickListener(this);
        tvClassify.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        tvPersonal.setOnClickListener(this);
        tvRecommand.setOnClickListener(this);

        onClick(tvHome);

    }


    //reset textView status
    public void selected(){
        tvHome.setSelected(false);
        tvClassify.setSelected(false);
        tvSearch.setSelected(false);
        tvPersonal.setSelected(false);
        tvRecommand.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction){
        if(fragmentHome != null){
            transaction.hide(fragmentHome);
        }
        if(fragmentClassify != null){
            transaction.hide(fragmentClassify);
        }
        if(fragmentSearch != null){
            transaction.hide(fragmentSearch);
        }
        if(fragmentPersonal != null){
            transaction.hide(fragmentPersonal);
        }
        if(fragmentRecommand != null){
            transaction.hide(fragmentRecommand);
        }
    }

    public void changeFragment(int id){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch(id){
            case R.id.tv_home:
                selected();
                tvHome.setSelected(true);
                if(fragmentHome == null){
                    fragmentHome = HomeFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentHome);
                }else{
                    transaction.show(fragmentHome);
                }
                break;

            case R.id.tv_classify:
                selected();
                tvClassify.setSelected(true);
                if(fragmentClassify == null){
                    fragmentClassify = ClassifyFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentClassify);
                }else{
                    transaction.show(fragmentClassify);
                }


                break;

            case R.id.tv_search:
                selected();
                tvSearch.setSelected(true);
                if(fragmentSearch == null){
                    fragmentSearch = SearchFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentSearch);
                }else{
                    transaction.show(fragmentSearch);
                }
                break;

            case R.id.tv_personal:
                selected();
                tvPersonal.setSelected(true);
                if(fragmentPersonal == null){
                    fragmentPersonal = PersonalFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentPersonal);
                }else{
                    transaction.show(fragmentPersonal);
                }
                break;

            case R.id.tv_recommend:
                selected();
                tvRecommand.setSelected(true);
                if(fragmentRecommand == null){
                    fragmentRecommand = RecommendFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentRecommand);
                }else{
                    transaction.show(fragmentRecommand);
                }
                break;

            default:
                break;
        }

        transaction.commit();
    }

    @Override
    public void onClick(View v) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch(v.getId()){
            case R.id.tv_home:
                selected();
                tvHome.setSelected(true);
                if(fragmentHome == null){
                    fragmentHome = HomeFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentHome);
                }else{
                    transaction.show(fragmentHome);
                }
                break;

            case R.id.tv_classify:
                selected();
                tvClassify.setSelected(true);
                if(fragmentClassify == null){
                    fragmentClassify = ClassifyFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentClassify);
                }else{
                    transaction.show(fragmentClassify);
                }
                break;

            case R.id.tv_search:
                selected();
                tvSearch.setSelected(true);
                if(fragmentSearch == null){
                    fragmentSearch = SearchFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentSearch);
                }else{
                    transaction.show(fragmentSearch);
                }
                break;

            case R.id.tv_personal:
                selected();
                tvPersonal.setSelected(true);
                if(fragmentPersonal == null){
                    fragmentPersonal = PersonalFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentPersonal);
                }else{
                    transaction.show(fragmentPersonal);
                }
                break;

            case R.id.tv_recommend:
                selected();
                tvRecommand.setSelected(true);
                if(fragmentRecommand == null){
                    fragmentRecommand = RecommendFragment.newInstance();
                    transaction.add(R.id.fragment_container,fragmentRecommand);
                }else{
                    transaction.show(fragmentRecommand);
                }
                break;

            default:
                break;
        }

        transaction.commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    protected boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    protected void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
