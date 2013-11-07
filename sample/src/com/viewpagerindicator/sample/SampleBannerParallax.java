package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import com.viewpagerindicator.BannerPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

public class SampleBannerParallax extends BaseSampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_banner_fullscreen);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (BannerPageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

    }
}