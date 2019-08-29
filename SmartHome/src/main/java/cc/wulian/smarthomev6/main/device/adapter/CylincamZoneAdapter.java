package cc.wulian.smarthomev6.main.device.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.lookever.bean.ZoneBean;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;

public class CylincamZoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private static final String TAG = "CylincamZoneAdapter";
    private int selectedPosition = -5; //默认一个参数


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public CylincamZoneAdapter() {
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_cylincam_zone, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ItemHolder) {
            ((ItemHolder) viewHolder).tvAreaName.setText(data.get(position));
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                        selectedPosition = position; //选择的position赋值给参数，
                        notifyItemChanged(selectedPosition);//刷新当前点击item
                    }
                });
            }
            viewHolder.itemView.setSelected(selectedPosition == position);
            if (selectedPosition == position) {
                ((ItemHolder) viewHolder).ivChecked.setVisibility(View.VISIBLE);
            } else {
                ((ItemHolder) viewHolder).ivChecked.setVisibility(View.INVISIBLE);
            }
        }

    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvAreaName;
        private ImageView ivChecked;

        public ItemHolder(View itemView) {
            super(itemView);
            tvAreaName = (TextView) itemView.findViewById(R.id.tv_area_name);
            ivChecked = (ImageView) itemView.findViewById(R.id.iv_checked);
        }
    }

    public void setSelectItem(int selectItem) {
        this.selectedPosition = selectItem;
    }

}
