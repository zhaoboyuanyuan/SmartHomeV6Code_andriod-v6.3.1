package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.device.lookever.setting.BindLockCallback;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by hxc on 2017/6/12.
 * 摄像机绑定门锁adapter
 */

public class DoorLockAdapter extends WLBaseAdapter<Device> {
    private LayoutInflater mInflater;
    private List<Device> mData;
    private String did;

    public DoorLockAdapter(Context context, List<Device> data, String did) {
        super(context, data);
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.did = did;
    }

    @Override
    public void swapData(List<Device> newData) {
        if (newData == null) {
            if (mData != null) {
                mData.clear();
                notifyDataSetInvalidated();
            }
        } else {
            mData = newData;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DoorLockAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new DoorLockAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_bind_doorlock, null);
            holder.ivDeviceIcon = (ImageView) convertView.findViewById(R.id.iv_device_icon);
            holder.tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            holder.tvDeviceArea = (TextView) convertView.findViewById(R.id.tv_device_area);
            holder.tvBind = (TextView) convertView.findViewById(R.id.tv_bind);
            holder.tvBind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (int) v.getTag();
                    bindLockCallback.bind(mData.get(p));
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (DoorLockAdapter.ViewHolder) convertView.getTag();
        }
        holder.tvBind.setTag(position);
        final Device device = mData.get(position);
        holder.ivDeviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(device.type));
        holder.tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));

        if (!device.isZigbee()) {
            holder.tvDeviceArea.setVisibility(View.INVISIBLE);
        } else {
            String areaName = ((MainApplication) mContext.getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            holder.tvDeviceArea.setVisibility(View.VISIBLE);
            holder.tvDeviceArea.setText("[" + areaName + "]");
        }
        return convertView;
    }

    public final class ViewHolder {
        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceArea;
        public TextView tvBind;
    }
    private BindLockCallback bindLockCallback;

    public void setbindLockCallback(BindLockCallback bindLockCallback) {
        this.bindLockCallback = bindLockCallback;

    }
}
