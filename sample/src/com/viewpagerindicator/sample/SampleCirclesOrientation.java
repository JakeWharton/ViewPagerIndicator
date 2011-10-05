package com.viewpagerindicator.sample;

import android.os.Bundle;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.R;
import com.directionalviewpager.DirectionalViewPager;

public class SampleCirclesOrientation extends BaseSampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_circles_vertical);
        
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        DirectionalViewPager pager = (DirectionalViewPager)findViewById(R.id.pager);
        pager.setOrientation(DirectionalViewPager.VERTICAL);
        pager.setAdapter(mAdapter);
        mPager = pager;

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setOrientation(CirclePageIndicator.VERTICAL);
        indicator.setViewPager(mPager);
    }
}