package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.viewpagerindicator.sample.R;
import com.viewpagerindicator.synchronizedScroll.MyScrollView;
import com.viewpagerindicator.synchronizedScroll.ScrolledLayout;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

public class SampleSynchronizedScrollDefault extends BaseSampleActivity {
	private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synchronized_scroll);

		mAdapter = new GoogleMusicAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

    MyScrollView scrollView = (MyScrollView) findViewById(R.id.scroll);
    ScrolledLayout scrolledLayout = (ScrolledLayout) findViewById(R.id.scrolled_layout);
    View movable = findViewById(R.id.movable);
    scrollView.setDelegate(scrolledLayout);

    scrolledLayout.setMovable(movable);
    scrolledLayout.setToMove((View) mIndicator);

	}
	
	class GoogleMusicAdapter extends TestFragmentAdapter implements TitleProvider {
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(SampleSynchronizedScrollDefault.CONTENT[position % SampleSynchronizedScrollDefault.CONTENT.length]);
		}

		@Override
		public int getCount() {
			return SampleSynchronizedScrollDefault.CONTENT.length;
		}

		@Override
		public String getTitle(int position) {
			return SampleSynchronizedScrollDefault.CONTENT[position % SampleSynchronizedScrollDefault.CONTENT.length].toUpperCase();
		}
	}
}
