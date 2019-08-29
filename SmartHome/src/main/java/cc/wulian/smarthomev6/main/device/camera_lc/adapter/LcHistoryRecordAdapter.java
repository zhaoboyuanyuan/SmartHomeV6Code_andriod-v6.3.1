package cc.wulian.smarthomev6.main.device.camera_lc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcLocalRecordBean;

public class LcHistoryRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "LcHistoryRecordAdapter";
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_CHILD = 1;
    private List<LcLocalRecordBean.RecordsBean> data;
    private List<String> timeKey;
    private Context context;
    private List<ItemBean> items;

    public interface OnItemClickListener {
        void onItemClick(View view, String recordId, String time1, String time2);
    }

    public LcHistoryRecordAdapter(Context context) {
        this.context = context;
    }

    private LcHistoryRecordAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public void update(List<LcLocalRecordBean.RecordsBean> data) {
        if (data != null) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    private class Group extends ItemBean {
        public String title;

        @Override
        public int getType() {
            return TYPE_GROUP;
        }
    }

    private class Child extends ItemBean {
        public LcLocalRecordBean.RecordsBean recordsBean;
        public String groupName;

        @Override
        public int getType() {
            return TYPE_CHILD;
        }
    }

    private abstract class ItemBean {
        public int position;

        public abstract int getType();
    }


    public void addData(List<LcLocalRecordBean.RecordsBean> data) {
        items = new ArrayList<>();
        timeKey = new ArrayList<>();
        String time = null;
        if (data != null) {
            this.data = data;
            for (int i = 0; i < data.size(); i++) {
                LcLocalRecordBean.RecordsBean record = data.get(i);
                time = record.getBeginTime().substring(record.getBeginTime().length() - 8, record.getBeginTime().length() - 6);
                Log.i(TAG, "hour:" + time);
                if (!timeKey.contains(time)) {
                    Group group = new Group();
                    group.title = time;
                    Child child = new Child();
                    child.recordsBean = record;
                    child.groupName = group.title;
                    items.add(i + timeKey.size(), group);
                    items.add(i + timeKey.size() + 1, child);
                    timeKey.add(time);
                } else {
                    Child child = new Child();
                    child.groupName = ((Child) items.get(i + timeKey.size() - 1)).groupName;
                    child.recordsBean = record;
                    items.add(i + timeKey.size(), child);
                }
            }
            notifyDataSetChanged();
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ItemViewHolder itemVH = null;
        switch (viewType) {
            case TYPE_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lc_history_group, parent, false);
                itemVH = new GroupHolder(view);

                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                itemVH.itemView.setLayoutParams(layoutParams);
                break;
            case TYPE_CHILD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lc_history_record, parent, false);
                itemVH = new ChildHolder(view);
                break;

        }
        return itemVH;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ItemBean item = items.get(position);
        switch (getItemViewType(position)) {
            case TYPE_GROUP:
                Group g = (Group) item;
                GroupHolder groupVH = (GroupHolder) holder;
                groupVH.tvGroup.setText(g.title + ":00");
                break;

            case TYPE_CHILD:
                final Child c = (Child) item;
                ChildHolder childVH = (ChildHolder) holder;
                childVH.tvTime.setText(c.recordsBean.getBeginTime().substring(c.recordsBean.getBeginTime().length() - 8));
                childVH.ivBg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, c.recordsBean.getRecordId(), c.recordsBean.getBeginTime(), c.recordsBean.getEndTime());
                    }
                });
                break;
        }

    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    class ChildHolder extends ItemViewHolder {

        private TextView tvTime;
        private ImageView ivBg;

        public ChildHolder(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            ivBg = (ImageView) view.findViewById(R.id.iv_bg);
        }

        @Override
        public int getType() {
            return TYPE_CHILD;
        }
    }

    class GroupHolder extends ItemViewHolder {
        private TextView tvGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            tvGroup = (TextView) itemView.findViewById(R.id.tv_group);
        }

        @Override
        public int getType() {
            return TYPE_GROUP;
        }
    }

    private abstract class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public abstract int getType();
    }
}
