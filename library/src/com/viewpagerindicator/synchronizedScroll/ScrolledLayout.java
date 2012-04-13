package com.viewpagerindicator.synchronizedScroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.viewpagerindicator.synchronizedScroll.MyScrollView.MyScrollViewDelegate;

public class ScrolledLayout extends FrameLayout implements MyScrollViewDelegate {
  private View mMovable;
  private View mToMove;
  private int lastT;

  public ScrolledLayout(Context context) {
    this(context, null);
  }

  public ScrolledLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ScrolledLayout(Context context, AttributeSet attrs,
      int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top,
      int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    ViewGroup.LayoutParams params = mMovable.getLayoutParams();
    params.width = mToMove.getWidth();
    params.height = mToMove.getHeight();
    mMovable.setLayoutParams(params);

    mToMove.offsetTopAndBottom(mMovable.getTop());
    lastT = mMovable.getTop();
  }

  @Override
  public void onScrollChanged(int t, int oldt) {
    final int top = mMovable.getTop();
    if ( t >= top ) {
      mToMove.offsetTopAndBottom((t-lastT));
      lastT = t;
    } else if ( lastT != top ) {
      mToMove.offsetTopAndBottom(top-lastT);
      lastT = top;
    }
  }

  public void setMovable(View movable) {
    mMovable = movable;
  }

  public void setToMove(View toMove) {
    mToMove = toMove;
  }
}
