package com.jakewharton.android.viewpagerindicator.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jakewharton.android.viewpagerindicator.CirclePageIndicator;
import com.jakewharton.android.viewpagerindicator.TitlePageIndicator;
import com.jakewharton.android.viewpagerindicator.TitleProvider;

public class SampleActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		
		//TITLES
		ViewPager pagerTitles = (ViewPager)findViewById(R.id.pager_titles);
		pagerTitles.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
		
		TitlePageIndicator pageIndicatorTitles = (TitlePageIndicator)findViewById(R.id.titles);
		pageIndicatorTitles.setViewPager(pagerTitles);
		
		//CIRCLES
		ViewPager pagerCircles = (ViewPager)findViewById(R.id.pager_circles);
		pagerCircles.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
		
		CirclePageIndicator pageIndicatorCircles = (CirclePageIndicator)findViewById(R.id.circles);
		pageIndicatorCircles.setViewPager(pagerCircles);
	}
	
	private static final class TestFragmentAdapter extends FragmentPagerAdapter implements TitleProvider {
		private static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };

		public TestFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new TestFragment(CONTENT[position]);
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public String getTitle(int position) {
			return CONTENT[position];
		}
	}
	
	private static final class TestFragment extends Fragment {
		private final String mContent;
		
		public TestFragment(String content) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 20; i++) {
				builder.append(content).append(" ");
			}
			builder.deleteCharAt(builder.length() - 1);
			mContent = builder.toString();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			TextView text = new TextView(getActivity());
			text.setText(mContent);
			text.setTextSize(20 * getResources().getDisplayMetrics().density);
			text.setPadding(20, 20, 20, 20);
			
			LinearLayout layout = new LinearLayout(getActivity());
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			layout.setGravity(Gravity.CENTER);
			layout.addView(text);
			
			return layout;
		}
	}
}