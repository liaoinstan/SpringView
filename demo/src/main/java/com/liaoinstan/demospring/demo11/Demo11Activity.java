package com.liaoinstan.demospring.demo11;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.aliheader.AliFooter;
import com.liaoinstan.springview.weixinheader.Program;
import com.liaoinstan.springview.weixinheader.WeixinHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Demo11Activity extends AppCompatActivity implements WeixinHeader.OnMoreClickListener, WeixinHeader.OnProgramClickListener, WeixinHeader.OnProgramLongClickListener {

    private SpringView springView;
    private WeixinHeader weixinHeader;

    //测试数据
    //注意这里本应该给每个对象一个网络图片url链接，再利用图片加载框架加载网络图片，
    //但是在本demo中不想引入无关框架，所以这里把本地资源图片id转成String类型保存在url字段中，加载图片的时候再转回int类型设置图片，这样做仅为演示
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
        springView.setMovePara(1.5f);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 2000);
            }
        });
        springView.setHeader(weixinHeader = new WeixinHeader());
        springView.setFooter(new AliFooter(this));

        /////////////////////////////////
        ////  初始化微信小程序header ////
        /////////////////////////////////

        //设置加载图片回调方法
        weixinHeader.setOnLoadImgCallback((imageView, imgUrl, position) -> {
            //在这个回调中自行使用项目中的图片加载框架加载网络图片，这里因为不想在demo中导入无关框架，所以直接加载本地图片演示
            imageView.setImageResource(Integer.parseInt(imgUrl));
        });
        //item 点击事件
        weixinHeader.setOnProgramClickListener(this);
        //item 长点击事件
        weixinHeader.setOnProgramLongClickListener(this);
        //“更多”按钮点击事件
        weixinHeader.setOnMoreClickListener(this);

        //设置小程序数据
        weixinHeader.freshItem(data);
    }

    @Override
    public void onClick(Program program, RecyclerView.ViewHolder holder, int position) {
        Toast.makeText(Demo11Activity.this, program.getName() + " click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(Program program, RecyclerView.ViewHolder holder, int position) {
        //弹出个对话框，点确定就删除该小程序
        new AlertDialog.Builder(this)
                .setMessage("确定要移除'" + program.getName() + "'？")
                .setPositiveButton("确定", (dialog, which) -> {
                    weixinHeader.removeItem(position);
                })
                .create()
                .show();
    }

    @Override
    public void onMoreClick() {
        //弹出个对话框，点确定就随机添加一个小程序
        new AlertDialog.Builder(this)
                .setMessage("确定要添加新的小程序？")
                .setPositiveButton("确定", (dialog, which) -> {
                    switch (new Random().nextInt(5)) {
                        case 0:
                            weixinHeader.addItem(new Program("ofo小黄车", String.valueOf(R.drawable.wx_program1)));
                            break;
                        case 1:
                            weixinHeader.addItem(new Program("哈图", String.valueOf(R.drawable.wx_program2)));
                            break;
                        case 2:
                            weixinHeader.addItem(new Program("好货", String.valueOf(R.drawable.wx_program3)));
                            break;
                        case 3:
                            weixinHeader.addItem(new Program("快闪", String.valueOf(R.drawable.wx_program4)));
                            break;
                        case 4:
                            weixinHeader.addItem(new Program("蘑菇街", String.valueOf(R.drawable.wx_program5)));
                            break;
                    }
                })
                .create()
                .show();
    }
}
