/*
 * Copyright (C) 2012 Jake Wharton
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova
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
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.TextView;

/*
 * Represents a textual indicator like <current_page_number> / <total_pages> (by default).
 * User can create his own template.
 */

public class TextPageIndicator extends TextView implements PageIndicator {

	private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private String mTextTemplate;
    
	public TextPageIndicator(Context context) {
		super(context);
		init(context, null);
	}

	public TextPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TextPageIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mTextTemplate = context.getString(R.string.textPageIndicatorBaseTemplate);
		
		if (attrs == null) {
			return;
		}

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextPageIndicator);
		int templateID = a.getResourceId(R.styleable.TextPageIndicator_template, 0);
		a.recycle();
		
		if (templateID > 0) {
			mTextTemplate = context.getString(templateID);
		}
	}
	
	public void setIndicatorTemplate(Context context, int templateID) {
		if (templateID > 0) {
			mTextTemplate = context.getString(templateID);
		}
		else {
			mTextTemplate = context.getString(R.string.textPageIndicatorBaseTemplate);
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
	public void onPageSelected(int arg0) {
		setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            //Clear us from the old pager.
            mViewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        setIndicatorText(1);
        invalidate();
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
        setIndicatorText(item+1);
        invalidate();
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	@Override
	public void notifyDataSetChanged() {
		invalidate();
	}

	private void setIndicatorText(int current) {
		setText(prepareIndicatorText(current, mViewPager.getAdapter().getCount()));
	}
	
	private String prepareIndicatorText(int current, int total) {
		return String.format(mTextTemplate, current, total);
	}
}