package cn.ws.sz.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by chenjianliang on 2018/1/17.
 */

public class ImageLayout extends RelativeLayout implements ImageAware{
    public ImageLayout(Context context) {
        super(context);
    }

    public ImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Returns {@linkplain ViewScaleType scale type} which is used for
     * scaling image for this image aware view. Must <b>NOT</b> return <b>null</b>.
     */
    @Override
    public ViewScaleType getScaleType() {
        return ViewScaleType.FIT_INSIDE;
    }

    /**
     * Returns wrapped Android {@link View View}. Can return <b>null</b> if no view is wrapped or view was
     * collected by GC.<br />
     * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
     */
    @Override
    public View getWrappedView() {
        return null;
    }

    /**
     * Returns a flag whether image aware view is collected by GC or whatsoever. If so then ImageLoader stop processing
     * of task for this image aware view and fires
     * {@link ImageLoadingListener#onLoadingCancelled(String, * View) ImageLoadingListener#onLoadingCancelled(String, View)} callback.<br />
     * Mey be called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
     *
     * @return <b>true</b> - if view is collected by GC and ImageLoader should stop processing this image aware view;
     * <b>false</b> - otherwise
     */
    @Override
    public boolean isCollected() {
        return false;
    }

    /**
     * Sets image drawable into this image aware view.<br />
     * Displays drawable in this image aware view
     * {@linkplain DisplayImageOptions.Builder#showImageForEmptyUri(
     *Drawable) for empty Uri},
     * {@linkplain DisplayImageOptions.Builder#showImageOnLoading(
     *Drawable) on loading} or
     * {@linkplain DisplayImageOptions.Builder#showImageOnFail(
     *Drawable) on loading fail}. These drawables can be specified in
     * {@linkplain DisplayImageOptions display options}.<br />
     * Also can be called in {@link BitmapDisplayer BitmapDisplayer}.< br />
     * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
     *
     * @param drawable
     * @return <b>true</b> if drawable was set successfully; <b>false</b> - otherwise
     */
    @Override
    public boolean setImageDrawable(Drawable drawable) {
        return false;
    }

    /**
     * Sets image bitmap into this image aware view.<br />
     * Displays loaded and decoded image {@link Bitmap} in this image view aware.
     * Actually it's used only in
     * {@link BitmapDisplayer BitmapDisplayer}.< br />
     * Is called on UI thread if ImageLoader was called on UI thread. Otherwise - on background thread.
     *
     * @param bitmap
     * @return <b>true</b> if bitmap was set successfully; <b>false</b> - otherwise
     */
    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        this.setBackground(new BitmapDrawable(getResources(),bitmap));
        return false;
    }
}
