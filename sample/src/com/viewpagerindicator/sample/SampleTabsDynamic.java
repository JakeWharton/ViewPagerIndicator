package com.viewpagerindicator.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.Random;

import com.viewpagerindicator.TabPageIndicator;

public class SampleTabsDynamic extends FragmentActivity {
    private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
    private TabPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_tabs);

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
    }
    
    @Override
    protected void onResume() {   
        super.onResume();
        /*
         * This AsyncTask updates the tab titles every second, 20 times.
         */
        new DynamicTabTitleTask().execute();
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        private final Random RANDOM = new Random();
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return new StringBuilder(CONTENT[position % CONTENT.length].toUpperCase())
                    .append("\n")
                    .append("(")
                    .append(RANDOM.nextInt(1000))
                    .append(")")
                    .toString();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
    
    private class DynamicTabTitleTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=0;i<20;i++){
                try {
                    Thread.sleep(1000);
                    this.publishProgress();
                } catch (InterruptedException e) {
                    //We don't care about this case
                    e.printStackTrace();
                }
            }
            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {   
            super.onProgressUpdate(values);
            /*
             * Calling notifyDatasetChanged on the _indicator_ object will
             * trigger an update in the tab titles.
             */
            mIndicator.notifyDataSetChanged();
        }
        
    }
}
