package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.ColorView;
import cc.wulian.smarthomev6.support.customview.EmulateCircleView;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.singlechoice.OnItemCheckedListenerAdapter;
import cc.wulian.smarthomev6.support.tools.singlechoice.SingleChoice;
import cc.wulian.smarthomev6.support.tools.singlechoice.SingleChoiceHelper;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * mini网关 灯光
 */
public class DeviceD8LightFragment extends Fragment {
    private static final String TAG = "DeviceD8LightFragment";
    private ColorView mColor1, mColor2, mColor3, mColor4, mColor5, mColor6;

    private String deviceID;
    private Device mDevice;
//    private View mViewCL, mViewLS, mViewKS, mViewMS;
//    private TextView mTextCL, mTextLS, mTextKS, mTextMS;
    private EmulateCircleView switch_btn;
    private ValueAnimator mValueAnimator;
    private View root_view;
    private View top_layout;
    private View bottom_layout;
    private int height;
    private int currentState;//0关1开
    private String mColorParam = "01", mStateParam = "00";

    public DeviceD8LightFragment() {
        // Required empty public constructor
    }

    public static DeviceD8LightFragment start(String did) {
        // Required empty public constructor
        DeviceD8LightFragment f = new DeviceD8LightFragment();
        Bundle b = new Bundle();
        b.putString("deviceID", did);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        deviceID = getArguments().getString("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_d8_light, container, false);

        mColor1 = (ColorView) view.findViewById(R.id.device_d8_color1);
        mColor2 = (ColorView) view.findViewById(R.id.device_d8_color2);
        mColor3 = (ColorView) view.findViewById(R.id.device_d8_color3);
        mColor4 = (ColorView) view.findViewById(R.id.device_d8_color4);
        mColor5 = (ColorView) view.findViewById(R.id.device_d8_color5);
        mColor6 = (ColorView) view.findViewById(R.id.device_d8_color6);
        root_view = view.findViewById(R.id.root_view);
        top_layout = view.findViewById(R.id.top_layout);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        switch_btn = (EmulateCircleView) view.findViewById(R.id.switch_btn);
        switch_btn.setSwitchDuration(400);
        switch_btn.setOnCircleClickListener(new EmulateCircleView.OnCircleClickListener() {
            @Override
            public void onBigCircleClick() {
                height = root_view.getHeight();
                currentState = currentState == 0 ? 1 : 0;
                createAnimator();
                switch (currentState) {
                    case 0:
//                        mValueAnimator.reverse();
                        mStateParam = "00";
                        sendCmd();
                        break;
                    case 1:
//                        mValueAnimator.start();
                        mStateParam = "05";
                        sendCmd();
                        break;
                }
            }

            @Override
            public void onSmallCircle1Click() {
                mStateParam = "01";
                sendCmd();
            }

            @Override
            public void onSmallCircle2Click() {
                mStateParam = "02";
                sendCmd();
            }

            @Override
            public void onSmallCircle3Click() {
                mStateParam = "03";
                sendCmd();
            }

            @Override
            public void onSmallCircle4Click() {
                mStateParam = "04";
                sendCmd();
            }
        });

//        mViewCL = view.findViewById(R.id.device_d8_item_cl);
//        mViewLS = view.findViewById(R.id.device_d8_item_ls);
//        mViewKS = view.findViewById(R.id.device_d8_item_ks);
//        mViewMS = view.findViewById(R.id.device_d8_item_ms);
//        mTextCL = (TextView) view.findViewById(R.id.device_d8_text_cl);
//        mTextLS = (TextView) view.findViewById(R.id.device_d8_text_ls);
//        mTextKS = (TextView) view.findViewById(R.id.device_d8_text_ks);
//        mTextMS = (TextView) view.findViewById(R.id.device_d8_text_ms);
//
//        FloatAnimator.with(mViewCL).setDuration(3000).start();
//        FloatAnimator.with(mViewLS).setDuration(3000).setStartDelay(750).start();
//        FloatAnimator.with(mViewKS).setDuration(3000).setStartDelay(1500).start();
//        FloatAnimator.with(mViewMS).setDuration(3000).setStartDelay(2250).start();
//        FloatAnimator.with(mTextCL).setDuration(3000).setRange(5).start();
//        FloatAnimator.with(mTextLS).setDuration(3000).setRange(5).setStartDelay(750).start();
//        FloatAnimator.with(mTextKS).setDuration(3000).setRange(5).setStartDelay(1500).start();
//        FloatAnimator.with(mTextMS).setDuration(3000).setRange(5).setStartDelay(2250).start();

        SingleChoiceHelper singleChoiceHelper = new SingleChoiceHelper();
        singleChoiceHelper.addSingleChoice(new Pair<SingleChoice, Object>(mColor1, "01"));
        singleChoiceHelper.addSingleChoice(new Pair<SingleChoice, Object>(mColor2, "05"));
        singleChoiceHelper.addSingleChoice(new Pair<SingleChoice, Object>(mColor3, "06"));
        singleChoiceHelper.addSingleChoice(new Pair<SingleChoice, Object>(mColor4, "03"));
        singleChoiceHelper.addSingleChoice(new Pair<SingleChoice, Object>(mColor5, "02"));
        singleChoiceHelper.addSingleChoice(new Pair<SingleChoice, Object>(mColor6, "07"));

        singleChoiceHelper.setOnItemCheckedListener(new OnItemCheckedListenerAdapter() {
            @Override
            public void onChecked(Object tag) {
                super.onChecked(tag);
                WLog.i(TAG, "onChecked: " + tag);
                if (!TextUtils.equals(mColorParam, tag.toString())) {
                    mColorParam = tag.toString();
                    sendCmd();
                }
            }
        });

//        view.findViewById(R.id.view_main).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        mViewCL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        mViewKS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        mViewMS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        mViewLS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return view;
    }

    private void createAnimator() {
        if(mValueAnimator == null){
            mValueAnimator = ValueAnimator.ofInt(0, (int)(height * 0.4));
            mValueAnimator.setDuration(400);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams mLayoutParams1 = top_layout.getLayoutParams();
                    mLayoutParams1.height = height - (int)animation.getAnimatedValue();
                    top_layout.setLayoutParams(mLayoutParams1);

                    ViewGroup.LayoutParams mLayoutParams2 = bottom_layout.getLayoutParams();
                    mLayoutParams2.height = (int)animation.getAnimatedValue();
                    bottom_layout.setLayoutParams(mLayoutParams2);

                }
            });
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(currentState == 1){
                        bottom_layout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(currentState == 0){
                        bottom_layout.setVisibility(View.GONE);
                    }
                    mValueAnimator.cancel();
                    mValueAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void changeState(int state) {
        //
    }

    private void updateView(String attributeValue) {
        if (!TextUtils.isEmpty(attributeValue)) {
            WLog.i(TAG, "updateView: " + attributeValue);
            String aa = attributeValue.substring(0, 2);
            String bb = attributeValue.substring(2);

            if ("00".equals(bb)) {
                currentState = 0;
            } else {
                if ("01".equals(bb)) {
                    switch_btn.check(EmulateCircleView.SMALL_CIRCLE1_TOUCH_DOWN);
                } else if ("02".equals(bb)) {
                    switch_btn.check(EmulateCircleView.SMALL_CIRCLE2_TOUCH_DOWN);
                } else if ("03".equals(bb)) {
                    switch_btn.check(EmulateCircleView.SMALL_CIRCLE3_TOUCH_DOWN);
                } else {
                    switch_btn.check(EmulateCircleView.SMALL_CIRCLE4_TOUCH_DOWN);
                }
                currentState = 1;
            }
            height = root_view.getHeight();
            createAnimator();
            switch (currentState) {
                case 0:
                    if (switch_btn.isOpen()) {
                        mValueAnimator.reverse();
                        switch_btn.setSwitchState(EmulateCircleView.CLOSE);
                    }
                    break;
                case 1:
                    if (!switch_btn.isOpen()) {
                        switch_btn.setSwitchState(EmulateCircleView.OPEN);
                        mValueAnimator.start();
                    }
                    break;
            }

            mStateParam = bb;
            mColorParam = aa;
            switch (aa) {
                case "00":
                case "01":
                    mColor1.setChecked(true);
                    break;
                case "02":
                    mColor5.setChecked(true);
                    break;
                case "03":
                    mColor4.setChecked(true);
                    break;
                case "04":
                case "05":
                    mColor2.setChecked(true);
                    break;
                case "06":
                    mColor3.setChecked(true);
                    break;
                case "07":
                    mColor6.setChecked(true);
                    break;
            }
        }
    }

    private void dealDevice(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray endpoints = object.getJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");
            if (attributeId == 0x8001) {
                updateView(attributeValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            WLog.i(TAG, "onDeviceReport: deviceID = " + event.device.devID);
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                dealDevice(event.device.data);
            }
        }
    }

    private String getParam() {
        return mColorParam + mStateParam;
    }

    private void sendCmd() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", deviceID);
            object.put("cluster", 0x0500);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0402);
            object.put("commandType", 1);
            object.put("commandId", 0x8001);
            JSONArray array = new JSONArray();
            array.put(getParam());
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", deviceID);
            object.put("cluster", 0x0500);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0402);
            object.put("commandType", 0);
            object.put("commandId", 0x8001);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
