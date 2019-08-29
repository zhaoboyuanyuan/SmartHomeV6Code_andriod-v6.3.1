package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;

/**
 * created by huxc  on 2017/11/28.
 * func：场景区域adapter
 * email: hxc242313@qq.com
 */

public class SceneAreaAdapter extends RecyclerView.Adapter<SceneAreaAdapter.MyViewHolder> {
    private List<SceneGroupListBean.DataBean> data;
    private Context context;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public SceneAreaAdapter(Context context) {
        this.context = context;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setSelectPosition(int position){
        this.selectedPosition = position;
    }

    public void update(List<SceneGroupListBean.DataBean> data) {
        if (data != null) {
            this.data = data;
            if (data.size() > 0) {
                SceneGroupListBean.DataBean dataBean = new SceneGroupListBean.DataBean();
                dataBean.setName(context.getString(R.string.Home_Scene_All));
                data.add(0, dataBean);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public SceneAreaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SceneAreaAdapter.MyViewHolder holder = new SceneAreaAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene_area, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final SceneAreaAdapter.MyViewHolder holder, int position) {
        holder.tv.setText(data.get(position).getName());
//        holder.itemView.setSelected(selectedPosition == position);
        if (selectedPosition == position) {
            holder.tv.setTextColor(context.getResources().getColor(R.color.v6_green));
        } else {
            holder.tv.setTextColor(context.getResources().getColor(R.color.black));
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                    selectedPosition = pos; //选择的position赋值给参数，
//                    notifyItemChanged(selectedPosition);//刷新当前点击item
                    notifyDataSetChanged();

                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_area_name);
        }
    }
}
