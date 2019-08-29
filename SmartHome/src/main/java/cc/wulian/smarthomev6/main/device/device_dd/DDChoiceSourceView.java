package cc.wulian.smarthomev6.main.device.device_dd;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * Created by Veev on 2017/11/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    恢复出厂
 */
public class DDChoiceSourceView extends LinearLayout implements IDeviceMore {

    private static String TAG = DDChoiceSourceView.class.getSimpleName();

    private String deviceID, url;
    private Device mDevice;

    private TextView mTextName;

    public DDChoiceSourceView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_dd_choice_reource, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) rootView.findViewById(R.id.bm_return_setts_text_sys_name);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("deviceID", deviceID);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            } else if ("url".equals(p.key)) {
                url = p.value;
            }
        }

        dealData(mDevice);
    }


    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            dealData(event.device);
        }
    }

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        if (device.isOnLine()) {
            mTextName.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }
    }
}
