/*
 * Copyright (C) 2011 Jake Wharton
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Francisco Figueiredo Jr.
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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;

/**
 * A TitlePageIndicator is a PageIndicator which displays the title of left view
 * (if exist), the title of the current select view (centered) and the title of
 * the right view (if exist). When the user scrolls the ViewPager then titles are
 * also scrolled.
 */
public class TitlePageIndicator extends View implements PageIndicator {
    /**
     * Percentage indicating what percentage of the screen width away from
     * center should the underline be fully faded. A value of 0.25 means that
     * halfway between the center of the screen and an edge.
     */
    private static final float SELECTION_FADE_PERCENTAGE = 0.25f;

    /**
     * Percentage indicating what percentage of the screen width away from
     * center should the selected text bold turn off. A value of 0.05 means
     * that 10% between the center and an edge.
     */
    private static final float BOLD_FADE_PERCENTAGE = 0.05f;

    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final String EMPTY_TITLE = "";

    /**
     * Interface for a callback when the center item has been clicked.
     */
    public interface OnCenterItemClickListener {
        /**
         * Callback when the center item has been clicked.
         *
         * @param position Position of the current center item.
         */
        void onCenterItemClick(int position);
    }

    public enum IndicatorStyle {
        None(0), Triangle(1), Underline(2);

        public final int value;

        private IndicatorStyle(int value) {
            this.value = value;
        }

        public static IndicatorStyle fromValue(int value) {
            for (IndicatorStyle style : IndicatorStyle.values()) {
                if (style.value == value) {
                    return style;
                }
            }
            return null;
        }
    }

    public enum LinePosition {
        Bottom(0), Top(1);

        public final int value;

        private LinePosition(int value) {
            this.value = value;
        }

        public static LinePosition fromValue(int value) {
            for (LinePosition position : LinePosition.values()) {
                if (position.value == value) {
                    return position;
                }
            }
            return null;
        }
    }

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private int mCurrentPage = -1;
    private float mPageOffset;
    private int mScrollState;
    private final Paint mPaintText = new Paint();
    private boolean mBoldText;
    private int mColorText;
    private int mColorSelected;
    private Path mPath = new Path();
    private final Rect mBounds = new Rect();
    private final Paint mPaintFooterLine = new Paint();
    private IndicatorStyle mFooterIndicatorStyle;
    private LinePosition mLinePosition;
    private final Paint mPaintFooterIndicator = new Paint();
    private float mFooterIndicatorHeight;
    private float mFooterIndicatorUnderlinePadding;
    private float mFooterPadding;
    private float mTitlePadding;
    private float mTopPadding;
    /** Left and right side padding for not active view titles. */
    private float mClipPadding;
    private float mFooterLineHeight;

    private static final int INVALID_POINTER = -1;

    private int mTouchSlop;
    private float mLastMotionX = -1;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsDragging;

    private OnCenterItemClickListener mCenterItemClickListener;


    public TitlePageIndicator(Context context) {
        this(context, null);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.vpiTitlePageIndicatorStyle);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultFooterColor = res.getColor(R.color.default_title_indicator_footer_color);
        final float defaultFooterLineHeight = res.getDimension(R.dimen.default_title_indicator_footer_line_height);
        final int defaultFooterIndicatorStyle = res.getInteger(R.integer.default_title_indicator_footer_indicator_style);
        final float defaultFooterIndicatorHeight = res.getDimension(R.dimen.default_title_indicator_footer_indicator_height);
        final float defaultFooterIndicatorUnderlinePadding = res.getDimension(R.dimen.default_title_indicator_footer_indicator_underline_padding);
        final float defaultFooterPadding = res.getDimension(R.dimen.default_title_indicator_footer_padding);
        final int defaultLinePosition = res.getInteger(R.integer.default_title_indicator_line_position);
        final int defaultSelectedColor = res.getColor(R.color.default_title_indicator_selected_color);
        final boolean defaultSelectedBold = res.getBoolean(R.bool.default_title_indicator_selected_bold);
        final int defaultTextColor = res.getColor(R.color.default_title_indicator_text_color);
        final float defaultTextSize = res.getDimension(R.dimen.default_title_indicator_text_size);
        final float defaultTitlePadding = res.getDimension(R.dimen.default_title_indicator_title_padding);
        final float defaultClipPadding = res.getDimension(R.dimen.default_title_indicator_clip_padding);
        final float defaultTopPadding = res.getDimension(R.dimen.default_title_indicator_top_padding);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitlePageIndicator, defStyle, 0);

        //Retrieve the colors to be used for this view and apply them.
        mFooterLineHeight = a.getDimension(R.styleable.TitlePageIndicator_footerLineHeight, defaultFooterLineHeight);
        mFooterIndicatorStyle = IndicatorStyle.fromValue(a.getInteger(R.styleable.TitlePageIndicator_footerIndicatorStyle, defaultFooterIndicatorStyle));
        mFooterIndicatorHeight = a.getDimension(R.styleable.TitlePageIndicator_footerIndicatorHeight, defaultFooterIndicatorHeight);
        mFooterIndicatorUnderlinePadding = a.getDimension(R.styleable.TitlePageIndicator_footerIndicatorUnderlinePadding, defaultFooterIndicatorUnderlinePadding);
        mFooterPadding = a.getDimension(R.styleable.TitlePageIndicator_footerPadding, defaultFooterPadding);
        mLinePosition = LinePosition.fromValue(a.getInteger(R.styleable.TitlePageIndicator_linePosition, defaultLinePosition));
        mTopPadding = a.getDimension(R.styleable.TitlePageIndicator_topPadding, defaultTopPadding);
        mTitlePadding = a.getDimension(R.styleable.TitlePageIndicator_titlePadding, defaultTitlePadding);
        mClipPadding = a.getDimension(R.styleable.TitlePageIndicator_clipPadding, defaultClipPadding);
        mColorSelected = a.getColor(R.styleable.TitlePageIndicator_selectedColor, defaultSelectedColor);
        mColorText = a.getColor(R.styleable.TitlePageIndicator_android_textColor, defaultTextColor);
        mBoldText = a.getBoolean(R.styleable.TitlePageIndicator_selectedBold, defaultSelectedBold);

        final float textSize = a.getDimension(R.styleable.TitlePageIndicator_android_textSize, defaultTextSize);
        final int footerColor = a.getColor(R.styleable.TitlePageIndicator_footerColor, defaultFooterColor);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);
        mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
        mPaintFooterLine.setColor(footerColor);
        mPaintFooterIndicator.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterIndicator.setColor(footerColor);

        Drawable background = a.getDrawable(R.styleable.TitlePageIndicator_android_background);
        if (background != null) {
          setBackgroundDrawable(background);
        }

        a.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }


    public int getFooterColor() {
        return mPaintFooterLine.getColor();
    }

    public void setFooterColor(int footerColor) {
        mPaintFooterLine.setColor(footerColor);
        mPaintFooterIndicator.setColor(footerColor);
        invalidate();
    }

    public float getFooterLineHeight() {
        return mFooterLineHeight;
    }

    public void setFooterLineHeight(float footerLineHeight) {
        mFooterLineHeight = footerLineHeight;
        mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
        invalidate();
    }

    public float getFooterIndicatorHeight() {
        return mFooterIndicatorHeight;
    }

    public void setFooterIndicatorHeight(float footerTriangleHeight) {
        mFooterIndicatorHeight = footerTriangleHeight;
        invalidate();
    }

    public float getFooterIndicatorPadding() {
        return mFooterPadding;
    }

    public void setFooterIndicatorPadding(float footerIndicatorPadding) {
        mFooterPadding = footerIndicatorPadding;
        invalidate();
    }

    public IndicatorStyle getFooterIndicatorStyle() {
        return mFooterIndicatorStyle;
    }

    public void setFooterIndicatorStyle(IndicatorStyle indicatorStyle) {
        mFooterIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public LinePosition getLinePosition() {
        return mLinePosition;
    }

    public void setLinePosition(LinePosition linePosition) {
        mLinePosition = linePosition;
        invalidate();
    }

    public int getSelectedColor() {
        return mColorSelected;
    }

    public void setSelectedColor(int selectedColor) {
        mColorSelected = selectedColor;
        invalidate();
    }

    public boolean isSelectedBold() {
        return mBoldText;
    }

    public void setSelectedBold(boolean selectedBold) {
        mBoldText = selectedBold;
        invalidate();
    }

    public int getTextColor() {
        return mColorText;
    }

    public void setTextColor(int textColor) {
        mPaintText.setColor(textColor);
        mColorText = textColor;
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

    public float getTopPadding() {
        return this.mTopPadding;
    }

    public void setTopPadding(float topPadding) {
        mTopPadding = topPadding;
        invalidate();
    }

    public float getClipPadding() {
        return this.mClipPadding;
    }

    public void setClipPadding(float clipPadding) {
        mClipPadding = clipPadding;
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        mPaintText.setTypeface(typeface);
        invalidate();
    }

    public Typeface getTypeface() {
        return mPaintText.getTypeface();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        // mCurrentPage is -1 on first start and after orientation changed. If so, retrieve the correct index from viewpager.
        if (mCurrentPage == -1 && mViewPager != null) {
            mCurrentPage = mViewPager.getCurrentItem();
        }

        //Calculate views bounds
        ArrayList<Rect> bounds = calculateAllBounds(mPaintText);
        final int boundsSize = bounds.size();

        //Make sure we're on a page that still exists
        if (mCurrentPage >= boundsSize) {
            setCurrentItem(boundsSize - 1);
            return;
        }

        final int countMinusOne = count - 1;
        final float halfWidth = getWidth() / 2f;
        final int left = getLeft();
        final float leftClip = left + mClipPadding;
        final int width = getWidth();
        int height = getHeight();
        final int right = left + width;
        final float rightClip = right - mClipPadding;

        int page = mCurrentPage;
        float offsetPercent;
        if (mPageOffset <= 0.5) {
            offsetPercent = mPageOffset;
        } else {
            page += 1;
            offsetPercent = 1 - mPageOffset;
        }
        final boolean currentSelected = (offsetPercent <= SELECTION_FADE_PERCENTAGE);
        final boolean currentBold = (offsetPercent <= BOLD_FADE_PERCENTAGE);
        final float selectedPercent = (SELECTION_FADE_PERCENTAGE - offsetPercent) / SELECTION_FADE_PERCENTAGE;

        //Verify if the current view must be clipped to the screen
        Rect curPageBound = bounds.get(mCurrentPage);
        float curPageWidth = curPageBound.right - curPageBound.left;
        if (curPageBound.left < leftClip) {
            //Try to clip to the screen (left side)
            clipViewOnTheLeft(curPageBound, curPageWidth, left);
        }
        if (curPageBound.right > rightClip) {
            //Try to clip to the screen (right side)
            clipViewOnTheRight(curPageBound, curPageWidth, right);
        }

        //Left views starting from the current position
        if (mCurrentPage > 0) {
            for (int i = mCurrentPage - 1; i >= 0; i--) {
                Rect bound = bounds.get(i);
                //Is left side is outside the screen
                if (bound.left < leftClip) {
                    int w = bound.right - bound.left;
                    //Try to clip to the screen (left side)
                    clipViewOnTheLeft(bound, w, left);
                    //Except if there's an intersection with the right view
                    Rect rightBound = bounds.get(i + 1);
                    //Intersection
                    if (bound.right + mTitlePadding > rightBound.left) {
                        bound.left = (int) (rightBound.left - w - mTitlePadding);
                        bound.right = bound.left + w;
                    }
                }
            }
        }
        //Right views starting from the current position
        if (mCurrentPage < countMinusOne) {
            for (int i = mCurrentPage + 1 ; i < count; i++) {
                Rect bound = bounds.get(i);
                //If right side is outside the screen
                if (bound.right > rightClip) {
                    int w = bound.right - bound.left;
                    //Try to clip to the screen (right side)
                    clipViewOnTheRight(bound, w, right);
                    //Except if there's an intersection with the left view
                    Rect leftBound = bounds.get(i - 1);
                    //Intersection
                    if (bound.left - mTitlePadding < leftBound.right) {
                        bound.left = (int) (leftBound.right + mTitlePadding);
                        bound.right = bound.left + w;
                    }
                }
            }
        }

        //Now draw views
        int colorTextAlpha = mColorText >>> 24;
        for (int i = 0; i < count; i++) {
            //Get the title
            Rect bound = bounds.get(i);
            //Only if one side is visible
            if ((bound.left > left && bound.left < right) || (bound.right > left && bound.right < right)) {
                final boolean currentPage = (i == page);
                final CharSequence pageTitle = getTitle(i);

                //Only set bold if we are within bounds
                mPaintText.setFakeBoldText(currentPage && currentBold && mBoldText);

                //Draw text as unselected
                mPaintText.setColor(mColorText);
                if(currentPage && currentSelected) {
                    //Fade out/in unselected text as the selected text fades in/out
                    mPaintText.setAlpha(colorTextAlpha - (int)(colorTextAlpha * selectedPercent));
                }

                //Except if there's an intersection with the right view
                if (i < boundsSize - 1)  {
                    Rect rightBound = bounds.get(i + 1);
                    //Intersection
                    if (bound.right + mTitlePadding > rightBound.left) {
                        int w = bound.right - bound.left;
                        bound.left = (int) (rightBound.left - w - mTitlePadding);
                        bound.right = bound.left + w;
                    }
                }
                canvas.drawText(pageTitle, 0, pageTitle.length(), bound.left, bound.bottom + mTopPadding, mPaintText);

                //If we are within the selected bounds draw the selected text
                if (currentPage && currentSelected) {
                    mPaintText.setColor(mColorSelected);
                    mPaintText.setAlpha((int)((mColorSelected >>> 24) * selectedPercent));
                    canvas.drawText(pageTitle, 0, pageTitle.length(), bound.left, bound.bottom + mTopPadding, mPaintText);
                }
            }
        }

        //If we want the line on the top change height to zero and invert the line height to trick the drawing code
        float footerLineHeight = mFooterLineHeight;
        float footerIndicatorLineHeight = mFooterIndicatorHeight;
        if (mLinePosition == LinePosition.Top) {
            height = 0;
            footerLineHeight = -footerLineHeight;
            footerIndicatorLineHeight = -footerIndicatorLineHeight;
        }

        //Draw the footer line
        mPath.reset();
        mPath.moveTo(0, height - footerLineHeight / 2f);
        mPath.lineTo(width, height - footerLineHeight / 2f);
        mPath.close();
        canvas.drawPath(mPath, mPaintFooterLine);

        float heightMinusLine = height - footerLineHeight;
        switch (mFooterIndicatorStyle) {
            case Triangle:
                mPath.reset();
                mPath.moveTo(halfWidth, heightMinusLine - footerIndicatorLineHeight);
                mPath.lineTo(halfWidth + footerIndicatorLineHeight, heightMinusLine);
                mPath.lineTo(halfWidth - footerIndicatorLineHeight, heightMinusLine);
                mPath.close();
                canvas.drawPath(mPath, mPaintFooterIndicator);
                break;

            case Underline:
                if (!currentSelected || page >= boundsSize) {
                    break;
                }

                Rect underlineBounds = bounds.get(page);
                final float rightPlusPadding = underlineBounds.right + mFooterIndicatorUnderlinePadding;
                final float leftMinusPadding = underlineBounds.left - mFooterIndicatorUnderlinePadding;
                final float heightMinusLineMinusIndicator = heightMinusLine - footerIndicatorLineHeight;

                mPath.reset();
                mPath.moveTo(leftMinusPadding, heightMinusLine);
                mPath.lineTo(rightPlusPadding, heightMinusLine);
                mPath.lineTo(rightPlusPadding, heightMinusLineMinusIndicator);
                mPath.lineTo(leftMinusPadding, heightMinusLineMinusIndicator);
                mPath.close();

                mPaintFooterIndicator.setAlpha((int)(0xFF * selectedPercent));
                canvas.drawPath(mPath, mPaintFooterIndicator);
                mPaintFooterIndicator.setAlpha(0xFF);
                break;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        if ((mViewPager == null) || (mViewPager.getAdapter().getCount() == 0)) {
            return false;
        }

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mLastMotionX = ev.getX();
                break;

            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, activePointerIndex);
                final float deltaX = x - mLastMotionX;

                if (!mIsDragging) {
                    if (Math.abs(deltaX) > mTouchSlop) {
                        mIsDragging = true;
                    }
                }

                if (mIsDragging) {
                    mLastMotionX = x;
                    if (mViewPager.isFakeDragging() || mViewPager.beginFakeDrag()) {
                        mViewPager.fakeDragBy(deltaX);
                    }
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!mIsDragging) {
                    final int count = mViewPager.getAdapter().getCount();
                    final int width = getWidth();
                    final float halfWidth = width / 2f;
                    final float sixthWidth = width / 6f;
                    final float leftThird = halfWidth - sixthWidth;
                    final float rightThird = halfWidth + sixthWidth;
                    final float eventX = ev.getX();

                    if (eventX < leftThird) {
                        if (mCurrentPage > 0) {
                            if (action != MotionEvent.ACTION_CANCEL) {
                                mViewPager.setCurrentItem(mCurrentPage - 1);
                            }
                            return true;
                        }
                    } else if (eventX > rightThird) {
                        if (mCurrentPage < count - 1) {
                            if (action != MotionEvent.ACTION_CANCEL) {
                                mViewPager.setCurrentItem(mCurrentPage + 1);
                            }
                            return true;
                        }
                    } else {
                        //Middle third
                        if (mCenterItemClickListener != null && action != MotionEvent.ACTION_CANCEL) {
                            mCenterItemClickListener.onCenterItemClick(mCurrentPage);
                        }
                    }
                }

                mIsDragging = false;
                mActivePointerId = INVALID_POINTER;
                if (mViewPager.isFakeDragging()) mViewPager.endFakeDrag();
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionX = MotionEventCompat.getX(ev, index);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                break;
        }

        return true;
    }

    /**
     * Set bounds for the right textView including clip padding.
     *
     * @param curViewBound
     *            current bounds.
     * @param curViewWidth
     *            width of the view.
     */
    private void clipViewOnTheRight(Rect curViewBound, float curViewWidth, int right) {
        curViewBound.right = (int) (right - mClipPadding);
        curViewBound.left = (int) (curViewBound.right - curViewWidth);
    }

    /**
     * Set bounds for the left textView including clip padding.
     *
     * @param curViewBound
     *            current bounds.
     * @param curViewWidth
     *            width of the view.
     */
    private void clipViewOnTheLeft(Rect curViewBound, float curViewWidth, int left) {
        curViewBound.left = (int) (left + mClipPadding);
        curViewBound.right = (int) (mClipPadding + curViewWidth);
    }

    /**
     * Calculate views bounds and scroll them according to the current index
     *
     * @param paint
     * @return
     */
    private ArrayList<Rect> calculateAllBounds(Paint paint) {
        ArrayList<Rect> list = new ArrayList<Rect>();
        //For each views (If no values then add a fake one)
        final int count = mViewPager.getAdapter().getCount();
        final int width = getWidth();
        final int halfWidth = width / 2;
        for (int i = 0; i < count; i++) {
            Rect bounds = calcBounds(i, paint);
            int w = bounds.right - bounds.left;
            int h = bounds.bottom - bounds.top;
            bounds.left = (int)(halfWidth - (w / 2f) + ((i - mCurrentPage - mPageOffset) * width));
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
        CharSequence title = getTitle(index);
        bounds.right = (int) paint.measureText(title, 0, title.length());
        bounds.bottom = (int) (paint.descent() - paint.ascent());
        return bounds;
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    /**
     * Set a callback listener for the center item click.
     *
     * @param listener Callback instance.
     */
    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        mCenterItemClickListener = listener;
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
        mScrollState = state;

        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        invalidate();

        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position;
            invalidate();
        }

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Measure our width in whatever mode specified
        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

        //Determine our height
        float height;
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            //Calculate the text bounds
            mBounds.setEmpty();
            mBounds.bottom = (int) (mPaintText.descent() - mPaintText.ascent());
            height = mBounds.bottom - mBounds.top + mFooterLineHeight + mFooterPadding + mTopPadding;
            if (mFooterIndicatorStyle != IndicatorStyle.None) {
                height += mFooterIndicatorHeight;
            }
        }
        final int measuredHeight = (int)height;

        setMeasuredDimension(measuredWidth, measuredHeight);
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

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
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

    private CharSequence getTitle(int i) {
        CharSequence title = mViewPager.getAdapter().getPageTitle(i);
        if (title == null) {
            title = EMPTY_TITLE;
        }
        return title;
    }
}
