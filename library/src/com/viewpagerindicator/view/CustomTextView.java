package com.viewpagerindicator.view;

import com.viewpagerindicator.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView implements CustomFontView {

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFontHelper.getInstance().setCustomFont(this, context, attrs, 
				R.styleable.TabPageIndicator,
				R.styleable.TabPageIndicator_vpiFont,
				R.styleable.TabPageIndicator_vpiTypeface);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomTextView(Context context) {
		this(context, null);
	}

}
