package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditAreaPopupWindow;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * 作者: mamengchao
 * 时间: 2017/3/22 0022
 * 描述:区域adapter
 * 联系方式: 805901025@qq.com
 */

public class AreaListAdapter extends WLBaseAdapter<RoomInfo> {
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private EditAreaPopupWindow popupWindow;

    private int popupIndex = -1;

    public AreaListAdapter(Context context, List<RoomInfo> data) {
        super(context, data);
        popupWindow = new EditAreaPopupWindow(context);

        popupWindow.setPopupClickListener(new EditAreaPopupWindow.OnPopupClickListener() {
            @Override
            public void onDelete() {
                showDelete(popupIndex);
            }

            @Override
            public void onEdit() {
                showEdit(popupIndex);
            }
        });
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater, ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.item_area_list, null, false);
    }

    @Override
    protected void bindView(Context context, View view, final int pos, RoomInfo item) {
        TextView tvDeviceDevice = (TextView) view.findViewById(R.id.item_area_text_name);
        TextView textDeviceCount = (TextView) view.findViewById(R.id.item_area_text_devices);
        ImageView areaEdit = (ImageView) view.findViewById(R.id.item_area_image_more);

        tvDeviceDevice.setText(item.getName());
        int count = 0;

        for (Device device : MainApplication.getApplication().getDeviceCache().getDevices()) {
            if (device.roomID == null) {
                continue;
            }
            if (TextUtils.equals(device.roomID, item.getRoomID())) {
                count++;
            }
        }
//        textDeviceCount.setText(((item.count == null || item.count.isEmpty()) ? "0" : item.count) + context.getString(R.string.item_area_list_Device_Count));
        textDeviceCount.setText(count + context.getString(R.string.Area_Device_Number));
        areaEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupIndex = pos;
                popupWindow.showParent(view);
            }
        });
    }

    private void showDelete(final int pos) {
        builder = new WLDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.Tip_Warm))
                .setCancelOnTouchOutSide(false)
                .setMessage(mContext.getString(R.string.Area_DeleteAreaTips_OK_Cancel))
                .setPositiveButton(mContext.getResources().getString(R.string.Sure))
                .setNegativeButton(mContext.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        RoomInfo roomInfo = mData.get(pos);
//                        mData.remove(pos);
//                        notifyDataSetChanged();

                        MainApplication.getApplication().getMqttManager().
                                publishEncryptedMessage(
                                        MQTTCmdHelper.createSetRoomInfo(
                                                Preference.getPreferences().getCurrentGatewayID(),
                                                3,
                                                null,
                                                roomInfo.getRoomID(),
                                                null),
                                        MQTTManager.MODE_GATEWAY_FIRST);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showEdit(int pos) {
        builder = new WLDialog.Builder(mContext);
        final RoomInfo roomInfo = mData.get(pos);
        builder.setTitle(mContext.getString(R.string.Area_UpdateArea))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.Area_NewArea_Hint)
                .setEditTextText(roomInfo.getName())
                .setPositiveButton(mContext.getResources().getString(R.string.Sure))
                .setNegativeButton(mContext.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {

                            MainApplication.getApplication().getMqttManager().
                                    publishEncryptedMessage(
                                            MQTTCmdHelper.createSetRoomInfo(
                                                    Preference.getPreferences().getCurrentGatewayID(),
                                                    2,
                                                    msg,
                                                    roomInfo.getRoomID(),
                                                    null),
                                            MQTTManager.MODE_GATEWAY_FIRST);
                            dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}
