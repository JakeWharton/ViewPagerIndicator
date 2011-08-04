/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Francisco Figueiredo Jr.
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

import java.util.ArrayList;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A TitlePageIndicator is a PageIndicator which displays the title of left view
 * (if exist), the title of the current select view (centered) and the title of
 * the right view (if exist). When the user scrolls the ViewFlow then titles are
 * also scrolled.
 */
public class TitlePageIndicator extends TextView implements PageIndicator {
    private static final float DEFAULT_TITLE_PADDING_DP = 5;
    private static final float DEFAULT_CLIP_PADDING_DP = 0;
    private static final int DEFAULT_SELECTED_COLOR = 0xFFFFFFFF;
    private static final boolean DEFAULT_SELECTED_BOLD = true;
    private static final int DEFAULT_TEXT_COLOR = 0xFFAAAAAA;
    private static final int DEFAULT_TEXT_SIZE_DP = 18;
    private static final float DEFAULT_FOOTER_LINE_HEIGHT = 1;
    private static final int DEFAULT_FOOTER_COLOR = 0xFF6899FF;
    private static final float DEFAULT_FOOTER_TRIANGLE_HEIGHT = 7;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentScroll;
    private TitleProvider mTitleProvider;
    private int mCurrentPosition;
    private final Paint mPaintText;
    private final Paint mPaintSelected;
    private Path mPath;
    private final Paint mPaintFooterLine;
    private final Paint mPaintFooterTriangle;
    private final float mFooterTriangleHeight;
    private final float mTitlePadding;
    /** Left and right side padding for not active view titles. */
    private final float mClipPadding;
    private final float mFooterLineHeight;

    /**
     * Default constructor
     */
    public TitlePageIndicator(Context context) {
        this(context, null);
    }

    /**
     * The constructor used with an inflater
     *
     * @param context
     * @param attrs
     */
    public TitlePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        final float density = getResources().getDisplayMetrics().density;
        final float defaultTextSize = DEFAULT_TEXT_SIZE_DP * density;
        final float defaultTitlePadding = DEFAULT_TITLE_PADDING_DP * density;
        final float defaultClipPadding = DEFAULT_CLIP_PADDING_DP * density;

        // Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleFlowIndicator);

        // Retrieve the colors to be used for this view and apply them.
        final int footerColor = a.getColor(R.styleable.TitleFlowIndicator_footerColor, DEFAULT_FOOTER_COLOR);
        mFooterLineHeight = a.getDimension(R.styleable.TitleFlowIndicator_footerLineHeight, DEFAULT_FOOTER_LINE_HEIGHT);
        mFooterTriangleHeight = a.getDimension(R.styleable.TitleFlowIndicator_footerTriangleHeight, DEFAULT_FOOTER_TRIANGLE_HEIGHT);
        final int selectedColor = a.getColor(R.styleable.TitleFlowIndicator_selectedColor, DEFAULT_SELECTED_COLOR);
        final boolean selectedBold = a.getBoolean(R.styleable.TitleFlowIndicator_selectedBold, DEFAULT_SELECTED_BOLD);
        final int textColor = a.getColor(R.styleable.TitleFlowIndicator_textColor, DEFAULT_TEXT_COLOR);
        final float textSize = a.getDimension(R.styleable.TitleFlowIndicator_textSize, defaultTextSize);
        mTitlePadding = a.getDimension(R.styleable.TitleFlowIndicator_titlePadding, defaultTitlePadding);
        mClipPadding = a.getDimension(R.styleable.TitleFlowIndicator_clipPadding, defaultClipPadding);

        a.recycle();

        mPaintText = new Paint();
        mPaintText.setColor(textColor);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);
        mPaintSelected = new Paint();
        mPaintSelected.setColor(selectedColor);
        mPaintSelected.setTextSize(textSize);
        mPaintSelected.setFakeBoldText(selectedBold);
        mPaintSelected.setAntiAlias(true);
        mPaintFooterLine = new Paint();
        mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
        mPaintFooterLine.setColor(footerColor);
        mPaintFooterTriangle = new Paint();
        mPaintFooterTriangle.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterTriangle.setColor(footerColor);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate views bounds
        ArrayList<Rect> bounds = calculateAllBounds(mPaintText);

        // If no value then add a fake one
        int count = (mViewPager != null && mViewPager.getAdapter() != null) ? mViewPager.getAdapter().getCount() : 1;

        // Verify if the current view must be clipped to the screen
        Rect curViewBound = bounds.get(mCurrentPosition);
        int curViewWidth = curViewBound.right - curViewBound.left;
        if (curViewBound.left < 0) {
            // Try to clip to the screen (left side)
            clipViewOnTheLeft(curViewBound, curViewWidth);
        }
        if (curViewBound.right > getLeft() + getWidth()) {
            // Try to clip to the screen (right side)
            clipViewOnTheRight(curViewBound, curViewWidth);
        }

        // Left views starting from the current position
        if (mCurrentPosition > 0) {
            for (int iLoop = mCurrentPosition - 1; iLoop >= 0; iLoop--) {
                Rect bound = bounds.get(iLoop);
                int w = bound.right - bound.left;
                // Si left side is outside the screen
                if (bound.left < 0) {
                    // Try to clip to the screen (left side)
                     clipViewOnTheLeft(bound, w);
                    // Except if there's an intersection with the right view
                    if (iLoop < count - 1 && mCurrentPosition != iLoop) {
                        Rect rightBound = bounds.get(iLoop + 1);
                        // Intersection
                        if (bound.right + (int)mTitlePadding > rightBound.left) {
                            bound.left = rightBound.left - (w + (int)mTitlePadding);
                        }
                    }
                }
            }
        }
        // Right views starting from the current position
        if (mCurrentPosition < count - 1) {
            for (int iLoop = mCurrentPosition + 1 ; iLoop < count; iLoop++) {
                Rect bound = bounds.get(iLoop);
                int w = bound.right - bound.left;
                // If right side is outside the screen
                if (bound.right > getLeft() + getWidth()) {
                    // Try to clip to the screen (right side)
                    clipViewOnTheRight(bound, w);
                    // Except if there's an intersection with the left view
                    if (iLoop > 0 && mCurrentPosition != iLoop) {
                        Rect leftBound = bounds.get(iLoop - 1);
                        // Intersection
                        if (bound.left - (int)mTitlePadding < leftBound.right) {
                            bound.left = leftBound.right + (int)mTitlePadding;
                        }
                    }
                }
            }
        }

        // Now draw views
        for (int iLoop = 0; iLoop < count; iLoop++) {
            // Get the title
            String title = getTitle(iLoop);
            Rect bound = bounds.get(iLoop);
            // Only if one side is visible
            if ((bound.left > getLeft() && bound.left < getLeft() + getWidth()) || (bound.right > getLeft() && bound.right < getLeft() + getWidth())) {
                Paint paint = mPaintText;
                // Change the color is the title is closed to the center
                int middle = (bound.left + bound.right) / 2;
                if (Math.abs(middle - (getWidth() / 2)) < 20) {
                    paint = mPaintSelected;
                }
                canvas.drawText(title, bound.left, bound.bottom, paint);
            }
        }

        // Draw the footer line
        mPath = new Path();
        mPath.moveTo(0, getHeight()-mFooterLineHeight);
        mPath.lineTo(getWidth(), getHeight()-mFooterLineHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaintFooterLine);
        // Draw the footer triangle
        mPath = new Path();
        mPath.moveTo(getWidth() / 2, getHeight()-mFooterLineHeight-mFooterTriangleHeight);
        mPath.lineTo(getWidth() / 2 + mFooterTriangleHeight, getHeight()-mFooterLineHeight);
        mPath.lineTo(getWidth() / 2 - mFooterTriangleHeight, getHeight()-mFooterLineHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaintFooterTriangle);

    }

    /**
     * Set bounds for the right textView including clip padding.
     *
     * @param curViewBound
     *            current bounds.
     * @param curViewWidth
     *            width of the view.
     */
    private void clipViewOnTheRight(Rect curViewBound, int curViewWidth) {
        curViewBound.right = getLeft() + getWidth() - (int)mClipPadding;
        curViewBound.left = curViewBound.right - curViewWidth;
    }

    /**
     * Set bounds for the left textView including clip padding.
     *
     * @param curViewBound
     *            current bounds.
     * @param curViewWidth
     *            width of the view.
     */
    private void clipViewOnTheLeft(Rect curViewBound, int curViewWidth) {
        curViewBound.left = 0 + (int)mClipPadding;
        curViewBound.right = curViewWidth;
    }

    /**
     * Calculate views bounds and scroll them according to the current index
     *
     * @param paint
     * @param currentIndex
     * @return
     */
    private ArrayList<Rect> calculateAllBounds(Paint paint) {
        ArrayList<Rect> list = new ArrayList<Rect>();
        // For each views (If no values then add a fake one)
        int count = (mViewPager != null && mViewPager.getAdapter() != null) ? mViewPager.getAdapter().getCount() : 1;
        for (int iLoop = 0; iLoop < count; iLoop++) {
            Rect bounds = calcBounds(iLoop, paint);
            int w = (bounds.right - bounds.left);
            int h = (bounds.bottom - bounds.top);
            bounds.left = (getWidth() / 2) - (w / 2) - mCurrentScroll + (iLoop * getWidth());
            bounds.right = bounds.left + w;
            bounds.top = 0;
            bounds.bottom = h;
            list.add(bounds);
        }

        return list;
    }

    /**
     * Calculate the bounds for a view's title
     *
     * @param index
     * @param paint
     * @return
     */
    private Rect calcBounds(int index, Paint paint) {
        // Get the title
        String title = getTitle(index);
        // Calculate the text bounds
        Rect bounds = new Rect();
        bounds.right = (int) paint.measureText(title);
        bounds.bottom = (int) (paint.descent()-paint.ascent());
        return bounds;
    }

    /**
     * Returns the title
     *
     * @param pos
     * @return
     */
    private String getTitle(int pos) {
        // If the TitleProvider exist
        if (mTitleProvider != null) {
            return mTitleProvider.getTitle(pos);
        }
        // Set the default title
        return "Page " + (pos + 1);
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        if (!(view.getAdapter() instanceof TitleProvider)) {
            throw new IllegalStateException("ViewPager adapter must implement TitleProvider to be used with TitlePageIndicator.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        mTitleProvider = (TitleProvider)mViewPager.getAdapter();
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
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        invalidate();

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
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

        if (specMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ViewFlow can only be used in EXACTLY mode.");
        }
        result = specSize;
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
            // Calculate the text bounds
            Rect bounds = new Rect();
            bounds.bottom = (int) (mPaintText.descent()-mPaintText.ascent());
            result = bounds.bottom - bounds.top + (int)mFooterTriangleHeight + (int)mFooterLineHeight + 10;
            return result;
        }
        return result;
    }
}
