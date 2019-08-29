package cc.wulian.smarthomev6.main.device;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.MoreConfigAdapter;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.event.DeleteDeviceEvent;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class DeviceMoreActivity extends BaseTitleActivity {

    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_MORE_CONFIG = "key_more_config";

    private RelativeLayout relativeRename, relativeReSetRoom, relativeInfo, relativeFind, relativeSpecification;
    private TextView textRenameName, textResetRoomName;
    private RecyclerView mRecyclerView;
    private Button btnDelete;

    protected WLDialog.Builder builder;
    protected WLDialog dialog;

    protected String deviceId, configStr;
    protected Device device;

    private List<MoreConfig.ItemBean> list = new ArrayList<>();

    private MoreConfigAdapter mRecyclerAdapter;

    private boolean isForeground = false;
    private boolean isBcBnSettings = false;

    private String[] supportFindDeviceList = new String[]{"37", "Am", "An", "Ao", "Aj", "At", "61", "62", "63", "52", "54", "55", "12", "Ai",
            "50", "77", "16", "03", "02", "06", "43", "09", "A5", "01", "04", "A6", "25", "42", "17", "19", "65", "Ar", "80", "81", "23", "22",
            "31", "Ap", "82", "Bu", "Bv", "Bw", "Bt", "38", "Cc", "Ca", "Cb", "64", "13", "Op", "Ck","Cl", "Cm", "Cn","Co","Cp"};

    private WLDialog mWLDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_more, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        mRecyclerAdapter.recycleAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Home_Edit_More));
    }

    @Override
    protected void initView() {
        relativeRename = (RelativeLayout) findViewById(R.id.item_device_more_rename);
        relativeReSetRoom = (RelativeLayout) findViewById(R.id.item_device_more_area);
        relativeInfo = (RelativeLayout) findViewById(R.id.item_device_more_info);
        relativeFind = (RelativeLayout) findViewById(R.id.item_device_more_find);
        relativeSpecification = (RelativeLayout) findViewById(R.id.item_device_more_specification);

        textRenameName = (TextView) findViewById(R.id.item_device_more_rename_name);
        textResetRoomName = (TextView) findViewById(R.id.item_device_more_area_name);


        mRecyclerView = (RecyclerView) findViewById(R.id.item_device_more_recycler);
        btnDelete = (Button) findViewById(R.id.item_device_more_delete);
//        if(Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())){
//            relativeInfo.setVisibility(View.GONE);
//        }else{
//            relativeInfo.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
//        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
//        skinManager.setTextColor(btnDelete, SkinResouceKey.COLOR_NAV);
    }

    @Override
    protected void initListeners() {
        relativeRename.setOnClickListener(this);
        relativeReSetRoom.setOnClickListener(this);
        relativeInfo.setOnClickListener(this);
        relativeFind.setOnClickListener(this);
        relativeSpecification.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        configStr = getIntent().getStringExtra(KEY_MORE_CONFIG);
        isBcBnSettings = getIntent().getBooleanExtra("isBcBnSettings", false);

        device = MainApplication.getApplication().getDeviceCache().get(deviceId);

        if (device != null) {
            textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            textResetRoomName.setText(areaName);
            if (device.type.equals("Bc") || device.type.equals("Bn")) {
                relativeInfo.setVisibility(View.GONE);
            }
            if (TextUtils.equals(device.type, "Bx") || TextUtils.equals(device.type, "Br")) {
                relativeSpecification.setVisibility(View.VISIBLE);
            }
            if (Arrays.asList(supportFindDeviceList).contains(device.type)) {
                relativeFind.setVisibility(View.VISIBLE);
            }
            if (isBcBnSettings) {
                setToolBarTitle(R.string.Device_set);
                relativeRename.setVisibility(View.GONE);
                relativeReSetRoom.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
            updateMode(device.mode);
        }
        parseConfig();

        mRecyclerAdapter = new MoreConfigAdapter(this, list, device);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private List<MoreConfig.ItemBean> parseConfig() {
        list.clear();

        if (TextUtils.isEmpty(configStr)) {
            return list;
        }

        try {
            JSONObject json = new JSONObject(configStr);
            JSONArray data = json.optJSONArray("data");
            if (data == null) {
                return list;
            }
            // 处理 group
            for (int i = 0, count = (data == null ? 0 : data.length()); i < count; i++) {
                JSONObject object = data.getJSONObject(i);
                String group = object.optString("group");
                String groupName = object.optString("name");
                JSONArray items = object.optJSONArray("item");

                // 显示地区
                String showInLocaleGroup = object.optString("showInLocale");
                // 若该字段为空, 表示: 任何地区都显示
                if (!TextUtils.isEmpty(showInLocaleGroup)) {
                    // 若该字段不为空, 表示: 符合地区才会显示
                    if (!TextUtils.equals(LanguageUtil.getLanguage(), showInLocaleGroup)) {
                        // 若地区不一致, 则不显示
                        continue;
                    }
                }

                // 根据登录状态 选择是否可见
                String showWithEnterTypeGroup = object.optString("showWithEnterType");
                // 若该字段为空, 表示: 任何登录状态都显示
                if (!TextUtils.isEmpty(showWithEnterTypeGroup)) {
                    // 若该字段不为空, 表示: 符合登录状态才会显示
                    if (!TextUtils.equals(showWithEnterTypeGroup, Preference.getPreferences().getUserEnterType())) {
                        // 若登录状态与显示状态不一致, 则不显示
                        continue;
                    }
                }

                // 设备在哪种登录情况下可用, 没有此属性, 默认都可用.
                // account 表示仅在账号登陆下可用. 网关登录禁用
                // gateway 表示仅在网关登录下可用, 账号登录禁用
                String groupEnableWithEnterType = object.optString("enableWithEnterType");

                // offLineDisable 设备离线不可用
                boolean groupOfflineDisable = object.optBoolean("offLineDisable", false);

                MoreConfig.ItemBean groupBean = new MoreConfig.ItemBean();
                groupBean.name = groupName;
                groupBean.type = "group";
                list.add(groupBean);

                List<MoreConfig.ParamBean> groupParamList = new ArrayList<>();
                JSONArray groupParams = object.optJSONArray("param");
                if (groupParams != null) {
                    for (int p = 0; p < groupParams.length(); p++) {
                        JSONObject object1 = groupParams.getJSONObject(p);
                        MoreConfig.ParamBean paramBean = new MoreConfig.ParamBean();
                        paramBean.key = (object1.optString("key"));
                        paramBean.type = (object1.optString("type"));
                        paramBean.value = (object1.optString("value"));
                        groupParamList.add(paramBean);
                    }
                }

                // 处理每个 group的 item
                for (int j = 0, jCount = (items == null ? 0 : items.length()); j < jCount; j++) {
                    JSONObject item = items.getJSONObject(j);
                    MoreConfig.ItemBean itemBean = new MoreConfig.ItemBean();

                    itemBean.name = (item.optString("name"));
                    itemBean.type = (item.optString("type"));
                    itemBean.action = (item.optString("action"));
                    itemBean.desc = (item.optString("desc"));

                    // 显示地区
                    String showInLocale = item.optString("showInLocale");
                    // 若该字段为空, 表示: 任何地区都显示
                    if (!TextUtils.isEmpty(showInLocale)) {
                        // 若该字段不为空, 表示: 符合地区才会显示
                        if (!TextUtils.equals(LanguageUtil.getLanguage(), showInLocale)) {
                            // 若地区不一致, 则不显示
                            continue;
                        }
                    }

                    // 根据登录状态 选择是否可见
                    String showWithEnterTypeItem = item.optString("showWithEnterType");
                    // 若该字段为空, 表示: 任何登录状态都显示
                    if (!TextUtils.isEmpty(showWithEnterTypeItem)) {
                        // 若该字段不为空, 表示: 符合登录状态才会显示
                        if (!TextUtils.equals(showWithEnterTypeItem, Preference.getPreferences().getUserEnterType())) {
                            // 若登录状态与显示状态不一致, 则不显示
                            continue;
                        }
                    }

                    // 设备在哪种登录情况下可用, 没有此属性, 默认都可用.
                    // account 表示仅在账号登陆下可用. 网关登录禁用
                    // gateway 表示仅在网关登录下可用, 账号登录禁用
                    String enableWithEnterType = item.optString("enableWithEnterType", groupEnableWithEnterType);
                    itemBean.enableWithEnterType = enableWithEnterType;

                    // offLineDisable 设备离线不可用
                    boolean offlineDisable = item.optBoolean("offLineDisable", groupOfflineDisable);
                    itemBean.offLineDisable = (offlineDisable);

                    List<MoreConfig.ParamBean> paramList = new ArrayList<>();
                    JSONArray params = item.optJSONArray("param");
                    if (params != null) {
                        for (int p = 0; p < params.length(); p++) {
                            JSONObject object1 = params.getJSONObject(p);
                            MoreConfig.ParamBean paramBean = new MoreConfig.ParamBean();
                            paramBean.key = (object1.optString("key"));
                            paramBean.type = (object1.optString("type"));
                            paramBean.value = (object1.optString("value"));
                            paramList.add(paramBean);
                        }
                    }
                    paramList.addAll(groupParamList);
                    itemBean.param = (paramList);
                    list.add(itemBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_device_more_rename:
                if (TextUtils.equals(device.type, "50")) {
                    showChangeSocketNameDialog();
                } else if (TextUtils.equals(device.type, "Ar") || TextUtils.equals(device.type, "65") || TextUtils.equals(device.type, "Ca") || TextUtils.equals(device.type, "Cb")) {
                    showChangeCurtainNameDialog();
                } else {
                    showChangeNameDialog();
                }
                break;
            case R.id.item_device_more_area:
                intent = new Intent(DeviceMoreActivity.this, DeviceDetailMoreAreaActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.item_device_more_info:
                intent = new Intent(DeviceMoreActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceId);

                startActivity(intent);
                break;
            case R.id.item_device_more_find:
                sendFindCmd();
                if (TextUtils.equals(device.type, "Bu") || TextUtils.equals(device.type, "Bv")
                        || TextUtils.equals(device.type, "Bw") || TextUtils.equals(device.type, "Bt")
                        || TextUtils.equals(device.type, "Cl") || TextUtils.equals(device.type, "Cm") || TextUtils.equals(device.type, "Cn")) {
                    String msg = getString(R.string.Device_Find_Flashing_3);
                    showFindDialog(msg);
                } else {
                    String msg = getString(R.string.device_Find_Flashing_10s);
                    showFindDialog(msg);
                }
                break;
            case R.id.item_device_more_delete:
                if (device.inner == 0) {
                    showDelete(deviceId);
                } else {
                    ToastUtil.show(getString(R.string.Radio_is_not_supported));
                }
                break;
            case R.id.item_device_more_specification:
                startActivity(new Intent(this, SpecificationActivity.class).putExtra("deviceId", deviceId));
                break;
            default:
                break;
        }
    }


    private void showFindDialog(final String message) {
        if (mWLDialog == null) {
            WLDialog.Builder builder = new WLDialog.Builder(this);
            builder.setCancelOnTouchOutSide(true);
            builder.setDismissAfterDone(false);
            builder.setMessage(message);
            builder.setPositiveButton(getResources().getString(R.string.device_Find_go_on));
            builder.setNegativeButton(getResources().getString(R.string.device_Find_ok));
            builder.setListener(new WLDialog.MessageListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClickPositive(View view, String msg) {
                    sendFindCmd();
                    mWLDialog.dismiss();
                    showFindDialog(message);
                }

                @Override
                public void onClickNegative(View view) {
                    mWLDialog.dismiss();
                }
            });
            mWLDialog = builder.create();
        }
        mWLDialog.show();
    }

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.EditText_Device_Nick)
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .hasSocketView(false)
                .hasCurtainView(false)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }

                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        if (!TextUtils.isEmpty(currentGatewayId)) {
                            ((MainApplication) getApplication())
                                    .getMqttManager()
                                    .publishEncryptedMessage(
                                            MQTTCmdHelper.createSetDeviceInfo(currentGatewayId, deviceId, 2, msg.trim(), null),
                                            MQTTManager.MODE_GATEWAY_FIRST);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showChangeSocketNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .hasSocketView(true)
                .hasCurtainView(false)
                .setEditTextHint(R.string.EditText_Device_Nick)
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setDeviceId(deviceId)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }
                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        if (!TextUtils.isEmpty(currentGatewayId)) {
                            preference.setSocketIconImg(deviceId, builder.getIconTag());
                            ((MainApplication) getApplication())
                                    .getMqttManager()
                                    .publishEncryptedMessage(
                                            MQTTCmdHelper.createSetDeviceInfo(currentGatewayId, deviceId, 2, msg.trim(), null),
                                            MQTTManager.MODE_GATEWAY_FIRST);
                            builder.hasSocketView(false);
                            builder.hasCurtainView(false);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        builder.hasSocketView(false);
                        builder.hasCurtainView(false);
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showChangeCurtainNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .hasSocketView(false)
                .hasCurtainView(true)
                .setEditTextHint(R.string.EditText_Device_Nick)
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setDeviceId(deviceId)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }
                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        if (!TextUtils.isEmpty(currentGatewayId)) {
                            preference.setCurtainIconImg(deviceId, builder.getIconTag());
                            ((MainApplication) getApplication())
                                    .getMqttManager()
                                    .publishEncryptedMessage(
                                            MQTTCmdHelper.createSetDeviceInfo(currentGatewayId, deviceId, 2, msg.trim(), null),
                                            MQTTManager.MODE_GATEWAY_FIRST);
                            builder.hasSocketView(false);
                            builder.hasCurtainView(false);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        builder.hasSocketView(false);
                        builder.hasCurtainView(false);
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    protected void showDelete(final String deviceId) {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.Device_Delete))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        ((MainApplication) getApplication())
                                .getMqttManager()
                                .publishEncryptedMessage(
                                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(), deviceId, 3, null, null),
                                        MQTTManager.MODE_GATEWAY_FIRST);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void updateMode(int mode) {
        if (mode == 1) {//上线
            relativeFind.setEnabled(true);
            relativeFind.setAlpha(1f);
        } else if (mode == 2) {//离线
            relativeFind.setEnabled(false);
            relativeFind.setAlpha(0.54f);
        }
    }

    public boolean isForeground() {
        return isForeground;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                DeviceCache deviceCache = ((MainApplication) getApplication()).getDeviceCache();
                device = deviceCache.get(deviceId);

                if (event.device.mode == 3) {
                    finish();
                }
                // 更新adapter
                // 主要刷新一下  离线不可点击的控制
                if (event.device.mode == 1 || event.device.mode == 2) {
                    mRecyclerAdapter.updateDeviceMode(device);
                    updateMode(event.device.mode);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChangedEvent(DeviceInfoChangedEvent event) {
        DeviceInfoBean bean = event.deviceInfoBean;
        DeviceCache deviceCache = ((MainApplication) getApplication()).getDeviceCache();
        device = deviceCache.get(deviceId);
//        // 更新adapter
//        // 主要刷新一下  离线不可点击的控制
//        if (event.deviceInfoBean.mode == 1 || event.deviceInfoBean.mode == 2) {
//            mRecyclerAdapter.updateDeviceMode(device);
//            updateMode(event.deviceInfoBean.mode);
//        }
        if (bean != null) {
            if (device != null && TextUtils.equals(bean.devID, deviceId)) {
                if (bean.mode == 3) {//删除设备时退出界面
                    setResult(RESULT_OK);
                    finish();
                } else {
                    if (bean.retCode == 0) {
                        if (bean.mode == 2) {
                            if (bean.name != null) {
                                textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, bean.name));
                                ToastUtil.show(R.string.Device_Name_Change_Success);
                            }
                            if (bean.roomID != null) {
                                String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                                textResetRoomName.setText(areaName);
                                ToastUtil.show(R.string.Device_Area_Change_Success);
                            }
                        }
                    } else if (bean.retCode == 1) {
                        ToastUtil.single(R.string.Device_More_Rename_Error);
                    } else if (bean.retCode == 255) {
                        ToastUtil.single(R.string.http_unknow_error);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteDeviceEvent(DeleteDeviceEvent event) {
        if (event != null && TextUtils.equals(event.devID, deviceId)) {
            if (event.retCode == 0) {
                setResult(RESULT_OK);
                finish();
            } else if (event.retCode == 2) {
                ToastUtil.show(getString(R.string.Radio_is_not_supported));
            }
        }
    }

    private void sendFindCmd() {
        if (!device.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", device.gwID);
            object.put("devID", device.devID);
            object.put("commandType", 1);
            object.put("commandId", 36641);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
