package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;


public class SceneGroupManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SceneGroupListBean.DataBean> datas;

    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, String tag);
    }

    public SceneGroupManageAdapter(Context context, List<SceneGroupListBean.DataBean> datas) {
        this.context = context;
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void update(List<SceneGroupListBean.DataBean> datas) {
        if (datas != null) {
            this.datas = datas;
            notifyDataSetChanged();
        }

    }

    public void remove(int index) {
        datas.remove(index);
        notifyItemRemoved(index);
        // 再次刷新，避免错乱
        notifyItemRangeChanged(index, datas.size());
    }

    public boolean isEmpty() {
        return datas.isEmpty();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = null;
        itemView = layoutInflater.inflate(R.layout.item_scene_group_manage, parent, false);
        ManageGroupHolder holder = new ManageGroupHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SceneGroupListBean.DataBean info = datas.get(position);
        ((ManageGroupHolder) holder).tvName.setText(info.getName());
        if (onClickListener != null) {
            ((ManageGroupHolder) holder).ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(position, v.getTag().toString());
                }
            });

            ((ManageGroupHolder) holder).ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(position, v.getTag().toString());
                }
            });
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    private class ManageGroupHolder extends RecyclerView.ViewHolder {

        private ImageView ivEdit;
        private ImageView ivDelete;
        private TextView tvName;

        ManageGroupHolder(View itemView) {
            super(itemView);
            ivEdit = (ImageView) itemView.findViewById(R.id.iv_edit);
            tvName = (TextView) itemView.findViewById(R.id.tv_scene_group_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
