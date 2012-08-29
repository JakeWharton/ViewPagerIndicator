package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.IconPageIndicator;

public class SampleIconsDefault extends BaseSampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_icons);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (IconPageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}
