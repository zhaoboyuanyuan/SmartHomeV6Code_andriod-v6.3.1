package cc.wulian.smarthomev6.main.ztest;

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
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by 王伟 on 2017/3/15
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class TestProjectRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mList;
    private String mChoiceName;

    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(String s);
    }

    public TestProjectRecyclerAdapter() {}

    public void update(List<String> datas) {
        this.mList = datas;
        notifyDataSetChanged();
    }

    public void setChoiceName(String name) {
        mChoiceName = name;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_test_progect, parent, false);
        ProjectHolder holder = new ProjectHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        String name = mList.get(position);
        holder.itemView.setTag(name);
        ((ProjectHolder) holder).name.setText(name);
        ((ProjectHolder) holder).name.setBackgroundColor(TextUtils.equals(mChoiceName, name) ? 0xff8dd652 : 0xffffffff);
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChoiceName = (String) v.getTag();
                    onClickListener.onItemClick((String) v.getTag());
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private class ProjectHolder extends RecyclerView.ViewHolder {

        private TextView name;

        ProjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.test_project_name);
        }
    }
}
