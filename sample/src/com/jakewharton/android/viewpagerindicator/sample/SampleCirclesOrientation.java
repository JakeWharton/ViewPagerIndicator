package com.jakewharton.android.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.jakewharton.android.view.CirclePageIndicator;
import com.jakewharton.android.view.DirectionalViewPager;

public class SampleCirclesOrientation extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_circles_vertical);

        DirectionalViewPager pager = (DirectionalViewPager)findViewById(R.id.pager);
        pager.setOrientation(DirectionalViewPager.VERTICAL);
        pager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setOrientation(CirclePageIndicator.VERTICAL);
        indicator.setViewPager(pager);
    }
}