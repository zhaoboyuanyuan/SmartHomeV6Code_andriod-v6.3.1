package cc.wulian.smarthomev6.main.device.device_Ap;

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

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

/**
 * Created by Veev on 2017/11/13
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    BmSystemChooseView
 */

public class ApSystemChooseView extends LinearLayout implements IDeviceMore {

    private static String TAG = ApSystemChooseView.class.getSimpleName();

    private LinearLayout mLinearSys, mLinearAnti;
    private TextView mTextPanel, mTextName, mTextName2;

    private String deviceID, sys, anti;
    private Device mDevice;
    // 系统
    // 01 电系统(默认), 02 水系统
    private String mSys = "01";

    public ApSystemChooseView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_bm_system_choose, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mLinearAnti = (LinearLayout) rootView.findViewById(R.id.bm_system_choose_linear_anti);
        mLinearSys = (LinearLayout) rootView.findViewById(R.id.bm_system_choose_linear_sys);
        mTextPanel = (TextView) rootView.findViewById(R.id.bm_system_choose_text_panel);
        mTextName = (TextView) rootView.findViewById(R.id.bm_system_choose_text_sys_name);
        mTextName2 = (TextView) rootView.findViewById(R.id.bm_system_choose_text_sys_name_2);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);

        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            }
            if ("sys".equals(p.key)) {
                sys = p.value;
            }
            if ("anti".equals(p.key)) {
                anti = p.value;
            }
        }

        dealData(mDevice);

        mLinearSys.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(sys)) {
                    Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                    intent.putExtra("url", sys);
                    intent.putExtra("deviceID", deviceID);
                    getContext().startActivity(intent);
                }
            }
        });
        mLinearAnti.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(anti)) {
                    Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                    intent.putExtra("url", anti);
                    intent.putExtra("deviceID", deviceID);
                    getContext().startActivity(intent);
                }
            }
        });
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
            mTextName2.setTextColor(getResources().getColor(R.color.newPrimaryText));
            mLinearSys.setEnabled(true);
            mLinearAnti.setEnabled(true);
            setEnabled(true);
        } else {
            mTextName.setTextColor(getResources().getColor(R.color.newStateText));
            mTextName2.setTextColor(getResources().getColor(R.color.newStateText));
            mLinearSys.setEnabled(false);
            mLinearAnti.setEnabled(false);
            setEnabled(false);
        }

        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8002) {
                    int l = attribute.attributeValue.length();
                    mSys = attribute.attributeValue.substring(l - 2, l);
                } else if (attribute.attributeId == 0x800B) {
                    mSys = attribute.attributeValue;
                }
            }
        });

        // 电
        if (TextUtils.equals(mSys, "01")) {
            mTextPanel.setText(R.string.Device_Bm_Details_System1);
            mLinearAnti.setVisibility(GONE);
        } else {
            // 水
            mTextPanel.setText(R.string.Device_Bm_Details_System2);
            mLinearAnti.setVisibility(VISIBLE);
        }

    }

}
