package com.liaoinstan.demospring.warning;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.widget.SpringView;

public class WarningActivity extends AppCompatActivity {

    private TextView textView;
    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        springView = (SpringView) findViewById(R.id.springview);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        springView.onFinishFreshAndLoad();
                    }
                }, 1000);
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        springView.onFinishFreshAndLoad();
                    }
                }, 1000);
            }
        });
        springView.setHeader(new WarningHeader());
    }
}
