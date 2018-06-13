package com.liaoinstan.demospring.demo11;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.container.MeituanFooter;
import com.liaoinstan.springview.container.MeituanHeader;
import com.liaoinstan.springview.container.WeixinHeader;
import com.liaoinstan.springview.ctrl.Program;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

public class Demo11Activity extends AppCompatActivity {

    private SpringView springView;

    //测试数据
    //注意这里本应该给每个对象一个网络图片链接，再利用图片加载框架加载在线图片，
    //但是在本demo中不想引入无关框架，所以这里把本地资源图片转成String类型保存在url中，加载图片的时候再转回int类型设置图片，这样做仅为演示
    private List<Program> data = new ArrayList<Program>() {{
        add(new Program("微信电竞", String.valueOf(R.drawable.acfun_footer)));
        add(new Program("刺激战场福袋", "xxx"));
        add(new Program("美团点评2", "xxx"));
        add(new Program("美团点评3", "xxx"));
        add(new Program("美团点评4", "xxx"));
        add(new Program("美团点评5", "xxx"));
        add(new Program("美团点评6", "xxx"));
        add(new Program("美团点评7", "xxx"));
        add(new Program("美团点评8", "xxx"));
        add(new Program("美团点评9", "xxx"));
        add(new Program("美团点评10", "xxx"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo11);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#18cfbe")));

        springView = (SpringView) findViewById(R.id.springview);
        springView.setType(SpringView.Type.FOLLOW);
        springView.setMovePara(1.5);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        springView.onFinishFreshAndLoad();
                    }
                }, 2000);
            }
        });
        WeixinHeader springHeader = new WeixinHeader();
        //设置加载图片回调方法
        springHeader.setOnWeixinHeaderLoadImgCallback(new WeixinHeader.OnWeixinHeaderLoadImgCallback() {
            @Override
            public void loadImg(ImageView imageView, String imgUrl, int position) {
                //在这个回调中自行使用项目中的图片加载框架加载网络图片，这里因为不想在demo中导入无关框架，所以直接加载本地图片演示
//                imageView.setImageResource(Integer.parseInt(imgUrl));
            }
        });
        springHeader.freshData(data);
        springView.setHeader(springHeader);
        springView.setFooter(new AliFooter(this));
    }
}
