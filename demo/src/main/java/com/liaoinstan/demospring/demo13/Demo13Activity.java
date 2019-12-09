package com.liaoinstan.demospring.demo13;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.aliheader.AliHeader;
import com.liaoinstan.springview.container.AutoFooter;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

public class Demo13Activity extends AppCompatActivity {
    private List<String> mDatas = new ArrayList<String>();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo13);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewAdapter = new RecyclerViewAdapter(mDatas);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        springView = findViewById(R.id.springview);
        springView.setType(SpringView.Type.SCROLL);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(Demo13Activity.this, "onRefresh", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 1000);
            }

            @Override
            public void onLoadmore() {
                if (springView.getFooter(AutoFooter.class).isInProgress()) {
                    //模拟网络请求列表数据
                    new Handler().postDelayed(() -> {
                        List<String> remoteData = getRemoteData();
                        if (remoteData != null) {
                            mDatas.addAll(remoteData);
                            recyclerViewAdapter.notifyItemInserted(mDatas.size() - 1);
                        } else {
                            springView.getFooter(AutoFooter.class).showBottomLine();
                        }
                    }, 1000);
                }
            }
        });
        springView.setHeader(new AliHeader(this));
        springView.setFooter(new AutoFooter());
    }

    //模拟请求服务器数据
    //如果数据小于14条就每次返回2条，大于14条就返回null，模拟服务器已经没有数据的情况，这只为演示
    private List<String> getRemoteData() {
        if (mDatas.size() < 20) {
            List<String> remoteData = new ArrayList<>();
            remoteData.add("add item");
            remoteData.add("add item");
            remoteData.add("add item");
            return remoteData;
        } else {
            return null;
        }
    }

    private void initData() {
        for (int i = 0; i < 15; i++) {
            mDatas.add(i == 0 ? "We are in RecyclerView" : (i == 1 ? "SpringView新增了SCROLL模式\n\n基于这种模式可以实现自动加载等效果\n\n下拉到底部试试" : ""));
        }
    }

    /**
     * Adapter for RecyclerView
     */
    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SampleViewHolder> {
        private List<String> results;

        public RecyclerViewAdapter(List<String> results) {
            this.results = results;
        }

        @NonNull
        @Override
        public SampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new SampleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SampleViewHolder holder, final int position) {
            holder.text_item.setText(results.get(position));
            if (position % 2 == 1) {
                holder.text_item.setBackgroundColor(Color.parseColor("#e3f1fc"));
                holder.text_item.setTextColor(Color.parseColor("#9dd2fc"));
            } else {
                holder.text_item.setBackgroundColor(Color.parseColor("#4468b3f3"));
                holder.text_item.setTextColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        class SampleViewHolder extends RecyclerView.ViewHolder {
            TextView text_item;

            SampleViewHolder(View view) {
                super(view);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.height = 250;
                itemView.setLayoutParams(layoutParams);
                text_item = view.findViewById(R.id.item_text);
            }
        }
    }
}
