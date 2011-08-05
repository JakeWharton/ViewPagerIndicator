package com.jakewharton.android.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.jakewharton.android.viewpagerindicator.CirclePageIndicator;

public class SampleCirclesStyledTheme extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//The look of this sample is set via a style in the manifest
		setContentView(R.layout.simple_circles);
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}
}