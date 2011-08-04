package com.jakewharton.android.viewpagerindicator.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter {
	protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };

	public TestFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return new TestFragment(CONTENT[position]);
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}
}