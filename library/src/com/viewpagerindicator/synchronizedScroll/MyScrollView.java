package com.viewpagerindicator.synchronizedScroll;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

  public interface MyScrollViewDelegate {
    void onScrollChanged(int t, int oldt);
  }

  private MyScrollViewDelegate mDelegate;

  public MyScrollView(Context context) {
    this(context, null, 0);
  }

  public MyScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if ( mDelegate != null ) {
      mDelegate.onScrollChanged(t, oldt);
    }
  }

  public MyScrollViewDelegate getDelegate() {
    return mDelegate;
  }

  public void setDelegate(MyScrollViewDelegate delegate) {
    this.mDelegate = delegate;
  }
}
