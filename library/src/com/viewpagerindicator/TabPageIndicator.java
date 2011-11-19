/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viewpagerindicator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {
    Runnable mTabSelector;
    
    private final OnClickListener mTabClickListener = new OnClickListener() {
    	@Override
        public void onClick(View view) {
            Tab tabView = (Tab) view;
            setCurrentItem(tabView.mPosition);
            mViewPager.setCurrentItem(tabView.mPosition);
            final int tabCount = mTabLayout.getChildCount();
            for (int i = 0; i < tabCount; i++) {
                final View child = mTabLayout.getChildAt(i);
                child.setSelected(child == view);
            }
        }
    };
    
    private LinearLayout mTabLayout;
    private ViewPager mViewPager;
    private OnPageChangeListener mListener;
    private TitleProvider mTitleProvider;

    int mMaxTabWidth;
    private int mSelectedTabIndex;
    

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = createTabLayout();
        addView(mTabLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.FILL_PARENT));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 &&
                (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            if (childCount > 2) {
                mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mListener != null) {
			mListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int position) {
		setCurrentItem(position);
		if (mListener != null) {
			mListener.onPageSelected(position);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
    	final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        if (!(adapter instanceof TitleProvider)) {
            throw new IllegalStateException("ViewPager adapter must implement TitleProvider to be used with TitlePageIndicator.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        mTitleProvider = (TitleProvider)adapter;
        notifyDataSetChanged();
	}
	
	public void notifyDataSetChanged() {
		mTabLayout.removeAllViews();
		final int count = ((PagerAdapter)mTitleProvider).getCount();
		for (int i = 0; i < count; i++) {
			mTabLayout.addView(createTabView(i, mTitleProvider.getTitle(i)));
		}
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
        mSelectedTabIndex = item;
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = i == item;
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

    private LinearLayout createTabLayout() {
    	//Workaround for not being able to specify a defStyle pre-3.0
    	return (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.vpi__tab_container, null);
    }

    public void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private Tab createTabView(int position, String text) {
    	//Workaround for not being able to pass a defStyle on pre-3.0
    	final Tab tabView = new Tab(getContext(), null, R.attr.vpiTabPageIndicatorStyle);
        tabView.init(this, position, text);
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        return tabView;
    }

    public static class Tab extends FrameLayout {
    	private TabPageIndicator mParent;
        int mPosition;
        private String mText;
        private TextView mTextView;

        public Tab(Context context, AttributeSet attrs, int defStyle) {
        	super(context, attrs, defStyle);
        }
        
        public void init(TabPageIndicator parent, int position, String text) {
        	mParent = parent;
        	mPosition = position;
        	mText = text;

            update();
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mParent.mMaxTabWidth > 0 && getMeasuredWidth() > mParent.mMaxTabWidth) {
            	final int spec = MeasureSpec.makeMeasureSpec(mParent.mMaxTabWidth, MeasureSpec.EXACTLY);
                super.onMeasure(spec, heightMeasureSpec);
            }
        }

        public void update() {
            if (mTextView == null) {
                TextView textView = new TextView(getContext(), null, R.attr.vpiTabTextStyle);
                textView.setEllipsize(TruncateAt.END);
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER_VERTICAL;
                textView.setLayoutParams(lp);
                addView(textView);
                mTextView = textView;
            }
            mTextView.setText(mText);
        }
    }
}
