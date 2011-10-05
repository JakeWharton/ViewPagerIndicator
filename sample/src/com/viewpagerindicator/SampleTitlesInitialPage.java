package com.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.TitlePageIndicator;

public class SampleTitlesInitialPage extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setCurrentItem(mAdapter.getCount() - 1);
		
		//You can also do: indicator.setViewPager(pager, initialPage);
	}
}