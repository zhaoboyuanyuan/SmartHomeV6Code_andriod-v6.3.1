package cc.wulian.smarthomev6.main.device.camera_lc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcCustomTimeBean;
import cc.wulian.smarthomev6.support.utils.DateUtil;

public class LcCustomTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<LcCustomTimeBean> data;

    public LcCustomTimeAdapter(Context context, List<LcCustomTimeBean> data) {
        this.context = context;
        this.data = new ArrayList<>();
    }


    public void setData(List<LcCustomTimeBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * @param map
     */
    public void setList(HashMap<String, List<LcCustomTimeBean>> map) {
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            //遍历数组
            if (map.get(key).size() > 0) {
                //这里是将group当成一个child一起加到mList中
                //也就是mList中包含了group和child，只不过group
                //所在的positon有个标识当前position是否为分组
                data.add(new LcCustomTimeBean(key, true));
            }
            data.addAll(map.get(key));
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            View itemView = layoutInflater.inflate(R.layout.item_lc_protect_time_group, parent, false);
            return new GroupHolder(itemView);
        }
        View itemView = layoutInflater.inflate(R.layout.item_lc_protect_time_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        LcCustomTimeBean bean = data.get(viewHolder.getAdapterPosition());
        if (viewHolder instanceof ItemHolder) {
            ((ItemHolder) viewHolder).tvProtectTime.setText(bean.time);
        }

        if (viewHolder instanceof GroupHolder) {
            ((GroupHolder) viewHolder).tvGroup.setText(DateUtil.convertEnWeekToCn(context, bean.time));
        }
    }

    public LcCustomTimeBean get(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isGroup ? 1 : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvProtectTime;

        public ItemHolder(View itemView) {
            super(itemView);
            tvProtectTime = (TextView) itemView.findViewById(R.id.tv_protect_time);
        }
    }

    private class GroupHolder extends RecyclerView.ViewHolder {

        private TextView tvGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            tvGroup = (TextView) itemView.findViewById(R.id.tv_group_name);
        }
    }
}
