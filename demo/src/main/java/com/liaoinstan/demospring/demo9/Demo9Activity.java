package com.liaoinstan.demospring.demo9;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
import com.liaoinstan.demospring.R;

public class Demo9Activity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager pager;
    private PagerAdapter adapterPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo9);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);

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
