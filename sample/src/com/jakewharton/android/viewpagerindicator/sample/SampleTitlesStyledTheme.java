package com.jakewharton.android.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.jakewharton.android.viewpagerindicator.TitlePageIndicator;

public class SampleTitlesStyledTheme extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//The look of this sample is set via a style in the manifest
		setContentView(R.layout.simple_titles);
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(new TestTitleFragmentAdapter(getSupportFragmentManager()));
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}
}