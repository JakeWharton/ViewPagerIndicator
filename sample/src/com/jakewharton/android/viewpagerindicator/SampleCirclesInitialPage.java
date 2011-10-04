package com.jakewharton.android.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.jakewharton.android.view.CirclePageIndicator;

public class SampleCirclesInitialPage extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_circles);
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setCurrentItem(mAdapter.getCount() - 1);
		
		//You can also do: indicator.setViewPager(pager, initialPage);
	}
}