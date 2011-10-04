package com.jakewharton.android.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.jakewharton.android.view.TitlePageIndicator;
import com.jakewharton.android.view.TitlePageIndicator.IndicatorStyle;

public class SampleTitlesStyledMethods extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		
		final float density = getResources().getDisplayMetrics().density;
		indicator.setBackgroundColor(0xFFDDDDDD);
		indicator.setFooterColor(0xFFEE3333);
		indicator.setFooterLineHeight(2 * density);
		indicator.setFooterIndicatorStyle(IndicatorStyle.None);
		indicator.setTextColor(0xFF999999);
		indicator.setSelectedColor(0xFF000000);
		indicator.setSelectedBold(false);
	}
}