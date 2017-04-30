package menezes.paulo.safe.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class NotifyingScrollView extends ScrollView {

	public interface OnScrollChangedListener {
		void onScrollChanged(ScrollView who, int l, int t, int oldL, int oldT);
	}
	
	private OnScrollChangedListener mOnScrollChangedListener;
	private boolean mIsOverScrollEnabled = true;
	
	public NotifyingScrollView(Context context) {
		super(context);
	}

	public NotifyingScrollView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}
	
	public NotifyingScrollView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldL, int oldT) {
		super.onScrollChanged(l, t, oldL, oldT);
		if(mOnScrollChangedListener != null)
			mOnScrollChangedListener.onScrollChanged(this, l, t, oldL, oldT);
	}
	
	public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		mOnScrollChangedListener = listener;
	}
	
	public void setOverScrollEnabled(boolean enabled) {
		mIsOverScrollEnabled = enabled;
	}
	
	public boolean isOverScrollEnabled() {
		return mIsOverScrollEnabled;
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return super.overScrollBy(
				deltaX, 
				deltaY, 
				scrollX, 
				scrollY, 
				scrollRangeX, 
				scrollRangeY, 
				mIsOverScrollEnabled ? maxOverScrollX : 0, 
			 	mIsOverScrollEnabled ? maxOverScrollY : 0, 
				isTouchEvent);
	}
}
