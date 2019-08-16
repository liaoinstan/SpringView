package com.liaoinstan.springview.weixinheader;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 微信Haader小程序列表适配器
 */
public class RecycleAdapterWeixinHeader extends RecyclerView.Adapter<RecycleAdapterWeixinHeader.Holder> {

    private WeixinHeader weixinHeader;
    private int itemWidth;

    public RecycleAdapterWeixinHeader(WeixinHeader weixinHeader) {
        this.weixinHeader = weixinHeader;
    }

    @Override
    public RecycleAdapterWeixinHeader.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weixin_header_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterWeixinHeader.Holder holder, int position) {
        final Program bean = weixinHeader.getResults().get(position);

        if (itemWidth != 0) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.width = itemWidth;
            holder.itemView.setLayoutParams(layoutParams);
        }

        if (position != getItemCount() - 1) {
            holder.text_name.setText(bean.getName());
            holder.img_header.setImageResource(R.drawable.shape_oval_dot);
            if (weixinHeader.getImgLoadCallback() != null) {
                weixinHeader.getImgLoadCallback().loadImg(holder.img_header, bean.getImg(), position);
            }
        } else {
            //最后一个是“更多”
            holder.text_name.setText("");
            holder.img_header.setImageResource(R.drawable.shape_more);
        }

        holder.img_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != getItemCount() - 1) {
                    if (weixinHeader.getOnProgramClickListener() != null) {
                        weixinHeader.getOnProgramClickListener().onClick(bean, holder, holder.getAdapterPosition());
                    }
                } else {
                    if (weixinHeader.getOnMoreClickListener() != null) {
                        weixinHeader.getOnMoreClickListener().onMoreClick();
                    }
                }
            }
        });
        holder.img_header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.getAdapterPosition() != getItemCount() - 1) {
                    if (weixinHeader.getOnProgramLongClickListener() != null) {
                        weixinHeader.getOnProgramLongClickListener().onLongClick(bean, holder, holder.getAdapterPosition());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return weixinHeader.getResults().size();
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

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    //////////////////////////////////////////////////


}