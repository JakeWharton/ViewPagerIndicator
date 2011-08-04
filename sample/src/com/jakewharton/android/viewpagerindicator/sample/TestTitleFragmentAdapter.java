package com.jakewharton.android.viewpagerindicator.sample;

import android.support.v4.app.FragmentManager;
import com.jakewharton.android.viewpagerindicator.TitleProvider;

class TestTitleFragmentAdapter extends TestFragmentAdapter implements TitleProvider {
	public TestTitleFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public String getTitle(int position) {
		return TestFragmentAdapter.CONTENT[position];
	}
}