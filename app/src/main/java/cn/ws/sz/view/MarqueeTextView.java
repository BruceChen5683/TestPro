package cn.ws.sz.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by BattleCall on 2018/1/18.
 */

public class MarqueeTextView extends TextView{

	private boolean isMarqueeEnable = false;
	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setMarqueeEnable(boolean enable) {
		if (isMarqueeEnable != enable) {
			isMarqueeEnable = enable;
			if (enable) {
				setEllipsize(TextUtils.TruncateAt.MARQUEE);
			} else {
				setEllipsize(TextUtils.TruncateAt.END);
			}
			onWindowFocusChanged(enable);
		}
	}

	public boolean isMarqueeEnable() {
		return isMarqueeEnable;
	}

	@Override
	public boolean isFocused() {
		return isMarqueeEnable;
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(isMarqueeEnable, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(isMarqueeEnable);
	}
}
