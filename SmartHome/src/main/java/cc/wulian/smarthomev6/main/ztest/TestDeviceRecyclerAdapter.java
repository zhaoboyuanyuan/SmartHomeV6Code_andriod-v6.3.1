package cc.wulian.smarthomev6.main.ztest;

import android.support.v7.widget.RecyclerView;
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

public class TestDeviceRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Device> mDeviceList;

    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(String deviceID);
    }

    public TestDeviceRecyclerAdapter() {}

    public void update(List<Device> datas) {
        this.mDeviceList = datas;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mDeviceList.isEmpty();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_test_devices, parent, false);
        DeviceHolder holder = new DeviceHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            ((DeviceHolder) holder).name.setText("不选");
            ((DeviceHolder) holder).imageView.setImageResource(R.drawable.icon_eyes_off);
            holder.itemView.setTag("666");
        } else {
            Device info = mDeviceList.get(position - 1);
            holder.itemView.setTag(info.devID);
            ((DeviceHolder) holder).name.setText(DeviceInfoDictionary.getNameByDevice(info));
            ((DeviceHolder) holder).imageView.setImageResource(DeviceInfoDictionary.getIconByDevice(info));
        }
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick((String) v.getTag());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDeviceList == null ? 1 : mDeviceList.size() + 1;
    }

    private class DeviceHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView name;
        private LinearLayout linearContent;

        DeviceHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.test_device_icon);
            name = (TextView) itemView.findViewById(R.id.test_device_name);
            linearContent = (LinearLayout) itemView.findViewById(R.id.test_device_content);
        }
    }
}
