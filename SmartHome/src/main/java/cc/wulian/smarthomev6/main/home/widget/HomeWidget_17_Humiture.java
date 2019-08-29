package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.HumitureText;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;

/**
 * Created by Veev on 2017/5/8
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 温湿度 首页widget
 */

public class HomeWidget_17_Humiture extends RelativeLayout implements IWidgetLifeCycle {

    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textArea, textState, textTem, textHum, textTemAppend, textHumAppend;

//    private HumitureText humitureText;
    private TextView mTextHumitureState;
    private RelativeLayout title;

    private int mode;

    public HomeWidget_17_Humiture(Context context) {
        super(context);
        initView(context);
    }

    public HomeWidget_17_Humiture(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);

        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        mode = mDevice.mode;
        // 设置在线离线的状态
        updateMode();

        dealDevice(mDevice);

        setName();
        setRoomName();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_17_humiture, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));//DisplayUtil.dip2Pix(context, 300)));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textTem = (TextView) rootView.findViewById(R.id.humiture_text_tem);
        textHum = (TextView) rootView.findViewById(R.id.humiture_text_hum);
        textHumAppend = (TextView) rootView.findViewById(R.id.humiture_text_hum_append);
        textTemAppend = (TextView) rootView.findViewById(R.id.humiture_text_tem_append);
//        humitureText = (HumitureText) rootView.findViewById(R.id.humiture_humiture);
        mTextHumitureState = (TextView) rootView.findViewById(R.id.humiture_text_state);

        if (LanguageUtil.isEnglish()) {
            mTextHumitureState.setTextSize(mTextHumitureState.getTextSize() * 0.3f);
        }

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);
    }

    /**
     * 收到数据后, 更新状态
     */
    private void updateState(String temp, String hum) {
        float tempF = Float.parseFloat(temp);
        float humF = Float.parseFloat(hum);
        textTem.setText(temp);
        textHum.setText(hum);

        String typeRes = getContext().getString(R.string.Home_Widget_Data_Offline);
        int color = getResources().getColor(R.color.home_widget_humiture_ss);
        if(humF >= 80 && humF <= 100 && tempF <= 18){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_SL);
            color = getResources().getColor(R.color.home_widget_humiture_hl);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_hl);
        }else if(humF >= 30 && humF < 80 && tempF >= 0 && tempF <= 18){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_PL);
            color = getResources().getColor(R.color.home_widget_humiture_hl);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_hl);
        }else if(humF >= 30 && humF < 80 && tempF < 0){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_HL);
            color = getResources().getColor(R.color.home_widget_humiture_hl);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_hl);
        }else if(humF >= 0 && humF < 30 && tempF <= 18){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_GL);
            color = getResources().getColor(R.color.home_widget_humiture_hl);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_hl);
        }else if(humF >= 80 && humF <= 100 && tempF > 18 && tempF <= 27){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_CS);
            color = getResources().getColor(R.color.home_widget_humiture_hl);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_hl);
        }else if(humF >= 30 && humF < 80 && tempF > 18 && tempF <= 27){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_SS);
            color = getResources().getColor(R.color.home_widget_humiture_ss);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_ss);
        }else if(humF >= 0 && humF < 30 && tempF > 18 && tempF <= 27){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_GZ);
            color = getResources().getColor(R.color.home_widget_humiture_gz);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_gz);
        }else if(humF >= 80 && humF <= 100 && tempF > 27 && tempF <= 60){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_SR);
            color = getResources().getColor(R.color.home_widget_humiture_gz);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_gz);
        }else if(humF >= 30 && humF < 80 && tempF > 27 && tempF <= 40){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_PR);
            color = getResources().getColor(R.color.home_widget_humiture_gz);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_gz);
        }else if(humF >= 30 && humF < 80 && tempF > 40 && tempF <= 60){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_YR);
            color = getResources().getColor(R.color.home_widget_humiture_gz);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_gz);
        }else if(humF >= 0 && humF < 30 && tempF > 27){
            typeRes = getContext().getString(R.string.Home_Widget_Humiture_GR);
            color = getResources().getColor(R.color.home_widget_humiture_gz);
            mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_gz);
        }

//        humitureText.setText(typeRes);
//        humitureText.setAllColor(color);

        typeRes = getContext().getString(R.string.Home_Widget_Humiture_SS);
        mTextHumitureState.setText(typeRes);
        mTextHumitureState.setTextColor(color);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                textState.setText(R.string.Device_Online);
                textState.setTextColor(getResources().getColor(R.color.colorPrimary));
                textTemAppend.setVisibility(VISIBLE);
                textHumAppend.setVisibility(VISIBLE);
                break;
            case 2:
                // 离线
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
                textTemAppend.setVisibility(INVISIBLE);
                textHumAppend.setVisibility(INVISIBLE);
                textTem.setText(R.string.Home_Widget_Data_Offline);
                textHum.setText(R.string.Home_Widget_Data_Offline);
//                humitureText.setText(R.string.Home_Widget_Data_Offline);
//                humitureText.setAllColor(getResources().getColor(R.color.v6_text_secondary));
                mTextHumitureState.setText(R.string.Home_Widget_Data_Offline);
                mTextHumitureState.setTextColor(getResources().getColor(R.color.newSecondaryText));
                mTextHumitureState.setBackgroundResource(R.drawable.shape_widget_17_nn);
                break;
            case 3:
                break;
        }
    }

    /**
     * 解析设备数据
     */
    private void dealDevice(Device device) {
        mode = device.mode;
        updateMode();
        if (device.mode == 3) {
            // 设备删除
        } else if (device.mode == 2) {
            // 设备离线
        } else if (device.mode == 1) {
            // 更新上线
            dealData(device.data);
        } else if (device.mode == 0) {
            dealData(device.data);
        }
    }

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.getJSONArray("endpoints");
            String temp, hum;
            int endpointNumber0 = ((JSONObject)endpoints.get(0)).getInt("endpointNumber");
            int endpointNumber1 = ((JSONObject)endpoints.get(1)).getInt("endpointNumber");
            JSONArray clusters0 = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
            JSONArray clusters1 = ((JSONObject) endpoints.get(1)).getJSONArray("clusters");
            JSONArray attributes0 = ((JSONObject) clusters0.get(0)).getJSONArray("attributes");
            JSONArray attributes1 = ((JSONObject) clusters1.get(0)).getJSONArray("attributes");
            if (endpointNumber0 == 1) {
                temp = ((JSONObject) attributes0.get(0)).optString("attributeValue");
                hum = ((JSONObject) attributes1.get(0)).optString("attributeValue");
            } else {
                hum = ((JSONObject) attributes0.get(0)).optString("attributeValue");
                temp = ((JSONObject) attributes1.get(0)).optString("attributeValue");
            }

            if (TextUtils.isEmpty(temp)) {
                temp = "26";
            }
            if (TextUtils.isEmpty(hum)) {
                hum = "45";
            }

            // 更新状态
            updateState(temp, hum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                dealDevice(event.device);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, event.deviceInfoBean.name));
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
}
