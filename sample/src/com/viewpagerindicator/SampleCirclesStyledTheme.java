package com.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class SampleCirclesStyledTheme extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//The look of this sample is set via a style in the manifest
		setContentView(R.layout.simple_circles);
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
	}
}