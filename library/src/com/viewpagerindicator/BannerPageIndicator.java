package com.viewpagerindicator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

/**
 * Created By: andrewgrosner
 * Date: 10/18/13
 * Contributors:
 * Description:
 */
public class BannerPageIndicator extends HorizontalScrollView implements PageIndicator{

    private ViewPager mPager;
    private int mSelectedTabIndex;

    private ViewPager.OnPageChangeListener mListener;

    private BannerView mBanner;

    private AttributeSet mAttrs;

    private int scrollOrigin = 0;
    private double bannerWidth = 0, screenWidth;

    public BannerPageIndicator(Context context) {
        this(context, null);
    }

    public BannerPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        mAttrs = attrs;
    }

    public void disableTouches(){
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * Allows for a custom view to be shown
     * @param bannerView
     */
    public void showCustomBannerView(BannerView bannerView){
        if(mBanner!=null){
            scrollTo(0,0);
            removeView(mBanner);
            mBanner = null;
        }

        mBanner = bannerView;
        mBanner.post(new Runnable() {
            @Override
            public void run() {
                if(mBanner instanceof BannerView.IBannerView){
                    bannerWidth = ((BannerView.IBannerView) mBanner).getBannerWidth();
                } else bannerWidth = mBanner.getWidth();
            }
        });
        screenWidth = bannerView.getContext().getResources().getDisplayMetrics().widthPixels;
        addView(mBanner);
    }

    @Override
    public void setViewPager(ViewPager view) {
        if(mPager!=view){
            if(mPager!=null){
                mPager.setOnPageChangeListener(null);
            }

            if(mBanner==null){
                mBanner = new BannerView(getContext(), mAttrs, R.attr.vpiBannerViewStyle);

                addView(mBanner);
            }

            final PagerAdapter adapter = view.getAdapter();
            if(adapter==null){
                throw new IllegalStateException("View pager does not have an adapter instance.");
            } else if(adapter.getCount()<=0){
                throw new IllegalStateException("Adapter needs to have at least one element");
            }

            mPager = view;
            mPager.setOnPageChangeListener(this);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == View.MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }


    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if(mPager==null){
            throw new IllegalStateException("Viewpager has not been bound");
        }

        mSelectedTabIndex = item;
        mPager.setCurrentItem(item);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {

        requestLayout();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(mListener!=null){
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        double scrollRate = (bannerWidth-screenWidth)/((double)mPager.getAdapter().getCount());
        smoothScrollTo((int) (scrollRate * (position + positionOffset)), getBottom());
        //smoothScrollBy((int) (scrollRate*positionOffset),0);
    }

    @Override
    public void onPageSelected(int i) {
        setCurrentItem(i);
        if(mListener!=null){
            mListener.onPageSelected(i);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
