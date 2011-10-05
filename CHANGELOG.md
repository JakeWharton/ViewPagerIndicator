Change Log
==========

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
