package com.viewpagerindicator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

/**
 * Enables custom views to move inline with a viewpager, providing either a parallax effect,
 * where the viewpager scrolls on top of the banner moving in the background, or a fluid horizontal
 * scroll as the viewpager scrolls.
 */
public class BannerPageIndicator extends HorizontalScrollView implements PageIndicator{

    private ViewPager mPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mSelectedTabIndex;
    private int mScrollOrigin = 0;
    private double mBannerWidth = 0, mScreenWidth;
    private boolean isTouchesEnabled = true;

    /**
     * The view that is placed inside the horizontalscrollview, can be customized
     */
    private BannerView mBanner;

    /**
     * Attributes that we set on the bannerview
     */
    private AttributeSet mAttrs;


    public BannerPageIndicator(Context context) {
        this(context, null);
    }

    public BannerPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        mAttrs = attrs;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isTouchesEnabled;
            }
        });
    }

    /**
     * Disallow user interaction on this indicator, use this to prevent unwanted touches when implementing parallax
     */
    public void disableTouches(){
        isTouchesEnabled = false;
    }

    /**
     * Allow user interaction after calling disableTouches()
     */
    public void enableTouches(){
        isTouchesEnabled = true;
    }

    /**
     * Allows for a custom view to be shown (only one at a time)
     * @param bannerView
     */
    public void showCustomBannerView(BannerView bannerView){
        if(mBanner!=null){
            scrollTo(0,0);
            removeView(mBanner);
            mBanner = null;
        }

        mBanner = bannerView;

        //wait for view to be measured in order to get the bannerwidth to acheive accurate scrolling
        mBanner.post(new Runnable() {
            @Override
            public void run() {
                if(mBanner instanceof IBannerView){
                    mBannerWidth = ((IBannerView) mBanner).getBannerWidth();
                } else mBannerWidth = mBanner.getWidth();
            }
        });
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
                mBanner.post(new Runnable() {
                    @Override
                    public void run() {
                        mBannerWidth = mBanner.getMeasuredWidth();
                    }
                });
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
        //we scroll the banner based on how many items in the adapter in order to get an even step
        double scrollRate = (mBannerWidth -(mScreenWidth /2))/((double)mPager.getAdapter().getCount());
        smoothScrollTo((int) (scrollRate * (position + positionOffset)), getBottom());
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
        if(mListener!=null){
            mListener.onPageScrollStateChanged(i);
        }
    }

    public BannerView getBannerView(){
        return mBanner;
    }

    public interface IBannerView{
        public int getBannerWidth();
    }

    public class BannerView extends FrameLayout {

        public BannerView(Context context) {
            super(context);
            setupUI(context);
        }

        public BannerView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setupUI(context);
        }

        public BannerView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            setupUI(context);
        }

        protected void setupUI(Context context){
            TextView title = new TextView(context);
            title.setText("BannerPageIndicator");
            title.setTextSize(75);
            title.setGravity(Gravity.CENTER);

            addView(title, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }
}
