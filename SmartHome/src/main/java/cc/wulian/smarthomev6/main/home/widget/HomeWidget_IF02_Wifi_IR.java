package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRCategoryListActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.airconditioner.AirConditionerMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerListBean;
import cc.wulian.smarthomev6.main.device.device_if02.custom.CustomMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.fan.FanMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.it_box.ITBoxMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.projector.ProjectorMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.stb.StbRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

/**
 * created by huxc  on 2018/6/13.
 * func：wifi红外转发器
 * email: hxc242313@qq.com
 */

public class HomeWidget_IF02_Wifi_IR extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = "HomeWidget_IF02_Wifi_IR";
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState;

    private ImageView mImageFirst, mImageSecond, mImageThird;
    private LinearLayout mLinearFirst, mLinearSecond, mLinearThird, mLinearMore;
    private TextView mTextFirst, mTextSecond, mTextThird, mTextAdd;
    private ConstraintLayout mLayoutAdd;
    private View mRootView;
    private View addView;

    private DataApiUnit dataApiUnit;
    private int mode;
    private List<ControllerListBean.ControllerBean> controllerList;

    public HomeWidget_IF02_Wifi_IR(Context context) {
        super(context);

        initView(context);
    }

    public HomeWidget_IF02_Wifi_IR(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        initView(context);
    }

    private void initView(final Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_if01_wifi_ir, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        textState = (TextView) mRootView.findViewById(R.id.widget_title_state);

        mImageFirst = (ImageView) mRootView.findViewById(R.id.widget_uei_image_first);
        mImageSecond = (ImageView) mRootView.findViewById(R.id.widget_uei_image_second);
        mImageThird = (ImageView) mRootView.findViewById(R.id.widget_uei_image_third);

        mLinearFirst = (LinearLayout) mRootView.findViewById(R.id.widget_uei_linear_first);
        mLinearSecond = (LinearLayout) mRootView.findViewById(R.id.widget_uei_linear_second);
        mLinearThird = (LinearLayout) mRootView.findViewById(R.id.widget_uei_linear_third);
        mLinearMore = (LinearLayout) mRootView.findViewById(R.id.widget_uei_linear_more);

        mTextFirst = (TextView) mRootView.findViewById(R.id.widget_uei_text_first);
        mTextSecond = (TextView) mRootView.findViewById(R.id.widget_uei_text_second);
        mTextThird = (TextView) mRootView.findViewById(R.id.widget_uei_text_third);
        mTextAdd = (TextView) mRootView.findViewById(R.id.widget_uei_text_add);

        mLayoutAdd = (ConstraintLayout) mRootView.findViewById(R.id.widget_uei_root_add);
        addView = mRootView.findViewById(R.id.widget_uei_view_add);

        mLinearFirst.setOnClickListener(this);
        mLinearSecond.setOnClickListener(this);
        mLinearThird.setOnClickListener(this);
        mLinearMore.setOnClickListener(this);
        mRootView.findViewById(R.id.widget_uei_view_add).setOnClickListener(this);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiIRActivity.start(context, mHomeItemBean.getWidgetID(), false);
            }
        });
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        mode = mDevice.mode;
        // 设置在线离线的状态
        updateMode();
        setName();
        getControllerList();
    }

    private void getControllerList() {
        dataApiUnit = new DataApiUnit(getContext());
        dataApiUnit.doGetWifiIRControllerList(mDevice.devID, new DataApiUnit.DataApiCommonListener<ControllerListBean>() {
            @Override
            public void onSuccess(ControllerListBean bean) {
                controllerList = bean.blocks;
                if (controllerList != null) {
                    updateController();
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void updateController() {
        int count = 0;
        if (controllerList.size() == 0) {
            // 没有遥控器
        } else {
            count = controllerList.size();
        }

        if (count > 3) {
            mLinearMore.setVisibility(VISIBLE);
        } else {
            mLinearMore.setVisibility(INVISIBLE);
        }

        count = count >= 3 ? 3 : count;

        mLinearFirst.setVisibility(INVISIBLE);
        mLinearSecond.setVisibility(INVISIBLE);
        mLinearThird.setVisibility(INVISIBLE);
        switch (count) {
            case 0:
                mLayoutAdd.setVisibility(VISIBLE);
                break;
            case 3:
                mLinearThird.setVisibility(VISIBLE);
                mTextThird.setText(controllerList.get(2).blockName);
                mImageThird.setImageResource(controllerList.get(2).getWidgetImg(mDevice.isOnLine(), controllerList.get(2).blockType));
            case 2:
                mLinearSecond.setVisibility(VISIBLE);
                mTextSecond.setText(controllerList.get(1).blockName);
                mImageSecond.setImageResource(controllerList.get(1).getWidgetImg(mDevice.isOnLine(), controllerList.get(1).blockType));
            case 1:
                mLinearFirst.setVisibility(VISIBLE);
                mTextFirst.setText(controllerList.get(0).blockName);
                mImageFirst.setImageResource(controllerList.get(0).getWidgetImg(mDevice.isOnLine(), controllerList.get(0).blockType));
            default:
                mLayoutAdd.setVisibility(INVISIBLE);
                break;
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }


    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }


    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                textState.setText(R.string.Device_Online);
                textState.setTextColor(getResources().getColor(R.color.colorPrimary));
                addView.setEnabled(true);
                mTextAdd.setEnabled(true);
                break;
            case 2:
                // 离线
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
                addView.setEnabled(false);
                mTextAdd.setEnabled(false);
                break;
            case 3:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                mode = event.device.mode;
                updateMode();
                setName();
                getControllerList();
            }
        }
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.widget_uei_linear_more) {
            WifiIRActivity.start(getContext(), mHomeItemBean.getWidgetID(), false);
            return;
        }

        if (mode == 2) {
            return;
        }
        int index = -1;
        switch (v.getId()) {
            case R.id.widget_uei_linear_first:
                index = 0;
                break;
            case R.id.widget_uei_linear_second:
                index = 1;
                break;
            case R.id.widget_uei_linear_third:
                index = 2;
                break;
            case R.id.widget_uei_view_add:
                WifiIRCategoryListActivity.start(getContext(), mHomeItemBean.getWidgetID());
                break;
        }

        if (index != -1 && index < controllerList.size()) {

            ControllerListBean.ControllerBean bean = controllerList.get(index);
            switch (bean.blockType) {
                case WifiIRManage.TYPE_TV:
                    TvRemoteMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib, true);
                    break;
                case WifiIRManage.TYPE_AIR:
                    AirConditionerMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib);
                    break;
                case WifiIRManage.TYPE_FAN:
                    FanMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib, true);
                    break;
                case WifiIRManage.TYPE_STB:
                    StbRemoteMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib, true);
                    break;
                case WifiIRManage.TYPE_IT_BOX:
                    ITBoxMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib, true);
                    break;
                case WifiIRManage.TYPE_PROJECTOR:
                    ProjectorMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib, true);
                    break;
                case WifiIRManage.TYPE_CUSTOM:
                    CustomMainActivity.start(v.getContext(), mDevice.devID, bean.blockType, bean.blockName, bean.blockId, bean.codeLib, true);
                    break;
            }
        }
    }

}