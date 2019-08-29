package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceDetailMoreAreaActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by syf on 2017/2/15
 */

public class DeviceDetailMoreAreaAdapter extends WLBaseAdapter<RoomInfo> {
    private Device device;

    public DeviceDetailMoreAreaAdapter(Context context, List<RoomInfo> data, Device device) {
        super(context, data);
        this.device = device;
    }

    public void swapDevice(Device device) {
        this.device = device;
        swapData(mData);
    }

    @Override
    public int getCount() {
        if (mData == null || mData.isEmpty()){
            return 1;
        }
        else{
            return mData.size() + 1;
        }
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        View v;
        if (convertView == null){
            v = newView(mContext, mInflater, parent, position);
        }
        else{
            v = convertView;
        }
        if (position == 0){
            bindViewAtFrist(mContext, v);
        }else {
            bindView(mContext, v, position, getItem(position - 1));
        }

        return v;
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater, ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.item_device_all_list,null,false);
    }

    @Override
    protected void bindView(Context context, View view, int pos, RoomInfo item) {
        final String roomId = item.getRoomID();
        TextView areaName = (TextView) view.findViewById(R.id.tv_area_name);
        ImageView areaSelect = (ImageView) view.findViewById(R.id.iv_area_select);

        areaName.setText(item.getName());

        if (StringUtil.equals(device.roomID, item.getRoomID())){
            areaSelect.setVisibility(View.VISIBLE);
        }else {
            areaSelect.setVisibility(View.INVISIBLE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialogManager.getDialogManager().showDialog(DeviceDetailMoreAreaActivity.SET_AREA, mContext, null, null, 10000);
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(),
                                device.devID,
                                2, null, roomId),
                        MQTTManager.MODE_GATEWAY_FIRST);
            }
        });
    }

    protected void bindViewAtFrist(Context context, View view) {
        TextView areaName = (TextView) view.findViewById(R.id.tv_area_name);
        ImageView areaSelect = (ImageView) view.findViewById(R.id.iv_area_select);

            areaName.setText(context.getResources().getString(R.string.Device_NoneArea));

        if (StringUtil.isNullOrEmpty(device.roomID)){
            areaSelect.setVisibility(View.VISIBLE);
        }else {
            areaSelect.setVisibility(View.INVISIBLE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialogManager.getDialogManager().showDialog(DeviceDetailMoreAreaActivity.SET_AREA, mContext, null, null, 10000);
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(),
                                device.devID,
                                2, null, ""),
                        MQTTManager.MODE_GATEWAY_FIRST);
            }
        });
    }

}
