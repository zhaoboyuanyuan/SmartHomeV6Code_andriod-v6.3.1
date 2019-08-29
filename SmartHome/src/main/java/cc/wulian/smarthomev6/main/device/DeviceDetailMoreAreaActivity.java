package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.DeviceDetailMoreAreaAdapter;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;

import static cc.wulian.smarthomev6.main.device.DeviceDetailMoreActivity.KEY_DEVICE_ID;

/**
 * 作者: mamengchao
 * 时间: 2017/4/5 0005
 * 描述: 设备详情页->更多->区域设置
 * 联系方式: 805901025@qq.com
 */

public class DeviceDetailMoreAreaActivity extends BaseTitleActivity {
    public static final String SET_AREA = "set_area";
    private ListView areaListView;
    private DeviceDetailMoreAreaAdapter areaAdapter;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private String deviceId;
    private List<RoomInfo> areaList;
    private Device device;
    private AreaManager areaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_more_area, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(getString(R.string.Device_Area), getString(R.string.Device_ManagerArea));
    }

    @Override
    protected void initView() {
        areaListView = (ListView) findViewById(R.id.device_detail_more_area_list);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        areaManager = new AreaManager(this);
        areaList = areaManager.loadDiskRoom();
        areaAdapter = new DeviceDetailMoreAreaAdapter(this, areaList, device);
        areaListView.setAdapter(areaAdapter);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.img_left:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                onBackPressed();
                break;
            case R.id.btn_right:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                startActivity(new Intent(DeviceDetailMoreAreaActivity.this, AreaActivity.class));
//                showCreate();
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                if (event.device.mode > 1) {
                    finish();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChangedEvent(DeviceInfoChangedEvent event) {
        DeviceInfoBean bean = event.deviceInfoBean;
        if (bean != null && TextUtils.equals(bean.devID, deviceId)) {
            device.roomID = bean.roomID;
            areaAdapter.swapDevice(device);
            ProgressDialogManager.getDialogManager().dimissDialog(SET_AREA, 0);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoChangedEvent(RoomInfoEvent event) {
        getAreaInfo();
    }

    private void getAreaInfo() {
        areaList = areaManager.loadDiskRoom();
        areaAdapter.swapData(areaList);
    }
}
