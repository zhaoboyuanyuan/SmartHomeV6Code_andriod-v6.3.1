package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by 上海滩小马哥 on 2017/10/11.
 * 门铃按钮02型  type：A5
 */

public class HomeWidget_A5_Doorbell02 extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private TextView tv_name, tv_sate, tv_room;
    private ImageView iv_doorbell, iv_defense, iv_not_defense;
    private TextView tv_toast;
    private Context mContext;
    private Device mDevice;

    public HomeWidget_A5_Doorbell02(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_A5_Doorbell02(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        setName();
        setRoomName();
        setState();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_a5_doorbell02, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        tv_toast = (TextView) rootView.findViewById(R.id.text_toast);
        iv_doorbell = (ImageView) rootView.findViewById(R.id.doorbell_image);
        iv_defense = (ImageView) rootView.findViewById(R.id.defense_image);
        iv_not_defense = (ImageView) rootView.findViewById(R.id.not_defense_image);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        iv_defense.setOnClickListener(this);
        iv_not_defense.setOnClickListener(this);
        rootView.findViewById(R.id.root_view).setOnClickListener(this);
    }

    private void setName() {
        tv_name.setText(DeviceInfoDictionary.getNameByDevice(mDevice));
    }

    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        tv_room.setText(areaName);
    }

    private void setState() {
        if (mDevice.mode == 2) {
            tv_sate.setText(getResources().getString(R.string.Device_Offline));
            tv_sate.setTextColor(0xff535353);
            iv_doorbell.setImageResource(R.drawable.icon_doorbell_offline);
            iv_defense.setImageResource(R.drawable.icon_defence_offline);
            iv_not_defense.setImageResource(R.drawable.icon_not_defence_offline);
        } else {
            tv_sate.setText(getResources().getString(R.string.Device_Online));
            tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_doorbell.setImageResource(R.drawable.icon_doorbell_normal);

            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    //endpointStatus:0.撤防，1.设防
                    if ("0".equals(endpoint.endpointStatus)) {
                        iv_defense.setImageResource(R.drawable.icon_defence_offline);
                        iv_not_defense.setImageResource(R.drawable.icon_doorbell_not_defense);
                    } else if ("1".equals(endpoint.endpointStatus)) {
                        iv_defense.setImageResource(R.drawable.icon_doorbell_defense);
                        iv_not_defense.setImageResource(R.drawable.icon_not_defence_offline);
                    }
                    if (attribute.attributeId == 2){
                        //attributeValue:0.消警，1.报警
                        if ("0".equals(attribute.attributeValue)){
                            iv_doorbell.setImageResource(R.drawable.icon_doorbell_normal);
                        }else if ("1".equals(attribute.attributeValue)){
                            iv_doorbell.setImageResource(R.drawable.icon_doorbell_call);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defense_image:
                sendCmd(mDevice, "1");
                break;
            case R.id.not_defense_image:
                sendCmd(mDevice, "0");
                break;
            case R.id.root_view:
                DeviceInfoDictionary.showDetail(mContext, mDevice);
                break;
        }
    }

    //state:0.撤防，1.设防
    private void sendCmd(Device device, String state) {
        if (device.isOnLine()) {
            final JSONObject object = new JSONObject();
            object.put("cmd", "502");
            object.put("gwID", device.gwID);
            object.put("devID", device.devID);
            object.put("mode", 0);
            object.put("endpointStatus", state);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    private void toast(String sss) {
        tv_toast.setText(sss);
        tv_toast.setVisibility(VISIBLE);
        tv_toast.removeCallbacks(toastRun);
        tv_toast.postDelayed(toastRun, 2000);
    }

    private void toast(@StringRes int sss) {
        tv_toast.setText(sss);
        tv_toast.setVisibility(VISIBLE);
        tv_toast.removeCallbacks(toastRun);
        tv_toast.postDelayed(toastRun, 2000);
    }

    private Runnable toastRun = new Runnable() {
        @Override
        public void run() {
            tv_toast.setVisibility(INVISIBLE);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.deviceInfoBean.mode == 2) {
                    setRoomName();
                    setName();
                }
                if (event.deviceInfoBean.mode == 0) {
                    setState();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setState();
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
