package com.liaoinstan.demospring.demo8;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

public class Demo8Activity extends AppCompatActivity {
    private List<String> mDatas = new ArrayList<String>();
    private SpringView springView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo8);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();

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
        springView.setHeader(new DefaultHeader(this));
        springView.setFooter(new DefaultFooter(this));

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(mDatas);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(recyclerAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            mDatas.add(i == 0 ? "SpringView不会和水平滑动有冲突，侧滑删除试试" : "item" + i);
        }
    }

    /**
     * Adapter for RecyclerView
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {
        private List<String> results;

        RecyclerAdapter(List<String> results) {
            this.results = results;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.textView.setText(results.get(position));
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
        }

        @Override
        public void onSwiped(int position) {
            results.remove(position);
            notifyItemRemoved(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            MyViewHolder(View itemView) {
                super(itemView);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.height = 150;
                itemView.setLayoutParams(layoutParams);
                textView = itemView.findViewById(R.id.item_text);
            }
        }
    }

}
