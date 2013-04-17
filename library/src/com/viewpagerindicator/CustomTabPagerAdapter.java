package com.viewpagerindicator;

import android.view.View;

public interface CustomTabPagerAdapter {
    /**
     * Get view representing the page at {@code index} in the adapter.
     */
    View getTabView(int index);

    // From PagerAdapter
    int getCount();
}
