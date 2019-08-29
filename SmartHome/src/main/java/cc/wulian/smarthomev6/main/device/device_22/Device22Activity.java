package cc.wulian.smarthomev6.main.device.device_22;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.adapter.Device22Adapter;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22DetailBean;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class Device22Activity extends BaseTitleActivity {

    public static final String TYPE_CUSTOM = "0";
    public static final String TYPE_TV = "1";
    public static final String TYPE_AC = "2";
    private String deviceId;
    private boolean isSceneOrHouseKeeper;
    private Device device;
    private List<Device22DetailBean> remotes;

    private ImageView emptyImageView;
    private TextView emptyTextView;
    private RecyclerView remoteList;
    private View addView;
    private ConstraintLayout constraintLayout;
    private RelativeLayout offlineLayour;
    private LinearLayout addRemoteView;
    private PopupWindow addRemotePopWindow;

    private Device22Adapter mAdapter;

    public static void start(Context context, String deviceId, boolean isSceneOrHouseKeeper) {
        Intent intent = new Intent(context, Device22Activity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("isSceneOrHouseKeeper", isSceneOrHouseKeeper);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_device22, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(addView, SkinResouceKey.BITMAP_BUTTON_BG_S);
    }

    @Override
    protected void initTitle() {
        deviceId = getIntent().getStringExtra("deviceId");
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(device), R.drawable.icon_more);
    }

    @Override
    protected void initView() {
        emptyImageView = (ImageView) findViewById(R.id.device_22_image_empty);
        emptyTextView = (TextView) findViewById(R.id.device_22_text_empty);
        remoteList = (RecyclerView) findViewById(R.id.device_22_recycler);
        addView = findViewById(R.id.device_22_add);
        offlineLayour = (RelativeLayout) findViewById(R.id.offline_relative);
        constraintLayout = (ConstraintLayout) findViewById(R.id.cl_content);

        addRemoteView = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.pop_add_remote, null);

        if (isSceneOrHouseKeeper){
            addView.setVisibility(View.GONE);
        }
        initPopwindow();
    }

    @Override
    protected void initData() {
        mAdapter = new Device22Adapter(deviceId);
        remoteList.setAdapter(mAdapter);
        remoteList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });

        if (!mainApplication.getDevice22KeyCodeCache().isRemoteCache(deviceId)){
            JSONObject jsonMsgContent = new JSONObject();
            try {
                jsonMsgContent.put("cmd", "516");
                jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
                jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
                jsonMsgContent.put("devID", deviceId);//设备id
                jsonMsgContent.put("operType", 4);//模式
                jsonMsgContent.put("mode", 1);//模式
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                    jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
            );
        }


        update();
        updateState();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        addView.setOnClickListener(this);
        mAdapter.setOnItemClickListener(new Device22Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Device22DetailBean bean) {
                String type = bean.type;
                switch (type) {
                    case TYPE_TV:
                        if (isSceneOrHouseKeeper){
                            TVRemoteActivity.startForScene(Device22Activity.this, deviceId, bean.name,
                                    bean.index);
                        }else {
                            TVRemoteActivity.start(Device22Activity.this, deviceId, bean.name,
                                    bean.index);
                        }

                        break;
                    case TYPE_AC:
                        if (isSceneOrHouseKeeper){
                            CustomRemoteActivity.startForScene(Device22Activity.this, deviceId, bean.name,
                                    type,
                                    bean.index);
                        }else {
                            CustomRemoteActivity.start(Device22Activity.this, deviceId, bean.name,
                                    type,
                                    bean.index);
                        }
                        break;
                    case TYPE_CUSTOM:
                        if (isSceneOrHouseKeeper){
                            CustomRemoteActivity.startForScene(Device22Activity.this, deviceId, bean.name,
                                    type,
                                    bean.index);
                        }else {
                            CustomRemoteActivity.start(Device22Activity.this, deviceId, bean.name,
                                    type,
                                    bean.index);
                        }
                        break;
                }
            }
        });
    }

    private void updateState() {
        addView.setEnabled(device.isOnLine());
        offlineLayour.setVisibility(device.isOnLine() ? View.GONE : View.VISIBLE);
    }

    private void update() {
        remotes = MainApplication.getApplication().getDevice22KeyCodeCache().getRemotesByDeviceId(deviceId);

        if (remotes == null || remotes.size() == 0) {
            mAdapter.clear();
            remoteList.setVisibility(View.INVISIBLE);
            emptyImageView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
            return;
        } else {
            remoteList.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
            mAdapter.setData(remotes);
        }
    }

    private void startMore() {
        JSONObject json = new JSONObject();
        try {
            json.put("deviceID", deviceId);
            JSONArray data = new JSONArray();

            JSONObject logJson = new JSONObject();
            logJson.put("group", "log");
            logJson.put("enableWithEnterType", "account");
            JSONArray param = new JSONArray();
            JSONObject deviceIdJson = new JSONObject();
            deviceIdJson.put("key", "deviceID");
            deviceIdJson.put("type", "string");
            deviceIdJson.put("value", deviceId);
            param.put(deviceIdJson);
            logJson.put("param", param);
            JSONArray item = new JSONArray();
            JSONObject alarmItem = new JSONObject();
            alarmItem.put("type", "jump");
            alarmItem.put("name", getString(R.string.Message_Center_AlarmMessage));
            alarmItem.put("action", "jump:Alarm");
            JSONObject logItem = new JSONObject();
            logItem.put("type", "jump");
            logItem.put("name", getString(R.string.Message_Center_Log));
            logItem.put("action", "jump:Log");
            item.put(alarmItem).put(logItem);
            logJson.put("item", item);

            JSONObject controllerJson = new JSONObject();
            controllerJson.put("group", "controller");
            controllerJson.put("enableWithEnterType", "account");
            controllerJson.put("offLineDisable", true);
            controllerJson.put("param", param);
            JSONArray item2 = new JSONArray();
            JSONObject deleteItem = new JSONObject();
            deleteItem.put("type", "custom");
            deleteItem.put("name", getString(R.string.Infraredrelay_More_Emptylibrary));
            deleteItem.put("action", "custom:22_Clear_Controller_Storage");
            item2.put(deleteItem);
            controllerJson.put("item", item2);

//            data.put(logJson);//.put(controllerJson);
            data.put(controllerJson);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, DeviceMoreActivity.class);
        intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceId);
        intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, json.toString());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                startMore();
                break;
            case R.id.device_22_add:
//                if (mAdapter.getItemCount() >= 20) {
//                    ToastUtil.single(R.string.Infraredtransponder_Upper_Limit);
//                } else {
                    showAddPopwindow(constraintLayout);
//                }
                break;
            default:
                break;
        }
    }

    private void showAddPopwindow(View view) {
        if (addRemotePopWindow == null) {
            initPopwindow();
        }
        addRemotePopWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        addRemotePopWindow.setFocusable(true);
        addRemotePopWindow.update();
    }

    private void initPopwindow() {
        addRemotePopWindow = new PopupWindow(this);
        // 指定popupWindow的宽和高
        addRemotePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        addRemotePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        addRemotePopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        addRemotePopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popwindow_add_remote_bg));
        addRemotePopWindow.setAnimationStyle(R.style.popwin_anim_style);
        addRemotePopWindow.setContentView(addRemoteView);

        addRemoteView.findViewById(R.id.iv_add_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device22AddActivity.start(Device22Activity.this, deviceId, TYPE_TV);
                if (addRemotePopWindow != null && addRemotePopWindow.isShowing()) {
                    addRemotePopWindow.dismiss();
                }
            }
        });
        addRemoteView.findViewById(R.id.iv_add_ac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device22AddActivity.start(Device22Activity.this, deviceId, TYPE_AC);
                if (addRemotePopWindow != null && addRemotePopWindow.isShowing()) {
                    addRemotePopWindow.dismiss();
                }
            }
        });
        addRemoteView.findViewById(R.id.iv_add_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device22AddActivity.start(Device22Activity.this, deviceId, TYPE_CUSTOM);
                if (addRemotePopWindow != null && addRemotePopWindow.isShowing()) {
                    addRemotePopWindow.dismiss();
                }
            }
        });
        addRemoteView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addRemotePopWindow != null && addRemotePopWindow.isShowing()) {
                    addRemotePopWindow.dismiss();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Device22ConfigEvent event) {
        WLog.d(TAG, "Device22ConfigEvent: " + event.data);
        if (!TextUtils.equals(event.deviceId, deviceId)) {
            return;
        }
        if (event.mode == 1){
            update();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceId)) {
            device = MainApplication.getApplication().getDeviceCache().get(deviceId);
            setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(device), R.drawable.icon_more);
            updateState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceInfoChangedEvent event) {
        if (event != null && TextUtils.equals(event.deviceInfoBean.devID, deviceId)) {
            device = MainApplication.getApplication().getDeviceCache().get(deviceId);
            setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(device), R.drawable.icon_more);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            finish();
        }
    }
}
