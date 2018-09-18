package com.liaoinstan.demospring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liaoinstan.demospring.demo1.Demo1Activity;
import com.liaoinstan.demospring.demo10.Demo10Activity;
import com.liaoinstan.demospring.demo11.Demo11Activity;
import com.liaoinstan.demospring.demo12.Demo12Activity;
import com.liaoinstan.demospring.demo2.Demo2Activity;
import com.liaoinstan.demospring.demo3.Demo3Activity;
import com.liaoinstan.demospring.demo4.Demo4Activity;
import com.liaoinstan.demospring.demo5.Demo5Activity;
import com.liaoinstan.demospring.demo6.Demo6Activity;
import com.liaoinstan.demospring.demo7.Demo7Activity;
import com.liaoinstan.demospring.demo8.Demo8Activity;
import com.liaoinstan.demospring.demo9.Demo9Activity;
import com.liaoinstan.demospring.test.TestActivity;
import com.liaoinstan.demospring.warning.WarningActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.demo1:
                intent.setClass(this, Demo1Activity.class);
                startActivity(intent);
                break;
            case R.id.demo2:
                intent.setClass(this, Demo2Activity.class);
                startActivity(intent);
                break;
            case R.id.demo3:
                intent.setClass(this, Demo3Activity.class);
                startActivity(intent);
                break;
            case R.id.demo4:
                intent.setClass(this, Demo4Activity.class);
                startActivity(intent);
                break;
            case R.id.demo5:
                intent.setClass(this, Demo5Activity.class);
                startActivity(intent);
                break;
            case R.id.demo6:
                intent.setClass(this, Demo6Activity.class);
                startActivity(intent);
                break;
            case R.id.demo7:
                intent.setClass(this, Demo7Activity.class);
                startActivity(intent);
                break;
            case R.id.demo8:
                intent.setClass(this, Demo8Activity.class);
                startActivity(intent);
                break;
            case R.id.demo9:
                intent.setClass(this, Demo9Activity.class);
                startActivity(intent);
                break;
            case R.id.demo10:
                intent.setClass(this, Demo10Activity.class);
                startActivity(intent);
                break;
            case R.id.demo11:
                intent.setClass(this, Demo11Activity.class);
                startActivity(intent);
                break;
            case R.id.demo12:
                intent.setClass(this, Demo12Activity.class);
                startActivity(intent);
                break;
            case R.id.warning:
                intent.setClass(this, WarningActivity.class);
                startActivity(intent);
                break;
            case R.id.test:
                intent.setClass(this, TestActivity.class);
                startActivity(intent);
                break;
        }
    }
}
