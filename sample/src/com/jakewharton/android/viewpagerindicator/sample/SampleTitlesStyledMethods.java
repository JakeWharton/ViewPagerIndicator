package com.jakewharton.android.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.jakewharton.android.viewpagerindicator.TitlePageIndicator;
import com.jakewharton.android.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class SampleTitlesStyledMethods extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(new TestTitleFragmentAdapter(getSupportFragmentManager()));
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		
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