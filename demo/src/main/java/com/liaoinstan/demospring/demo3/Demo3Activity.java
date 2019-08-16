package com.liaoinstan.demospring.demo3;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.aliheader.AliFooter;
import com.liaoinstan.springview.aliheader.AliHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

public class Demo3Activity extends AppCompatActivity {
    private List<String> mDatas = new ArrayList<String>();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        recyclerViewAdapter = new RecyclerViewAdapter(mDatas);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        springView = findViewById(R.id.springview);
        springView.setType(SpringView.Type.FOLLOW);
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 1000);
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(() -> springView.onFinishFreshAndLoad(), 1000);
            }
        });
        springView.setHeader(new AliHeader(this, R.drawable.ali, true));   //参数为：logo图片资源，是否显示文字
        springView.setFooter(new AliFooter(this, false));
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mDatas.add(i == 0 ? "We are in RecyclerView" : (i == 3 ? "SpringView支持RecyclerView\n\n这是一个仿阿里旅行的header\n\nlogo可以图片可自行替换" : ""));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add: {
                mDatas.add("add item");
                recyclerViewAdapter.notifyItemInserted(mDatas.size() - 1);
                break;
            }
            case R.id.sub:
                if (mDatas.size() > 0) {
                    int position = mDatas.size() - 1;
                    mDatas.remove(position);
                    recyclerViewAdapter.notifyItemRemoved(position);
                }
                break;
        }
    }


    /**
     * Adapter for RecyclerView
     */
    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SampleViewHolder> {
        private List<String> results;

        RecyclerViewAdapter(List<String> results) {
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
            if ((position + 1) / 2 % 2 == 1) {
                holder.text_item.setBackgroundColor(Color.parseColor("#e3f1fc"));
                holder.text_item.setTextColor(Color.parseColor("#9dd2fc"));
            } else {
                holder.text_item.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.text_item.setTextColor(Color.parseColor("#cccccc"));
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
                text_item = view.findViewById(R.id.item_text);
            }
        }
    }
}
