package com.liaoinstan.demospring.demo5;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.acfunheader.AcFunFooter;
import com.liaoinstan.springview.acfunheader.AcFunHeader;
import com.liaoinstan.springview.widget.SpringView;

public class Demo5Activity extends AppCompatActivity {
    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo5);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        springView = findViewById(R.id.springview);
        springView.setGive(SpringView.Give.NONE);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
            }
        });
        springView.setHeader(new AcFunHeader(this, R.drawable.acfun_header));
        springView.setFooter(new AcFunFooter(this, R.drawable.acfun_footer));
    }
}
