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

import com.alibaba.fastjson.JSON;
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
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/12/8.
 * func： 向往背景音乐
 * email: hxc242313@qq.com
 */

public class HomeWidget_Wish_BGM extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private Device mDevice;
    private HomeItemBean mHomeItem;

    private ImageView ivLast;
    private ImageView ivNext;
    private ImageView ivPlay;
    private CircleImageView albumImg;
    private TextView albumName;
    private TextView albumArtist;
    private TextView tvName;
    private TextView tvState;

    private Context context;
    private String songId;
    private EntApiUnit entApiUnit;
    private Handler handler;

    public HomeWidget_Wish_BGM(Context context) {
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
        setMode();
        initData();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_wish_bgm, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.context = context;

        ivLast = (ImageView) findViewById(R.id.iv_last);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        albumImg = (CircleImageView) findViewById(R.id.iv_music_pic);
        tvName = (TextView) findViewById(R.id.home_view_xw01_name);
        tvState = (TextView) findViewById(R.id.tv_state);

        albumName = (TextView) findViewById(R.id.tv_album_name);
        albumArtist = (TextView) findViewById(R.id.tv_album_artist);

    }

    private void initData() {
        if (mDevice == null) {
            return;
        }
        handler = new Handler();
        doGetPlayMode();
    }

    private void initListener() {
        ivLast.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
    }

    private void setName() {
        if (mDevice == null) {
            tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItem.getName(), mHomeItem.getType()));
        } else {
            tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    private void setMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            tvState.setText(R.string.Device_Online);
            tvState.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tvState.setText(R.string.Device_Offline);
            tvState.setTextColor(getResources().getColor(R.color.newStateText));
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
            default:
                break;

        }

    }

    private void playAndStopMusic() {
        if (ivPlay.getTag().equals("play")) {
            doChangePlayMode("1");

        } else if (ivPlay.getTag().equals("stop")) {
            doChangePlayMode("0");
        }
    }


    /**
     * 获取播放信息
     */
    private void doGetPlayMode() {
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
                    if (TextUtils.equals(musicBean.status, "0")) {
                        ivPlay.setTag("play");
                        ivPlay.setImageResource(R.drawable.icon_play_music);
                    } else {
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


    /**
     * 修改播放状态
     *
     * @param state
     */
    private void doChangePlayMode(String state) {
        entApiUnit.doChangePlayMode(state, mDevice.devID, mDevice.devID, new EntApiUnit.CommonListener() {
            @Override
            public void onSuccess(Object bean) {
                doGetPlayMode();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    /**
     * 上一首或下一首
     *
     * @param nextOrLast
     */
    private void doNextOrLast(String nextOrLast) {
        entApiUnit.doNextOrLast(mDevice.devID, nextOrLast, new EntApiUnit.CommonListener() {
            @Override
            public void onSuccess(Object bean) {
                doGetPlayMode();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void updateCurrentSongView(Device device) {
        if (TextUtils.isEmpty(device.extData)) {
            return;
        }
        PlayModeBean bean = JSON.parseObject(device.extData, PlayModeBean.class);
        String duration = bean.entContent.duration;
        if (!TextUtils.equals(duration, "0")) {
            songId = bean.entContent.songId;
            albumName.setText(bean.entContent.trackTitle);
            albumArtist.setText(bean.entContent.nickname);
            if (!TextUtils.isEmpty(bean.entContent.avatarUrl)) {
                ImageLoader.getInstance().displayImage(bean.entContent.avatarUrl, albumImg);
            } else if (!TextUtils.isEmpty(bean.entContent.url)) {
                ImageLoader.getInstance().displayImage(bean.entContent.url, albumImg);
            } else {
                albumImg.setImageResource(R.drawable.icon_widget_d9_music);
            }
        }
        if (TextUtils.equals(bean.entContent.status, "0")) {
            ivPlay.setTag("play");
            ivPlay.setImageResource(R.drawable.icon_play_music);
        } else {
            ivPlay.setTag("stop");
            ivPlay.setImageResource(R.drawable.icon_stop_music);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, event.deviceInfoBean.name));
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);

                setName();
                setMode();
                updateCurrentSongView(event.device);
            }
        }
    }
}
