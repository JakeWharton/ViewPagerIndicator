LoopingViewPager
================

An android ViewPager extension allowing infinite scrolling.

This viewPager is fully compatible with [ViewPagerIndicator][1], and [PagerSlidingTabStrip][2]!



Usage
-----

To use it simply change `<android.support.v4.view.ViewPager>` to `<com.imbryk.viewPager.LoopViewPager>`

I your PagerAdapter is used only to create Views (i.e. you don't use FragmentPagerAdapter or FragmentStatePagerAdapter),
then no additional code changes are needed!


If you want to use LoopViewPager with FragmentPagerAdapter or FragmentStatePagerAdapter
additional changes in the adapter must be done.    
The adapter must be prepared to create 2 extra items e.g.:

The original adapter creates 4 items: `[0,1,2,3]`   
The modified adapter will have to create 6 items `[0,1,2,3,4,5]`   
with mapping `realPosition=(position-1)%count`   
`[0->3, 1->0, 2->1, 3->2, 4->3, 5->0]`


Sometimes "blinking" can be seen when paginating to first or last view. 
To remove this effect, simply call setBoundaryCaching( true ) on your LoopViewPager,
or change DEFAULT_BOUNDARY_CASHING to true, if you want to set boundary caching
on all LoopViewPager instances



License
=======

    Copyright 2013 Leszek Mzyk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    
    
 [1]: https://github.com/JakeWharton/Android-ViewPagerIndicator
 [2]: https://github.com/astuetz/PagerSlidingTabStrip