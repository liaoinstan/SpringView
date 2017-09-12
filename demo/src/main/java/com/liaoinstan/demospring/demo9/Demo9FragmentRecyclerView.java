package com.liaoinstan.demospring.demo9;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liaoinstan
 */
public class Demo9FragmentRecyclerView extends Fragment {

    private int position;
    private View rootView;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SpringView springView;
    private List<String> mDatas = new ArrayList<String>();

    public static Fragment newInstance(int position) {
        Demo9FragmentRecyclerView fragment = new Demo9FragmentRecyclerView();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_demo9_recyclerview, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        //初始化recyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(mDatas);
        recyclerView.setAdapter(recyclerViewAdapter);
        //初始化SpringView
        springView = (SpringView) rootView.findViewById(R.id.springview);
        springView.setHeader(new AliHeader(getContext(), false));
        springView.setFooter(new AliFooter(getContext(), false));
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
                        mDatas.add("add item");
                        recyclerViewAdapter.notifyItemInserted(mDatas.size() - 1);
                        springView.onFinishFreshAndLoad();
                    }
                }, 1000);
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 9; i++) {
            mDatas.add(i == 0 ? "We are in RecyclerView" : (i == 1 ? "单个拖拽事件能够在AppBarLayout、SpringView\n和RecyclerView中自由地传递和切换\n\n按住RecyclerView不断上下拖拽试试" : ""));
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SampleViewHolder> {
        private List<String> results;

        public RecyclerViewAdapter(List<String> results) {
            this.results = results;
        }

        @Override
        public RecyclerViewAdapter.SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new RecyclerViewAdapter.SampleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewAdapter.SampleViewHolder holder, final int position) {
            holder.text_item.setText(results.get(position));
            if (position % 2 == 1) {
                holder.text_item.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.text_item.setTextColor(Color.parseColor("#cccccc"));
            } else {
                holder.text_item.setBackgroundColor(Color.parseColor("#bce1d8"));
                holder.text_item.setTextColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        public class SampleViewHolder extends RecyclerView.ViewHolder {
            public TextView text_item;

            public SampleViewHolder(View view) {
                super(view);
                text_item = (TextView) view.findViewById(R.id.item_text);
            }
        }
    }
}
