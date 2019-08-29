package cc.wulian.smarthomev6.main.message.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UserPushInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.setts.MessageSettingsDetailActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/12
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MessageSettingsAdapter
 */

public class MessageSettingsAdapter extends RecyclerView.Adapter<MessageSettingsAdapter.SettsHolder> {

    private List<UserPushInfo.UserPushInfoBean> mData = new ArrayList<>();
    private Context mContext;

    public MessageSettingsAdapter(Context context) {
        this.mContext = context;
    }

    public MessageSettingsAdapter(List<UserPushInfo.UserPushInfoBean> data) {
        mData = data;
    }

    public void setData(List<UserPushInfo.UserPushInfoBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public SettsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View itemView = layoutInflater.inflate(R.layout.item_message_setts, parent, false);
        return new SettsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SettsHolder holder, int position) {
        final UserPushInfo.UserPushInfoBean bean = mData.get(position);
        Device device = MainApplication.getApplication().getDeviceCache().get(bean.deviceId);

        final String name, type;
        int icon;
        if (device == null) {
            if (TextUtils.equals("houseKeeper", bean.deviceId)) {
                name = bean.name;
                icon = R.drawable.icon_log_housekeeper;
                type = bean.deviceId;
            } else {
                name = DeviceInfoDictionary.getNameByTypeAndName(bean.type, bean.name);
                icon = DeviceInfoDictionary.getIconByType(bean.type);
                type = bean.type;
            }

        } else {
            name = DeviceInfoDictionary.getNameByDevice(device);
            icon = DeviceInfoDictionary.getIconByDevice(device);
            type = device.type;
        }

        holder.mTextName.setText(name);
        holder.mImageIcon.setImageResource(icon);

        if (TextUtils.equals(type, "houseKeeper")) {
            holder.ivRight.setVisibility(View.GONE);
            holder.mTextMessage.setVisibility(View.GONE);
            holder.tbHouseKeeper.setVisibility(View.VISIBLE);
            holder.tbHouseKeeper.setChecked(bean.pushFlag == 1);
            holder.tbHouseKeeper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("deviceId", Preference.getPreferences().getCurrentGatewayID());
                        jsonObject.put("messageCode", "0101052");
                        jsonObject.put("pushFlag", isChecked ? "1" : "0");
                        jsonObject.put("pushType", "2");
                        jsonObject.put("msgType", "2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new DeviceApiUnit(mContext).doSaveUserPushSetts(jsonObject.toString(), new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {

                            WLog.i("MessageSettingAdapter", "doSaveUserPushSetts");
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                }
            });
        } else {
            holder.ivRight.setVisibility(View.VISIBLE);
            holder.mTextMessage.setVisibility(View.VISIBLE);
            holder.tbHouseKeeper.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageSettingsDetailActivity.start(holder.itemView.getContext(), bean.deviceId, bean.pushFlag, bean.logPushFlag, type);
                }
            });
            String t;
            switch (bean.totalPushFlag) {
                case 0:
                    t = holder.itemView.getContext().getString(R.string.Message_Center_Close_All);
                    break;
                case 1:
                    t = holder.itemView.getContext().getString(R.string.Message_Center_Turnon_All);
                    break;
                case 2:
                    t = holder.itemView.getContext().getString(R.string.Message_Center_Turnon_Partial);
                    break;
                default:
                    t = holder.itemView.getContext().getString(R.string.Message_Center_Turnon_All);
            }
            holder.mTextMessage.setText(t);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class SettsHolder extends RecyclerView.ViewHolder {

        private ImageView mImageIcon;
        private TextView mTextName, mTextMessage;
        private ToggleButton tbHouseKeeper;
        private ImageView ivRight;

        public SettsHolder(View itemView) {
            super(itemView);

            mImageIcon = (ImageView) itemView.findViewById(R.id.message_setts_image_icon);
            mTextMessage = (TextView) itemView.findViewById(R.id.message_setts_text_desc);
            mTextName = (TextView) itemView.findViewById(R.id.message_setts_text_name);
            ivRight = (ImageView) itemView.findViewById(R.id.iv_message_setting_right);
            tbHouseKeeper = itemView.findViewById(R.id.tb_housekeeper);
        }
    }
}
