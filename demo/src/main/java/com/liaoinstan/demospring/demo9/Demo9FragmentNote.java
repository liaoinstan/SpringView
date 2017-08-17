package com.liaoinstan.demospring.demo9;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.RotationFooter;
import com.liaoinstan.springview.container.RotationHeader;
import com.liaoinstan.springview.widget.SpringView;


/**
 * Created by liaoinstan
 */
public class Demo9FragmentNote extends Fragment {

    private int position;
    private View rootView;

    private SpringView springView;

    public static Fragment newInstance(int position) {
        Demo9FragmentNote fragment = new Demo9FragmentNote();
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
        rootView = inflater.inflate(R.layout.fragment_demo9_note, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        //初始化SpringView
        springView = (SpringView) rootView.findViewById(R.id.springview);
//        springView.setHeader(new AliHeader(getContext(), false));
//        springView.setFooter(new AliFooter(getContext(), false));
        springView.setType(SpringView.Type.OVERLAP);    //设为重叠样式
        springView.setHeader(new RotationHeader(getContext()));
        springView.setFooter(new RotationFooter(getContext()));
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
    }
}
