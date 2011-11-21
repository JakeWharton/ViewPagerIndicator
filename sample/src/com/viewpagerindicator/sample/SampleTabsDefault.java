package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.R;
import com.viewpagerindicator.TabPageIndicator;

public class SampleTabsDefault extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
	}
}
