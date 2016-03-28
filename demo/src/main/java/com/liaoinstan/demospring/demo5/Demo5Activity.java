package com.liaoinstan.demospring.demo5;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.AcFunFooter;
import com.liaoinstan.springview.container.AcFunHeader;
import com.liaoinstan.springview.widget.SpringView;

public class Demo5Activity extends AppCompatActivity {
    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo5);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f8d3e4")));

        springView = (SpringView) findViewById(R.id.springview);
        springView.setGive(SpringView.Give.NONE);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
            }
        });
        springView.setHeader(new AcFunHeader(R.drawable.acfun_header));
        springView.setFooter(new AcFunFooter(R.drawable.acfun_footer));
    }
}
