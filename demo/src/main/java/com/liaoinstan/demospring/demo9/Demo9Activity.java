package com.liaoinstan.demospring.demo9;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.liaoinstan.demospring.R;

public class Demo9Activity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager pager;
    private PagerAdapter adapterPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo9);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tab = (TabLayout) findViewById(R.id.tab);
        pager = (ViewPager) findViewById(R.id.pager);

        adapterPager = new PagerAdapter(getSupportFragmentManager(), new String[]{"ScrollView", "RecyclerView", "note"});
        pager.setAdapter(adapterPager);
        tab.setupWithViewPager(pager);
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private String[] titles;

        public PagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Demo9FragmentScrollView.newInstance(position);
                case 1:
                    return Demo9FragmentRecyclerView.newInstance(position);
                case 2:
                    return Demo9FragmentNote.newInstance(position);
                default:
                    return null;
            }
        }
    }
}
