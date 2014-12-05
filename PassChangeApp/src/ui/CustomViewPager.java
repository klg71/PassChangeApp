package ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class CustomViewPager extends ViewPager {

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//		if (getCurrentItem() != 0) {
//			Log.e("Debug", "other item scoll check"+dx+" "+((WebViewFragment) ((MainFragmentStatePager) getAdapter())
//					.getCustomItem(getCurrentItem())).getWebView()
//					.canScrollHorizontally(-dx));
//			return ((WebViewFragment) ((MainFragmentStatePager) getAdapter())
//					.getCustomItem(getCurrentItem())).getWebView()
//					.canScrollHorizontally(-dx);
//		} else {
//			return super.canScroll(v, checkV, dx, x, y);
//		}
//	}
//
//	
//
//	@Override
//	public boolean canScrollHorizontally(int direction) {
//		if (getCurrentItem() != 0) {
//			return !((WebViewFragment) ((MainFragmentStatePager) getAdapter())
//					.getCustomItem(getCurrentItem())).getWebView()
//					.canScrollHorizontally(direction);
//		} else {
//			return super.canScrollHorizontally(direction);
//		}
//	}

}
