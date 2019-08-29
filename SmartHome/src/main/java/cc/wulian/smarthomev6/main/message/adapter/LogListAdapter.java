package cc.wulian.smarthomev6.main.message.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AlarmInfoTest;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
import cc.wulian.smarthomev6.main.message.log.HouseKeeperLogActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogActivity;
import cc.wulian.smarthomev6.main.message.log.SceneLogActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.BadgeView2;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.slidemenu.SlidingHelper;
import cc.wulian.smarthomev6.support.tools.slidemenu.SlidingMenu;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by Veev on 2017/7/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AlarmListAdapter
 */

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.AlarmListHolder> implements SlidingHelper {

    private List<AlarmInfoTest> list;
    private List<SlidingMenu> mSlidingMenuList = new ArrayList<>();
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private Context mContext;

    public LogListAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<AlarmInfoTest> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public LogListAdapter.AlarmListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_log_list, parent, false);
        SlidingMenu slidingMenu = (SlidingMenu) itemView.findViewById(R.id.sliding_menu);
        slidingMenu.setSlidingHelper(this);
        return new AlarmListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LogListAdapter.AlarmListHolder holder, int position) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final AlarmInfoTest info = list.get(holder.getAdapterPosition());
        final Device device = MainApplication.getApplication().getDeviceCache().get(info.deviceId);
        boolean isOnline;
        boolean isDeletedDevice;
        if (device != null) {
            //网关缓存中查的到，设备未被删除
            isOnline = device.isOnLine();
            isDeletedDevice = false;
        } else {
            if (info.deviceId.equals("scene") || info.deviceId.equals("housekeeper")) {
                //场景管家的显示默认设为在线未被删除状态
                isOnline = true;
                isDeletedDevice = false;
            } else {
                //当前网关缓存中不存在,设备已被删除
                isOnline = false;
                isDeletedDevice = true;
            }
        }
        holder.mViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(info.deviceId, "scene")) {
                    SceneLogActivity.start(holder.itemView.getContext(), "scene", Preference.getPreferences().getCurrentGatewayID());
                } else if (TextUtils.equals(info.deviceId, "housekeeper")) {
                    HouseKeeperLogActivity.start(holder.itemView.getContext(), "houseKeeper", Preference.getPreferences().getCurrentGatewayID());
                } else {

                    MessageLogActivity.start(holder.itemView.getContext(), info.deviceId, info.type, info.deviceName);
                }
                closeOpenMenu();
            }
        });

        holder.mViewNode.setVisibility(info.alarmCount > 0 ? View.VISIBLE : View.INVISIBLE);

        holder.mTextName.setText(info.deviceName);
        holder.mTextMessage.setText(info.alarmMessage);

        Resources r = mContext.getResources();
        Drawable[] layers = new Drawable[2];
        layers[0] = r.getDrawable(info.deviceIcon);
        layers[1] = r.getDrawable(info.deviceIcon);
        if (isDeletedDevice) {
            layers[1] = r.getDrawable(R.drawable.bg_device_deleted);
            holder.mTextName.setAlpha(0.54f);
            holder.mTextMessage.setAlpha(0.54f);
        } else if (!isOnline) {
            layers[1] = r.getDrawable(R.drawable.bg_device_offline);
            holder.mTextName.setAlpha(0.54f);
            holder.mTextMessage.setAlpha(0.54f);
        } else {
            holder.mTextName.setAlpha(1.0f);
            holder.mTextMessage.setAlpha(1.0f);
        }
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        holder.mImageIcon.setImageDrawable(layerDrawable);
        if (info.deviceId.equals("scene") || info.deviceId.equals("housekeeper")) {
            holder.mTextDelete.setVisibility(View.GONE);
            holder.mTextMessage.setVisibility(View.GONE);
        }
        holder.mTextDelete.setTag(info);
        holder.mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLog(v);
                closeOpenMenu();
            }
        });
    }

    private void deleteLog(final View v) {
        builder = new WLDialog.Builder(v.getContext());
        final AlarmInfoTest info = (AlarmInfoTest) v.getTag();
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(v.getContext().getString(R.string.Message_Center_EmptyConfirm))
                .setPositiveButton(v.getContext().getResources().getString(R.string.Sure))
                .setNegativeButton(v.getContext().getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        new DataApiUnit(view.getContext()).deDeleteLog(info.deviceId,
                                DeviceInfoDictionary.isWiFiDevice(info.type)
                                        ? info.deviceId
                                        : Preference.getPreferences().getCurrentGatewayID(), "", "",
                                new DataApiUnit.DataApiCommonListener() {
                                    @Override
                                    public void onSuccess(Object bean) {

                                    }

                                    @Override
                                    public void onFail(int code, String msg) {

                                    }
                                });
                        int index = list.indexOf(info);
                        list.remove(index);
                        notifyItemRemoved(index);
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

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public void holdOpenMenu(SlidingMenu slidingMenu) {
        mSlidingMenuList.add(slidingMenu);
    }

    @Override
    public void closeOpenMenu() {
        for (SlidingMenu menu : mSlidingMenuList) {
            if (menu != null && menu.isOpen()) {
                menu.closeMenu();
            }
        }
        mSlidingMenuList.clear();
    }

    class AlarmListHolder extends RecyclerView.ViewHolder {

        private ImageView mImageIcon;
        private TextView mTextName, mTextMessage;
        private View mViewNode;
        private TextView mTextDelete;
        private View mViewMain;

        public AlarmListHolder(View itemView) {
            super(itemView);

            mImageIcon = (ImageView) itemView.findViewById(R.id.iv_alarm_icon);
            mTextMessage = (TextView) itemView.findViewById(R.id.tv_alarm_message);
            mTextName = (TextView) itemView.findViewById(R.id.tv_alarm_device);
            mViewNode = itemView.findViewById(R.id.view_log_node);
            mTextDelete = (TextView) itemView.findViewById(R.id.sliding_delete);
            mViewMain = itemView.findViewById(R.id.sliding_main);
        }
    }
}