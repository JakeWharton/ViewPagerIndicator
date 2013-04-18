package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.viewpagerindicator.TitlePageIndicator;

public class SampleTitlesXofY extends BaseSampleActivity {

	private int currentPos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);

		mAdapter = new MyXofYPagerAdapter();

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(final int position) {
				currentPos = position;
			}

		});

	}

	class MyXofYPagerAdapter extends TestFragmentAdapter {

		public MyXofYPagerAdapter() {
			super(getSupportFragmentManager());
		}

		/**
		 * depending on position returns X of Y
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			if (position < currentPos) {
				return getString(R.string.x_of_y_view_pager_prev);
			} else if (position > currentPos) {
				return getString(R.string.x_of_y_view_pager_next);
			} else {
				return getString(R.string.x_of_y_view_pager_title,
						new Object[] { position + 1, getCount() });
			}
		}

	}

}