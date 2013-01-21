Change Log
==========

Version 2.4.1 *(2012-09-11)*
----------------------------

 * Fix: `TitlePageIndicator`, `LinePageIndicator`, and `UnderlinePageIndicator`
   support of `android:background`.


Version 2.4.0 *(2012-09-06)*
----------------------------

 * New `IconPageIndicator`! Uses state-list images to represent pages.
 * `TabPageIndicator` now supports icons via `IconPagerAdapter` interface.
 * Support `android:background` attribute on `Canvas`-based views.
 * Title indicator allows for drawing its line, underline, and/or triangle on
   top of the titles for placement underneath a `ViewPager`.
 * Tab indicator now supports ICS-style dividers (see styled sample).
 * Fix: Do not attempt to change the `ViewPager` page when a motion is
   cancelled.
 * Fix: Long titles no longer overlap when swiping to the right.


Version 2.3.1 *(2012-05-19)*
----------------------------

 * Fix: Corrected filename with erroneous lowercase letter.


Version 2.3.0 *(2012-05-19)*
----------------------------

 * New `LinePageIndicator`! Draws small indicators lines much like the circle
   indicator but much more subtle.
 * New `UnderlinePageIndicator`! Acts like the indicator on the Ice Cream
   Sandwich application launcher.
 * Circle indicator now uses `android:orientation` rather than custom attribute.
 * Title indicator adapter callback now uses the standard `getPageTitle(int)`
   method introduced in the r6 version of the support library.
 * Title indicator now uses `android:textSize` and `android:textColor` in its
   styles.
 * Fix: Do not create objects in drawing, layout, or measurement steps of each
   indicator.
 * Fix: Improve offset detection when page margins are in use on the pager.
 * Maven: The dependency on the support library is now using an artifact from
   central rather than requiring you to deploy your own locally


Version 2.2.3 *(2012-01-26)*
----------------------------

 * Correctly handle removing the last page when it is selected.
 * Use antialiased text for the title indicators.
 * New circle fill color for circle indicators.


Version 2.2.2 *(2011-12-31)*
----------------------------

 * Fix incorrect `R.java` imports in all of the sample activities.


Version 2.2.1 *(2011-12-31)*
----------------------------

 * New `setTypeface(Typeface)` and `getTypeface()` methods for title indicator.
   (Thanks Dimitri Fedorov)
 * Added styled tab indicator sample.
 * Support for widths other than those that could be measured exactly.


Version 2.2.0 *(2011-12-13)*
----------------------------

 * Default title indicator style is now 'underline'.
 * Title indicator now allows specifying an `OnCenterItemClickListener` which
   will give you callbacks when the current item title has been clicked.
   (Thanks Chris Banes)


Version 2.1.0 *(2011-11-30)*
----------------------------

 * Indicators now have a `notifyDataSetChanged` method which should be called
   when changes are made to the adapter.
 * Fix: Avoid `NullPointerException`s when the `ViewPager` is not immediately
   bound to the indicator.


Version 2.0.0 *(2011-11-20)*
----------------------------

 * New `TabPageIndicator`! Uses the Ice Cream Sandwich-style action bar tabs
   which fill the width of the view when there are only a few tabs or provide
   horizontal animated scrolling when there are many.
 * Update to link against ACLv4r4. This will now be required in all implementing
   applications.
 * Allow dragging the title and circle indicators to drag the pager.
 * Remove orientation example as the DirectionalViewPager library has not been
   updated to ACLv4r4.


Version 1.2.1 *(2011-10-20)*
----------------------------

Maven 3 is now required when building from the command line.

 * Update to support ADT 14.


Version 1.2.0 *(2011-10-04)*
----------------------------

 * Move to `com.viewpagerindicator` package.
 * Move maven group and artifact to `com.viewpagerindicator:library`.


Version 1.1.0 *(2011-10-02)*
----------------------------

 * Package changed from `com.jakewharton.android.viewpagerindicator` to
   `com.jakewharton.android.view`.
 * Add vertical orientation support to the circle indicator.
 * Fix: Corrected drawing bug where a single frame would be drawn as if the
   pager had completed its scroll when in fact it was still scrolling.
   (Thanks SimonVT!)


Version 1.0.1 *(2011-09-15)*
----------------------------

 * Fade selected title color to normal text color during the swipe to and from
   the center position.
 * Fix: Ensure both the indicator and footer line are updated when changing the
   footer color via the `setFooterColor` method.


Version 1.0.0 *(2011-08-07)*
----------------------------

Initial release.
