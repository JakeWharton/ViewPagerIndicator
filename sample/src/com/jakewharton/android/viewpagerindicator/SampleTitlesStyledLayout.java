package com.jakewharton.android.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.jakewharton.android.view.TitlePageIndicator;

public class SampleTitlesStyledLayout extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.themed_titles);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
	}
}