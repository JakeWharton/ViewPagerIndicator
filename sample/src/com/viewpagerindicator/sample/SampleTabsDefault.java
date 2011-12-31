package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.sample.R;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

public class SampleTabsDefault extends BaseSampleActivity {
	private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		
		mAdapter = new GoogleMusicAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}
	
	class GoogleMusicAdapter extends TestFragmentAdapter implements TitleProvider {
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(SampleTabsDefault.CONTENT[position % SampleTabsDefault.CONTENT.length]);
		}

		@Override
		public int getCount() {
			return SampleTabsDefault.CONTENT.length;
		}

		@Override
		public String getTitle(int position) {
			return SampleTabsDefault.CONTENT[position % SampleTabsDefault.CONTENT.length].toUpperCase();
		}
	}
}
