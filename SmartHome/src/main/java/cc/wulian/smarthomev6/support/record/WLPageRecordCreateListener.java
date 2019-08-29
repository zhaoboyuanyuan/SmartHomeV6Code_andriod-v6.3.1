package cc.wulian.smarthomev6.support.record;

import android.app.Activity;
import android.text.TextUtils;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.wrecord.record.page.PageRecord;
import cc.wulian.wrecord.record.page.PageRecordCreateListener;

/**
 * Created by Veev on 2017/8/16
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    WLPageRecordCreateListener
 */

public class WLPageRecordCreateListener implements PageRecordCreateListener {

    @Override
    public void onCreate(Activity activity, PageRecord record) {
        if (activity == null) {
            return;
        }

        switch (activity.getClass().getName()) {
            case "cc.wulian.smarthomev6.main.device.DeviceDetailActivity":
                dealDevice(activity, record);
                break;
            case "cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity":
            case "cc.wulian.smarthomev6.main.message.alarm.EquesAlarmActivity":
            case "cc.wulian.smarthomev6.main.message.alarm.ICamAlarmActivity":
                dealAlarm(activity, record);
                break;
            case "cc.wulian.smarthomev6.main.message.log.MessageLogActivity":
                dealLog(activity, record);
                break;
            case "cc.wulian.smarthomev6.main.message.setts.MessageSettingsDetailActivity":
                dealPush(activity, record);
                break;
            case "cc.wulian.smarthomev6.main.home.HomeActivity":
                dealHome(activity, record);
                break;
        }
    }

    private void dealHome(Activity activity, PageRecord record) {
        try {
            HomeActivity homeActivity = (HomeActivity) activity;
            record.pid = homeActivity.getTabSelected();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealDevice(Activity activity, PageRecord record) {
        String did = activity.getIntent().getStringExtra("key_device_id");
        Device device = MainApplication.getApplication().getDeviceCache().get(did);
        if (device != null) {
            record.pid = "device" + device.type;
        }
    }

    private void dealAlarm(Activity activity, PageRecord record) {
        String type = activity.getIntent().getStringExtra("type");
        if (TextUtils.isEmpty(type)) {
            String did = activity.getIntent().getStringExtra("deviceID");
            Device device = MainApplication.getApplication().getDeviceCache().get(did);
            if (device != null) {
                type = device.type;
            } else {
                type = "";
            }
        }
        record.pid = "alarm" + type;
    }

    private void dealLog(Activity activity, PageRecord record) {
        String type = activity.getIntent().getStringExtra("type");
        if (TextUtils.isEmpty(type)) {
            String did = activity.getIntent().getStringExtra("deviceID");
            Device device = MainApplication.getApplication().getDeviceCache().get(did);
            if (device != null) {
                type = device.type;
            } else {
                type = "";
            }
        }
        record.pid = "log" + type;
    }

    private void dealPush(Activity activity, PageRecord record) {
        String type = activity.getIntent().getStringExtra("type");
        if (TextUtils.isEmpty(type)) {
            String did = activity.getIntent().getStringExtra("deviceID");
            Device device = MainApplication.getApplication().getDeviceCache().get(did);
            if (device != null) {
                type = device.type;
            } else {
                type = "";
            }
        }
        record.pid = "push" + type;
    }

}
