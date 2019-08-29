package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cateye.CateyeDetailActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.RatioImageView;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.FileUtil;

/**
 * 作者: chao
 * 时间: 2017/5/18
 * 描述: 猫眼首页widget
 * 联系方式: 805901025@qq.com
 */

public class HomeWidget_CMICA1_Cateye extends RelativeLayout implements IWidgetLifeCycle {
    private Context context;
    private Device device;
    private ImageView play;
    private RatioImageView imageBg;
    private TextView name;
    private TextView state;
    private ImageView ivBattery;

    public HomeWidget_CMICA1_Cateye(Context context) {
        super(context);
        initView(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        device = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        name.setText(DeviceInfoDictionary.getNameByDevice(device));

        setState(device);
        String snapshotPath = FileUtil.getLastFramePath();
        String fileName = bean.getWidgetID() + ".jpg";
        showSnapshot(snapshotPath + "/" + fileName);
        String batteryValue = Preference.getPreferences().getCateyeBattery(device.devID);
        if (!TextUtils.isEmpty(batteryValue)) {
            setBatteryLevel(batteryValue, ivBattery);
        }
    }

    @Override
    public void onViewRecycled() {

    }

    private void initView(Context context) {
        this.context = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_cmica1_cateye, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        play = (ImageView) findViewById(R.id.home_view_cateye_play);
        imageBg = (RatioImageView) findViewById(R.id.home_view_cateye_bg);
        name = (TextView) findViewById(R.id.home_view_cateye_name);
        state = (TextView) findViewById(R.id.home_view_cateye_state);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);

        imageBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CateyeDetailActivity.start(HomeWidget_CMICA1_Cateye.this.context, device);
            }
        });


    }

    private void setState(Device device) {
        if (device != null) {
            if (device.isOnLine()) {
                // 上线
                state.setText(getResources().getString(R.string.Device_Online));
                play.setImageResource(R.drawable.home_view_cateye_play_online);
            } else {
                //下线
                state.setText(getResources().getString(R.string.Device_Offline));
                play.setImageResource(R.drawable.home_view_cateye_play_offline);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                device = MainApplication.getApplication().getDeviceCache().get(device.devID);
                device.mode = event.device.mode;
                setState(device);
                name.setText(DeviceInfoDictionary.getNameByDevice(device));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLastFrameEvent(LastFrameEvent event) {
        if (device.devID.equals(event.deviceId)) {
            showSnapshot(event.path);
        }
    }


    private void showSnapshot(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageBg.setImageBitmap(bitmap);
            }
        }
    }

    private void setBatteryLevel(String value, ImageView view) {
        int battery = Integer.parseInt(value);
        if (battery < 25) {
            view.setImageResource(R.drawable.icon_battery_0);
        } else if (battery >= 25 && battery < 50) {
            view.setImageResource(R.drawable.icon_battery_1);
        } else if (battery >= 50 && battery < 75) {
            view.setImageResource(R.drawable.icon_battery_2);
        } else {
            view.setImageResource(R.drawable.icon_battery_3);
        }
    }

}
