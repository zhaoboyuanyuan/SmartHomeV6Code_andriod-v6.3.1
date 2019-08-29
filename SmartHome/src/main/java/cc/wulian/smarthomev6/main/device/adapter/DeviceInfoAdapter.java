package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreRouter;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.LeaveHomeButtonView;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.UserManagerButtonView;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.WifiSettingButtonView;
import cc.wulian.smarthomev6.main.device.lock_bd.BdDoorPanelView;
import cc.wulian.smarthomev6.main.device.lock_op.OpAccountManageView;
import cc.wulian.smarthomev6.main.device.more.AjBindModeView;
import cc.wulian.smarthomev6.main.device.more.AjClearQuantityView;
import cc.wulian.smarthomev6.main.device.more.AjRecoverStateView;
import cc.wulian.smarthomev6.main.device.more.AtBindModeView;
import cc.wulian.smarthomev6.main.device.more.AtRecoverStateView;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * Created by Veev on 2017/6/6
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MoreConfigAdapter
 */

public class DeviceInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Map<String ,String>> info;

    public DeviceInfoAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Map<String ,String>> info){
        this.info = info;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_device_info_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemHolder) {
            Map<String ,String> item = info.get(position);
            for (String key : item.keySet()){
                switch (key){
                    case "name":
                        ((ItemHolder) viewHolder).itemName.setText(R.string.Product_Name);
                        ((ItemHolder) viewHolder).itemValue.setText(item.get(key));
                        break;
                    case "number":
                        ((ItemHolder) viewHolder).itemName.setText(R.string.Device_Number);
                        ((ItemHolder) viewHolder).itemValue.setText(item.get(key));
                        break;
                    case "version":
                        ((ItemHolder) viewHolder).itemName.setText(R.string.Firmware_Version);
                        ((ItemHolder) viewHolder).itemValue.setText(item.get(key));
                        break;
                    case "localIP":
                        ((ItemHolder) viewHolder).itemName.setText("IP");
                        ((ItemHolder) viewHolder).itemValue.setText(item.get(key));
                        break;
                    default:
                        ((ItemHolder) viewHolder).itemName.setText(key);
                        ((ItemHolder) viewHolder).itemValue.setText(item.get(key));
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView itemName;

        private TextView itemValue;

        public ItemHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemValue = (TextView) itemView.findViewById(R.id.item_value);
        }
    }
}
