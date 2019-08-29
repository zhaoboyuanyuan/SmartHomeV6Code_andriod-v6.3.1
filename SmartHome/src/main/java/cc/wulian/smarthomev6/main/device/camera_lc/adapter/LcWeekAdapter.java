package cc.wulian.smarthomev6.main.device.camera_lc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;

public class LcWeekAdapter extends RecyclerView.Adapter<LcWeekAdapter.MyViewHolder> {
    private List<String> data;
    private Context context;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public LcWeekAdapter(Context context) {
        this.context = context;
    }

    private LcWeekAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(LcWeekAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setSelectPosition(int position) {
        this.selectedPosition = position;
    }

    public void update(List<String> data) {
        if (data != null) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public LcWeekAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LcWeekAdapter.MyViewHolder holder = new LcWeekAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lc_week, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final LcWeekAdapter.MyViewHolder holder, int position) {
        holder.tv.setText(data.get(position));
        if (selectedPosition == position) {
            holder.tv.setBackgroundResource(R.drawable.shape_lc_week_selected);
        } else {
            holder.tv.setBackgroundResource(R.drawable.shape_lc_week_unselect);
        }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                    selectedPosition = pos; //选择的position赋值给参数，
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