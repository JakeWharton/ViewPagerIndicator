Icon Page Indicator
===================

An edit to [Jake Wharton][11]'s ViewPagerIndicator library giving users the ability to 
use drawable icons as indicators the same way text was used in the 
TitlePageIndicator class. Full README for Jake's original class is below this section.

![IconPageIndicator Sample Screenshots][12]



Usage
=====

*For a working implementation of the IconPageIndicator class see the `sample/`
folder. Try changing the drawables to see how it works!

  1. Include the IconPageIndicator widget in your view:

		<com.viewpagerindicator.IconPageIndicator
			android:id="@+id/indicator"
			android:padding="5dp"
			android:layout_height="wrap_content"
			android:layout_width="fill_parent" />
  
  2. In your `onCreate` method (or `onCreateView` for a fragment), bind the
     indicator to the `ViewPager`:
	 
		//From the sample, note that IconPagerAdapter is a simple nested class I created
		ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
		IconPagerAdapter adapter = new IconPagerAdapter();
		pager.setAdapter(adapter);
		pager.setCurrentItem(1); //set this to whatever you want to start with focus
		IconPageIndicator indicator = (IconPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager, 1); //set the int to the same page that has focus above
		
		Resources res = this.getResources();
		float density = res.getDisplayMetrics().density;
		
		indicator.setTopPadding(8 * density);
		//set whatever other features you want

		
  3. Implementing the IconProvider Interface requires overriding the method `getIconArray` method.
	 For each view in your ViewPager, you must provide an array of length 3 providing the desired
	 drawable for when the view is at the left, center, and right position in indicies 0, 1 and 2 
	 respectively. An example of this implementation is below (as well as in the sample app):
		
		@Override
		public Integer[] getIconArray(int i) {
			switch(i) {
			case LEFT_ACTIVITY :
				Integer[] mainDrawables = 
				{R.drawable.arrow_left, R.drawable.leftlogo, R.drawable.arrow_right};
				return mainDrawables;
			case CENTER_ACTIVITY :
				Integer[] settingsDrawables = 
				{R.drawable.arrow_left, R.drawable.uglylogo, R.drawable.arrow_right};
				return settingsDrawables;
			case RIGHT_ACTIVITY :
				Integer[] friendDrawables = 
				{R.drawable.rightlogomini, R.drawable.rightlogo, R.drawable.rightlogomini};
				return friendDrawables;
			default :
				throw new IllegalArgumentException("Page does not exist");
			}
		}
  
	 Note that for each activity you are specifying the icons for the activities' relative position
	 on the screen, NOT the icons that will necessarily appear on that view. For the above example, 
	 the center activity will show the `ugly logo` drawable in the center when it is in focus,
	 the `arrow_right` icon when it is to the right of the view in focus, and the `arrow_left` icon
	 when it is to the left of the view in focus.
	 
	 In the right activity, it shows the `rightlogo` when it is the view in focus. However, the left
	 and right icons will NOT be `rightlogomini` when it is in focus. Instead, the center activity's
	 left icon, `arrow_left` will be shown, because the center activity is to the left of the view in
	 focus. `rightlogomini` will appear when the view in focus is the center activity (when the right 
	 activity is to the right of the focus).
	 
	 This may sound a bit counter-intuitive, but it allows for users to easily add or rearrange the 
	 order of their views without needing to reconfigure their icons every single time.
  
  
Give it a try and give me some feedback! You can reach me via email directly at jmrboosties@gmail.com, 
or just PM me on github. Thanks to [Jake Wharton][11] for this fantastic library, I'm glad I could add
a neat feature to it.
  
Android ViewPagerIndicator
==========================

Port of [Patrik Åkerfeldt][1]'s paging indicators that are compatible with the
ViewPager from the [Android Compatibility Library][2] and
[ActionBarSherlock][3].

Try out the sample application [on the Android Market][10].

![ViewPagerIndicator Sample Screenshots][9]




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

     *Note*: If you are using `TitlePageIndicator` your adapter must implement
     `TitleProvider`.

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

Android-ViewPagerIndicator is presented as an [Android library project][7]. A
standalone JAR is not possible due to the theming capabilities offered by the
indicator widgets.

You can include this project by [referencing it as a library project][8] in
Eclipse or ant.

If you are a Maven user you can easily include the library by specifying it as
a dependency:

    <dependency>
      <groupId>com.viewpagerindicator</groupId>
      <artifactId>library</artifactId>
      <version>2.2.3</version>
      <type>apklib</type>
    </dependency>

You must also include the following repository:

    <repository>
      <id>com.jakewharton</id>
      <url>http://r.jakewharton.com/maven/release</url>
    </repository>



This project depends on the `ViewPager` class which is available in the
[Android Compatibility Library][2] or [ActionBarSherlock][3]. Details for
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

    Copyright 2011 Patrik Åkerfeldt
    Copyright 2011 Francisco Figueiredo Jr.
    Copyright 2011 Jake Wharton
	Copyright 2012 Jesse Ridgway

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
 [7]: http://developer.android.com/guide/developing/projects/projects-eclipse.html
 [8]: http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject
 [9]: https://raw.github.com/JakeWharton/Android-ViewPagerIndicator/master/sample/screens.png
 [10]: https://market.android.com/details?id=com.viewpagerindicator.sample
 [11]: https://github.com/jakewharton/android-viewpagerindicator
 [12]: http://raw.github.com/jmrboosties/android-viewpagerindicator/master/sample/iconscreens.png
