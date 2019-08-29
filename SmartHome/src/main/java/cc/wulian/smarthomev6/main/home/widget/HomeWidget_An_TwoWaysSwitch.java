package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.util.Base64;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_Ao.DevAoForChooseBindActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.customview.ProgressRing;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    HomeWidget_An_TwoWaysSwitch
 */

public class HomeWidget_An_TwoWaysSwitch extends FrameLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final int SWITCH_TYPE = 0x0006;
    private static final int BIND_TYPE = 0x0007;
    private static final int SCENE_TYPE = 0x0005;
    private static final String BIND_DEVICE = "bind_device";
    private static final String BIND_SCENE = "bind_scene";
    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private TextView tv_one_switch_name, tv_two_switch_name;
    private ImageView iv_one_switch, iv_two_switch;
    private ProgressRing pr_one_switch, pr_two_switch;
    private Device mDevice;
    private TextView tv_toast;
    private boolean isOpenCommand_oneSwitch = false;
    private boolean isOpenCommand_twoSwitch = false;

    public HomeWidget_An_TwoWaysSwitch(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_An_TwoWaysSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_an_two_ways_switch, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        tv_toast = (TextView) rootView.findViewById(R.id.text_toast);
        iv_one_switch = (ImageView) rootView.findViewById(R.id.one_switch_image);
        iv_two_switch = (ImageView) rootView.findViewById(R.id.two_switch_image);

        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);
        pr_two_switch = (ProgressRing) rootView.findViewById(R.id.two_switch_progress);

        tv_one_switch_name = (TextView) rootView.findViewById(R.id.one_switch_name);
        tv_two_switch_name = (TextView) rootView.findViewById(R.id.two_switch_name);
        tv_one_switch_name.setText(mContext.getString(R.string.Home_Widget_Switch) + "1");
        tv_two_switch_name.setText(mContext.getString(R.string.Home_Widget_Switch) + "2");
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);

        iv_one_switch.setOnClickListener(this);
        iv_two_switch.setOnClickListener(this);
        iv_one_switch.setEnabled(false);
        iv_two_switch.setEnabled(false);
//        rootView.findViewById(R.id.root_view).setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        // 设置标题
        setName();
        setRoomName();

        dealDevice(mDevice.data);
        JSONArray ja = new JSONArray();
        ja.put("0");
        sendCmd(2, 0, ja);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        tv_name.setText(DeviceInfoDictionary.getNameByDevice(mDevice));
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        tv_room.setText(areaName);
    }

    /**
     * 发出控制指令后, 更新状态
     */
    private void updateState(int mode, int endpointNumber, int type, String name, String iconType) {
        ImageView iv_icon_temp = null;
        TextView tv_name_temp = null;
        Object attributeValue = null;
        switch (endpointNumber){
            case 1:
                iv_icon_temp = iv_one_switch;
                tv_name_temp = tv_one_switch_name;
                attributeValue = iv_icon_temp.getTag();
                if(attributeValue != null && !attributeValue.equals(iconType)){
                    pr_one_switch.setState(ProgressRing.FINISHED);
                }
                break;
            case 2:
                iv_icon_temp = iv_two_switch;
                tv_name_temp = tv_two_switch_name;
                attributeValue = iv_icon_temp.getTag();
                if(attributeValue != null && !attributeValue.equals(iconType)){
                    pr_two_switch.setState(ProgressRing.FINISHED);
                }
                break;
        }

        if(mode == 2){
            //设置title离线状态
            tv_sate.setText(R.string.Device_Offline);
            tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);

            //设置按钮状态
            iv_icon_temp.setEnabled(false);
            BitmapDrawable bd = (BitmapDrawable) iv_icon_temp.getDrawable();
            Bitmap bitmap = BitmapUtil.changeBitmapColor(bd.getBitmap(), 0xffC9CFDB);
            iv_icon_temp.setImageBitmap(bitmap);
        }else {
            //设置title上线状态
            tv_sate.setText(R.string.Device_Online);
            tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));

            //设置按钮状态
            iv_icon_temp.setTag(iconType);
            switch (type){
                case SWITCH_TYPE:
                    switch (iconType) {
                        case "0":
                            iv_icon_temp.setImageResource(R.drawable.switch_off);
                            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
                            break;
                        case "1":
                            iv_icon_temp.setImageResource(R.drawable.switch_on);
                            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                            break;
                        default:
                            iv_icon_temp.setImageResource(R.drawable.switch_off);
                            iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
                            break;
                    }
                    if(TextUtils.isEmpty(name)){
                        tv_name_temp.setText(mContext.getString(R.string.Home_Widget_Switch) + endpointNumber);
                    }else{
                        setText(tv_name_temp, name);
                    }
                    iv_icon_temp.setEnabled(true);
                    break;
                case BIND_TYPE:
                    if(BIND_DEVICE.equals(iconType)){
                        iv_icon_temp.setEnabled(true);
                        iv_icon_temp.setImageResource(R.drawable.icon_switch_add);
                        iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        tv_name_temp.setText(mContext.getString(R.string.Home_Widget_Unboundswitch));
                    }else{
                        iv_icon_temp.setEnabled(false);
                        Device device = MainApplication.getApplication().getDeviceCache().get(iconType);
                        if(device != null && device.type != null){
                            setText(tv_name_temp, DeviceInfoDictionary.getNameByTypeAndName(device.type, name));
                            switch (device.type) {
                                case "Am":
                                    iv_icon_temp.setImageResource(R.drawable.icon_switch_one);
                                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                                    break;
                                case "An":
                                    iv_icon_temp.setImageResource(R.drawable.icon_switch_two);
                                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                                    break;
                                case "Ao":
                                    iv_icon_temp.setImageResource(R.drawable.icon_switch_three);
                                    iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                                    break;
                            }
                        }
                    }
                    break;
                case SCENE_TYPE:
                    if(BIND_SCENE.equals(iconType)){
                        iv_icon_temp.setEnabled(true);
                        iv_icon_temp.setImageResource(R.drawable.icon_switch_add);
                        iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        tv_name_temp.setText(mContext.getString(R.string.Device_More_Bind_Scene_Default));
                    }else{
                        iv_icon_temp.setEnabled(false);
                        try {
                            SceneBean scene = MainApplication.getApplication().getSceneCache().get(iconType);
                            if (scene != null) {
                                setText(tv_name_temp, scene.name);
                                BitmapDrawable bd = (BitmapDrawable) SceneManager.getSceneIconNormal(mContext, scene.icon);
                                Bitmap bitmap = BitmapUtil.changeBitmapColor(bd.getBitmap(), getResources().getColor(R.color.colorPrimary));
                                iv_icon_temp.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        iv_icon_temp.setBackgroundResource(R.drawable.home_widget_circle_green);
                    }
                    break;
            }
        }
    }

    private void setText(TextView textView, String text){
        if(text == null){
            return;
        }
        if(text.length() > 9){
            textView.setText(text.substring(0,9) + "...");
        }else{
            textView.setText(text);
        }
    }

    private void dealDevice(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray endpoints = object.getJSONArray("endpoints");
//            int mode = object.getInt("mode");
            for(int i=0; i < endpoints.length(); i++){
                JSONArray clusters = endpoints.getJSONObject(i).getJSONArray("clusters");
                JSONArray attributes = clusters.getJSONObject(0).getJSONArray("attributes");
                int clusterId = clusters.getJSONObject(0).getInt("clusterId");
                int endpointNumber = endpoints.getJSONObject(i).optInt("endpointNumber");
                String switchName = endpoints.getJSONObject(i).optString("endpointName");
                String iconType = "";
                int attributeId = attributes.getJSONObject(0).optInt("attributeId");
                if(attributeId == 0){
                    iconType = attributes.getJSONObject(0).optString("attributeValue");
                }else{
                    iconType = "0";
                }
                String data = object.optString("extData");
                if(!TextUtils.isEmpty(data)){
                    JSONArray extData = new JSONArray(new String(Base64.decodeFast(data)));
                    for(int j=0; j<extData.length(); j++){
                        if(endpointNumber == extData.getJSONObject(j).optInt("endpointNumber")){
                            if(clusterId == BIND_TYPE){
                                switchName = extData.getJSONObject(j).optString("name");
                                iconType = extData.getJSONObject(j).optString("bindDevID");
                                iconType = TextUtils.isEmpty(iconType) ? BIND_DEVICE : iconType;
                            }else if(clusterId == SCENE_TYPE){
                                switchName = extData.getJSONObject(j).optString("sceneName");
                                iconType = extData.getJSONObject(j).optString("sceneID");
                                iconType = TextUtils.isEmpty(iconType) ? BIND_SCENE : iconType;
                            }
                            break;
                        }
                    }
                }
                updateState(mDevice.mode, endpointNumber, clusterId, switchName, iconType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.deviceInfoBean.mode == 2) {
                    setRoomName();
                    setName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if(event.device.mode == 0 || event.device.mode == 2){
                    dealDevice(event.device.data);
                }else if(event.device.mode == 1){
                    JSONArray ja = new JSONArray();
                    ja.put("0");
                    sendCmd(2, 0, ja);
                }
            }
        }else{
            mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
            if (mDevice!=null) {
                dealDevice(mDevice.data);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_switch_image:
                Object one_type = iv_one_switch.getTag();
                if(BIND_DEVICE.equals(one_type)){
                    Intent one_Intent = new Intent();
                    one_Intent.setClass(this.mContext, DevAoForChooseBindActivity.class);
                    one_Intent.putExtra("gwID", mDevice.gwID);
                    one_Intent.putExtra("devID", mDevice.devID);
                    one_Intent.putExtra("epNum", "1");
                    one_Intent.putExtra("chooseMode", 1);
                    mContext.startActivity(one_Intent);
                }else if(BIND_SCENE.equals(one_type)){
                    Intent one_Intent = new Intent();
                    one_Intent.setClass(this.mContext, DevAoForChooseBindActivity.class);
                    one_Intent.putExtra("gwID", mDevice.gwID);
                    one_Intent.putExtra("devID", mDevice.devID);
                    one_Intent.putExtra("epNum", "1");
                    one_Intent.putExtra("chooseMode", 2);
                    mContext.startActivity(one_Intent);
                }else {
                    iv_one_switch.setEnabled(false);
                    if ("0".equals(one_type)) {
                        sendCmd(0x01, 1, null);//打开
                        isOpenCommand_oneSwitch = true;
                    } else {
                        sendCmd(0x00, 1, null);//关闭
                        isOpenCommand_oneSwitch = false;
                    }
                    pr_one_switch.setTimeout(5000);
                    pr_one_switch.setState(ProgressRing.WAITING);
                    pr_one_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                        @Override
                        public void onTimeout() {
                            if (isOpenCommand_oneSwitch) {
                                toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                        tv_one_switch_name.getText()));
                            } else {
                                toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                        tv_one_switch_name.getText()));
                            }
                            iv_one_switch.setEnabled(true);
                        }

                        @Override
                        public void onEnd() {
                            iv_one_switch.setEnabled(true);
                        }
                    });
                }
                break;
            case R.id.two_switch_image:
                Object two_type = iv_two_switch.getTag();
                if(BIND_DEVICE.equals(two_type)){
                    Intent one_Intent = new Intent();
                    one_Intent.setClass(this.mContext, DevAoForChooseBindActivity.class);
                    one_Intent.putExtra("gwID", mDevice.gwID);
                    one_Intent.putExtra("devID", mDevice.devID);
                    one_Intent.putExtra("epNum", "2");
                    one_Intent.putExtra("chooseMode", 1);
                    mContext.startActivity(one_Intent);
                }else if(BIND_SCENE.equals(two_type)){
                    Intent one_Intent = new Intent();
                    one_Intent.setClass(this.mContext, DevAoForChooseBindActivity.class);
                    one_Intent.putExtra("gwID", mDevice.gwID);
                    one_Intent.putExtra("devID", mDevice.devID);
                    one_Intent.putExtra("epNum", "2");
                    one_Intent.putExtra("chooseMode", 2);
                    mContext.startActivity(one_Intent);
                }else {
                    iv_two_switch.setEnabled(false);
                    if ("0".equals(two_type)) {
                        sendCmd(0x01, 2, null);//打开
                        isOpenCommand_twoSwitch = true;
                    } else {
                        sendCmd(0x00, 2, null);//关闭
                        isOpenCommand_twoSwitch = false;
                    }
                    pr_two_switch.setTimeout(5000);
                    pr_two_switch.setState(ProgressRing.WAITING);
                    pr_two_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                        @Override
                        public void onTimeout() {
                            if (isOpenCommand_twoSwitch) {
                                toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                        tv_two_switch_name.getText()));
                            } else {
                                toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                        tv_two_switch_name.getText()));
                            }
                            iv_two_switch.setEnabled(true);
                        }

                        @Override
                        public void onEnd() {
                            iv_two_switch.setEnabled(true);
                        }
                    });
                }
                break;
            case R.id.root_view:
                DeviceInfoDictionary.showDetail(mContext, mDevice);
                break;
        }
    }

    private void sendCmd(int commandId, int endpointNumber, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", endpointNumber);
            if(parameter != null){
                object.put("parameter", parameter);
            }else{
                object.put("clusterId", SWITCH_TYPE);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onSetSceneBindingEvent(SetSceneBindingEvent result) {
        if (result != null) {
            JSONArray ja = new JSONArray();
            ja.put("0");
            sendCmd(2, 0, ja);
        }
    }
}
