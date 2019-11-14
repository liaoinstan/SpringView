package com.liaoinstan.springview.weixinheaderv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liaoinstan.springview.weixinheader.Program;
import com.liaoinstan.springview.weixinheader.R;

import java.util.List;

/**
 * 微信Haader小程序列表适配器
 */
public class RecycleAdapterWeixinHeaderV2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int TYPE_HEADER = 1;
    public final static int TYPE_ITEM = 0;
    public final static int TYPE_MORE = 2;

    private WeixinHeaderV2 weixinHeaderV2;

    public RecycleAdapterWeixinHeaderV2(WeixinHeaderV2 weixinHeaderV2) {
        this.weixinHeaderV2 = weixinHeaderV2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HolderHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.weixin_header_item_title_v2, parent, false));
            case TYPE_ITEM:
                return new HolderItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.weixin_header_item_v2, parent, false));
            case TYPE_MORE:
                return new HolderMore(LayoutInflater.from(parent.getContext()).inflate(R.layout.weixin_header_item_more_v2, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Program bean = weixinHeaderV2.getResults().get(position);
        if (holder instanceof HolderHeader) {
            bindHeaderHolder((HolderHeader) holder, position, bean);
        } else if (holder instanceof HolderItem) {
            bindItemHolder((HolderItem) holder, position, bean);
        } else if (holder instanceof HolderMore) {
            bindMoreHolder((HolderMore) holder, position, bean);
        }
    }

    private void bindItemHolder(final HolderItem holder, int position, final Program bean) {
        holder.itemView.setVisibility(View.VISIBLE);
        holder.text_name.setText(bean.getName());
        if (weixinHeaderV2.getImgLoadCallback() != null) {
            weixinHeaderV2.getImgLoadCallback().loadImg(holder.img_header, bean.getImg(), position);
        }

        holder.img_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weixinHeaderV2.getOnProgramClickListener() != null) {
                    weixinHeaderV2.getOnProgramClickListener().onClick(bean, holder, holder.getAdapterPosition());
                }
            }
        });
        holder.img_header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerLongClickListener != null) {
                    onRecyclerLongClickListener.onLongClick(holder);
                    return true;
                }
                return false;
            }
        });
    }

    private void bindHeaderHolder(final HolderHeader holder, int position, Program bean) {
        holder.text_title.setText(bean.getTitleName());
        holder.text_drag_area.setVisibility(Utils.isLastItem(weixinHeaderV2.getResults(), position) ? View.VISIBLE : View.GONE);
    }

    private void bindMoreHolder(final HolderMore holder, int position, Program bean) {
        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weixinHeaderV2.getOnMoreClickListener() != null) {
                    weixinHeaderV2.getOnMoreClickListener().onMoreClick();
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Program program = weixinHeaderV2.getResults().get(position);
        return program.getType();
    }

    @Override
    public int getItemCount() {
        return weixinHeaderV2.getResults().size();
    }

    public List<Program> getResults() {
        return weixinHeaderV2.getResults();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_HEADER) {
                        return 4;
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    class HolderItem extends RecyclerView.ViewHolder {
        ImageView img_header;
        TextView text_name;

        HolderItem(View itemView) {
            super(itemView);
            img_header = itemView.findViewById(R.id.img_header);
            text_name = itemView.findViewById(R.id.text_name);
        }
    }

    class HolderHeader extends RecyclerView.ViewHolder {
        TextView text_title;
        TextView text_drag_area;

        HolderHeader(View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            text_drag_area = itemView.findViewById(R.id.text_drag_area);
        }
    }

    class HolderMore extends RecyclerView.ViewHolder {
        ImageView img_more;

        HolderMore(View itemView) {
            super(itemView);
            img_more = itemView.findViewById(R.id.img_more);
        }
    }

    private OnRecyclerLongClickListener onRecyclerLongClickListener;

    public void setOnRecyclerLongClickListener(OnRecyclerLongClickListener onRecyclerLongClickListener) {
        this.onRecyclerLongClickListener = onRecyclerLongClickListener;
    }

    //////////////////////////////////////////////////
    interface OnRecyclerLongClickListener {
        void onLongClick(RecyclerView.ViewHolder viewHolder);
    }

}