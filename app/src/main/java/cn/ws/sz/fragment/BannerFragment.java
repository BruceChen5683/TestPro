package cn.ws.sz.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ws.sz.R;
import cn.ws.sz.handler.BannerHandler;
import cn.ws.sz.utils.DeviceUtils;
import cn.ws.sz.view.BaseViewPager;

/**
 * Created by chenjianliang on 2018/1/7.
 */

public class BannerFragment extends Fragment implements ViewPager.OnPageChangeListener{

	private final static String TAG = BannerFragment.class.getSimpleName();
    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private ImageView[] indicators;

    private FrameLayout viewPagerFragmentLayout;
    private LinearLayout indicatorLayout;
    private TextView tvType;
    private BaseViewPager viewPager;
    private BaseViewPager parentViewPager;
    private ViewPagerAdapter adapter;
    private BannerHandler handler;
    private int time = 5000;// 轮播时间
    private int currentPosition = 0;
    private boolean isScrolling = false; // 滚动框是否滚动着
    private boolean isCycle = true; // 是否循环
    private boolean isWheel = false; // 是否轮播
    private long releaseTime = 0; // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换
    private final int WHEEL = 100; // 转动
    private final int WHEEL_WAIT = 101; // 等待

    private ImageCycleViewListener mImageCycleViewListener;
//    private String[] studyImg;
//    private String[] types = { "血液", "尿液", "骨髓", "寄生虫(粪及其他)", "胸液/腹液/心包积液",
//            "脑髓液", "肺泡灌洗液", "关节腔积液", "腹透液", "其他" };


    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null && !getActivity().isFinishing()
                    && isWheel) {
                long now = System.currentTimeMillis();
                // 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
                if (now - releaseTime > time - 500) {
                    handler.sendEmptyMessage(WHEEL);
                } else {
                    handler.sendEmptyMessage(WHEEL_WAIT);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View bannerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_banner_contet, null);

        viewPager = (BaseViewPager) bannerView.findViewById(R.id.viewPager);
        indicatorLayout = (LinearLayout) bannerView
                .findViewById(R.id.layout_viewpager_indicator);

        viewPagerFragmentLayout = (FrameLayout) bannerView
                .findViewById(R.id.layout_viewager_content);

        handler = new BannerHandler(getActivity()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == WHEEL && imageViews.size() != 0) {
                    if (!isScrolling) {
                        int max = imageViews.size() + 1;
                        int position = (currentPosition + 1)
                                % imageViews.size();
                        viewPager.setCurrentItem(position, true);
                        if (position == max) { // 最后一页时回到第一页
                            viewPager.setCurrentItem(1, false);
                        }
                    }
                    releaseTime = System.currentTimeMillis();
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, time);
                    return;
                }
                if (msg.what == WHEEL_WAIT && imageViews.size() != 0) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, time);
                }
            }
        };

        return bannerView;
    }


    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    public void setData(List<ImageView> views, ImageCycleViewListener listener) {
        mImageCycleViewListener = listener;
        this.imageViews.clear();
        if (views.size() == 0) {
            viewPagerFragmentLayout.setVisibility(View.GONE);
            return;
        }
		Log.d(TAG, "setData: after gone");
		viewPagerFragmentLayout.setVisibility(View.VISIBLE);
        for (ImageView item : views) {
            this.imageViews.add(item);
        }
        int ivSize = views.size();
        // 设置指示器
        indicators = new ImageView[ivSize];
        if (isCycle)
            indicators = new ImageView[ivSize - 2];
        indicatorLayout.removeAllViews();
        for (int i = 0; i < indicators.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.view_cycle_viewpager_indicator, null);
            indicators[i] = (ImageView) view.findViewById(R.id.image_indicator);
            indicatorLayout.addView(view);
        }
        adapter = new ViewPagerAdapter();
        // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
        setIndicator(0);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        int showPosition = 0;
        if (isCycle) {
            showPosition = showPosition + 1;
        }
        viewPager.setCurrentItem(showPosition);
    }

    /**
     * 设置指示器居中，默认指示器在右方
     */
    public void setIndicatorCenter() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorLayout.setLayoutParams(params);
    }


    /**
     * 是否循环，默认不开启，开启前，请将views的最前面与最后面各加入一个视图，用于循环
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    public void setVisibility(int visibility){
		viewPagerFragmentLayout.setVisibility(visibility);
	}

    /**
     * 是否处于循环状态
     */
    public boolean isCycle() {
        return isCycle;
    }

    /**
     * 设置是否轮播，默认不轮播,轮播一定是循环的
     */
    public void setWheel(boolean isWheel) {
        this.isWheel = isWheel;
        isCycle = true;
        if (isWheel) {
            handler.postDelayed(runnable, time);
        }
    }

    /**
     * 是否处于轮播状态
     */
    public boolean isWheel() {
        return isWheel;
    }

    /**
     * 释放指示器高度，可能由于之前指示器被限制了高度，此处释放
     */
    public void releaseHeight() {
        getView().getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        refreshData();
    }
    /**
     * 设置fragment高度，屏高 1/3
     */
    public void setHeight() {
        getView().getLayoutParams().height = DeviceUtils
                .getActivityScreenHeight(getActivity()) / 5;
    }

    /**
     * 设置轮播暂停时间，即没多少秒切换到下一张视图.默认5000ms
     *
     * @param time
     *            毫秒为单位
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * 刷新数据，当外部视图更新后，通知刷新数据
     */
    public void refreshData() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 隐藏CycleViewPager
     */
    public void hide() {
        viewPagerFragmentLayout.setVisibility(View.GONE);
    }

    /**
     * 返回内置的viewpager
     *
     * @return viewPager
     */
    public BaseViewPager getViewPager() {
        return viewPager;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int arg0) {
        int max = imageViews.size() - 1;
        int position = arg0;
        currentPosition = arg0;
        if (isCycle) {
            if (arg0 == 0) {
                currentPosition = max - 1;
            } else if (arg0 == max) {
                currentPosition = 1;
            }
            position = currentPosition - 1;
        }
        setIndicator(position);
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param arg0 The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 1) { // viewPager在滚动
            isScrolling = true;
            return;
        } else if (arg0 == 0) { // viewPager滚动结束
            if (parentViewPager != null)
                parentViewPager.setScrollable(true);
            releaseTime = System.currentTimeMillis();
            viewPager.setCurrentItem(currentPosition, false);
        }
        isScrolling = false;
    }

    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable
     */
    public void setScrollable(boolean enable) {
        viewPager.setScrollable(enable);
    }

    /**
     * 返回当前位置,循环时需要注意返回的position包含之前在views最前方与最后方加入的视图，即当前页面试图在views集合的位置
     *
     * @return
     */
    public int getCurrentPostion() {
        return currentPosition;
    }

    /**
     * 设置指示器
     *
     * @param selectedPosition
     *            默认指示器位置
     */
    private void setIndicator(int selectedPosition) {

//        tvType.setText(types[selectedPosition]);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(R.mipmap.icon_point);
        }
        if (indicators.length > selectedPosition)
            indicators[selectedPosition]
                    .setBackgroundResource(R.mipmap.icon_point_pre);
    }


    /**
     * 如果当前页面嵌套在另一个viewPager中，为了在进行滚动时阻断父ViewPager滚动，可以 阻止父ViewPager滑动事件
     * 父ViewPager需要实现ParentViewPager中的setScrollable方法
     */
    public void disableParentViewPagerTouchEvent(BaseViewPager parentViewPager) {
        if (parentViewPager != null)
            parentViewPager.setScrollable(false);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub
            ImageView v;
            v = imageViews.get(position);
            if (mImageCycleViewListener != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageCycleViewListener
                                .onImageClick(currentPosition, v);
                    }
                });
            }

            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            container.addView(v);
            return v;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

    }

    /**
     * 轮播控件的监听事件
     */
    public static interface ImageCycleViewListener {
        // 单击图片事件
        public void onImageClick(int postion, View imageView);
    }
}
