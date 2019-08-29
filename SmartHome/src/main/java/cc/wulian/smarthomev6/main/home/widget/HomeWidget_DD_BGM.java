package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.DreamFlowerBroadcastEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2017/12/8.
 * func： 背景音乐 widget
 * email: hxc242313@qq.com
 */

public class HomeWidget_DD_BGM extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView mTextName, mTextRoom, mTextState, mTextSongTitle, mTextSongSinger;
    private View mRootView;
    private ImageView mImageLast, mImagePlay, mImageNext;

    private String mPlayStatus = "1", mSongName = "", mSongSinger = "";

    public HomeWidget_DD_BGM(Context context) {
        super(context);
        initView(context);
        initListener();
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        dealData(mDevice);
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_dd_bgm, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        mTextRoom = (TextView) mRootView.findViewById(R.id.widget_title_room);
        mTextState = (TextView) mRootView.findViewById(R.id.widget_title_state);

        mImageLast = (ImageView) mRootView.findViewById(R.id.widget_dd_image_last);
        mImagePlay = (ImageView) mRootView.findViewById(R.id.widget_dd_image_play);
        mImageNext = (ImageView) mRootView.findViewById(R.id.widget_dd_image_next);
        mTextSongTitle = (TextView) mRootView.findViewById(R.id.widget_dd_text_title);
        mTextSongSinger = (TextView) mRootView.findViewById(R.id.widget_dd_text_singer);
    }

    private void initListener() {
        mImageLast.setOnClickListener(this);
        mImagePlay.setOnClickListener(this);
        mImageNext.setOnClickListener(this);
    }

    private void setName() {
        if (mDevice == null) {
            mTextName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            mTextName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        mTextRoom.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mDevice.mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                mTextState.setText(R.string.Device_Online);
                mTextState.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 2:
                // 离线
                mTextState.setText(R.string.Device_Offline);
                mTextState.setTextColor(getResources().getColor(R.color.newStateText));
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widget_dd_image_last:
                sendCmd(0x800C, null);
                break;
            case R.id.widget_dd_image_play:
                toggle();
                break;
            case R.id.widget_dd_image_next:
                sendCmd(0x800D, null);
                break;
            default:
                break;
        }

    }

    private void toggle() {
        if (TextUtils.isEmpty(mPlayStatus)) {
            mPlayStatus = "1";
        }
        switch (mPlayStatus) {
            case "0":
                sendCmd(0x8002, "1");
                break;
            default:
                sendCmd(0x8002, "0");
                break;
        }
    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x0001 || attribute.attributeId == 0x0007) {
                    String[] v = attribute.attributeValue.split(",");
                    if (v.length > 2) {
                        mSongName = v[1];
                        mSongSinger = v[2];
                        if (TextUtils.equals(v[2], "<unknown>")) {
                            mSongSinger = getResources().getString(R.string.Music_unknown);
                        }
                    }
                } else if (attribute.attributeId == 0x0002) {
                    mPlayStatus = attribute.attributeValue;
                } else if (attribute.attributeId == 0x000C) {
                    mPlayStatus = "0";
                    sendCmd(0x8007, null);
                } else if (attribute.attributeId == 0x000D) {
                    mPlayStatus = "0";
                    sendCmd(0x8007, null);
                }
            }
        });

        updateData();
    }

    private void updateData() {
        if (TextUtils.isEmpty(mPlayStatus)) {
            mPlayStatus = "1";
        }
        switch (mPlayStatus) {
            case "0":
                mImagePlay.setImageResource(R.drawable.icon_play_music);
                break;
            default:
                mImagePlay.setImageResource(R.drawable.icon_stop_music);
                break;
        }

        mTextSongTitle.setText(mSongName);
        mTextSongSinger.setText(mSongSinger);
    }

    /**
     * 发送命令
     */
    private void sendCmd(int commandId, Object args){
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0102);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            if (args != null) {
                JSONArray array = new JSONArray();
                array.put(args);
                object.put("parameter", array);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setName();
                    setRoomName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                dealData(JSON.parseObject(event.device.data, Device.class));
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                setRoomName();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }
}
