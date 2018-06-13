package com.liaoinstan.springview.ctrl;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.container.WeixinHeader;

import java.util.ArrayList;
import java.util.List;

public class RecycleAdapterWeixinHeader extends RecyclerView.Adapter<RecycleAdapterWeixinHeader.Holder> {

    private WeixinHeader.OnWeixinHeaderLoadImgCallback imgLoadCallback;
    private int itemWidth;

    private List<Program> results = new ArrayList<>();

    public List<Program> getResults() {
        return results;
    }

    public RecycleAdapterWeixinHeader(WeixinHeader.OnWeixinHeaderLoadImgCallback imgLoadCallback) {
        this.imgLoadCallback = imgLoadCallback;
    }

    @Override
    public RecycleAdapterWeixinHeader.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weixin_header_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterWeixinHeader.Holder holder, final int position) {
        final Program bean = results.get(position);

        if (itemWidth != 0) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = itemWidth;
            holder.itemView.setLayoutParams(layoutParams);
        }

        if (position != getItemCount() - 1) {
            holder.text_name.setText(bean.getName());
            holder.img_header.setImageResource(R.drawable.shape_oval_dot);
            if (imgLoadCallback != null) {
                imgLoadCallback.loadImg(holder.img_header, bean.getImg(), position);
            }
        } else {
            //最后一个是“更多”
            holder.text_name.setText("");
            holder.img_header.setImageResource(R.drawable.shape_more);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView img_header;
        TextView text_name;

        public Holder(View itemView) {
            super(itemView);
            img_header = itemView.findViewById(R.id.img_header);
            text_name = itemView.findViewById(R.id.text_name);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }
}