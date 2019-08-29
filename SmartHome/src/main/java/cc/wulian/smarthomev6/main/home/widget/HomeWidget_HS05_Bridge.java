package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.CircleImageView;
import cc.wulian.smarthomev6.support.customview.WhewView;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.wrecord.C;

/**
 * created by huxc  on 2018/5/30.
 * func： 海信冰箱
 * email: hxc242313@qq.com
 */

public class HomeWidget_HS05_Bridge extends RelativeLayout implements IWidgetLifeCycle, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "HomeWidget_HS05_Bridge";
    private Context context;
    private CheckedTextView btnColdStorage;
    private CheckedTextView btnGreenHouse;
    private CheckedTextView btnColdRoom;
    private LinearLayout llColdStorage;
    private LinearLayout llGreenHouse;
    private LinearLayout llColdRoom;
    private TextView tvName;
    private TextView tvState;
    private TextView tvSetTemperature1;
    private TextView tvSetTemperature2;
    private TextView tvSetTemperature3;
    private ViewGroup switch_layout;
    private SeekBar sbColdStorage;
    private SeekBar sbGreenHouse;
    private SeekBar sbColdRoom;


    private HomeItemBean mHomeItem;
    private DeviceApiUnit deviceApiUnit;

    private Device mDevice;
    private int temperature1, temperature2, temperature3;
    private String deviceId;


    public HomeWidget_HS05_Bridge(Context context) {
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_hs05_bridge, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.context = context;
        btnColdStorage = (CheckedTextView) findViewById(R.id.btn_cold_storage);
        btnGreenHouse = (CheckedTextView) findViewById(R.id.btn_green_house);
        btnColdRoom = (CheckedTextView) findViewById(R.id.btn_cold_room);
        llColdStorage = (LinearLayout) findViewById(R.id.ll_cold_storage);
        llGreenHouse = (LinearLayout) findViewById(R.id.ll_green_house);
        llColdRoom = (LinearLayout) findViewById(R.id.ll_cold_room);
        tvName = (TextView) findViewById(R.id.home_view_hs05_name);
        tvState = (TextView) findViewById(R.id.home_view_hs05_state);
        tvSetTemperature1 = (TextView) findViewById(R.id.tv_set_temperature1);
        tvSetTemperature2 = (TextView) findViewById(R.id.tv_set_temperature2);
        tvSetTemperature3 = (TextView) findViewById(R.id.tv_set_temperature3);
        sbColdStorage = (SeekBar) findViewById(R.id.seekbar1);
        sbGreenHouse = (SeekBar) findViewById(R.id.seekbar2);
        sbColdRoom = (SeekBar) findViewById(R.id.seekbar3);
        switch_layout = (ViewGroup) rootView.findViewById(R.id.switch_layout);
    }

    private void initData() {
        if (mDevice == null) {
            return;
        }
        deviceApiUnit = new DeviceApiUnit(context);
        getInitTemperature();
        updateView();


    }

    private void initListener() {
        initSwitchLayout(switch_layout, 0);
        sbColdStorage.setOnSeekBarChangeListener(this);
        sbGreenHouse.setOnSeekBarChangeListener(this);
        sbColdRoom.setOnSeekBarChangeListener(this);
    }

    private void initSwitchLayout(final ViewGroup parent, final int checkPosition) {

        final CheckedTextView textView0 = (CheckedTextView) parent.getChildAt(0);
        final CheckedTextView textView1 = (CheckedTextView) parent.getChildAt(1);
        final CheckedTextView textView2 = (CheckedTextView) parent.getChildAt(2);


        textView0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectItem(textView0.getTag().toString());
                textView0.setChecked(true);
                textView1.setChecked(false);
                textView2.setChecked(false);
                textView0.setTextColor(getResources().getColor(R.color.white));
                textView1.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                textView2.setTextColor(getResources().getColor(R.color.home_widget_curtain));

            }
        });

        textView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectItem(textView1.getTag().toString());
                textView0.setChecked(false);
                textView1.setChecked(true);
                textView2.setChecked(false);
                textView0.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                textView1.setTextColor(getResources().getColor(R.color.white));
                textView2.setTextColor(getResources().getColor(R.color.home_widget_curtain));
            }
        });

        textView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectItem(textView2.getTag().toString());
                textView0.setChecked(false);
                textView1.setChecked(false);
                textView2.setChecked(true);
                textView0.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                textView1.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                textView2.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }

    private void showSelectItem(String text) {
        switch (text) {
            case "coldStorage":
                llColdStorage.setVisibility(VISIBLE);
                llGreenHouse.setVisibility(GONE);
                llColdRoom.setVisibility(GONE);
                break;
            case "greenHouse":
                llColdStorage.setVisibility(GONE);
                llGreenHouse.setVisibility(VISIBLE);
                llColdRoom.setVisibility(GONE);
                break;
            case "coldRoom":
                llColdStorage.setVisibility(GONE);
                llGreenHouse.setVisibility(GONE);
                llColdRoom.setVisibility(VISIBLE);
                break;
        }
    }


    private void getInitTemperature() {
        deviceApiUnit.doGetHisenseChildDevicesInfo(mDevice.devID, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i("getInitTemperature: onSuccess");
                parseData(bean);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void updateView() {
        sbColdStorage.setProgress(6 - (8 - temperature2));
        sbGreenHouse.setProgress(23 - (5 - temperature1));
        sbColdRoom.setProgress(10 - (-15 - temperature3));

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

    private void parseData(Object object) {
        com.alibaba.fastjson.JSONArray array = (com.alibaba.fastjson.JSONArray) object;
        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) array.get(0);
        com.alibaba.fastjson.JSONArray endpointArray = obj.getJSONArray("endpoints");
        com.alibaba.fastjson.JSONObject obj2 = (com.alibaba.fastjson.JSONObject) endpointArray.get(0);
        com.alibaba.fastjson.JSONObject propertiesObj = obj2.getJSONObject("properties");
        com.alibaba.fastjson.JSONArray temperatureArray = propertiesObj.getJSONArray("Temperature");
        com.alibaba.fastjson.JSONObject zoneObject1 = (com.alibaba.fastjson.JSONObject) temperatureArray.get(0);
        com.alibaba.fastjson.JSONObject zoneObject2 = (com.alibaba.fastjson.JSONObject) temperatureArray.get(1);
        com.alibaba.fastjson.JSONObject zoneObject3 = (com.alibaba.fastjson.JSONObject) temperatureArray.get(2);
        deviceId = obj.getString("deviceId");
        temperature1 = zoneObject1.getIntValue("value");
        temperature2 = zoneObject2.getIntValue("value");
        temperature3 = zoneObject3.getIntValue("value");

        updateView();

    }

    private void setName() {
        if (mDevice == null) {
            tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItem.getName(), mHomeItem.getType()));
        } else {
            tvName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }


    private void sendControlCmd(String deviceId, String zone, int value) {
        deviceApiUnit.doControlHisDevice(deviceId, zone, value, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "doControlHisDevice: ");

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekbar1:
                tvSetTemperature1.setText(sbColdStorage.getProgress() + 2 + "");
                WLog.i(TAG, "onProgressChanged1: " + sbColdStorage.getProgress());
                break;
            case R.id.seekbar2:
                tvSetTemperature2.setText(sbGreenHouse.getProgress() - 18 + "");
                WLog.i(TAG, "onProgressChanged2: " + sbGreenHouse.getProgress());
                break;
            case R.id.seekbar3:
                tvSetTemperature3.setText(sbColdRoom.getProgress() - 25 + "");
                WLog.i(TAG, "onProgressChanged3: " + sbColdRoom.getProgress());
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekbar1:
                sendControlCmd(deviceId, "2", Integer.parseInt(tvSetTemperature1.getText().toString()));
                WLog.i(TAG, "onStopTrackingTouch1: " + Integer.parseInt(tvSetTemperature1.getText().toString()));
                break;
            case R.id.seekbar2:
                sendControlCmd(deviceId, "1", Integer.parseInt(tvSetTemperature2.getText().toString()));
                WLog.i(TAG, "onStopTrackingTouch2: " + Integer.parseInt(tvSetTemperature2.getText().toString()));
                break;
            case R.id.seekbar3:
                sendControlCmd(deviceId, "3", Integer.parseInt(tvSetTemperature3.getText().toString()));
                WLog.i(TAG, "onStopTrackingTouch3: " + Integer.parseInt(tvSetTemperature3.getText().toString()));
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setName();
                    setMode();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                setMode();
            }
        }
    }
}
