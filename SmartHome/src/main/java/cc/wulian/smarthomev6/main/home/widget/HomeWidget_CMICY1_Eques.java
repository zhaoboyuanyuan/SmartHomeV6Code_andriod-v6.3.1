package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.EquesPlayActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.BatteryStatusBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.RatioImageView;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/6/8
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    HomeWidget_CMICY1_Eques
 */

public class HomeWidget_CMICY1_Eques extends RelativeLayout implements IWidgetLifeCycle {
    private static final String TAG = "HomeWidget_CMICY1_Eques";
    private TextView  textName, textState;
    private ImageView imagePower, home_view_cateye_play;
    private RatioImageView imageBg;

    private Device mDevice;

    public HomeWidget_CMICY1_Eques(Context context) {
        super(context);

        initView(context);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);

        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        setPower();
        setName();
        setMode();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_cmicy1_eques, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.home_view_cateye_name);
        textState = (TextView) rootView.findViewById(R.id.home_view_cateye_state);
        imagePower = (ImageView) rootView.findViewById(R.id.home_view_cmicy1_eques_img_power);
        imageBg = (RatioImageView) rootView.findViewById(R.id.home_view_cmicy1_eques_img_bg);
        home_view_cateye_play = (ImageView) rootView.findViewById(R.id.home_view_cateye_play);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EquesPlayActivity.start(context, mDevice);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLastFrameEvent(LastFrameEvent event) {
        if (mDevice.devID.equals(event.deviceId)) {
            File file = new File(event.path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(event.path);
                if (bitmap == null) {
                    imageBg.setImageResource(R.drawable.home_widget_eques_bg);
                    WLog.i(TAG, "onLastFrameEvent: " + "null");
                } else {
                    imageBg.setImageBitmap(bitmap);
                    WLog.i(TAG, "onLastFrameEvent: " + "not null");
                }
//                ImageLoader.getInstance().displayImage("file://" + event.path, imageBg);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryStatusBean(BatteryStatusBean bean) {
        setPower();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                setMode();
            }
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

    private void setName() {
        textName.setText(DeviceInfoDictionary.getNameByDevice(mDevice));
    }

    private void setMode() {
        if (mDevice != null) {
            switch (mDevice.mode) {
                case 0:
                case 1:
                case 4:
                    textState.setText(R.string.Device_Online);
                    home_view_cateye_play.setImageResource(R.drawable.home_view_cateye_play_online);
                    break;
                case 2:
                    textState.setText(R.string.Device_Offline);
                    home_view_cateye_play.setImageResource(R.drawable.home_view_cateye_play_offline);
                    break;
            }
        }
    }

    private void setPower() {
        if (mDevice.isOnLine()) {
            imagePower.setVisibility(VISIBLE);
        } else {
            imagePower.setVisibility(INVISIBLE);
            return;
        }
        try {
            JSONObject json = new JSONObject(mDevice.data);
            JSONObject equesInfoBeanJson = json.optJSONObject("equesInfoBean");

            if (equesInfoBeanJson == null) {
                return;
            }
            String levStr = equesInfoBeanJson.optString("battery_level");
            try {
                int lev = Integer.parseInt(levStr);
                if (lev > 80) {
                    imagePower.setImageResource(R.drawable.icon_eques_power_100);
                } else if (lev > 60) {
                    imagePower.setImageResource(R.drawable.icon_eques_power_80);
                } else if (lev > 40) {
                    imagePower.setImageResource(R.drawable.icon_eques_power_60);
                } else if (lev > 20) {
                    imagePower.setImageResource(R.drawable.icon_eques_power_40);
                } else {
                    imagePower.setImageResource(R.drawable.icon_eques_power_20);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
