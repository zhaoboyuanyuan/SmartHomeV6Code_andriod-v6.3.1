package cc.wulian.smarthomev6.main.device.camera_lc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;

public class NvrDeviceListAdapter extends RecyclerView.Adapter<NvrDeviceListAdapter.MyViewHolder> {
    private List<LcDeviceInfoBean.ExtDataBean.ChannelsBean> data;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public NvrDeviceListAdapter(Context context) {
        this.context = context;
    }

    private NvrDeviceListAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(NvrDeviceListAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public void update(List<LcDeviceInfoBean.ExtDataBean.ChannelsBean> data) {
        if (data != null) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public NvrDeviceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nvr_device_list, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvName.setText(data.get(position).getChannelName());
        if (data.get(position).getChannelStatus() == 1) {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.tvOffline.setVisibility(View.GONE);
        } else {
            holder.ivPlay.setVisibility(View.GONE);
            holder.tvOffline.setVisibility(View.VISIBLE);
        }
        if (mOnItemClickListener != null) {
            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (data.get(position).getChannelStatus() == 1) {
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvOffline;
        private ImageView ivPlay;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_channel_name);
            tvOffline = (TextView) view.findViewById(R.id.tv_offline);
            ivPlay = (ImageView) view.findViewById(R.id.iv_play);
        }
    }
}
