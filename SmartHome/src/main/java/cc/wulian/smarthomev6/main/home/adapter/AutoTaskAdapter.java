package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AutoTaskBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;

public class AutoTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<AutoTaskBean.RuleArrayBean> datas;

    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, String tag);
    }


    public AutoTaskAdapter(Context context, List<AutoTaskBean.RuleArrayBean> datas) {
        this.context = context;
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void update(List<AutoTaskBean.RuleArrayBean> datas) {
        if (datas != null) {
            this.datas = datas;
            notifyDataSetChanged();
        }

    }


    public void remove(int index) {
        datas.remove(index);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return datas.isEmpty();
    }

    public void setOnClickListener(AutoTaskAdapter.OnItemClickListener listener) {
        this.onClickListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_auto_task_swipe_menu, parent, false);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        itemView.getLayoutParams().height = height / 10;
        return new ItemHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder) {
            AutoTaskBean.RuleArrayBean bean = datas.get(position);
            if (TextUtils.equals("1", bean.getTriggerArray().get(0).getType())) {
                ((ItemHolder) holder).ivIcon.setImageResource(R.drawable.icon_auto_task_timer);
                ((ItemHolder) holder).tvInfo.setVisibility(View.VISIBLE);
                String[] array = bean.getTriggerArray().get(0).getExp().split(" ");
                ((ItemHolder) holder).tvInfo.setText(array[1] + ":" + array[0] + StringUtil.autoTaskDataParser(array[4]));
            } else if (TextUtils.equals("2", bean.getTriggerArray().get(0).getType())) {
                ((ItemHolder) holder).ivIcon.setImageResource(R.drawable.icon_auto_task_scene);
                ((ItemHolder) holder).tvInfo.setVisibility(View.INVISIBLE);
            }
            ((ItemHolder) holder).tvName.setText(bean.getProgramName());
            ((ItemHolder) holder).tbAutoTask.setChecked(TextUtils.equals("1", bean.getStatus()));


            if (onClickListener != null) {
                ((ItemHolder) holder).tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onItemClick(position, v.getTag().toString());
                    }
                });
                ((ItemHolder) holder).tbAutoTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onItemClick(position, v.getTag().toString());
                    }
                });
                ((ItemHolder) holder).llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onItemClick(position, v.getTag().toString());
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }


    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvInfo;
        private TextView tvDelete;
        private ImageView ivIcon;
        private ToggleButton tbAutoTask;
        private LinearLayout llItem;
//        private TextView tvEdit;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_auto_task_name);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_auto_task_info);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
//            tvEdit = (TextView) itemView.findViewById(R.id.tv_edit);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_auto_task);
            tbAutoTask = (ToggleButton) itemView.findViewById(R.id.tb_auto_task);
            llItem = itemView.findViewById(R.id.item_auto_task);
        }
    }
}