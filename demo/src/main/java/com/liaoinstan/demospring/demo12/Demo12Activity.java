package com.liaoinstan.demospring.demo12;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.demospring.utils.StatusBarUtil;
import com.liaoinstan.springview.weixinheader.Program;
import com.liaoinstan.springview.weixinheaderv2.WeixinHeaderV2;
import com.liaoinstan.springview.weixinheaderv2.WeixinTitleBar;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Demo12Activity extends AppCompatActivity implements WeixinHeaderV2.OnMoreClickListener, WeixinHeaderV2.OnProgramClickListener, WeixinHeaderV2.OnProgramDropListener, WeixinHeaderV2.OnSearchClickListener {

    private View bottomView;
    private WeixinTitleBar weixinTitleBar;
    private SpringView springView;
    private WeixinHeaderV2 weixinHeaderV2;

    //测试数据
    //注意这里本应该给每个对象一个网络图片url链接，再利用图片加载框架加载网络图片，
    //但是在本demo中不想引入无关框架，所以这里把本地资源图片id转成String类型保存在url字段中，加载图片的时候再转回int类型设置图片，这样做仅为演示
    private List<Program> dataRecent = new ArrayList<Program>() {{
        add(new Program("ofo小黄车", String.valueOf(R.drawable.wx_program1)));
        add(new Program("哈图", String.valueOf(R.drawable.wx_program2)));
        add(new Program("好货", String.valueOf(R.drawable.wx_program3)));
        add(new Program("快闪", String.valueOf(R.drawable.wx_program4)));
        add(new Program("蘑菇街", String.valueOf(R.drawable.wx_program5)));
        add(new Program("ofo小黄车", String.valueOf(R.drawable.wx_program1)));
        add(new Program("哈图", String.valueOf(R.drawable.wx_program2)));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo12);
        bottomView = findViewById(R.id.bottom_view);
        weixinTitleBar = findViewById(R.id.weixin_title_bar);
        springView = findViewById(R.id.springview);
        //设置状态栏为透明重叠（沉浸式）
        StatusBarUtil.setTranslucent(this);

        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
            }
        });

        ///////////////////////////////////
        ////  初始化微信小程序headerV2 ////
        ///////////////////////////////////

        //微信headerV2 的构造函数需要bottomView，weixinTitleBar两个参数
        //bottomView 是页面底部导航条，weixinHeaderV2在拖拽过程中会控制它收起或弹出，可以是任意View，这里为了方便使用了material包下的BottomNavigationView
        //weixinTitleBar 是页面顶部的标题栏，weixinHeaderV2在拖拽过程中会控制它进行动画交互
        //这两个参数均可为 null，为null则 weixinHeaderV2不会进行任何处理
        weixinHeaderV2 = new WeixinHeaderV2(bottomView, weixinTitleBar);

        //设置加载图片回调方法
        weixinHeaderV2.setOnLoadImgCallback((imageView, imgUrl, position) -> {
            //在这个回调中自行使用项目中的图片加载框架加载网络图片，这里因为不想在demo中导入无关框架，所以直接加载本地图片演示
            imageView.setImageResource(Integer.parseInt(imgUrl));
        });
        //item 点击事件
        weixinHeaderV2.setOnProgramClickListener(this);
        //item drop（拖拽删除）事件
        weixinHeaderV2.setOnProgramDropListener(this);
        //“更多”按钮点击事件
        weixinHeaderV2.setOnMoreClickListener(this);
        //“搜索框”点击事件
        weixinHeaderV2.setOnSearchClickListener(this);

        //设置小程序数据('最近使用')
        //如果要添加数据到'我的小程序'，调用addItemMine(...)方法
        weixinHeaderV2.addItemRecent(dataRecent);

        //设置header到SpringView
        springView.setHeader(weixinHeaderV2);
    }

    @Override
    public void onClick(Program program, RecyclerView.ViewHolder holder, int position) {
        Toast.makeText(Demo12Activity.this, program.getName() + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onDrop(Program program, RecyclerView.ViewHolder holder, int position) {
        //Toast.makeText(Demo12Activity.this, "删除：" + program.getName(), Toast.LENGTH_SHORT).show();
        //返回ture会执行删除操作，false不删除
        return true;
    }

    @Override
    public void onSearchClick() {
        Toast.makeText(this, "onSearchClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreClick() {
        //弹出个对话框，点确定就随机添加一个小程序
        new AlertDialog.Builder(this)
                .setMessage("确定要添加新的小程序？（仅测试）")
                .setPositiveButton("确定", (dialog, which) -> {
                    switch (new Random().nextInt(5)) {
                        case 0:
                            weixinHeaderV2.addItemRecent(new Program("ofo小黄车", String.valueOf(R.drawable.wx_program1)));
                            break;
                        case 1:
                            weixinHeaderV2.addItemRecent(new Program("哈图", String.valueOf(R.drawable.wx_program2)));
                            break;
                        case 2:
                            weixinHeaderV2.addItemRecent(new Program("好货", String.valueOf(R.drawable.wx_program3)));
                            break;
                        case 3:
                            weixinHeaderV2.addItemRecent(new Program("快闪", String.valueOf(R.drawable.wx_program4)));
                            break;
                        case 4:
                            weixinHeaderV2.addItemRecent(new Program("蘑菇街", String.valueOf(R.drawable.wx_program5)));
                            break;
                    }
                })
                .create()
                .show();
    }
}
