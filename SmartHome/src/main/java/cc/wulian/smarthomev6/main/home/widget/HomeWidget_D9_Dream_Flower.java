package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.dreamFlower.radioStation.RadioStationSettingActivity;
import cc.wulian.smarthomev6.support.core.apiunit.EntApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MusicBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.PlayModeBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.CircleImageView;
import cc.wulian.smarthomev6.support.customview.WhewView;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.DreamFlowerBroadcastEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * created by huxc  on 2017/12/8.
 * func： 梦想之花语音播报和电台widget
 * email: hxc242313@qq.com
 */

public class HomeWidget_D9_Dream_Flower extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private static final long BROADCAST_TIME = 25*1000;

    private Device mDevice;
    private HomeItemBean mHomeItem;

    private View viewStation;
    private TextView tvBroadcast;
    private View viewBroadcast;
    private ImageView ivLast;
    private ImageView ivNext;
    private ImageView ivPlay;
    private CheckedTextView btnStation;
    private CheckedTextView btnBroadcast;
    private CircleImageView albumImg;
    private TextView albumName;
    private TextView albumArtist;
    private TextView tvName;
    private TextView tvArea;
    private TextView tvSetting;

    private WhewView mWhewView;

    private Context context;
    private ViewGroup switch_layout;
    private int checkPosition = 0;
    private String songId;
    private EntApiUnit entApiUnit;
    private Handler handler;

    public HomeWidget_D9_Dream_Flower(Context context) {
        super(context);
        entApiUnit = new EntApiUnit(context);
        initView(context);
        initListener();
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItem = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        setName();
        setRoomName();
        initData();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_d9_dream_flower, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.context = context;

        viewStation = findViewById(R.id.view_station);
        viewBroadcast = findViewById(R.id.layout_broadcast);
        tvBroadcast = (TextView) findViewById(R.id.tv_broadcast);
        ivLast = (ImageView) findViewById(R.id.iv_last);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        btnStation = (CheckedTextView) findViewById(R.id.btn_station);
        btnBroadcast = (CheckedTextView) findViewById(R.id.btn_broadcast);
        albumImg = (CircleImageView) findViewById(R.id.iv_music_pic);
        tvName = (TextView) findViewById(R.id.home_view_d9_name);
        tvArea = (TextView) findViewById(R.id.widget_title_room);
        tvSetting = (TextView) findViewById(R.id.tv_setting);

        switch_layout = (ViewGroup) rootView.findViewById(R.id.switch_layout);
        albumName = (TextView) findViewById(R.id.tv_album_name);
        albumArtist = (TextView) findViewById(R.id.tv_album_artist);
        mWhewView = (WhewView) findViewById(R.id.whewview);

    }

    private void initData() {
        if (mDevice == null) {
            return;
        }
        handler = new Handler();
        entApiUnit.doGetMode(mDevice.devID, "0", new EntApiUnit.CommonListener<PlayModeBean>() {
            @Override
            public void onSuccess(PlayModeBean bean) {
                MusicBean musicBean = bean.entContent;
                if (musicBean != null) {
                    songId = musicBean.songId;
                    albumName.setText(musicBean.trackTitle);
                    albumArtist.setText(musicBean.nickname);
                    if (!TextUtils.isEmpty(musicBean.avatarUrl)) {
                        ImageLoader.getInstance().displayImage(musicBean.avatarUrl, albumImg);
                    } else if (!TextUtils.isEmpty(musicBean.url)) {
                        ImageLoader.getInstance().displayImage(musicBean.url, albumImg);
                    } else {
                        albumImg.setImageResource(R.drawable.icon_widget_d9_music);
                    }
                    if (TextUtils.equals(musicBean.status, "1")) {
                        ivPlay.setTag("play");
                        ivPlay.setImageResource(R.drawable.icon_play_music);
                    } else if (TextUtils.equals(musicBean.status, "2")) {
                        ivPlay.setTag("stop");
                        ivPlay.setImageResource(R.drawable.icon_stop_music);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void initListener() {
        ivLast.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        btnStation.setOnClickListener(this);
        btnBroadcast.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvBroadcast.setOnClickListener(this);
        viewBroadcast.setOnClickListener(this);
        initSwitchLayout(switch_layout, 0);
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        tvArea.setText(areaName);
    }

    private void setName() {
        if (mDevice == null) {
            tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItem.getName(), mHomeItem.getType()));
        } else {
            tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }


    private void showSelectItem(String text) {
        switch (text) {
            case "radio":
                viewBroadcast.setVisibility(INVISIBLE);
                tvBroadcast.setVisibility(INVISIBLE);
                viewStation.setVisibility(VISIBLE);
                tvSetting.setVisibility(INVISIBLE);
                break;
            case "broadcast":
                updateBroadcastView(false);
                if (mWhewView.isStarting()) {//如果动画正在运行就停止，否则就继续执行
                    mWhewView.stop();
                }
                viewBroadcast.setVisibility(VISIBLE);
                viewStation.setVisibility(INVISIBLE);
                tvSetting.setVisibility(VISIBLE);
                break;
        }
    }

    private void initSwitchLayout(final ViewGroup parent, final int checkPosition) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final CheckedTextView text = (CheckedTextView) parent.getChildAt(i);
            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectItem(text.getTag().toString());
                    for (int j = 0; j < parent.getChildCount(); j++) {
                        CheckedTextView checkedTextView = (CheckedTextView) parent.getChildAt(j);
                        if (checkedTextView.isChecked()) {
                            checkedTextView.setChecked(false);
                            checkedTextView.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                        } else {
                            HomeWidget_D9_Dream_Flower.this.checkPosition = j;
                            checkedTextView.setChecked(true);
                            checkedTextView.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                }
            });
            if (i == checkPosition) {
                this.checkPosition = checkPosition;
                text.setChecked(true);
                text.setTextColor(getResources().getColor(R.color.white));
            } else {
                text.setChecked(false);
                text.setTextColor(getResources().getColor(R.color.home_widget_curtain));
            }
        }
    }


    private void playAndStopMusic() {
        if (ivPlay.getTag().equals("play")) {
            doChangePlayMode("1");
        } else if (ivPlay.getTag().equals("stop")) {
            doChangePlayMode("0");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_last:
                doNextOrLast("last");
                break;
            case R.id.iv_play:
                playAndStopMusic();
                break;
            case R.id.iv_next:
                doNextOrLast("next");
                break;
            case R.id.tv_setting:
                RadioStationSettingActivity.start(context, mDevice.devID);
                break;
            case R.id.tv_broadcast:
                sendSetDreamFlowerConfigMsg("1", "");
                mWhewView.start();
                updateBroadcastView(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWhewView.stop();
                        updateBroadcastView(false);
                    }
                }, BROADCAST_TIME);

                break;
            default:
                break;

        }

    }

    private void updateBroadcastView(boolean isBroadcasting){
        if(isBroadcasting){
            tvBroadcast.setVisibility(INVISIBLE);
            mWhewView.setVisibility(VISIBLE);
        }else{
            tvBroadcast.setVisibility(VISIBLE);
            mWhewView.setVisibility(INVISIBLE);
        }
    }


    /**
     * 设置梦想之花播报设置信息
     *
     * @param cmdindex
     * @param data
     */
    public void sendSetDreamFlowerConfigMsg(String cmdindex, Object data) {
        JSONObject object = new JSONObject();

        try {
            object.put("cmd", "402");
            object.put("gwID", mDevice.gwID);
            object.put("cmdindex", cmdindex);
            object.put("cmdtype", "set");
            if (data != null) {
                object.put("data", data);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

    }

    private void doChangePlayMode(String state) {
        entApiUnit.doChangePlayMode(state, Preference.getPreferences().getCurrentGatewayID(), mDevice.devID, new EntApiUnit.CommonListener() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void doNextOrLast(String nextOrLast) {
        entApiUnit.doNextOrLast(mDevice.devID, nextOrLast, new EntApiUnit.CommonListener() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDreamFlowerBroadcast(DreamFlowerBroadcastEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                dealData(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, event.deviceInfoBean.name));
                    setRoomName();
                }
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

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            org.json.JSONArray endpoints = object.optJSONArray("endpoints");
            org.json.JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            org.json.JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");
            if (attributeId == 1 || attributeId == 3) {
                initData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
