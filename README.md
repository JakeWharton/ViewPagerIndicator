Android ViewPagerIndicator
==========================

[ ![Download](https://api.bintray.com/packages/msdx/maven/ViewPagerIndicator/images/download.svg) ][12]

Paging indicator widgets that are compatible with the `ViewPager` from the
[Android Support Library][2] to improve discoverability of content.

Try out the sample application [on the Android Market][10].

![ViewPagerIndicator Sample Screenshots][9]

These widgets can also be used in conjunction with [ActionBarSherlock][3]!



Usage
=====

*For a working implementation of this project see the `sample/` folder.*

  1. Include one of the widgets in your view. This should usually be placed
     adjacent to the `ViewPager` it represents.

        <com.viewpagerindicator.TitlePageIndicator
            android:id="@+id/titles"
            android:layout_height="wrap_content"
            android:layout_width="fill_parent" />

  2. In your `onCreate` method (or `onCreateView` for a fragment), bind the
     indicator to the `ViewPager`.

         //Set the pager with an adapter
         ViewPager pager = (ViewPager)findViewById(R.id.pager);
         pager.setAdapter(new TestAdapter(getSupportFragmentManager()));

         //Bind the title indicator to the adapter
         TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
         titleIndicator.setViewPager(pager);

  3. *(Optional)* If you use an `OnPageChangeListener` with your view pager
     you should set it in the indicator rather than on the pager directly.

         //continued from above
         titleIndicator.setOnPageChangeListener(mPageChangeListener);


Theming
-------

There are three ways to style the look of the indicators.

 1. **Theme XML**. An attribute for each type of indicator is provided in which
    you can specify a custom style.
 2. **Layout XML**. Through the use of a custom namespace you can include any
    desired styles.
 3. **Object methods**. Both styles have getters and setters for each style
    attribute which can be changed at any point.

Each indicator has a demo which creates the same look using each of these
methods.


Including In Your Project
-------------------------

The new version of Android-ViewPagerIndicator in [my branch][11] is presented as an [Android library project][7]. A
standalone JAR is not possible due to the theming capabilities offered by the
indicator widgets.

You can include this project by [referencing it as a aar file][7] in gradle.

If you are a Gradle user you can easily include the library by specifying it as
a dependency:

    compile 'com.githang:viewpagerindicator:2.5@aar'


For SNAPSHOT version, you need to declare oss-snapshot-local repository at first:

        maven { url "http://oss.jfrog.org/oss-snapshot-local/" }

Then include it into your build.gradle:

    compile 'com.githang:viewpagerindicator:2.5-SNAPSHOT@aar'

Note: Use snapshot repository may slow down the speed of gradle resolving dependencies if you use android support library.


This project depends on the `ViewPager` class which is available in the
[Android Support Library][2] or [ActionBarSherlock][3]. Details for
including one of those libraries is available on their respecitve web sites.




Developed By
============

 * Jake Wharton - <jakewharton@gmail.com>


Credits
-------

 * [Patrik Åkerfeldt][1] - Author of [ViewFlow][4], a precursor to the ViewPager,
   which supports paged views and is the original source of both the title
   and circle indicators.
 * [Francisco Figueiredo Jr.][5] - Idea and [first implementation][6] for
   fragment support via ViewPager.




License
=======

    Copyright 2012 Jake Wharton
    Copyright 2011 Patrik Åkerfeldt
    Copyright 2011 Francisco Figueiredo Jr.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.






 [1]: https://github.com/pakerfeldt
 [2]: http://developer.android.com/sdk/compatibility-library.html
 [3]: http://actionbarsherlock.com
 [4]: https://github.com/pakerfeldt/android-viewflow
 [5]: https://github.com/franciscojunior
 [6]: https://gist.github.com/1122947
 [7]: http://tools.android.com/tech-docs/new-build-system/aar-format
 [9]: https://raw.github.com/JakeWharton/Android-ViewPagerIndicator/master/sample/screens.png
 [10]: https://play.google.com/store/apps/details?id=com.viewpagerindicator.sample
 [11]: https://github.com/msdx/ViewPagerIndicator/tree/msdx
 [12]: https://bintray.com/msdx/maven/ViewPagerIndicator/_latestVersion
