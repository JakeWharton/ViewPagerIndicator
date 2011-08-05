/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Jake Wharton
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
package com.jakewharton.android.viewpagerindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

/**
 * A FlowIndicator which draws circles (one for each view). The current view
 * position is filled and others are only stroked.<br/><br/>
 * Available attributes are:<br/>
 * <ul>fillColor: Define the color used to fill a circle (default to white)</ul>
 * <ul>strokeColor: Define the color used to stroke a circle (default to white)</ul>
 * <ul>mRadius: Define the circle mRadius (default to 4.0)</ul>
 */
public class CirclePageIndicator extends View implements PageIndicator {
    private final float mRadius;
    private final Paint mPaintStroke;
    private final Paint mPaintFill;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentScroll;
    private int mFlowWidth;
    private boolean mCentered;


    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.circlePageIndicatorStyle);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultFillColor = res.getColor(R.color.default_circle_indicator_fill_color);
        final int defaultStrokeColor = res.getColor(R.color.default_circle_indicator_stroke_color);
        final float defaultRadius = res.getDimension(R.dimen.default_circle_indicator_radius);
        final boolean defaultCentered = res.getBoolean(R.bool.default_circle_indicator_centered);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CirclePageIndicator, defStyle, R.style.Widget_CirclePageIndicator);

        final int fillColor = a.getColor(R.styleable.CirclePageIndicator_fillColor, defaultFillColor);
        final int strokeColor = a.getColor(R.styleable.CirclePageIndicator_strokeColor, defaultStrokeColor);
        mRadius = a.getDimension(R.styleable.CirclePageIndicator_radius, defaultRadius);
        mCentered = a.getBoolean(R.styleable.CirclePageIndicator_centered, defaultCentered);

        a.recycle();

        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setStyle(Style.STROKE);
        mPaintStroke.setColor(strokeColor);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Style.FILL);
        mPaintFill.setColor(fillColor);
    }


    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = (mViewPager != null && mViewPager.getAdapter() != null) ? mViewPager.getAdapter().getCount() : 1;
        float leftOffset = getPaddingLeft();
        if (mCentered) {
            leftOffset += ((getWidth() - getPaddingLeft() - getPaddingRight()) / 2.0f) - ((count * mRadius * 3) / 2.0f);
        }
        // Draw stroked circles
        for (int iLoop = 0; iLoop < count; iLoop++) {
            canvas.drawCircle(leftOffset + mRadius
                    + (iLoop * (2 * mRadius + mRadius)),
                    getPaddingTop() + mRadius, mRadius, mPaintStroke);
        }
        float cx = 0;
        if (mFlowWidth != 0) {
            // Draw the filled circle according to the current scroll
            cx = (mCurrentScroll * (2 * mRadius + mRadius)) / mFlowWidth;
        }
        // The flow width has been updated yet. Draw the default position
        canvas.drawCircle(leftOffset + mRadius + cx,
                    getPaddingTop() + mRadius, mRadius, mPaintFill);

    }

    @Override
    public void setViewPager(ViewPager view) {
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        mFlowWidth = mViewPager.getWidth();
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentScroll = (position * mViewPager.getWidth()) + positionOffsetPixels;
        mFlowWidth = mViewPager.getWidth();
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec
     *            A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Calculate the width according the views count
        else {
            int count = (mViewPager != null && mViewPager.getAdapter() != null) ? mViewPager.getAdapter().getCount() : 1;
            result = (int)(getPaddingLeft() + getPaddingRight()
                    + (count * 2 * mRadius) + (count - 1) * mRadius + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec
     *            A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        // Measure the height
        else {
            result = (int)(2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Sets the fill color
     *
     * @param color
     *            ARGB value for the text
     */
    public void setFillColor(int color) {
        mPaintFill.setColor(color);
        invalidate();
    }

    /**
     * Sets the stroke color
     *
     * @param color
     *            ARGB value for the text
     */
    public void setStrokeColor(int color) {
        mPaintStroke.setColor(color);
        invalidate();
    }
}
