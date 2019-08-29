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
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcCustomTimeBean;

public class LcDayTimeAdapter extends RecyclerView.Adapter<LcDayTimeAdapter.MyViewHolder> {
    private List<LcCustomTimeBean> data;
    private Context context;
    private onDeleteClickListener listener;

    public interface onDeleteClickListener {
        void onclick(int position);
    }

    public LcDayTimeAdapter(Context context) {
        this.context = context;
    }

    public void setOnDeleteListener(onDeleteClickListener listener) {
        this.listener = listener;
    }


    public void update(List<LcCustomTimeBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    @Override
    public LcDayTimeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LcDayTimeAdapter.MyViewHolder holder = new LcDayTimeAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lc_day_time, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final LcDayTimeAdapter.MyViewHolder holder, final int position) {
        holder.tvTime.setText(data.get(position).time);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(position);
                listener.onclick(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime;
        private ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            tvTime = view.findViewById(R.id.tv_time);
            ivDelete = view.findViewById(R.id.iv_delete);
        }
    }
}