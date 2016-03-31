package com.liaoinstan.demospring.demo2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.container.RotationFooter;
import com.liaoinstan.springview.container.RotationHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Demo2Activity extends AppCompatActivity {

    private SpringView springView;
    private List<String> mDatas = new ArrayList<String>();
    private ListView listView;
    private AdapterForList listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#999999"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        initData();

        listView = (ListView) findViewById(R.id.list);
        listAdapter = new AdapterForList(this, mDatas);
        listView.setAdapter(listAdapter);

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
        springView.setHeader(new RotationHeader(this));
        springView.setFooter(new RotationFooter(this));
    }

    private void initData(){
        for (int i = 0; i < 8; i++) {
            mDatas.add(i==0?"We are in ListView":(i==1?"SpringView支持ListView\n\n你可以轻易定制个性化的头部和尾部并在任何控件中使用它":""));
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.add: {
                mDatas.add("add item");
                listAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.sub:
                if (mDatas.size()>0) {
                    int position = mDatas.size() - 1;
                    mDatas.remove(position);
                    listAdapter.notifyDataSetChanged();
                }
                break;
        }
    }



    private class AdapterForList extends BaseAdapter {
        private Context context = null;
        private List<String> results;
        public AdapterForList(Context context,List<String> results){
            this.context = context;
            this.results = results;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return results.get(position);
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item_text;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
                item_text = (TextView)convertView.findViewById(R.id.item_text);
                convertView.setTag(item_text);
            }else {
                item_text = (TextView) convertView.getTag();
            }
            if (position%2==1) {
                item_text.setBackgroundColor(Color.parseColor("#e3f1fc"));
                item_text.setTextColor(Color.parseColor("#9dd2fc"));
            }
            else {
                item_text.setBackgroundColor(Color.parseColor("#ffffff"));
                item_text.setTextColor(Color.parseColor("#cccccc"));
            }
            item_text.setText(results.get(position));
            return convertView;
        }
    }
}