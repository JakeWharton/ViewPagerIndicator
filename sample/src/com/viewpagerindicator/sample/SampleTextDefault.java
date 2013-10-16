package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.TextPageIndicator;

public class SampleTextDefault extends BaseSampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_text);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (TextPageIndicator)findViewById(R.id.indicator);
        //if you don't specify custom template in XML layout, you can do it here before setting a viewpager
        //((TextPageIndicator)mIndicator).setIndicatorTemplate(getApplicationContext(), R.string.textPageIndicatorTemplate);
        mIndicator.setViewPager(mPager);
    }
}
