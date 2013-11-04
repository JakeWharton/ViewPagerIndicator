package com.viewpagerindicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created By: andrewgrosner
 * Date: 10/18/13
 * Contributors:
 * Description:
 */
public class BannerView extends FrameLayout{

    public interface IBannerView{
        public int getBannerWidth();
    }

    public BannerView(Context context) {
        super(context);
        setupUI(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupUI(context);
    }

    protected void setupUI(Context context){
        TextView title = new TextView(context);
        title.setText("BannerPageIndicator");
        title.setTextSize(75);

        addView(title, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

}
