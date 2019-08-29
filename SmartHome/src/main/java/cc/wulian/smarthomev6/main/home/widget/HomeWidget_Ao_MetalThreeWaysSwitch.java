package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
import cc.wulian.smarthomev6.main.device.device_Ao.DevAoForChooseBindActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceAoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceAoExtDataBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.ProgressRing;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;

/**
 * Created by hxc on 2017/7/19.
 * Func:   金属三路开关首页widget
 * Email:  hxc242313@qq.com
 */

public class HomeWidget_Ao_MetalThreeWaysSwitch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private static final String QUERY = "query";
    private Context mContext;
    private TextView tvToast;
    private TextView textName, textArea, textState;
    private TextView tvOneSwitch, tvTwoSwitch, tvThreeSwitch;
    private ImageView ivOneSwitch, ivTwoSwitch, ivThreeSwitch;
    private ProgressRing prOneSwitch,prTwoSwitch,prThreeSwitch;
    private RelativeLayout title;
    private Device mDevice;
    private HomeItemBean mHomeItem;
    private int mode;
    private SharedPreferences sp;
    private DeviceAoBean deviceAoBean = null;
    private DeviceAoExtDataBean switch1Bean;
    private DeviceAoExtDataBean switch2Bean;
    private DeviceAoExtDataBean switch3Bean;

    public HomeWidget_Ao_MetalThreeWaysSwitch(@NonNull Context context) {
        super(context);
        mContext = context;
        deviceAoBean = new DeviceAoBean();
        deviceAoBean.initExtDataBean("Ao");
        initView(context);
        initListener();
    }

    public HomeWidget_Ao_MetalThreeWaysSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
        initListener();
    }

    @Subscribe
    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItem = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        String data = mDevice.data;
        updateMode();
        setName();
        setRoomName();
        sendCmd(0x8010, 0);
    }

    @Subscribe
    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_ao_metal_three_ways_switch, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        title = (RelativeLayout) rootView.findViewById(R.id.widget_relative_title);
        tvToast = (TextView) rootView.findViewById(R.id.text_toast);

        ivOneSwitch = (ImageView) rootView.findViewById(R.id.one_switch_image);
        ivTwoSwitch = (ImageView) rootView.findViewById(R.id.two_switch_image);
        ivThreeSwitch = (ImageView) rootView.findViewById(R.id.three_switch_image);

        prOneSwitch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);
        prTwoSwitch = (ProgressRing) rootView.findViewById(R.id.two_switch_progress);
        prThreeSwitch = (ProgressRing) rootView.findViewById(R.id.three_switch_progress);

        tvOneSwitch = (TextView) rootView.findViewById(R.id.tv_one_switch_name);
        tvTwoSwitch = (TextView) rootView.findViewById(R.id.tv_two_switch_name);
        tvThreeSwitch = (TextView) rootView.findViewById(R.id.tv_three_switch_name);
        tvOneSwitch.setText(getResources().getString(R.string.Home_Widget_Switch) + 1);
        tvTwoSwitch.setText(getResources().getString(R.string.Home_Widget_Switch) + 2);
        tvThreeSwitch.setText(getResources().getString(R.string.Home_Widget_Switch) + 3);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);
//        rootView.findViewById(R.id.root_view).setOnClickListener(this);

    }

    private void initListener() {
        ivOneSwitch.setOnClickListener(this);
        ivTwoSwitch.setOnClickListener(this);
        ivThreeSwitch.setOnClickListener(this);
    }

    private void setName() {
        textName.setText(DeviceInfoDictionary.getNameByDevice(mDevice));
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        textArea.setText(areaName);
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
                textState.setText(R.string.Device_Online);
                textState.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 2:
                // 离线
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
                break;
            case 3:
                break;
        }
    }

    private void sendCmd(int commandId, int endpointNumber) {
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

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 不同功能按键的点击事件
     * 注：作为开关是发送对应的控制按键，作为空绑定场景和空绑定设备时跳到对应的h5界面，其余情况没有响应
     *
     * @param bean
     * @param number
     */
    private void sendCmdBySwitchData(final DeviceAoExtDataBean bean, final int number) {
        sp = mContext.getSharedPreferences("hxc", Context.MODE_PRIVATE);

        if ((bean != null) && TextUtils.equals("1", bean.mode)) {
//            if (bean.attributesId == 0x0000) {
                if (bean.attributesValue.equals("0")||bean.attributesValue.equals("")||bean.attributesValue.endsWith("01")) {
                    sendCmd(0x01, number);//打开控制命令
                } else if (bean.attributesValue.equals("1")) {
                    sendCmd(0x00, number);//关闭控制命令
                }
                startProgressAnimation(bean,number);
//            }
        } else if ((bean != null) && TextUtils.equals("2", bean.mode)) {
            if (TextUtils.isEmpty(bean.bindDevID)) {//空绑定设备
                Intent intent = new Intent();
                intent.setClass(this.mContext, DevAoForChooseBindActivity.class);
                intent.putExtra("gwID", mDevice.gwID);
                intent.putExtra("devID", mDevice.devID);
                intent.putExtra("epNum", bean.endpointNumber + "");
                intent.putExtra("chooseMode", 1);
                this.mContext.startActivity(intent);
            }
        } else if ((bean != null) && TextUtils.equals("3", bean.mode)) {
            if (TextUtils.isEmpty(bean.sceneIcon)) {//空绑定场景
                Intent intent = new Intent();
                intent.setClass(this.mContext, DevAoForChooseBindActivity.class);
                intent.putExtra("gwID", mDevice.gwID);
                intent.putExtra("devID", mDevice.devID);
                intent.putExtra("epNum", bean.endpointNumber + "");
                intent.putExtra("chooseMode", 2);
                this.mContext.startActivity(intent);
            }
        }
    }

    private void startProgressAnimation(final DeviceAoExtDataBean bean,int number){
        switch (number){
            case 1:
                prOneSwitch.setTimeout(5000);
                prOneSwitch.setState(ProgressRing.WAITING);
                prOneSwitch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        if (TextUtils.equals("0", bean.attributesValue)) {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                    tvOneSwitch.getText()));
                        } else if(TextUtils.equals("1", bean.attributesValue)){
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                    tvOneSwitch.getText()));
                        }
                        ivOneSwitch.setEnabled(true);
                    }
                    @Override
                    public void onEnd() {
                        ivOneSwitch.setEnabled(true);
                    }
                });
                break;
            case 2:
                prTwoSwitch.setTimeout(5000);
                prTwoSwitch.setState(ProgressRing.WAITING);
                prTwoSwitch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        if (TextUtils.equals("0", bean.attributesValue)) {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                    tvTwoSwitch.getText()));
                        } else if(TextUtils.equals("1", bean.attributesValue)){
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                    tvTwoSwitch.getText()));
                        }
                        ivTwoSwitch.setEnabled(true);
                    }
                    @Override
                    public void onEnd() {
                        ivTwoSwitch.setEnabled(true);
                    }
                });
                break;
            case 3:
                prThreeSwitch.setTimeout(5000);
                prThreeSwitch.setState(ProgressRing.WAITING);
                prThreeSwitch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        if (TextUtils.equals("0", bean.attributesValue)) {
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Open_Overtime),
                                    tvThreeSwitch.getText()));
                        } else if(TextUtils.equals("1", bean.attributesValue)){
                            toast(String.format(mContext.getString(R.string.Metalsingleway_Close_Overtime),
                                    tvThreeSwitch.getText()));
                        }
                        ivThreeSwitch.setEnabled(true);
                    }
                    @Override
                    public void onEnd() {
                        ivThreeSwitch.setEnabled(true);
                    }
                });
                break;
        }

    }

    private synchronized void parseJson(String json) {
        try {
            JSONObject jsonobject = new JSONObject(json);
            JSONArray endpoints = jsonobject.getJSONArray("endpoints");
            JSONArray extDatas = null;
            if (jsonobject.has("extData")) {
                String base64Datas = jsonobject.getString("extData");
                byte[] decodeMsgContent = Base64.decode(base64Datas, Base64.NO_WRAP);
                String strDatas = new String(decodeMsgContent);
                //此处因网关有时候返回数组，有时候返回对象导致解析失败，需网关改正后
                if (strDatas.contains("[") && strDatas.contains("]")) {
                    extDatas = new JSONArray(strDatas);
                }
            }

            for (int i = 0; i < endpoints.length(); i++) {
                JSONObject epJson = endpoints.getJSONObject(i);
                int endpointNumber = epJson.getInt("endpointNumber");
                int endpointType = epJson.getInt("endpointType");
                String endpointName = epJson.getString("endpointName");
                JSONArray clusters = epJson.getJSONArray("clusters");
                JSONObject cluData = clusters.getJSONObject(0);
                JSONArray attr = cluData.getJSONArray("attributes");
                JSONObject attrData = attr.getJSONObject(0);
                int attributesId = attrData.getInt("attributeId");
                String attributesValue = attrData.getString("attributeValue");
                if (endpointType < 0 || attributesValue == "F") {
                    continue;
                }
                JSONObject extJson = deviceAoBean.getExtData(extDatas, endpointNumber);
                DeviceAoExtDataBean extBean = deviceAoBean.getExtBeanByEpNum(endpointNumber);
                extBean.endPointType = endpointType;
                if (extJson != null) {//todo 根据不同的mode来解析数据
                    extBean.bindDevID = extJson.optString("bindDevID");
                    extBean.bindDevtype = extJson.optString("bindDevtype");
                    extBean.endpointNumber = extJson.optInt("endpointNumber");
                    extBean.endpointName = endpointName;
                    extBean.epName = extJson.optString("epName");
                    extBean.mode = extJson.optString("mode");
                    extBean.name = extJson.optString("name");
                    extBean.sceneID = extJson.optString("sceneID");
                    extBean.sceneIcon = extJson.optString("sceneIcon");
                    extBean.sceneName = extJson.optString("sceneName");
                } else {
                    continue;
                }
                extBean.attributesId = attributesId;
                extBean.attributesValue = attributesValue;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateUI();//根据解析的json刷新UI
    }

    private void updateUI() {
        switch1Bean = deviceAoBean.getExtBeanByEpNum(1);
        switch2Bean = deviceAoBean.getExtBeanByEpNum(2);
        switch3Bean = deviceAoBean.getExtBeanByEpNum(3);
        for (int i = 0; i < deviceAoBean.extDataBeanList.size(); i++) {
            judgeMode(deviceAoBean.extDataBeanList.get(i));
        }
    }

    /**
     * 判断每一个键的功能，并且设置对应的Icon
     *
     * @param bean
     */
    private void judgeMode(DeviceAoExtDataBean bean) {
        switch (bean.endPointType) {
            case 0x0002://开关功能
                setSwitchIcon(bean);
                break;
            case 0x0103://绑定功能
                setBindDeviceIcon(bean);
                break;
            case 0x0004://场景功能
                setSceneIcon(bean);
                break;
        }
    }

    /**
     * 设置开关的状态Icon
     *
     * @param bean
     */
    private void setSwitchIcon(DeviceAoExtDataBean bean) {
        switch (bean.endpointNumber) {
            case 1:
                prOneSwitch.setState(ProgressRing.FINISHED);
                if (mDevice.isOnLine()) {
                    if (bean.attributesValue.equals("0") || bean.attributesValue.equals("")||bean.attributesValue.endsWith("01")) {//关闭
                        ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivOneSwitch.setImageResource(R.drawable.switch_off);
                    } else if (bean.attributesValue.equals("1")) {//打开
                        ivOneSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        ivOneSwitch.setImageResource(R.drawable.switch_on);
                    }
                } else {
                    ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    ivOneSwitch.setImageResource(R.drawable.switch_off);
                }
                if (TextUtils.isEmpty(bean.endpointName)) {
                    tvOneSwitch.setText(mContext.getString(R.string.Home_Widget_Switch) + "1");
                } else {
                    tvOneSwitch.setText(bean.endpointName);
                }
                break;
            case 2:
                prTwoSwitch.setState(ProgressRing.FINISHED);
                if (mDevice.isOnLine()) {
                    if (bean.attributesValue.equals("0") || bean.attributesValue.equals("")||bean.attributesValue.endsWith("01")) {
                        ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivTwoSwitch.setImageResource(R.drawable.switch_off);
                    } else if (bean.attributesValue.equals("1")) {
                        ivTwoSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        ivTwoSwitch.setImageResource(R.drawable.switch_on);
                    }
                } else {
                    ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    ivTwoSwitch.setImageResource(R.drawable.switch_off);
                }
                if (TextUtils.isEmpty(bean.endpointName)) {
                    tvTwoSwitch.setText(mContext.getString(R.string.Home_Widget_Switch) + "2");
                } else {
                    tvTwoSwitch.setText(bean.endpointName);
                }
                break;
            case 3:
                prThreeSwitch.setState(ProgressRing.FINISHED);
                if (mDevice.isOnLine()) {
                    if (bean.attributesValue.equals("0") || bean.attributesValue.equals("")||bean.attributesValue.endsWith("01")) {
                        ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivThreeSwitch.setImageResource(R.drawable.switch_off);
                    } else if (bean.attributesValue.equals("1")) {
                        ivThreeSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        ivThreeSwitch.setImageResource(R.drawable.switch_on);
                    }
                } else {
                    ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    ivThreeSwitch.setImageResource(R.drawable.switch_off);
                }
                if (TextUtils.isEmpty(bean.endpointName)) {
                    tvThreeSwitch.setText(mContext.getString(R.string.Home_Widget_Switch) + "3");
                } else {
                    tvThreeSwitch.setText(bean.endpointName);
                }
                break;
        }

    }

    /**
     * 设置绑定设备的Icon
     *
     * @param bean
     */
    private void setBindDeviceIcon(DeviceAoExtDataBean bean) {
        switch (bean.endpointNumber) {
            case 1:
                if (TextUtils.isEmpty(bean.bindDevID)) {
                    ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    ivOneSwitch.setImageResource(R.drawable.icon_ao_add);
                    tvOneSwitch.setText(getResources().getString(R.string.Home_Widget_Unboundswitch));
                } else {
                    if (mDevice.isOnLine()) {
                        ivOneSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        ivOneSwitch.setImageResource(selectBindDeviceIcon(bean));
                    } else {
                        ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivOneSwitch.setImageResource(selectBindDeviceIcon(bean));
                    }
                    tvOneSwitch.setText(DeviceInfoDictionary.getNameByTypeAndName(bean.bindDevtype, bean.name));
                }
                break;
            case 2:
                if (TextUtils.isEmpty(bean.bindDevID)) {
                    ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    ivTwoSwitch.setImageResource(R.drawable.icon_ao_add);
                    tvTwoSwitch.setText(getResources().getString(R.string.Home_Widget_Unboundswitch));
                } else {
                    if (mDevice.isOnLine()) {
                        ivTwoSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        ivTwoSwitch.setImageResource(selectBindDeviceIcon(bean));
                    } else {
                        ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivTwoSwitch.setImageResource(selectBindDeviceIcon(bean));
                    }
                    tvTwoSwitch.setText(DeviceInfoDictionary.getNameByTypeAndName(bean.bindDevtype, bean.name));
                }
                break;
            case 3:
                if (TextUtils.isEmpty(bean.bindDevID)) {
                    ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                    ivThreeSwitch.setImageResource(R.drawable.icon_ao_add);
                    tvThreeSwitch.setText(getResources().getString(R.string.Home_Widget_Unboundswitch));
                } else {
                    if (mDevice.isOnLine()) {
                        ivThreeSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        ivThreeSwitch.setImageResource(selectBindDeviceIcon(bean));
                    } else {
                        ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivThreeSwitch.setImageResource(selectBindDeviceIcon(bean));
                    }
                    tvThreeSwitch.setText(DeviceInfoDictionary.getNameByTypeAndName(bean.bindDevtype, bean.name));
                }
                break;
        }

    }

    /**
     * 设置场景功能按键的Icon
     *
     * @param bean
     */
    private void setSceneIcon(DeviceAoExtDataBean bean) {
        Bitmap bitmap;
        switch (bean.endpointNumber) {
            case 1:
                if (mDevice.isOnLine()) {
                    if (!TextUtils.isEmpty(bean.sceneIcon)) {
                        ivOneSwitch.setImageDrawable(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        ivOneSwitch.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff8dd652));
                        ivOneSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        tvOneSwitch.setText(bean.sceneName);
                    } else {
                        ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivOneSwitch.setImageResource(R.drawable.icon_ao_add);
                        tvOneSwitch.setText(R.string.Device_More_Bind_Scene_Default);
                    }
                } else {
                    if (!TextUtils.isEmpty(bean.sceneIcon)) {
                        ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivOneSwitch.setImageDrawable(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        tvOneSwitch.setText(bean.sceneName);
                    } else {
                        ivOneSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivOneSwitch.setImageResource(R.drawable.icon_ao_add);
                        tvOneSwitch.setText(R.string.Device_More_Bind_Scene_Default);
                    }
                }
                break;
            case 2:
                if (mDevice.isOnLine()) {
                    if (!TextUtils.isEmpty(bean.sceneIcon)) {
                        ivTwoSwitch.setImageDrawable(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        ivTwoSwitch.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff8dd652));
                        ivTwoSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        tvTwoSwitch.setText(bean.sceneName);
                    } else {
                        ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivTwoSwitch.setImageResource(R.drawable.icon_ao_add);
                        tvTwoSwitch.setText(R.string.Device_More_Bind_Scene_Default);
                    }
                } else {
                    if (!TextUtils.isEmpty(bean.sceneIcon)) {
                        ivTwoSwitch.setImageDrawable(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        tvTwoSwitch.setText(bean.sceneName);
                    } else {
                        ivTwoSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivTwoSwitch.setImageResource(R.drawable.icon_ao_add);
                        tvTwoSwitch.setText(R.string.Device_More_Bind_Scene_Default);
                    }
                }
                break;
            case 3:
                if (mDevice.isOnLine()) {
                    if (!TextUtils.isEmpty(bean.sceneIcon)) {
                        ivThreeSwitch.setImageDrawable(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        ivThreeSwitch.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff8dd652));
                        ivThreeSwitch.setBackgroundResource(R.drawable.ao_widget_circle_green);
                        tvThreeSwitch.setText(bean.sceneName);
                    } else {
                        ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivThreeSwitch.setImageResource(R.drawable.icon_ao_add);
                        tvThreeSwitch.setText(R.string.Device_More_Bind_Scene_Default);
                    }
                } else {
                    if (!TextUtils.isEmpty(bean.sceneIcon)) {
                        ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivThreeSwitch.setImageDrawable(SceneManager.getSceneIconNormal(mContext, bean.sceneIcon));
                        tvThreeSwitch.setText(bean.sceneName);
                    } else {
                        ivThreeSwitch.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        ivThreeSwitch.setImageResource(R.drawable.icon_ao_add);
                        tvThreeSwitch.setText(R.string.Device_More_Bind_Scene_Default);
                    }
                }
                break;
        }

    }


    /**
     * 选择已绑定设备的Icon(Am、An、Ar、Ao)
     *
     * @param bean
     * @return
     */
    private int selectBindDeviceIcon(DeviceAoExtDataBean bean) {
        int id = R.drawable.icon_device_unknown;
        switch (bean.bindDevtype) {
            case "Am":
                if (mDevice.isOnLine()) {
                    id = R.drawable.icon_switch_one;
                } else {
                    id = R.drawable.icon_switch_one_offline;
                }
                break;
            case "An":
                if (mDevice.isOnLine()) {
                    id = R.drawable.icon_switch_two;
                } else {
                    id = R.drawable.icon_switch_two_offline;
                }
                break;
            case "Ao":
                if (mDevice.isOnLine()) {
                    id = R.drawable.icon_switch_three;
                } else {
                    id = R.drawable.icon_switch_three_offline;
                }
                break;
            case "Ar":
                if (mDevice.isOnLine()) {
                    id = R.drawable.icon_metal_curtain;
                } else {
                    id = R.drawable.icon_metal_curtain_off;
                }
                break;
        }
        return id;
    }

    private void toast(String sss) {
        tvToast.setText(sss);
        tvToast.setVisibility(VISIBLE);
        tvToast.removeCallbacks(toastRun);
        tvToast.postDelayed(toastRun, 2000);
    }

    private Runnable toastRun = new Runnable() {
        @Override
        public void run() {
            tvToast.setVisibility(INVISIBLE);
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_switch_image:
                sendCmdBySwitchData(switch1Bean, 1);
                break;
            case R.id.two_switch_image:
                sendCmdBySwitchData(switch2Bean, 2);
                break;
            case R.id.three_switch_image:
                sendCmdBySwitchData(switch3Bean, 3);
                break;
            case R.id.root_view:
                DeviceInfoDictionary.showDetail(mContext, mDevice);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
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
                setName();
//                mDevice.mode = 2;
                if (mDevice.mode == 0 || mDevice.mode == 2) {
                    parseJson(mDevice.data);
                } else if (mDevice.mode == 1) {//此时不准确需要重新查询
                    sendCmd(0x8010, 0);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetSceneBindingEvent(SetSceneBindingEvent result) {
        if (result != null) {
            sendCmd(0x8010, 0);
        }
    }

}
