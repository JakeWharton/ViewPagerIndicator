/*
* Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
* Copyright (C) 2015 Huang Haohang <msdx.android@qq.com>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.viewpagerindicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class StripPageIndicator extends HorizontalScrollView implements PageIndicator {
    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on
    private LinearLayout.LayoutParams mDefaultTabLayoutParams;
    private LinearLayout.LayoutParams mExpandedTabLayoutParams;
    public OnPageChangeListener mListener;
    private LinearLayout mTabsContainer;
    private ViewPager mViewPager;
    private int mTabCount;
    private int mCurrentPage;
    private int mLinePosition;
    private float mCurrentPositionOffset;
    private Paint mRectPaint;
    private Paint mDividerPaint;
    private int mIndicatorColor;
    private int mUnderlineColor;
    private int mDividerColor;
    private boolean mShouldExpand;
    private boolean mTextAllCaps;
    private int mScrollOffset;
    private int mIndicatorHeight;
    private int mUnderlineHeight;
    private int mDividerPadding;
    private int mTabPadding;
    private int mDividerWidth;
    private int mTabTextSize;
    private ColorStateList mTabTextColor;
    private Typeface mTabTypeface;
    private int mTabTypefaceStyle = Typeface.BOLD;
    private int mLastScrollX = 0;
    private int mBackgroundResId = R.drawable.vpi__tab_background;
    private Locale mLocale;

    public StripPageIndicator(Context context) {
        this(context, null);
    }

    public StripPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StripPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);
        setWillNotDraw(false);

        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        mTabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mTabsContainer);

        // Load defaults
        final Resources res = getResources();
        mIndicatorColor = res.getColor(R.color.default_strip_indicator_indicator_color);
        mUnderlineColor = res.getColor(R.color.default_strip_indicator_divider_color);
        mDividerColor = res.getColor(R.color.default_strip_indicator_divider_color);
        mShouldExpand = res.getBoolean(R.bool.default_strip_indicator_should_expand);
        mTextAllCaps = res.getBoolean(R.bool.default_strip_indicator_text_all_caps);
        mScrollOffset = res.getDimensionPixelSize(R.dimen.default_strip_indicator_scroll_offset);
        mIndicatorHeight = res.getDimensionPixelSize(R.dimen.default_strip_indicator_indicator_height);
        mUnderlineHeight = res.getDimensionPixelSize(R.dimen.default_strip_indicator_underline_height);
        mDividerPadding = res.getDimensionPixelSize(R.dimen.default_strip_indicator_divider_padding);
        mTabPadding = res.getDimensionPixelSize(R.dimen.default_strip_indicator_tab_padding);
        mDividerWidth = res.getDimensionPixelSize(R.dimen.default_strip_indicator_divider_width);
        mTabTextSize = res.getDimensionPixelSize(R.dimen.default_strip_indicator_text_size);

        // get system attrs (android:textSize and android:textColor)
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyle, 0);
        mTabTextSize = a.getDimensionPixelSize(0, mTabTextSize);
        mTabTextColor = a.getColorStateList(1);
        a.recycle();

        if(mTabTextColor == null) {
            mTabTextColor = res.getColorStateList(R.color.default_strip_indicator_text_color);
        }

        // get custom attrs
        a = context.obtainStyledAttributes(attrs, R.styleable.StripPageIndicator);
        mIndicatorColor = a.getColor(R.styleable.StripPageIndicator_vpiIndicatorColor, mIndicatorColor);
        mUnderlineColor = a.getColor(R.styleable.StripPageIndicator_vpiUnderlineColor, mUnderlineColor);
        mDividerColor = a.getColor(R.styleable.StripPageIndicator_vpiDividerColor, mDividerColor);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.StripPageIndicator_vpiIndicatorHeight, mIndicatorHeight);
        mUnderlineHeight = a.getDimensionPixelSize(R.styleable.StripPageIndicator_vpiUnderlineHeight, mUnderlineHeight);
        mDividerPadding = a.getDimensionPixelSize(R.styleable.StripPageIndicator_vpiDividerPadding, mDividerPadding);
        mTabPadding = a.getDimensionPixelSize(R.styleable.StripPageIndicator_vpiTabPaddingLeftRight, mTabPadding);
        mBackgroundResId = a.getResourceId(R.styleable.StripPageIndicator_vpiTabBackground, mBackgroundResId);
        mShouldExpand = a.getBoolean(R.styleable.StripPageIndicator_vpiShouldExpand, mShouldExpand);
        mScrollOffset = a.getDimensionPixelSize(R.styleable.StripPageIndicator_vpiScrollOffset, mScrollOffset);
        mTextAllCaps = a.getBoolean(R.styleable.StripPageIndicator_vpiTextAllCaps, mTextAllCaps);
        mTabTextSize = a.getDimensionPixelSize(R.styleable.StripPageIndicator_android_textSize, mTabTextSize);
        ColorStateList textColor = a.getColorStateList(R.styleable.StripPageIndicator_android_textColor);
        if (textColor != null) mTabTextColor = textColor;
        a.recycle();

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Style.FILL);
        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mDividerWidth);
        mDefaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mExpandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        if (mLocale == null) {
            mLocale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        this.mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        setTabSelected();
        invalidate();
    }

    private void setTabSelected() {
        for (int i = 0; i < mTabCount; i++) {
            final View child = mTabsContainer.getChildAt(i);
            child.setSelected(i == mCurrentPage);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("OnPageScrolled", position + ":" + positionOffset +":" + positionOffsetPixels);
        mLinePosition = position;
        mCurrentPositionOffset = positionOffset;
        scrollToChild(position, (int) (positionOffset * mTabsContainer.getChildAt(position).getWidth()));
        invalidate();
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            scrollToChild(mViewPager.getCurrentItem(), 0);
        }
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        // select the selected tab
        setTabSelected();
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        final PagerAdapter adapter = mViewPager.getAdapter();
        mTabCount = adapter.getCount();
        IconPagerAdapter iconAdapter = adapter instanceof IconPagerAdapter ? (IconPagerAdapter) adapter : null;

        for (int i = 0; i < mTabCount; i++) {
            if (iconAdapter != null) {
                addIconTab(i, iconAdapter.getIconResId(i));
            } else {
                addTextTab(i, adapter.getPageTitle(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                mCurrentPage = mViewPager.getCurrentItem();
                scrollToChild(mCurrentPage, 0);
                setTabSelected();
            }
        });
    }

    private void addTextTab(final int position, CharSequence title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(position);
            }
        });
        tab.setPadding(mTabPadding, 0, mTabPadding, 0);
        mTabsContainer.addView(tab, position, mShouldExpand ? mExpandedTabLayoutParams : mDefaultTabLayoutParams);
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            v.setBackgroundResource(mBackgroundResId);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                tab.setTypeface(mTabTypeface, mTabTypefaceStyle);
                tab.setTextColor(mTabTextColor);
                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (mTextAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(mLocale));
                    }
                }
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        if (mTabCount == 0) {
            return;
        }
        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) {
            newScrollX -= mScrollOffset;
        }
        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line
        mRectPaint.setColor(mIndicatorColor);

        // default: line below current tab
        View currentTab = mTabsContainer.getChildAt(mLinePosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (mCurrentPositionOffset > 0f && mLinePosition < mTabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(mLinePosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();
            lineLeft += (nextTabLeft - lineLeft) * mCurrentPositionOffset;
            lineRight += (nextTabRight - lineRight) * mCurrentPositionOffset;
        }

        canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height, mRectPaint);
        // draw underline
        mRectPaint.setColor(mUnderlineColor);
        canvas.drawRect(0, height - mUnderlineHeight, mTabsContainer.getWidth(), height, mRectPaint);
        // draw divider
        mDividerPaint.setColor(mDividerColor);
        for (int i = 0; i < mTabCount - 1; i++) {
            View tab = mTabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), mDividerPadding, tab.getRight(), height - mDividerPadding, mDividerPaint);
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.mIndicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.mIndicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.mUnderlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.mDividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.mUnderlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return mUnderlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.mDividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return mDividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.mScrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return mScrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.mShouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return mShouldExpand;
    }

    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.mTabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return mTabTextSize;
    }

    public void setTextColor(int textColor) {
        this.mTabTextColor = ColorStateList.valueOf(textColor);
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.mTabTextColor = getResources().getColorStateList(resId);
        updateTabStyles();
    }

    public ColorStateList getTextColor() {
        return mTabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.mTabTypeface = typeface;
        this.mTabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.mBackgroundResId = resId;
    }

    public int getTabBackground() {
        return mBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.mTabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return mTabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = mCurrentPage;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPage;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}