package com.viewpagerindicator;

import android.graphics.drawable.Drawable;

public interface ImageTabDrawableAdapter {
	
	public int getCount();

	public Drawable getDrawable(int position);
	
}
