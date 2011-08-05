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
import java.util.HashMap;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
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
    public enum IndicatorStyle {
        None(0), Triangle(1), Underline(2);

        public final int value;

        private IndicatorStyle(int value) {
            this.value = value;
        }

        private static final HashMap<Integer, IndicatorStyle> MAP = new HashMap<Integer, IndicatorStyle>();
        static {
            for (IndicatorStyle style : IndicatorStyle.values()) {
                MAP.put(style.value, style);
            }
        }

        public static IndicatorStyle fromValue(int value) {
            return MAP.get(value);
        }
    }

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private TitleProvider mTitleProvider;
    private int mCurrentPage;
    private int mCurrentOffset;
    private final Paint mPaintText;
    private final Paint mPaintSelected;
    private Path mPath;
    private final Paint mPaintFooterLine;
    private IndicatorStyle mFooterIndicatorStyle;
    private final Paint mPaintFooterIndicator;
    private float mFooterIndicatorHeight;
    private float mTitlePadding;
    /** Left and right side padding for not active view titles. */
    private float mClipPadding;
    private float mFooterLineHeight;


    public TitlePageIndicator(Context context) {
        this(context, null);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.titlePageIndicatorStyle);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultFooterColor = res.getColor(R.color.default_title_indicator_footer_color);
        final float defaultFooterLineHeight = res.getDimension(R.dimen.default_title_indicator_footer_line_height);
        final int defaultFooterIndicatorStyle = res.getInteger(R.integer.default_title_indicator_footer_indicator_style);
        final float defaultFooterIndicatorHeight = res.getDimension(R.dimen.default_title_indicator_footer_indicator_height);
        final int defaultSelectedColor = res.getColor(R.color.default_title_indicator_selected_color);
        final boolean defaultSelectedBold = res.getBoolean(R.bool.default_title_indicator_selected_bold);
        final int defaultTextColor = res.getColor(R.color.default_title_indicator_text_color);
        final float defaultTextSize = res.getDimension(R.dimen.default_title_indicator_text_size);
        final float defaultTitlePadding = res.getDimension(R.dimen.default_title_indicator_title_padding);
        final float defaultClipPadding = res.getDimension(R.dimen.default_title_indicator_clip_padding);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitlePageIndicator, defStyle, R.style.Widget_TitlePageIndicator);

        //Retrieve the colors to be used for this view and apply them.
        mFooterLineHeight = a.getDimension(R.styleable.TitlePageIndicator_footerLineHeight, defaultFooterLineHeight);
        mFooterIndicatorStyle = IndicatorStyle.fromValue(a.getInteger(R.styleable.TitlePageIndicator_footerIndicatorStyle, defaultFooterIndicatorStyle));
        mFooterIndicatorHeight = a.getDimension(R.styleable.TitlePageIndicator_footerIndicatorHeight, defaultFooterIndicatorHeight);
        mTitlePadding = a.getDimension(R.styleable.TitlePageIndicator_titlePadding, defaultTitlePadding);
        mClipPadding = a.getDimension(R.styleable.TitlePageIndicator_clipPadding, defaultClipPadding);

        final float textSize = a.getDimension(R.styleable.TitlePageIndicator_textSize, defaultTextSize);
        final int footerColor = a.getColor(R.styleable.TitlePageIndicator_footerColor, defaultFooterColor);
        mPaintText = new Paint();
        mPaintText.setColor(a.getColor(R.styleable.TitlePageIndicator_textColor, defaultTextColor));
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);
        mPaintSelected = new Paint();
        mPaintSelected.setColor(a.getColor(R.styleable.TitlePageIndicator_selectedColor, defaultSelectedColor));
        mPaintSelected.setTextSize(textSize);
        mPaintSelected.setFakeBoldText(a.getBoolean(R.styleable.TitlePageIndicator_selectedBold, defaultSelectedBold));
        mPaintSelected.setAntiAlias(true);
        mPaintFooterLine = new Paint();
        mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
        mPaintFooterLine.setColor(footerColor);
        mPaintFooterIndicator = new Paint();
        mPaintFooterIndicator.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterIndicator.setColor(footerColor);

        a.recycle();
    }


    public int getFooterColor() {
        return mPaintFooterLine.getColor();
    }

    public void setFooterColor(int footerColor) {
        mPaintFooterLine.setColor(footerColor);
        invalidate();
    }

    public float getFooterLineHeight() {
        return mFooterLineHeight;
    }

    public void setFooterLineHeight(float footerLineHeight) {
        mFooterLineHeight = footerLineHeight;
        invalidate();
    }

    public float getFooterIndicatorHeight() {
        return mFooterIndicatorHeight;
    }

    public void setFooterIndicatorHeight(float footerTriangleHeight) {
        mFooterIndicatorHeight = footerTriangleHeight;
        invalidate();
    }

    public IndicatorStyle getFooterIndicatorStyle() {
        return mFooterIndicatorStyle;
    }

    public void setFooterIndicatorStyle(IndicatorStyle indicatorStyle) {
        mFooterIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public int getSelectedColor() {
        return mPaintSelected.getColor();
    }

    public void setSelectedColor(int selectedColor) {
        mPaintSelected.setColor(selectedColor);
        invalidate();
    }

    public boolean isSelectedBold() {
        return mPaintSelected.isFakeBoldText();
    }

    public void setSelectedBold(boolean selectedBold) {
        mPaintSelected.setFakeBoldText(selectedBold);
        invalidate();
    }

    public int getTextColor() {
        return mPaintText.getColor();
    }

    public void setTextColor(int textColor) {
        mPaintText.setColor(textColor);
        invalidate();
    }

    public float getTextSize() {
        return mPaintText.getTextSize();
    }

    public void setTextSize(float textSize) {
        mPaintText.setTextSize(textSize);
        invalidate();
    }

    public float getTitlePadding() {
        return this.mTitlePadding;
    }

    public void setTitlePadding(float titlePadding) {
        mTitlePadding = titlePadding;
        invalidate();
    }

    public float getClipPadding() {
        return this.mClipPadding;
    }

    public void setClipPadding(float clipPadding) {
        mClipPadding = clipPadding;
        invalidate();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Calculate views bounds
        ArrayList<Rect> bounds = calculateAllBounds(mPaintText);

        final int count = mViewPager.getAdapter().getCount();
        final int halfWidth = getWidth() / 2;

        //Verify if the current view must be clipped to the screen
        Rect curViewBound = bounds.get(mCurrentPage);
        int curViewWidth = curViewBound.right - curViewBound.left;
        if (curViewBound.left < 0) {
            //Try to clip to the screen (left side)
            clipViewOnTheLeft(curViewBound, curViewWidth);
        }
        if (curViewBound.right > getLeft() + getWidth()) {
            //Try to clip to the screen (right side)
            clipViewOnTheRight(curViewBound, curViewWidth);
        }

        //Left views starting from the current position
        if (mCurrentPage > 0) {
            for (int i = mCurrentPage - 1; i >= 0; i--) {
                Rect bound = bounds.get(i);
                int w = bound.right - bound.left;
                //Is left side is outside the screen
                if (bound.left < 0) {
                    //Try to clip to the screen (left side)
                     clipViewOnTheLeft(bound, w);
                    //Except if there's an intersection with the right view
                    if (i < count - 1 && mCurrentPage != i) {
                        Rect rightBound = bounds.get(i + 1);
                        //Intersection
                        if (bound.right + (int)mTitlePadding > rightBound.left) {
                            bound.left = rightBound.left - (w + (int)mTitlePadding);
                        }
                    }
                }
            }
        }
        //Right views starting from the current position
        if (mCurrentPage < count - 1) {
            for (int i = mCurrentPage + 1 ; i < count; i++) {
                Rect bound = bounds.get(i);
                int w = bound.right - bound.left;
                //If right side is outside the screen
                if (bound.right > getLeft() + getWidth()) {
                    //Try to clip to the screen (right side)
                    clipViewOnTheRight(bound, w);
                    //Except if there's an intersection with the left view
                    if (i > 0 && mCurrentPage != i) {
                        Rect leftBound = bounds.get(i - 1);
                        //Intersection
                        if (bound.left - (int)mTitlePadding < leftBound.right) {
                            bound.left = leftBound.right + (int)mTitlePadding;
                        }
                    }
                }
            }
        }

        //Now draw views
        for (int i = 0; i < count; i++) {
            //Get the title
            Rect bound = bounds.get(i);
            //Only if one side is visible
            if ((bound.left > getLeft() && bound.left < getLeft() + getWidth()) || (bound.right > getLeft() && bound.right < getLeft() + getWidth())) {
                Paint paint = mPaintText;
                //Change the color is the title is closed to the center
                int middle = (bound.left + bound.right) / 2;
                if (Math.abs(middle - halfWidth) < 20) {
                    paint = mPaintSelected;
                }
                canvas.drawText(mTitleProvider.getTitle(i), bound.left, bound.bottom, paint);
            }
        }

        //Draw the footer line
        mPath = new Path();
        mPath.moveTo(0, getHeight() - mFooterLineHeight);
        mPath.lineTo(getWidth(), getHeight() - mFooterLineHeight);
        mPath.close();
        canvas.drawPath(mPath, mPaintFooterLine);

        switch (mFooterIndicatorStyle) {
            case Triangle:
                mPath = new Path();
                mPath.moveTo(halfWidth, getHeight() - mFooterLineHeight - mFooterIndicatorHeight);
                mPath.lineTo(halfWidth + mFooterIndicatorHeight, getHeight() - mFooterLineHeight);
                mPath.lineTo(halfWidth - mFooterIndicatorHeight, getHeight() - mFooterLineHeight);
                mPath.close();
                canvas.drawPath(mPath, mPaintFooterIndicator);
                break;

            case Underline:
                float deltaPercentage = mCurrentOffset * 1.0f / getWidth();
                int alpha = 0xFF;
                int page = mCurrentPage;
                if (deltaPercentage <= 10) {
                    alpha = (int)(0xFF * (deltaPercentage / 10));
                } else if (deltaPercentage >= 90) {
                    alpha = (int)(0xFF * ((100 - deltaPercentage) / 10));
                    page += 1; //If we are >90% we are coming into the next page
                } else if (mCurrentOffset != 0) {
                    break; //Not in underline scope
                }

                Rect underlineBounds = bounds.get(page);
                mPath = new Path();
                mPath.moveTo(underlineBounds.left, getHeight() - mFooterLineHeight - mFooterIndicatorHeight);
                mPath.lineTo(underlineBounds.right, getHeight() - mFooterLineHeight - mFooterIndicatorHeight);
                mPath.close();

                mPaintFooterIndicator.setAlpha(alpha);
                canvas.drawPath(mPath, mPaintFooterIndicator);
                mPaintFooterIndicator.setAlpha(0xFF);
                break;
        }
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
        //For each views (If no values then add a fake one)
        int count = mViewPager.getAdapter().getCount();
        final int halfWidth = getWidth() / 2;
        for (int i = 0; i < count; i++) {
            Rect bounds = calcBounds(i, paint);
            int w = (bounds.right - bounds.left);
            int h = (bounds.bottom - bounds.top);
            bounds.left = (halfWidth) - (w / 2) - mCurrentOffset + ((i - mCurrentPage) * getWidth());
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
        //Calculate the text bounds
        Rect bounds = new Rect();
        bounds.right = (int)paint.measureText(mTitleProvider.getTitle(index));
        bounds.bottom = (int)(paint.descent() - paint.ascent());
        return bounds;
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
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
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
        mCurrentPage = position;
        mCurrentOffset = positionOffsetPixels;
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
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

        //We were told how big to be
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        //Measure the height
        else {
            //Calculate the text bounds
            Rect bounds = new Rect();
            bounds.bottom = (int) (mPaintText.descent()-mPaintText.ascent());
            result = bounds.bottom - bounds.top + (int)mFooterIndicatorHeight + (int)mFooterLineHeight + 10;
            return result;
        }
        return result;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        setFreezesText(true);
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
