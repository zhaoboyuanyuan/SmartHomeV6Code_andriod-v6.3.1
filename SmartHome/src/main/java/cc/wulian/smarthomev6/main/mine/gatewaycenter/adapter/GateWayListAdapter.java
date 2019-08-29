package cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuLayout;

/**
 * 作者: mamengchao
 * 时间: 2017/3/28 0028
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class GateWayListAdapter extends SwipeMenuAdapter<DeviceBean> {
    private LayoutInflater mInflater;

    public GateWayListAdapter(Context context, List<DeviceBean> data) {
        super(context, data);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            View view = mInflater.inflate(R.layout.item_device_list, null);
            holder.ivDeviceIcon = (ImageView) view.findViewById(R.id.iv_device_icon);
            holder.tvDeviceName = (TextView) view.findViewById(R.id.tv_device_name);
            holder.tvDeviceType = (TextView) view.findViewById(R.id.tv_device_type);
            holder.tvDeviceArea = (TextView) view.findViewById(R.id.tv_device_area);
            holder.tvDeviceinfo = (ImageView) view.findViewById(R.id.iv_device_info);
            holder.tv_device_desc = (TextView) view.findViewById(R.id.tv_device_desc);
            convertView = createMenuView(position, parent, view);
            convertView.setTag(holder);
        } else {
            ((SwipeMenuLayout) convertView).closeMenu();
            ((SwipeMenuLayout) convertView).setPosition(position);
            holder = (ViewHolder) convertView.getTag();
        }

        DeviceBean deviceBean = mData.get(position);
        holder.ivDeviceIcon.setVisibility(View.GONE);
        holder.tvDeviceName.setText(deviceBean.getName());
        if (deviceBean.getDeviceId().startsWith("CG")) {
            holder.tvDeviceType.setText(mContext.getString(R.string.BindGateway_GatewayID) + ":" + deviceBean.getDeviceId().substring(0, 11));
        } else {
            holder.tvDeviceType.setText(mContext.getString(R.string.BindGateway_GatewayID) + ":" + deviceBean.getDeviceId());
        }
        holder.tvDeviceArea.setVisibility(View.GONE);
        if ("1".equals(deviceBean.getState())) {
            holder.tvDeviceName.setTextColor(Color.BLACK);
        } else {
            holder.tvDeviceName.setTextColor(Color.GRAY);
        }
        if (deviceBean.getIsSelect()) {
            holder.tvDeviceinfo.setVisibility(View.VISIBLE);
            holder.tvDeviceinfo.setImageResource(R.drawable.theme_select);
        } else {
            holder.tvDeviceinfo.setVisibility(View.GONE);
        }

        //分享设备的分享者信息
        if (deviceBean.isShared()) {
            String shareSource = deviceBean.granterUserPhone;
            if (TextUtils.isEmpty(shareSource)) {
                shareSource = deviceBean.granterUserEmail;
            }
            if (TextUtils.isEmpty(shareSource)) {
                holder.tv_device_desc.setVisibility(View.GONE);
            } else {
                holder.tv_device_desc.setVisibility(View.VISIBLE);
                holder.tv_device_desc.setText(String.format(mContext.getString(R.string.Share_Source), shareSource));
            }
        } else {
            holder.tv_device_desc.setVisibility(View.GONE);
        }

        return convertView;
    }

    public final class ViewHolder {
        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceType;
        public TextView tvDeviceArea;
        public ImageView tvDeviceinfo;
        public TextView tv_device_desc;
    }
}
