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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AlarmInfoTest;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.alarm.BcAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.CylincamAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.EquesHistoryAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.ICamHistoryAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.LcCameraAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.BadgeView2;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.slidemenu.SlidingHelper;
import cc.wulian.smarthomev6.support.tools.slidemenu.SlidingMenu;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AlarmListAdapter
 */

public class AlarmListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SlidingHelper {

    private List<AlarmInfoTest> list;
    private List<SlidingMenu> mSlidingMenuList = new ArrayList<>();

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private int pageSize;
    private final static int FOOTER_TYPE = 101;
    private FooterHolder footerHolder;
    private Animation loadAnimation;
    private boolean isShowFooter = false;
    private boolean hasMoreData = true;
    private Context mContext;

    public AlarmListAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<AlarmInfoTest> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void startLoadAnimation() {
        if (footerHolder != null) {
            if (loadAnimation == null) {
                loadAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                loadAnimation.setRepeatCount(Animation.INFINITE);
                loadAnimation.setDuration(700);
            }
            footerHolder.itemView.setVisibility(View.VISIBLE);
            footerHolder.loadingView.startAnimation(loadAnimation);
        }
    }

    public void stopLoadMore(boolean hasMoreData) {
        if (footerHolder != null) {
            if (hasMoreData) {
                footerHolder.loadingView.clearAnimation();
                footerHolder.itemView.setVisibility(View.GONE);
            } else {
                notifyItemRemoved(getItemCount() - 1);
            }
            this.hasMoreData = hasMoreData;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == FOOTER_TYPE) {
            View itemView = layoutInflater.inflate(R.layout.activity_message_alarm_list_footer, parent, false);
            return new FooterHolder(itemView);
        } else {
            View itemView = layoutInflater.inflate(R.layout.item_alarm_list, parent, false);
            SlidingMenu slidingMenu = (SlidingMenu) itemView.findViewById(R.id.sliding_menu);
            slidingMenu.setSlidingHelper(this);
            return new AlarmListHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (holder instanceof FooterHolder) {
            footerHolder = (FooterHolder) holder;
        } else {
            AlarmListHolder alarmListHolder = (AlarmListHolder) holder;
            final AlarmInfoTest info = list.get(holder.getAdapterPosition());
            final Device device = MainApplication.getApplication().getDeviceCache().get(info.deviceId);
            String type0;
            boolean isOnline;
            boolean isDeletedDevice;
            if (device != null) {
                type0 = device.type;
                isOnline = device.isOnLine();
                isDeletedDevice = false;
            } else {
                type0 = info.type;
                isOnline = false;
                isDeletedDevice = true;
            }
            final String type = type0;

            alarmListHolder.mViewMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    if (TextUtils.equals(type, "CMICA1") || TextUtils.equals(type, "CMICA2") || TextUtils.equals(type, "CMICA3") || TextUtils.equals(type, "CMICA5") || TextUtils.equals(type, "CMICA6")) {
                        String sdomain = "";
                        try {
                            ICamGetSipInfoBean i = Preference.getPreferences().getSipInfo(ApiConstant.getUserID(), info.deviceId);
                            sdomain = i == null ? "" : i.deviceDomain;
                        } catch (Exception e) {

                        }
                        ICamHistoryAlarmActivity.start(holder.itemView.getContext(), info.deviceId,
                                sdomain,
                                type);
                    } else if (TextUtils.equals(type, "CMICY1")) {
                        EquesHistoryAlarmActivity.start(holder.itemView.getContext(), info.deviceId);
                    } else if (TextUtils.equals(type, "Bc") || TextUtils.equals(type, "Bn")) {
                        BcAlarmActivity.start(holder.itemView.getContext(), info.deviceId, BcAlarmActivity.TYPE_ALL);
                    } else if (TextUtils.equals(type, "CMICA4")) {
                        CylincamAlarmActivity.start(holder.itemView.getContext(), info.deviceId);
                    } else if (DeviceInfoDictionary.isLcCamera(type)) {
                        LcCameraAlarmActivity.start(holder.itemView.getContext(), info.deviceId);
                    } else {
                        MessageAlarmActivity.start(holder.itemView.getContext(), info.deviceId, type, info.deviceName);
                    }
                    closeOpenMenu();
                }
            });

            if (alarmListHolder.mImageIcon.getTag() == null) {
                BadgeView2 alarmBadge = new BadgeView2(alarmListHolder.mImageIcon.getContext());
                alarmBadge.setTargetView(alarmListHolder.mImageIcon);
                alarmBadge.setBadgeCount(info.alarmCount);
                alarmBadge.setBadgeGravity(Gravity.TOP | Gravity.END);
                alarmListHolder.mImageIcon.setTag(alarmBadge);
            } else {
                BadgeView2 alarmBadge = (BadgeView2) alarmListHolder.mImageIcon.getTag();
                alarmBadge.setBadgeCount(info.alarmCount);
            }

            WLog.i("Message", "id: " + info.deviceId + ", count: " + info.alarmCount + ", msg: " + info.alarmMessage);

            alarmListHolder.mTextName.setText(info.deviceName);
            alarmListHolder.mTextMessage.setText(info.alarmMessage);
            alarmListHolder.mImageIcon.setImageResource(info.deviceIcon);
            Resources r = mContext.getResources();
            Drawable[] layers = new Drawable[2];
            layers[0] = r.getDrawable(info.deviceIcon);
            layers[1] = r.getDrawable(info.deviceIcon);
            if (isDeletedDevice) {
                layers[1] = r.getDrawable(R.drawable.bg_device_deleted);
                alarmListHolder.mTextName.setAlpha(0.54f);
                alarmListHolder.mTextMessage.setAlpha(0.54f);
            } else if (!isOnline) {
                layers[1] = r.getDrawable(R.drawable.bg_device_offline);
                alarmListHolder.mTextName.setAlpha(0.54f);
                alarmListHolder.mTextMessage.setAlpha(0.54f);
            } else {
                alarmListHolder.mTextName.setAlpha(1.0f);
                alarmListHolder.mTextMessage.setAlpha(1.0f);
            }
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            alarmListHolder.mImageIcon.setImageDrawable(layerDrawable);

            alarmListHolder.mTextDelete.setTag(info);
            alarmListHolder.mTextDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAlarm(v, "", "");
                    closeOpenMenu();
                }
            });
        }
    }

    private void deleteAlarm(final View v, final String startTime, final String endTime) {
        final AlarmInfoTest info = (AlarmInfoTest) v.getTag();
        builder = new WLDialog.Builder(v.getContext());
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(v.getContext().getString(R.string.Message_Center_EmptyConfirm))
                .setPositiveButton(v.getContext().getResources().getString(R.string.Sure))
                .setNegativeButton(v.getContext().getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        new DataApiUnit(view.getContext()).deDeleteAlarm(info.deviceId,
                                DeviceInfoDictionary.isWiFiDevice(info.type)
                                        ? info.deviceId
                                        : Preference.getPreferences().getCurrentGatewayID(), startTime, endTime,
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

    public int lastPosition() {
        return list.size() - 1;
    }

    @Override
    public int getItemCount() {
        isShowFooter = list != null && list.size() >= pageSize && hasMoreData;
        if (isShowFooter) {
            return list == null ? 0 : list.size() >= pageSize ? list.size() + 1 : list.size();
        } else {
            return list == null ? 0 : list.size();
        }
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

    @Override
    public int getItemViewType(int position) {
        if (isShowFooter && position + 1 == getItemCount() && getItemCount() > pageSize) {
            return FOOTER_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }

    class AlarmListHolder extends RecyclerView.ViewHolder {

        private ImageView mImageIcon;
        private TextView mTextName, mTextMessage;
        private TextView mTextDelete;
        private View mViewMain;

        public AlarmListHolder(View itemView) {
            super(itemView);

            mImageIcon = (ImageView) itemView.findViewById(R.id.iv_alarm_icon);
            mTextMessage = (TextView) itemView.findViewById(R.id.tv_alarm_message);
            mTextName = (TextView) itemView.findViewById(R.id.tv_alarm_device);
            mTextDelete = (TextView) itemView.findViewById(R.id.sliding_delete);
            mViewMain = itemView.findViewById(R.id.sliding_main);
        }
    }

    class FooterHolder extends RecyclerView.ViewHolder {

        private ImageView loadingView;

        public FooterHolder(View itemView) {
            super(itemView);
            loadingView = (ImageView) itemView.findViewById(R.id.loading_view);
        }
    }
}