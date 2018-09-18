package com.liaoinstan.demospring.demo12;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.aliheader.AliFooter;
import com.liaoinstan.springview.aliheader.AliHeader;
import com.liaoinstan.springview.wangyiheader.WangyiHeader;
import com.liaoinstan.springview.weixinheader.Program;
import com.liaoinstan.springview.weixinheader.WeixinHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Demo12Activity extends AppCompatActivity {

    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo12);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        springView = findViewById(R.id.springview);
//        springView.setMovePara(1.3);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 2000);
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 2000);
            }
        });
        springView.setHeader(new WangyiHeader());
        springView.setFooter(new AliFooter(this));

    }
}
