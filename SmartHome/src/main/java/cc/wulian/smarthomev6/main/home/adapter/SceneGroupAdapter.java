package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class SceneGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SceneGroupListBean.DataBean> datas;

    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public SceneGroupAdapter(Context context, List<SceneGroupListBean.DataBean> datas) {
        this.context = context;
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void update(List<SceneGroupListBean.DataBean> datas) {
        if (datas != null && datas.size() > 0) {
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
        itemView = layoutInflater.inflate(R.layout.item_scene_group, parent, false);
        GroupHolder holder = new GroupHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SceneGroupListBean.DataBean info = datas.get(position);
        ((GroupHolder) holder).name.setText(info.getName());
        holder.itemView.setTag(position);
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class GroupHolder extends RecyclerView.ViewHolder {

        private TextView name;

        GroupHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_scene_group_name);
        }
    }
}
