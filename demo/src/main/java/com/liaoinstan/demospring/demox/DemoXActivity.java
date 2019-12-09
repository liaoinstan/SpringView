package com.liaoinstan.demospring.demox;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.aliheader.AliFooter;
import com.liaoinstan.springview.wangyiheader.WangyiHeader;
import com.liaoinstan.springview.widget.SpringView;

/**
 * 这是仿网易新闻的例子，使用了SpringView新增的'收场动画'特性
 * 目前该特性还处于试验阶段，并未正式发布，如果效果不佳可能在后续版本移除，目前暂时移除掉这个例子
 */
public class DemoXActivity extends AppCompatActivity {

    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demox);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        springView = findViewById(R.id.springview);
        springView.setMovePara(1.3f);
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
