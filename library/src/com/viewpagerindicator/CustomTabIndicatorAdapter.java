package com.viewpagerindicator;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by pengwei on 15/4/4.
 */
public interface CustomTabIndicatorAdapter {

    View getTabView(int position, ViewGroup parent);

    // From PagerAdapter
    int getTabCount();
}
