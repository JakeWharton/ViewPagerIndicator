package com.viewpagerindicator.sample;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconProvider;

public class SampleIconsDefault extends Activity {

	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initLayout();
	}

	private void initLayout() {
		setContentView(R.layout.simple_icons);
		ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
		IconPagerAdapter adapter = new IconPagerAdapter();
		pager.setAdapter(adapter);
		pager.setCurrentItem(1); //set this to whatever you want to start with focus
		IconPageIndicator indicator = (IconPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager, 1); //set the int to the same page that has focus above
		
		Resources res = this.getResources();
		float density = res.getDisplayMetrics().density;
		
		indicator.setTopPadding(8 * density);
		//set whatever other features you want
	}
	
	private void initLeftLayout(LinearLayout layout) {
		//special stuff like button listeners go here
	}
	
	private void initCenterLayout(LinearLayout layout) {
		//special stuff like button listeners go here
	}
	
	private void initRightLayout(LinearLayout layout) {
		//special stuff like button listeners go here
	}
	
	private class IconPagerAdapter extends PagerAdapter implements IconProvider {

		private int COUNT = 3; //however many views you want
		
		private static final int LEFT_ACTIVITY = 0;
		private static final int CENTER_ACTIVITY = 1;
		private static final int RIGHT_ACTIVITY = 2;
		
		@Override
		public int getCount() {
			return COUNT;
		}
		
		@Override
		public Integer[] getIconArray(int i) {
			switch(i) {
			case LEFT_ACTIVITY :
				Integer[] mainDrawables = {R.drawable.arrow_left, R.drawable.leftlogo, R.drawable.arrow_right};
				return mainDrawables;
			case CENTER_ACTIVITY :
				Integer[] settingsDrawables = {R.drawable.arrow_left, R.drawable.uglylogo, R.drawable.arrow_right};
				return settingsDrawables;
			case RIGHT_ACTIVITY :
				Integer[] friendDrawables = {R.drawable.arrow_left, R.drawable.rightlogo, R.drawable.arrow_right};
				return friendDrawables;
			default :
				throw new IllegalArgumentException("Page does not exist");
			}
		}
		
		@Override
		public Object instantiateItem(View collection, int position) {
			LinearLayout layout = new LinearLayout(getApplicationContext());
			LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch(position) {
			case LEFT_ACTIVITY :
				layout = (LinearLayout) inflater.inflate(R.layout.icon_sample_left, null, false);
				initLeftLayout(layout);
				break;
			case CENTER_ACTIVITY :
				layout = (LinearLayout) inflater.inflate(R.layout.icon_sample_center, null, false);
				initCenterLayout(layout);
				break;
			case RIGHT_ACTIVITY :
				layout = (LinearLayout) inflater.inflate(R.layout.icon_sample_right, null, false);
				initRightLayout(layout);
				break;
			}
			
			((ViewPager)collection).addView(layout, 0);
			return layout;
		}

		@Override
		public boolean isViewFromObject(View v, Object o) {
			return v == (LinearLayout) o;
		}
		
		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager)collection).removeView((LinearLayout)view);
		}
		
		@Override
        public Parcelable saveState() {
        	return null;
        }
		
	}
	
}
