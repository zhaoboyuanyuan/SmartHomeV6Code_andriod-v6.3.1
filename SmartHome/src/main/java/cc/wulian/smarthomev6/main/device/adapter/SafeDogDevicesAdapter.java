package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogProtectDeviceBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by 上海滩小马哥 on 2018/01/24.
 *
 */

public class SafeDogDevicesAdapter extends BaseAdapter {

    Context context;
    List<SafeDogProtectDeviceBean> data;

    public SafeDogDevicesAdapter(Context context, List<SafeDogProtectDeviceBean> data) {
        this.context = context;
        this.data = data;
    }

    public void swapData(List<SafeDogProtectDeviceBean> newData) {
        if (newData == null) {
            if (data != null) {
                data.clear();
                notifyDataSetInvalidated();
            }
        } else {
            data = newData;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_safedog_devices, null);
            viewHolder.deviceIcon = (ImageView) convertView
                    .findViewById(R.id.iv_device_icon);
            viewHolder.deviceName = (TextView) convertView
                    .findViewById(R.id.tv_device_name);
            viewHolder.deviceType = (TextView) convertView
                    .findViewById(R.id.tv_device_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.deviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(data.get(position).deviceType));
        viewHolder.deviceName.setText(DeviceInfoDictionary.getDefaultNameByType(data.get(position).deviceType));
        if (TextUtils.equals(data.get(position).deviceType, "CMICA1") ||
                TextUtils.equals(data.get(position).deviceType, "CMICA2") ||
                TextUtils.equals(data.get(position).deviceType, "CMICY1") ||
                TextUtils.equals(data.get(position).deviceType, "CMICA3") ||
                TextUtils.equals(data.get(position).deviceType, "CMICA6") ||
                TextUtils.equals(data.get(position).deviceType, "CMICA4") ||
                TextUtils.equals(data.get(position).deviceType, "CMICA5") ||
                TextUtils.equals(data.get(position).deviceType, "HS01") ||
                TextUtils.equals(data.get(position).deviceType, "HS02") ||
                TextUtils.equals(data.get(position).deviceType, "HS03") ||
                TextUtils.equals(data.get(position).deviceType, "HS04") ||
                TextUtils.equals(data.get(position).deviceType, "HS05") ||
                TextUtils.equals(data.get(position).deviceType, "HS06")) {
            viewHolder.deviceType.setText("WiFi");
        } else {
            viewHolder.deviceType.setText("Zigbee");
        }
        return convertView;
    }

    class ViewHolder {
        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceType;
    }
}
