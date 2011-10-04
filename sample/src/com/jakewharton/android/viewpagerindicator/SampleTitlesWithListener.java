package com.jakewharton.android.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
import com.jakewharton.android.view.TitlePageIndicator;

public class SampleTitlesWithListener extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		
		//We set this on the indicator, NOT the pager
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Toast.makeText(SampleTitlesWithListener.this, "Changed to page " + position, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
}