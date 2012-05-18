package com.viewpagerindicator.sample;

import android.support.v4.app.FragmentManager;

class TestTitleFragmentAdapter extends TestFragmentAdapter {
    public TestTitleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TestFragmentAdapter.CONTENT[position % CONTENT.length];
    }
}