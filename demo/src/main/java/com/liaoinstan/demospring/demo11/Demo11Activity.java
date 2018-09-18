package com.liaoinstan.demospring.demo11;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.aliheader.AliFooter;
import com.liaoinstan.springview.weixinheader.Program;
import com.liaoinstan.springview.weixinheader.WeixinHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Demo11Activity extends AppCompatActivity {

    private SpringView springView;
    private WeixinHeader springHeader;

    //测试数据
    //注意这里本应该给每个对象一个网络图片链接，再利用图片加载框架加载在线图片，
    //但是在本demo中不想引入无关框架，所以这里把本地资源图片转成String类型保存在url中，加载图片的时候再转回int类型设置图片，这样做仅为演示
    private List<Program> data = new ArrayList<Program>() {{
        add(new Program("ofo小黄车", String.valueOf(R.drawable.wx_program1)));
        add(new Program("哈图", String.valueOf(R.drawable.wx_program2)));
        add(new Program("好货", String.valueOf(R.drawable.wx_program3)));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo11);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        springView = findViewById(R.id.springview);
        springView.setMovePara(1.5);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 2000);
            }
        });
        springView.setHeader(springHeader = new WeixinHeader());
        springView.setFooter(new AliFooter(this));

        //////////////////////////////
        ////  初始化微信小程序header
        //////////////////////////////

        //设置加载图片回调方法
        springHeader.setOnLoadImgCallback((imageView, imgUrl, position) -> {
            //在这个回调中自行使用项目中的图片加载框架加载网络图片，这里因为不想在demo中导入无关框架，所以直接加载本地图片演示
            imageView.setImageResource(Integer.parseInt(imgUrl));
        });
        //“更多”按钮点击事件
        springHeader.setOnMoreClickListener(() -> {
            addItemClick();
        });
        //item 点击事件
        springHeader.setOnProgramClickListener((program, holder, position) -> {
            Toast.makeText(Demo11Activity.this, program.getName() + " click", Toast.LENGTH_SHORT).show();
        });
        //item 长点击事件
        springHeader.setOnProgramLongClickListener((program, holder, position) -> {
            addItemLongClick(program, position);
        });

        //设置数据
        springHeader.freshItem(data);
    }


    private void addItemClick() {
        new AlertDialog.Builder(this)
                .setMessage("确定要添加新的小程序？")
                .setPositiveButton("确定", (dialog, which) -> {
                    //随机添加一个小程序
                    switch (new Random().nextInt(5)) {
                        case 0:
                            springHeader.addItem(new Program("ofo小黄车", String.valueOf(R.drawable.wx_program1)));
                            break;
                        case 1:
                            springHeader.addItem(new Program("哈图", String.valueOf(R.drawable.wx_program2)));
                            break;
                        case 2:
                            springHeader.addItem(new Program("好货", String.valueOf(R.drawable.wx_program3)));
                            break;
                        case 3:
                            springHeader.addItem(new Program("快闪", String.valueOf(R.drawable.wx_program4)));
                            break;
                        case 4:
                            springHeader.addItem(new Program("蘑菇街", String.valueOf(R.drawable.wx_program5)));
                            break;
                    }
                })
                .create()
                .show();
    }

    private void addItemLongClick(Program program, int position) {
        new AlertDialog.Builder(this)
                .setMessage("确定要移除'" + program.getName() + "'？")
                .setPositiveButton("确定", (dialog, which) -> {
                    springHeader.removeItem(position);
                })
                .create()
                .show();
    }
}
