/*
 * Copyright (C) 2011 Jake Wharton
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Francisco Figueiredo Jr.
 * Copyright (C) 2012 Jesse Ridgway
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

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class IconPageIndicator extends View implements PageIndicator {

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
//    private static final float BOLD_FADE_PERCENTAGE = 0.05f;

    /**
     * Interface for a callback when the center item has been clicked.
     */
    public static interface OnCenterItemClickListener {
        /**
         * Callback when the center item has been clicked.
         *
         * @param position Position of the current center item.
         */
        public void onCenterItemClick(int position);
    }
    
    //Icon Position Enum, optional
    public enum IconPosition {
    	LEFT, CENTER, RIGHT
    }

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private IconProvider mIconProivder;
    private int mCurrentPage;
    private int mCurrentOffset;
    private int mScrollState;
    private final Paint mPaintIndicator = new Paint();
//    private boolean mBoldText;
//    private int mColorText;
//    private int mColorSelected;
    private Path mPath;
    private final Paint mPaintFooterLine = new Paint();
    private float mFooterPadding;
    private float mIconPadding;
    private float mTopPadding;
    /** Left and right side padding for not active view titles. */
    private float mClipPadding;
    private float mFooterLineHeight;
    
    private int mSideIconVerticalShift;
    private float mAboveIconPadding;
    
    private static final int INVALID_POINTER = -1;

    private int mTouchSlop;
    private float mLastMotionX = -1;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsDragging;

    private OnCenterItemClickListener mCenterItemClickListener;


    public IconPageIndicator(Context context) {
        this(context, null);
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.vpiIconPageIndicatorStyle);
    }

    public IconPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultFooterColor = res.getColor(R.color.default_icon_indicator_footer_color);
        final float defaultFooterLineHeight = res.getDimension(R.dimen.default_icon_indicator_footer_line_height);
        final float defaultFooterPadding = res.getDimension(R.dimen.default_icon_indicator_footer_padding);
//        final int defaultSelectedColor = res.getColor(R.color.default_title_indicator_selected_color);
//        final boolean defaultSelectedBold = res.getBoolean(R.bool.default_title_indicator_selected_bold);
//        final int defaultTextColor = res.getColor(R.color.default_title_indicator_text_color);
//        final float defaultTextSize = res.getDimension(R.dimen.default_title_indicator_text_size);
        final float defaultTitlePadding = res.getDimension(R.dimen.default_icon_indicator_icon_padding);
        final float defaultClipPadding = res.getDimension(R.dimen.default_icon_indicator_clip_padding);
        final float defaultTopPadding = res.getDimension(R.dimen.default_icon_indicator_top_padding);
        final float defaultAboveIconPadding = res.getDimension(R.dimen.default_icon_indicator_above_icon_padding);
        final int defaultSideIconVerticalShift = res.getInteger(R.integer.default_icon_indicator_side_icon_vertical_shift);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconPageIndicator, defStyle, R.style.Widget_IconPageIndicator);

        //Retrieve the colors to be used for this view and apply them.
        mFooterLineHeight = a.getDimension(R.styleable.IconPageIndicator_footerHeight, defaultFooterLineHeight);
        mFooterPadding = a.getDimension(R.styleable.IconPageIndicator_iconFooterPadding, defaultFooterPadding);
        mTopPadding = a.getDimension(R.styleable.IconPageIndicator_footerLineTopPadding, defaultTopPadding);
        Log.e("top padding after set", mTopPadding + "");
        mIconPadding = a.getDimension(R.styleable.IconPageIndicator_iconPadding, defaultTitlePadding);
        mClipPadding = a.getDimension(R.styleable.IconPageIndicator_iconSidePadding, defaultClipPadding);
//        mColorSelected = a.getColor(R.styleable.TitlePageIndicator_selectedColor, defaultSelectedColor);
//        mColorText = a.getColor(R.styleable.TitlePageIndicator_textColor, defaultTextColor);
//        mBoldText = a.getBoolean(R.styleable.TitlePageIndicator_selectedBold, defaultSelectedBold);
        
        mSideIconVerticalShift = defaultSideIconVerticalShift;
        mAboveIconPadding = defaultAboveIconPadding;
        
//        final float textSize = a.getDimension(R.styleable.TitlePageIndicator_textSize, defaultTextSize);
        final int footerColor = a.getColor(R.styleable.IconPageIndicator_footerLineColor, defaultFooterColor);
//        mPaintIndicator.setTextSize(textSize);
        mPaintIndicator.setAntiAlias(true);
        mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
        mPaintFooterLine.setColor(footerColor);

        a.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
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
        mPaintFooterLine.setStrokeWidth(mFooterLineHeight);
        invalidate();
    }

    public float getFooterPadding() {
        return mFooterPadding;
    }

    public void setFooterPadding(float footerIndicatorPadding) {
        mFooterPadding = footerIndicatorPadding;
        invalidate();
    }

    public float getTitlePadding() {
        return this.mIconPadding;
    }

    public void setTitlePadding(float titlePadding) {
        mIconPadding = titlePadding;
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

	public int getSideIconVerticalShift() {
		return mSideIconVerticalShift;
	}

	public void setSideIconVerticalShift(int shift) {
		this.mSideIconVerticalShift = shift;
		invalidate();
	}

	public float getAboveIconPadding() {
		return mAboveIconPadding;
	}

	public void setAboveIconPadding(float padding) {
		this.mAboveIconPadding = padding;
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
        
        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        //Calculate views bounds
        ArrayList<RectF> bounds = calculateAllBounds();
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
        final int height = getHeight();
        final int right = left + width;
        final float rightClip = right - mClipPadding;

        int page = mCurrentPage;
        
        float offsetPercent;
        int leftAlpha = 255;
        int rightAlpha = 255;
        if (mCurrentOffset <= halfWidth) {
            offsetPercent = 1.0f * mCurrentOffset / width;
        } else {
            page += 1;
            offsetPercent = 1.0f * (width - mCurrentOffset) / width;
        }
        if(offsetPercent > SELECTION_FADE_PERCENTAGE) {
        	if(mCurrentOffset > halfWidth) {
        		leftAlpha = (int) ((.5 - offsetPercent) * 4 * 255);
        	}
        	else if(mCurrentOffset < halfWidth) {
        		rightAlpha = (int) ((.5 - offsetPercent) * 4 * 255);
        	}
        	else {
        		leftAlpha = 0;
        		rightAlpha = 0;
        	}
        }
        final boolean currentSelected = (offsetPercent <= SELECTION_FADE_PERCENTAGE);
//        final boolean currentBold = (offsetPercent <= BOLD_FADE_PERCENTAGE);
        final float selectedPercent = (SELECTION_FADE_PERCENTAGE - offsetPercent) / SELECTION_FADE_PERCENTAGE;
        
        //Verify if the current view must be clipped to the screen
        RectF curPageBound = bounds.get(mCurrentPage);
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
                RectF bound = bounds.get(i);
                //Is left side is outside the screen
                if (bound.left < leftClip) {
                    float w = bound.right - bound.left;
                    //Try to clip to the screen (left side)
                    clipViewOnTheLeft(bound, w, left);
                    //Except if there's an intersection with the right view
                    RectF rightBound = bounds.get(i + 1);
                    //Intersection
                    Log.e("left bound altered", bound.left + "");
                    if (bound.right + mIconPadding > rightBound.left) {
                        bound.left = rightBound.left - w - mIconPadding;
                        bound.right = bound.left + w;
                        Log.e("left bound altered in if statement", bound.left + "");
                    }
                }
            }
        }
        //Right views starting from the current position
        if (mCurrentPage < countMinusOne) {
            for (int i = mCurrentPage + 1 ; i < count; i++) {
                RectF bound = bounds.get(i);
                //If right side is outside the screen
                if (bound.right > rightClip) {
                    float w = bound.right - bound.left;
                    //Try to clip to the screen (right side)
                    clipViewOnTheRight(bound, w, right);
                    //Except if there's an intersection with the left view
                    RectF leftBound = bounds.get(i - 1);
                    //Intersection
                    if (bound.left - mIconPadding < leftBound.right) {
                        bound.left = leftBound.right + mIconPadding;
                        bound.right = bound.left + w;
                        Log.e("right altered", bound.right + "");
                    }
                }
            }
        }

        //Now draw views
        Bitmap bitmap;
        Resources res = getResources();
        int leftIcon;
        int rightIcon;
        int centerIcon;
        int colorTextAlpha = 255; //mColorText >>> 24;
        for (int i = 0; i < count; i++) {
        	RectF bound = bounds.get(i);
            Log.e(i + " top, bottom, left, right", "" + bound.top + " " + bound.bottom + " " + bound.left + " " + bound.right);
            //Only if one side is visible
            if ((bound.left > left && bound.left < right) || (bound.right > left && bound.right < right)) {
                final boolean currentPage = (i == page);
                
                leftIcon = mIconProivder.getIconArray(i)[0];
                rightIcon = mIconProivder.getIconArray(i)[2];
                centerIcon = mIconProivder.getIconArray(i)[1];
                
                float trueHeight = getHeight() - mFooterPadding - mTopPadding;
                float density = res.getDisplayMetrics().density;
                                
            	if(currentPage && currentSelected) {
            		mPaintIndicator.setAlpha((int)(colorTextAlpha * selectedPercent));
            		bitmap = BitmapFactory.decodeResource(res, centerIcon);
            		float change = trueHeight / bitmap.getHeight();
            		float trueWidth = bitmap.getWidth() * change;
            		bitmap = Bitmap.createScaledBitmap(bitmap, (int)trueWidth, (int)trueHeight, true);
            		canvas.drawBitmap(bitmap, bound.left, bound.top + (float)(mAboveIconPadding), mPaintIndicator);
            	}
            	else if(i == (page - 1)) { //left
            		mPaintIndicator.setAlpha(leftAlpha);
            		bitmap = BitmapFactory.decodeResource(res, leftIcon);
            		float change = trueHeight / bitmap.getHeight();
            		float trueWidth = bitmap.getWidth() * change;
            		bitmap = Bitmap.createScaledBitmap(bitmap, (int)trueWidth, (int)trueHeight, true);
					canvas.drawBitmap(bitmap, bound.left, bound.top + (float)(mAboveIconPadding + (mSideIconVerticalShift * density)), mPaintIndicator);
            	}
            	else if(i == (page + 1)) { //right
            		mPaintIndicator.setAlpha(rightAlpha);
            		bitmap = BitmapFactory.decodeResource(res, rightIcon);
            		float change = trueHeight / bitmap.getHeight();
            		float trueWidth = bitmap.getWidth() * change;
            		bitmap = Bitmap.createScaledBitmap(bitmap, (int)trueWidth, (int)trueHeight, true);
					canvas.drawBitmap(bitmap, bound.right - bitmap.getWidth(), bound.top + (float)(mAboveIconPadding + (mSideIconVerticalShift * density)), mPaintIndicator);
            	}
                
//            	if(currentPage && currentSelected) {
//                    //Fade out/in unselected text as the selected text fades in/out
//                    mPaintText.setAlpha(colorTextAlpha - (int)(colorTextAlpha * selectedPercent));
//                }
//                canvas.drawText(mTitleProvider.getTitle(i), bound.left, bound.bottom + mTopPadding, mPaintText);
//
//                //If we are within the selected bounds draw the selected text
//                if (currentPage && currentSelected) {
//                    mPaintText.setColor(mColorSelected);
//                    mPaintText.setAlpha((int)((mColorSelected >>> 24) * selectedPercent));
//                    canvas.drawText(mTitleProvider.getTitle(i), bound.left, bound.bottom + mTopPadding, mPaintText);
//                }
                              
            }
        }

        //Draw the footer line
        mPath = new Path();
        mPath.moveTo(0, height - mFooterLineHeight / 2f);
        mPath.lineTo(width, height - mFooterLineHeight / 2f);
        mPath.close();
        canvas.drawPath(mPath, mPaintFooterLine);
    }

    public boolean onTouchEvent(android.view.MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        if ((mViewPager == null) || (mViewPager.getAdapter().getCount() == 0)) {
            return false;
        }

        final int action = ev.getAction();

        switch (action & MotionEventCompat.ACTION_MASK) {
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
                    if (!mViewPager.isFakeDragging()) {
                        mViewPager.beginFakeDrag();
                    }

                    mLastMotionX = x;

                    mViewPager.fakeDragBy(deltaX);
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
                            mViewPager.setCurrentItem(mCurrentPage - 1);
                            return true;
                        }
                    } else if (eventX > rightThird) {
                        if (mCurrentPage < count - 1) {
                            mViewPager.setCurrentItem(mCurrentPage + 1);
                            return true;
                        }
                    } else {
                        //Middle third
                        if (mCenterItemClickListener != null) {
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
                final float x = MotionEventCompat.getX(ev, index);
                mLastMotionX = x;
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
    };

    /**
     * Set bounds for the right textView including clip padding.
     *
     * @param curViewBound
     *            current bounds.
     * @param curViewWidth
     *            width of the view.
     */
    private void clipViewOnTheRight(RectF curViewBound, float curViewWidth, int right) {
        curViewBound.right = right - mClipPadding;
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
    private void clipViewOnTheLeft(RectF curViewBound, float curViewWidth, int left) {
        curViewBound.left = left + mClipPadding;
        curViewBound.right = mClipPadding + curViewWidth;
    }

    /**
     * Calculate views bounds and scroll them according to the current index
     *
     * @param paint
     * @param currentIndex
     * @return
     */
    private ArrayList<RectF> calculateAllBounds() {
        ArrayList<RectF> list = new ArrayList<RectF>();
        //For each views (If no values then add a fake one)
        final int count = mViewPager.getAdapter().getCount();
        final int width = getWidth();
        final int halfWidth = width / 2;
        for (int i = 0; i < count; i++) {
            RectF bounds = calcBounds(i);
            float w = (bounds.right - bounds.left);
            float h = (bounds.bottom - bounds.top);
            bounds.left = (halfWidth) - (w / 2) - mCurrentOffset + ((i - mCurrentPage) * width);
            Log.e("left bound original", bounds.left + "");
            bounds.right = bounds.left + w;
            Log.e("right bound original", bounds.right + "");
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
    private RectF calcBounds(int index) {
        //Calculate the text bounds
        RectF bounds = new RectF();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mIconProivder.getIconArray(index)[1]);
        float trueHeight = getHeight() - mFooterPadding - mTopPadding;
        float change = trueHeight / bitmap.getHeight();
		float trueWidth = bitmap.getWidth() * change;
		bitmap = Bitmap.createScaledBitmap(bitmap, (int)trueWidth, (int)trueHeight, true);
        bounds.right = bitmap.getWidth();
        bounds.bottom = bitmap.getHeight();
        return bounds;
    }

    @Override
    public void setViewPager(ViewPager view) {
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        if(!(adapter instanceof IconProvider)) {
        	throw new IllegalStateException("Needs to implement IconProvider");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        mIconProivder = (IconProvider)adapter;
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
        mCurrentOffset = positionOffsetPixels;
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
        float height = 0;
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            //Calculate the text bounds
            RectF bounds = new RectF();
            bounds.bottom = mPaintIndicator.descent()-mPaintIndicator.ascent();
            height = bounds.bottom - bounds.top + mFooterLineHeight + mFooterPadding + mTopPadding;
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
