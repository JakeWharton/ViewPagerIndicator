/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jakewharton.android.viewpagerindicator;

import android.support.v4.view.ViewPager;


/**
 * An interface which defines the contract between a ViewFlow and a
 * PageIndicator.<br/>
 * A PageIndicator is responsible to show an visual indicator on the total views
 * number and the current visible view.<br/>
 *
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {

    /**
     * Set the current ViewFlow. This method is called by the ViewFlow when the
     * PageIndicator is attached to it.
     *
     * @param view
     */
    public void setViewPager(ViewPager view);

    /**
     * Set a page change listener which will receive forwarded events.
     *
     * @param listener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
}
