package cc.wulian.smarthomev6.main.device.device_if02.airconditioner;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.Space;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.ControllerMoreActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBlocksBean;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueListBean;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.event.WifiIRSceneEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;

/**
 * created by huxc  on 2018/6/12.
 * func：空调主界面
 * email: hxc242313@qq.com
 */

public class AirConditionerMainActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String OPEN = "r_s0_26_u1_l1_p0";//默认的开机码
    private boolean isSceneOrHouseKeeper;
    private boolean isHasSet;//场景管家是否点击了右上角确认键

    private ImageView mImageStateSwing, mImageStateMode, mImageStateSpeed, mImageTemFirst,
            mImageTemSecond, mImageCircle, mImagePower, mImageMode, mImageSpeed,
            mImageSwingV, mImageSwingH, mImageTempDown, mImageTempUp, mImageStateTempUnit;
    private TextView mTextStateMode;
    private RelativeLayout mRelativeOn, mRelativeOff;
    private Space mSpace;
    private View mViewTitleImage;

    private String deviceID;
    private String blockType;
    private String blockName;
    private String blockId;
    private String codeLib;

    private Device device;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private static final int CHANGE_MODE_DURATION = 500;

    private boolean mState = false;
    private DataApiUnit dataApiUnit;
    private int lastSpeed;
    private int lastTemp;
    private String lastMode = "";
    private String lastUD;
    private String lastLR;
    private String lastCodeSum;
    private String lastPowerStatus;
    private String lastPowerValue;
    private boolean hasSet = true;
    private boolean isSupportSwingV = true;
    private boolean isSupportSwingH = true;

    private long clickTime;

    private List<ControllerBlocksBean.keyBean> keys;

    private int mTemBumResArr[] = {
            R.drawable.num_0,
            R.drawable.num_1,
            R.drawable.num_2,
            R.drawable.num_3,
            R.drawable.num_4,
            R.drawable.num_5,
            R.drawable.num_6,
            R.drawable.num_7,
            R.drawable.num_8,
            R.drawable.num_9,
    };

    private int mSpeedResArr[] = {
            R.drawable.icon_cloud_auto,
            R.drawable.icon_cloud_2,
            R.drawable.icon_cloud_3,
            R.drawable.icon_cloud_4,
            R.drawable.icon_cloud_5,
            R.drawable.icon_cloud_6
    };

    private int mModeResArr[] = {
            R.drawable.icon_air_mode_cold,
            R.drawable.icon_air_mode_hot,
            R.drawable.icon_air_mode_auto,
            R.drawable.icon_air_mode_water,
            R.drawable.icon_air_mode_songfeng,
    };

    private int mModeNameArr[] = {
            R.string.Infraredtransponder_Airconditioner_Refrigeration,
            R.string.Infraredtransponder_Airconditioner_Heating,
            R.string.Infraredtransponder_Airconditioner_Automatic,
            R.string.Infraredtransponder_Airconditioner_Dehumidify,
            R.string.Infraredtransponder_Airconditioner_Air,
    };

    //制冷、制热、自动、抽湿、送风
    private String modeTagArr[] = {
            "r", "h", "a", "d", "w"
    };

    public static void startForScene(Activity context, String deviceID, String blockType, String blockName, String blockId, String codeLib) {
        Intent intent = new Intent(context, AirConditionerMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockId", blockId);
        intent.putExtra("codeLib", codeLib);
        intent.putExtra("isSceneOrHouseKeeper", true);
        context.startActivityForResult(intent, 1);
    }

    /**
     * @param blockType 遥控器类型
     * @param blockName 遥控器名称
     * @param blockId   遥控器id
     * @param codeLib   码库id
     */
    public static void start(Context context, String deviceID, String blockType, String blockName, String blockId, String codeLib) {
        Intent intent = new Intent(context, AirConditionerMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockId", blockId);
        intent.putExtra("codeLib", codeLib);
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_conditioner_main, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Intent intent = getIntent();
        deviceID = intent.getStringExtra("deviceID");
        blockId = intent.getStringExtra("blockId");
        blockName = intent.getStringExtra("blockName");
        blockType = intent.getStringExtra("blockType");
        codeLib = intent.getStringExtra("codeLib");
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);

        setTitle();
    }


    /**
     * 设置标题
     */
    private void setTitle() {
        if (isSceneOrHouseKeeper) {
            if (!TextUtils.isEmpty(blockName)) {
                setToolBarTitleAndRightBtn(blockName, getResources().getString(R.string.Config_Next_Step));
            } else {
                setToolBarTitleAndRightBtn(R.string.Infraredtransponder_List_Airconditioner, R.string.Config_Next_Step);
            }
        } else {
            if (!TextUtils.isEmpty(blockName)) {
                setToolBarTitleAndRightImg(blockName, R.drawable.icon_more);
            } else {
                setToolBarTitleAndRightImg(R.string.Infraredtransponder_List_Airconditioner, R.drawable.icon_more);
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();

        // 初始化视图
        mImageStateSpeed = (ImageView) findViewById(R.id.air_state_image_speed);
        mImageStateMode = (ImageView) findViewById(R.id.air_state_image_mode);
        mImageStateSwing = (ImageView) findViewById(R.id.air_state_image_swing);
        mImageTemFirst = (ImageView) findViewById(R.id.air_num_first);
        mImageTemSecond = (ImageView) findViewById(R.id.air_num_second);
        mImageStateTempUnit = (ImageView) findViewById(R.id.air_state_image_unit);
        mImageCircle = (ImageView) findViewById(R.id.air_image_circle);
        mTextStateMode = (TextView) findViewById(R.id.air_state_text_mode);
        mRelativeOn = (RelativeLayout) findViewById(R.id.air_state_relate_on);
        mRelativeOff = (RelativeLayout) findViewById(R.id.air_state_relate_off);
        mSpace = (Space) findViewById(R.id.air_place_match);

        mImagePower = (ImageView) findViewById(R.id.air_image_close);
        mImageSwingV = (ImageView) findViewById(R.id.air_image_swing_v);
        mImageSwingH = (ImageView) findViewById(R.id.air_image_swing_h);
        mImageSpeed = (ImageView) findViewById(R.id.air_image_speed);
        mImageMode = (ImageView) findViewById(R.id.air_image_refresh);
        mImageTempDown = (ImageView) findViewById(R.id.air_image_tem_down);
        mImageTempUp = (ImageView) findViewById(R.id.air_image_tem_up);

        mViewTitleImage = findViewById(R.id.air_title_image);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(mViewTitleImage, SkinResouceKey.BITMAP_AIRCOND_UNDER_TITLE_BG);
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        findViewById(R.id.air_image_back).setOnClickListener(this);
        findViewById(R.id.air_image_more).setOnClickListener(this);
        mImageSwingV.setOnClickListener(this);
        mImageSwingH.setOnClickListener(this);
        mImageSpeed.setOnClickListener(this);
        mImagePower.setOnClickListener(this);
        mImageMode.setOnClickListener(this);
        mImageTempDown.setOnClickListener(this);
        mImageTempUp.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dataApiUnit = new DataApiUnit(this);
        getLastDeviceStatus();
        getCurrentCodeLib();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                saveLastDeviceStatus(getLastCodeSum());
                finish();
                break;
            case R.id.img_right:
                ControllerMoreActivity.start(this, deviceID, blockName, blockType, blockId, codeLib, false);
                break;
            case R.id.btn_right:
                if (isSceneOrHouseKeeper) {
                    isHasSet = true;
                    sendControlCmd();
                }
                break;
            case R.id.air_image_tem_up:
                if (lastTemp < 30 && isEnableAdjustTemperature()) {
                    ++lastTemp;
                    updateTemp(lastTemp + "");
                    sendControlCmd();
                }
                break;
            case R.id.air_image_tem_down:
                if (lastTemp > 16 && isEnableAdjustTemperature()) {
                    --lastTemp;
                    updateTemp(lastTemp + "");
                    sendControlCmd();
                }
                break;
            case R.id.air_image_refresh:
                int index = -1;
                for (int i = 0; i < modeTagArr.length; i++) {
                    if (lastMode == modeTagArr[i]) {
                        index = i;
                    }
                }
                if (index == 4) {
                    index = 0;
                } else {
                    ++index;
                }
                updateMode(modeTagArr[index]);
                setTmpButtonClickable();
                sendControlCmd();
                break;
            case R.id.air_image_close:
                if (TextUtils.equals(lastPowerStatus, "off")) {
                    if (!hasSet) {
                        hasSet = true;
                        lastPowerValue = OPEN;
                        lastCodeSum = OPEN;
                    } else {
                        lastPowerValue = getLastCodeSum();
                    }
                } else if (TextUtils.equals(lastPowerStatus, "on")) {
                    lastPowerValue = getLastCodeSum();
                    saveLastDeviceStatus(lastPowerValue);
                }
                updateAll(lastCodeSum);
                setOtherKeyClickable(lastPowerStatus.equals("off"));
                sendControlCmd();
                break;
            case R.id.air_image_speed:
                if (lastSpeed == 3) {
                    lastSpeed = 0;
                } else {
                    ++lastSpeed;
                }
                updateSpeed("s" + lastSpeed);
                sendControlCmd();
                break;
            case R.id.air_image_swing_v:
                if (TextUtils.equals(lastLR, "l0")) {
                    lastLR = "l1";
                } else if (TextUtils.equals(lastLR, "l1")) {
                    lastLR = "l0";
                }
                updateSwing(lastUD, lastLR);
                sendControlCmd();
                break;
            case R.id.air_image_swing_h:
                if (TextUtils.equals(lastUD, "u0")) {
                    lastUD = "u1";
                } else if (TextUtils.equals(lastUD, "u1")) {
                    lastUD = "u0";
                }
                updateSwing(lastUD, lastLR);
                sendControlCmd();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private void getCurrentCodeLib() {
        dataApiUnit.doGetWifiIRCodeList(deviceID, blockId, new DataApiUnit.DataApiCommonListener<ControllerBlocksBean>() {
            @Override
            public void onSuccess(ControllerBlocksBean bean) {
//                WLog.i(TAG, "onSuccess: " + bean.blocks);
                if (bean != null && bean.blocks != null) {
                    keys = bean.blocks.get(0).keys;
                    isFullKeyFormat();
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    /**
     * 判断是否是完整的key
     * r_s0_27_u0_l0_p0 是完整的
     * r_s0_27_u0__p0 不是完整的
     * r_s0_27__l0_p0 不是完整的
     * r_s0_27___p0 不是完整的
     *
     * @return
     */
    private Boolean isFullKeyFormat() {
        if (keys != null && keys.size() > 0) {
            String[] array = keys.get(0).keyId.split("_");
            if (TextUtils.isEmpty(array[3])) {
                isSupportSwingH = false;
                mImageSwingH.setEnabled(isSupportSwingH);
            }
            if (TextUtils.isEmpty(array[4])) {
                isSupportSwingV = false;
                mImageSwingV.setEnabled(isSupportSwingV);
            }
        }
        return isSupportSwingV && isSupportSwingH;
    }


    /**
     * 除湿和自动模式下温度加减不可点击
     */
    private boolean isEnableAdjustTemperature() {
        if (lastMode.equals("d") || lastMode.equals("a")) {
            return false;
        } else {
            return true;
        }
    }

    private void setTmpButtonClickable() {
        if (isEnableAdjustTemperature()) {
            mImageTempDown.setEnabled(true);
            mImageTempUp.setEnabled(true);
        } else {
            mImageTempDown.setEnabled(false);
            mImageTempUp.setEnabled(false);
        }
    }

    /**
     * 获取上次退出时设置的状态
     */
    private void getLastDeviceStatus() {
        dataApiUnit.doGetDeviceKeyValue(deviceID, "wifiInfrared" + deviceID + blockId, new DataApiUnit.DataApiCommonListener<KeyValueListBean>() {
            @Override
            public void onSuccess(KeyValueListBean bean) {
                if (bean != null && bean.keyValues != null && bean.keyValues.size() > 0) {
                    String mValue = null;
                    for (KeyValueBean b : bean.keyValues
                            ) {
                        if (TextUtils.equals("wifiInfrared" + deviceID + blockId, b.key)) {
                            mValue = b.value;
                            WLog.i(TAG, "获取到的数值: " + mValue);
                        }
                    }
                    if (!TextUtils.isEmpty(mValue)) {
                        if (mValue.startsWith("off#") && mValue.length() > 4) {
                            hasSet = true;
                            updateAll(mValue.substring(4, mValue.length()));
                            setOtherKeyClickable(false);
                        } else {
                            lastPowerStatus = "on";
                            updateAll(mValue);
                        }

                    }
                } else {
                    hasSet = false;//从未设置过
                    setOtherKeyClickable(false);
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    /**
     * 根据开关状态确定其他按键是否点击
     */
    private void setOtherKeyClickable(boolean isEnable) {
        mImageMode.setEnabled(isEnable);
        mImageSwingH.setEnabled(isEnable);
        mImageSwingV.setEnabled(isEnable);
        mImageSpeed.setEnabled(isEnable);
        mImageTempUp.setEnabled(isEnable && isEnableAdjustTemperature());
        mImageTempDown.setEnabled(isEnable && isEnableAdjustTemperature());
        if (isEnable) {
            lastPowerStatus = "on";
            mRelativeOn.setVisibility(View.VISIBLE);
            mRelativeOff.setVisibility(View.INVISIBLE);
        } else {
            lastPowerStatus = "off";
            mRelativeOn.setVisibility(View.INVISIBLE);
            mRelativeOff.setVisibility(View.VISIBLE);
        }

    }

    private String[] getKeys(String code) {
        return code.split("_");
    }

    /**
     * 退出时同步当前空调的状态
     *
     * @param value
     */
    private void saveLastDeviceStatus(String value) {
        if (!hasSet) {
            value = "off#" + OPEN;
        } else if (lastPowerStatus.equals("off")) {
            value = "off#" + value;
        }
        WLog.i(TAG, "保存: " + value);
        dataApiUnit.doSaveDeviceKeyValue(deviceID, "wifiInfrared" + deviceID + blockId, value, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    /**
     * 刷新全部
     */
    private void updateAll(String data) {
        String array[] = getKeys(data);
        updateState(lastPowerStatus);
        if (array != null && array.length > 4) {
            updateMode(array[0]);
            updateSpeed(array[1]);
            updateTemp(array[2]);
            updateSwing(array[3], array[4]);
            setTmpButtonClickable();
        }
    }


    /**
     * 刷新风速
     */
    private void updateSpeed(String data) {
        mImageSpeed.setEnabled(true);
        int speed = 0;
        if (!TextUtils.isEmpty(data) && data.length() > 1) {
            speed = Integer.parseInt(data.substring(1, 2));
            lastSpeed = speed;
        }
        ObjectAnimator.ofFloat(mImageStateSpeed, View.ROTATION, 0f, 90f, 180f, 360f, 720f).setDuration(700).start();

        final int finalSpeed = speed;

        mImageStateSpeed.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageStateSpeed.setImageResource(mSpeedResArr[finalSpeed]);
            }
        }, 666);
    }


    /**
     * 刷新模式
     */
    public void updateMode(String data) {
        mImageMode.setEnabled(true);
        String mode = null;
        if (!TextUtils.isEmpty(data)) {
            mode = data;
        }

        if (mode == lastMode) {
            return;
        }

        AnimatorSet setOut = new AnimatorSet();
        // 退场
        switch (lastMode) {
            case "r":
                // 制冷
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 1f, 0.6f, 0f))
                        .with(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 1f, 0.6f, 0f));
                break;
            case "h":
                // 制热
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.ROTATION, 0f, 360f));
                break;
            case "d":
                // 除湿
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, 0f, 100f));
                break;
            case "w":
                // 送风
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, 0f, 100f));
                break;
            case "a":
            default:
                // 自动
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 1f, 0f));
                break;
        }

        setOut.setDuration(CHANGE_MODE_DURATION).start();

        if (!TextUtils.isEmpty(mode)) {
            lastMode = mode;
        }

        // 进场
        switch (mode) {
            case "r":
                // 制冷
                mImageStateMode.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageStateMode.setImageResource(mModeResArr[0]);
                        mTextStateMode.setText(mModeNameArr[0]);
                        AnimatorSet setIn = new AnimatorSet();
                        AnimatorSet.Builder b = setIn.play(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 0f, 0.4f, 1f))
                                .with(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 0f, 0.4f, 1f));
                        if (mImageStateMode.getScaleX() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 1f, 0f));
                        }
                        if (mImageStateMode.getScaleY() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 1f, 0f));
                        }
                        if (mImageStateMode.getAlpha() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 0f, 1f));
                        }
                        if (mImageStateMode.getTranslationX() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, -100f, 0f));
                        }
                        if (mImageStateMode.getTranslationY() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, -100f, 0f));
                        }
                        setIn.setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case "h":
                // 制热
                mImageStateMode.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageStateMode.setImageResource(mModeResArr[1]);
                        mTextStateMode.setText(mModeNameArr[1]);
                        AnimatorSet setIn = new AnimatorSet();
                        AnimatorSet.Builder b = setIn.play(ObjectAnimator.ofFloat(mImageStateMode, View.ROTATION, 0f, -360f));
                        if (mImageStateMode.getAlpha() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 0f, 1f));
                        }
                        if (mImageStateMode.getTranslationX() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, -100f, 0f));
                        }
                        if (mImageStateMode.getTranslationY() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, -100f, 0f));
                        }
                        if (mImageStateMode.getScaleX() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 0f, 1f));
                        }
                        if (mImageStateMode.getScaleY() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 0f, 1f));
                        }
                        setIn.setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case "d":
                // 除湿
                mImageStateMode.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageStateMode.setImageResource(mModeResArr[3]);
                        mTextStateMode.setText(mModeNameArr[3]);
                        AnimatorSet setIn = new AnimatorSet();
                        AnimatorSet.Builder b = setIn.play(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, -100f, 0f));
                        mImageStateMode.setTranslationY(-100f);
                        if (mImageStateMode.getAlpha() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 0f, 1f));
                        }
                        if (mImageStateMode.getTranslationX() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, -100f, 0f));
                        }
                        if (mImageStateMode.getScaleX() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 0f, 1f));
                        }
                        if (mImageStateMode.getScaleY() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 0f, 1f));
                        }
                        setIn.setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case "w":
                // 送风
                mImageStateMode.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageStateMode.setImageResource(mModeResArr[4]);
                        mTextStateMode.setText(mModeNameArr[4]);
                        AnimatorSet setIn = new AnimatorSet();
                        AnimatorSet.Builder b = setIn.play(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, -100f, 0f));
                        mImageStateMode.setTranslationX(-100f);
                        if (mImageStateMode.getAlpha() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 0f, 1f));
                        }
                        if (mImageStateMode.getTranslationY() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, -100f, 0f));
                        }
                        if (mImageStateMode.getScaleX() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 0f, 1f));
                        }
                        if (mImageStateMode.getScaleY() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 0f, 1f));
                        }
                        setIn.setDuration(CHANGE_MODE_DURATION).start();
//                        ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, -100f, 0f).setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case "a":
            default:
                // 自动
                mImageStateMode.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageStateMode.setImageResource(mModeResArr[2]);
                        mTextStateMode.setText(mModeNameArr[2]);
                        AnimatorSet setIn = new AnimatorSet();
                        AnimatorSet.Builder b = setIn.play(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 0f, 1f));
                        if (mImageStateMode.getAlpha() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 1f, 0f));
                        }
                        if (mImageStateMode.getTranslationX() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, -100f, 0f));
                        }
                        if (mImageStateMode.getTranslationY() > 0f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, -100f, 0f));
                        }
                        if (mImageStateMode.getScaleX() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 0f, 1f));
                        }
                        if (mImageStateMode.getScaleY() < 1f) {
                            b.after(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 0f, 1f));
                        }
                        setIn.setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
        }
    }

    /**
     * 刷新状态 power
     */
    private void updateState(String lastPowerStatus) {
        mImagePower.setEnabled(true);
        mRelativeOn.setVisibility("on".equals(lastPowerStatus) ? View.VISIBLE : View.INVISIBLE);
        mRelativeOff.setVisibility("on".equals(lastPowerStatus) ? View.INVISIBLE : View.VISIBLE);

        if (!mState) {
            ObjectAnimator.ofFloat(mImageCircle, View.ALPHA, 1f, 0f, 1f, 0f, 1f, 0f, 1f).setDuration(2000).start();
        }
    }

    /**
     * 刷新温度
     */
    private void updateTemp(String data) {
        mImageTempDown.setEnabled(true);
        mImageTempUp.setEnabled(true);

        int f, s;
        int temp = Integer.parseInt(data);
        lastTemp = temp;
        if (temp >= 10) {
            f = temp % 100 / 10;
        } else {
            f = 0;
        }
        s = temp % 10;
        mImageTemFirst.setImageResource(mTemBumResArr[f]);
        mImageTemSecond.setImageResource(mTemBumResArr[s]);
    }


    /**
     * 刷新风向
     */
    private void updateSwing(String u, String l) {
        mImageSwingV.setEnabled(true);
        mImageSwingH.setEnabled(true);
        lastUD = u;
        lastLR = l;
        switch (u + l) {
            case "u0l0":
                mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_n);
                break;
            case "u0l1":
                mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_v);
                break;
            case "u1l0":
                mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_h);
                break;
            case "u1l1":
                mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_a);
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute
                            attribute) {
                        if (cluster.clusterId == 0x0F01) {
                            switch (attribute.attributeId) {
                                case 0x8002:
                                    parserControlData(attribute.attributeValue);
                                    break;
                            }
                        }
                    }
                });
            }
        }
    }


    private void parserControlData(String attribute) {
        switch (attribute) {
            case WifiIRManage.MODE_CONTROL_SUCCESS:
                WLog.i(TAG, "IR_SUCCESS");
                break;
            case WifiIRManage.MODE_CONTROL_FAIL:
                WLog.i(TAG, "DATA_ERROR");
                break;
            case WifiIRManage.MALLOC_FAIL2_6:
                WLog.i(TAG, "MALLOC_FAIL2_6");
                break;
            case WifiIRManage.CHECK_FAIL:
                WLog.i(TAG, "CHECK_FAIL");
                break;
        }
    }

    /**
     * 数据拼接
     *
     * @return
     */
    private String getLastCodeSum() {
        //{模式}_{风量}_{温度}_{上下扫风}_{左右扫风}_{睡眠}
        String mMode = lastMode;
        String mSpeed = "s" + lastSpeed;
        String mTemp = lastTemp + "";
        String mUD = lastUD;
        String mLR = lastLR;

//        WLog.i(TAG, "mMode =  " + mMode);
//        WLog.i(TAG, "mSpeed =  " + mSpeed);
//        WLog.i(TAG, "mTemp =  " + mTemp);
//        WLog.i(TAG, "mUD =  " + mUD);
//        WLog.i(TAG, "mLR =  " + mLR);
        lastCodeSum = lastMode + "_" + "s" + lastSpeed + "_" + lastTemp + "_" + lastUD + "_" + lastLR + "_" + "p0";
        WLog.i(TAG, "面板上的数值: " + lastCodeSum);
        return lastCodeSum;
    }


    /**
     * 发送控制命令
     */
    private void sendControlCmd() {
        String recordData = null;
        String controlData = null;
        boolean hasKeyId = false;
        if (TextUtils.equals("off", lastPowerStatus)) {
            recordData = "off";
            controlData = recordData;
        } else {
            recordData = getLastCodeSum();
            if (!isFullKeyFormat()) {
                String[] array = recordData.split("_");
                if (!isSupportSwingH) {
                    array[3] = "";
                }
                if (!isSupportSwingV) {
                    array[4] = "";
                }
                controlData = array[0] + "_" + array[1] + "_" + array[2] + "_" + array[3] + "_" + array[4] + "_" + array[5];
                Log.i(TAG, "控制的真实数据: " + controlData);
                Log.i(TAG, "纪录的数据: " + recordData);
            }

        }
        if (keys == null) {
            return;
        }
        for (ControllerBlocksBean.keyBean keyBean :
                keys) {
            if (keyBean.keyId.equals(controlData)) {
                hasKeyId = true;
                break;
            }
        }
        if (!hasKeyId) {
            isHasSet = false;
            ToastUtil.show(getString(R.string.IF_Code_library));
            return;
        }
        VibratorUtil.holdSpeakVibration();

        if (isSceneOrHouseKeeper && isHasSet) {
            isHasSet = false;
            EventBus.getDefault().post(new WifiIRSceneEvent("controlDevice", deviceID, "0", blockId, controlData, blockName));
            setResult(RESULT_OK);
            finish();
        } else {
            //点击间隔0.5s防止数据错误
            long currentTime = (new Date()).getTime();
            if (currentTime - clickTime > 500) {
                WLog.i(TAG, "time interval: " + (currentTime - clickTime));
                clickTime = currentTime;
                Log.i(TAG, "sendControlCmd: " + controlData);
                dataApiUnit.doControlWifiIR(deviceID, "0", blockId, controlData, null, new DataApiUnit.DataApiCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        }
    }

}
