package com.liaoinstan.demospring.demo14;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.duheader.DuHeader;
import com.liaoinstan.springview.duheader.TopBarFrameLayout;
import com.liaoinstan.springview.widget.SpringView;

public class Demo14Activity extends AppCompatActivity {

    private SpringView springView;
    private TopBarFrameLayout topBarFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo14);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        topBarFrameLayout = findViewById(R.id.top_bar_frame_layout);
        springView = findViewById(R.id.springview);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    //0.5秒后再结束header刷新动画（模仿'毒'的延迟，大概0.5秒左右）
                    springView.onFinishFreshAndLoadDelay(500);
                    //开始展开topBar顶部提示，并在3秒回自动收回
                    topBarFrameLayout.setTopBarText("为你更新20条新内容");
                    topBarFrameLayout.showAndHideDelay(3000);
                }, 2000);
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 2000);
            }
        });
        springView.setHeader(new DuHeader());
    }
}
