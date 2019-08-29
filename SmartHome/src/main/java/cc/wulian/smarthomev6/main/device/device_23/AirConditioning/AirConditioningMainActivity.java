package cc.wulian.smarthomev6.main.device.device_23.AirConditioning;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.support.v4.widget.Space;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.uei.control.ACEService;
import com.uei.control.AirConFunction;
import com.uei.control.AirConSDKManager;
import com.uei.control.AirConState;
import com.uei.control.AirConWidgetStatus;
import com.uei.control.ResultCode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.ControllerMoreActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.BrandBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueListBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.event.UEIEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.dialog.MessageListenerAdapter;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/8/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    红外转发器 空调页面
 */
public class AirConditioningMainActivity extends BaseTitleActivity implements View.OnClickListener {

    private final static int TYPE_CONTROL = 0;
    private final static int TYPE_MATCH = 1;

    private final static String ADD_AIR = "ADD_AIR";
    private boolean isSceneOrHouseKeeper;

    private ImageView mImageStateSwing, mImageStateMode, mImageStateSpeed, mImageTemFirst,
            mImageTemSecond, mImageCircle, mImagePower, mImageMode, mImageSpeed,
            mImageSwingV, mImageSwingH, mImageTempDown, mImageTempUp, mImageStateTempUnit;
    private TextView mTextStateMode, mTextPrep, mTextNext, mTextDownload,
            mTextCodeNum, mTextName;
    private RelativeLayout mRelativeOn, mRelativeOff;
    private ViewStub mStubMatch;
    private LinearLayout mLinearMatch;
    private Space mSpace;
    private View mViewTitleImage;

    private String deviceID;
    private String controlData;
    private String state;
    private String mUeiUserID, brandName, mLocalName, mCreatedTime, mName, mPickCode;
    private int mStartType = TYPE_CONTROL;
    // 码库列表
    private List<String> mCodeList = new ArrayList<>();
    // 当前选择的码库的下标
    private int mCheckIndex = 0;
    // 码库文件列表
    private List<String> mCodeFileList = new ArrayList<>();
    private Map<String, String> codeMap = new ArrayMap<>();
    private DataApiUnit mApiUnit;
    // 按钮对应 ID
//    private ArrayMap<String, Integer> mKeyIdMap;
    private ArraySet<Integer> mKeyIdSet;

    private static final int CHANGE_MODE_DURATION = 500;

    private boolean mState = false;
    private AirShow mAirShow;
    private DataApiUnit dataApiUnit;

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

    public static void startForScene(Activity context, String deviceID, String name, String brand, String pick, String time) {
        Intent intent = new Intent(context, AirConditioningMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mStartType", TYPE_CONTROL);
        intent.putExtra("name", name);
        intent.putExtra("brand", brand);
        intent.putExtra("pick", pick);
        intent.putExtra("time", time);
        intent.putExtra("isSceneOrHouseKeeper", true);
        context.startActivityForResult(intent, 1);
    }

    /**
     * 遥控
     *
     * @param name  遥控器名
     * @param brand 品牌
     * @param pick  码库
     */
    public static void start(Context context, String deviceID, String name, String brand, String pick, String time) {
        Intent intent = new Intent(context, AirConditioningMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mStartType", TYPE_CONTROL);
        intent.putExtra("name", name);
        intent.putExtra("brand", brand);
        intent.putExtra("pick", pick);
        intent.putExtra("time", time);
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }

    /**
     * 匹配品牌
     *
     * @param brandName 品牌名
     */
    public static void match(Context context, String deviceID, String brandName, String localName) {
        Intent intent = new Intent(context, AirConditioningMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("startType", TYPE_MATCH);
        intent.putExtra("brandName", brandName);
        if (localName != null) {
            intent.putExtra("localName", localName);
        }
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_conditioning_main, true);
        EventBus.getDefault().register(this);


        // 初始化标题
        setTitle();

    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Intent intent = getIntent();
        // 获取 intent
        deviceID = intent.getStringExtra("deviceID");
        brandName = intent.getStringExtra("brandName");
        mLocalName = intent.getStringExtra("localName");
        mCreatedTime = intent.getStringExtra("time");
        mName = intent.getStringExtra("name");
        mPickCode = intent.getStringExtra("pick");
        mStartType = intent.getIntExtra("startType", TYPE_CONTROL);
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);

        setTitle();
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (mStartType == TYPE_MATCH) {
//            mTextName.setText(R.string.Infraredrelay_Addremote_Matchingmodel);
            setToolBarTitleAndRightBtn(R.string.Infraredrelay_Addremote_Matchingmodel, R.string.Infraredtransponder_Airconditioner_Instructions);
        } else {
            if (isSceneOrHouseKeeper) {
                if (!TextUtils.isEmpty(mName)) {
//                mTextName.setText(mName);
                    setToolBarTitleAndRightBtn(mName, getResources().getString(R.string.Config_Next_Step));
                } else {
//                mTextName.setText(R.string.Infraredtransponder_List_Airconditioner);
                    setToolBarTitleAndRightBtn(R.string.Infraredtransponder_List_Airconditioner, R.string.Config_Next_Step);
                }
            } else {
                if (!TextUtils.isEmpty(mName)) {
//                mTextName.setText(mName);
                    setToolBarTitleAndRightImg(mName, R.drawable.icon_more);
                } else {
//                mTextName.setText(R.string.Infraredtransponder_List_Airconditioner);
                    setToolBarTitleAndRightImg(R.string.Infraredtransponder_List_Airconditioner, R.drawable.icon_more);
                }
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
//        mImageMore = (ImageView) findViewById(R.id.air_image_more);
        mImageTemFirst = (ImageView) findViewById(R.id.air_num_first);
        mImageTemSecond = (ImageView) findViewById(R.id.air_num_second);
        mImageStateTempUnit = (ImageView) findViewById(R.id.air_state_image_unit);
        mImageCircle = (ImageView) findViewById(R.id.air_image_circle);
        mTextStateMode = (TextView) findViewById(R.id.air_state_text_mode);
        mTextName = (TextView) findViewById(R.id.air_text_title);
//        mTextInstructions = (TextView) findViewById(R.id.air_text_instructions);
        mRelativeOn = (RelativeLayout) findViewById(R.id.air_state_relate_on);
        mRelativeOff = (RelativeLayout) findViewById(R.id.air_state_relate_off);
        mStubMatch = (ViewStub) findViewById(R.id.device_23_stub_match);
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

        mApiUnit = new DataApiUnit(this);
        dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doGetDeviceKeyValue(deviceID, mCreatedTime, new DataApiUnit.DataApiCommonListener<KeyValueListBean>() {
            @Override
            public void onSuccess(KeyValueListBean bean) {
                if (TextUtils.equals(bean.deviceId, deviceID)) {
                    for (KeyValueBean b : bean.keyValues
                            ) {
                        if (TextUtils.equals(b.key, mCreatedTime)) {
                            state = b.value;
                            doWithControlMode();
                        }
                    }
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });

        // 初始化 uei sdk
        AirConSDKManager.initialize(this);
        // 拿到 uei 用户 ID
        // deviceID 添加时间戳, 保证每次时间不一样
        mUeiUserID = ACEService.ACEncryptUserId(deviceID + System.currentTimeMillis());
        // 初始化状态
        mAirShow = AirShow.showDefault();
        // 拿到本地 pick 列表
        File airFolder = new File(FileUtil.getUeiAirDataPath());
        File[] file_list = airFolder.listFiles();
        for (File f : file_list) {
            mCodeFileList.add(f.getName());
        }
        WLog.i(TAG, "mCodeFileList: " + mCodeFileList);

        // 根据启动状态 操作
        if (mStartType == TYPE_MATCH) {
            doWithMatchMode();
        } else {
            doWithControlMode();
        }

        updateAll();
    }

    private void doWithMatchMode() {
        mSpace.setVisibility(View.GONE);
        mStubMatch.setVisibility(View.VISIBLE);
//        mTextInstructions.setVisibility(View.VISIBLE);
//        mImageMore.setVisibility(View.INVISIBLE);

        mTextPrep = (TextView) findViewById(R.id.device_23_text_prev);
        mTextNext = (TextView) findViewById(R.id.device_23_text_next);
        mTextDownload = (TextView) findViewById(R.id.device_23_text_download);
        mTextCodeNum = (TextView) findViewById(R.id.device_23_text_code_no);
        mLinearMatch = (LinearLayout) findViewById(R.id.device_23_linear_match);

        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(mTextDownload, SkinResouceKey.BITMAP_AIRCOND_BUTTON_ROUND_DOWNLOAD_BG);

        mTextNext.setOnClickListener(this);
        mTextPrep.setOnClickListener(this);
        mTextDownload.setOnClickListener(this);
//        mTextInstructions.setOnClickListener(this);

        setCodeClickEnable();

        mApiUnit.doGetBrandCodeList("Z", brandName, mUeiUserID, new DataApiUnit.DataApiCommonListener<BrandBean.CodeListBean>() {
            @Override
            public void onSuccess(BrandBean.CodeListBean bean) {
                WLog.i(TAG, "onSuccess 0: " + bean.codes);
                mCodeList.addAll(bean.codes);
                setCodeClickEnable();
                // 显示码库
                showCurrentCode();
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i(TAG, "onFail 0: " + msg);
            }
        });

        if (!Preference.getPreferences().isShownInstructionForUeiAddDevice(deviceID)) {
            showInstruction();
            Preference.getPreferences().showInstructionForUeiAddDevice(deviceID);
        }
    }

    private void updateMatchPickName(String pick) {
        if (mStartType == TYPE_MATCH) {
            mTextCodeNum.setText(pick);
        }
    }

    private void doWithControlMode() {
        loadDiskPick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GatewayConfigEvent event) {
        if (!TextUtils.equals(event.bean.d, deviceID)) {
            return;
        }

        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.v)) {
                boolean hasMe = false;
                List<UeiConfig> configs = JSON.parseArray(bean.v, UeiConfig.class);
                for (UeiConfig config : configs) {
                    if (TextUtils.equals(mCreatedTime, config.time)) {
                        mName = config.getName();

                        setTitle();

                        hasMe = true;
                        break;
                    }
                }

                // 匹配模式
                if (mStartType == TYPE_MATCH) {
                    // 添加成功
                    if (hasMe) {
                        EventBus.getDefault().post(UEIEvent.addAir());
                        progressDialogManager.dimissDialog(ADD_AIR, 0);
                        ToastUtil.single(R.string.Config_Add_Success);
                        finish();
                    }
                    // 遥控模式
                } else if (mStartType == TYPE_CONTROL) {
                    // 删除成功
                    if (!hasMe) {
                        finish();
                    }
                }
            }
        } else {
            finish();
        }

    }

    @Override
    public void onClick(View v) {

        if (v.getTag() != null) {
            String tag = (String) v.getTag();
            int keyId = 0;
            try {
                keyId = Integer.valueOf(tag);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(v.getTag().toString()) && mKeyIdSet != null && mKeyIdSet.contains(keyId)) {
                WLog.i(TAG, "onClick: " + keyId);
                int result = ACEService.ACProcessKey(keyId);
                if (ResultCode.SUCCESS == result) {
                    WLog.i(TAG, "ACEService.ACProcessKey 执行成功！");
                    byte[] irData = ACEService.ACGetLastKeyPatternData();
                    if (irData != null && irData.length > 0) {
                        if (isSceneOrHouseKeeper) {
                            controlData = bytesToHexString(irData);
                            WLog.i(TAG, "controlData1---->: "+controlData);
                        } else {
                            String sendData = bytesToHexString(irData);
                            sendCmd(sendData);
                        }
                    }
                    getKeys();
                } else {
                    WLog.i(TAG, "ACEService.ACProcessKey 执行失败！");
                }
            }
        }

        switch (v.getId()) {
            case R.id.img_left:
                saveDeviceKeyValue();
                finish();
                break;
            case R.id.btn_right:
                if (isSceneOrHouseKeeper) {
                    if (TextUtils.isEmpty(controlData)) {
                        ToastUtil.show(getString(R.string.set_the_button_first));
                        break;
                    }
                    EventBus.getDefault().post(new UeiSceneEvent(mName, controlData));
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showInstruction();
                }
                break;
            case R.id.img_right:
                ControllerMoreActivity.start(AirConditioningMainActivity.this, deviceID, mCreatedTime, mName, "Z");
                break;
            case R.id.air_image_tem_up:
                break;
            case R.id.air_image_tem_down:
                break;
            case R.id.air_image_refresh:
                break;
            case R.id.air_image_close:
                break;
            case R.id.air_image_speed:
                break;
            case R.id.air_image_swing_v:
                break;
            case R.id.air_image_swing_h:
                break;
            case R.id.device_23_text_prev:
                prevCodeClick();
                break;
            case R.id.device_23_text_next:
                nextCodeClick();
                break;
            case R.id.device_23_text_download:
                downLoadPick();
                break;
            case R.id.device_23_text_code_no:
                break;
        }
    }


    private void saveDeviceKeyValue() {
        String value = ACEService.ACEngineStop();
        dataApiUnit.doSaveDeviceKeyValue(deviceID, mCreatedTime, value, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });

    }

    private WLDialog.Builder builder;
    private WLDialog dialog;

    /**
     * 添加失败
     */
    private void showAddFailure() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(R.string.Config_Add_Fail)
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredrelay_Downloadfailed_Prompt)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new MessageListenerAdapter() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        finish();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 显示使用说明弹框
     */
    private void showInstruction() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("1. ").append(getString(R.string.Infraredtransponder_Instructions_Tips1)).append('\n')
                .append("2. ").append(getString(R.string.Infraredtransponder_Instructions_Tips2)).append('\n')
                .append("3. ").append(getString(R.string.Infraredtransponder_Instructions_Tips3));
        TextView textView = new TextView(this);
        textView.setText(sb.toString());
        textView.setGravity(Gravity.START);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        int dp = DisplayUtil.dip2Pix(AirConditioningMainActivity.this, 16);
        textView.setPadding(dp, dp, dp, dp);
        builder = new WLDialog.Builder(this);
        builder.setTitle(R.string.Infraredtransponder_Airconditioner_Instructions)
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setContentView(textView)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        dialog.dismiss();
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

    /**
     * 遥控模式, 加载码库
     */
    private void loadDiskPick() {
        getPickByName(mPickCode);
    }

    /**
     * 下载当前码库
     * 然后将模式切换到  遥控模式
     */
    private void downLoadPick() {
        if (mCodeList == null || mCodeList.isEmpty() || mCodeList.size() <= mCheckIndex) {
            return;
        }
        String pick = mCodeList.get(mCheckIndex);
        int m = 2;
        String time = System.currentTimeMillis() + "";
        mCreatedTime = time;
        UeiConfig ueiConfig = UeiConfig.newUeiDevice(mCreatedTime, brandName, mLocalName, pick, "Z", null, null);
        List<UeiConfig> configs;
        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean == null) {
            m = 1;
            configs = new ArrayList<>();
        } else {
            if (TextUtils.isEmpty(bean.v)) {
                m = 1;
                configs = new ArrayList<>();
            } else {
                configs = JSON.parseArray(bean.v, UeiConfig.class);
            }
        }
        configs.add(ueiConfig);

        String v = Base64.encodeToString(JSON.toJSONString(configs).getBytes(), Base64.NO_WRAP);

        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                MQTTCmdHelper.createGatewayConfig(
                        Preference.getPreferences().getCurrentGatewayID(),
                        m,
                        MainApplication.getApplication().getLocalInfo().appID,
                        deviceID,
                        "list",
                        v,
                        System.currentTimeMillis() + ""
                ), MQTTManager.MODE_GATEWAY_FIRST
        );

        progressDialogManager.showDialog(ADD_AIR, AirConditioningMainActivity.this, null, new CustomProgressDialog.OnDialogDismissListener() {
            @Override
            public void onDismiss(CustomProgressDialog var1, int var2) {
                if (var2 != 0) {
                    // 添加失败
                    showAddFailure();
                }
            }
        }, getResources().getInteger(R.integer.http_timeout));
    }

    private String getFileNameByCode(String code) {
        return FileUtil.getUeiAirDataPath() + "/" + code + ".txt";
    }

    /**
     * 显示当前码库
     */
    private void showCurrentCode() {
        getPickByName(mCodeList.get(mCheckIndex));
    }

    private void getPickByName(final String pick) {
        final String pickFile = pick + ".txt";

        // 如果文件列表里有, 就用文件里的
        // 文件格式  码库.txt
        if (mCodeFileList.contains(pickFile)) {
            updateMatchPickName(pick);
            WLog.i(TAG, "码库 from 文件: " + FileUtil.readFileStr(getFileNameByCode(pick)));
            getKeys(FileUtil.readFileStr(getFileNameByCode(pick)));
        } else {
            // 如果文件里没有, 就下载
            // 下载完, 保存到文件
            mApiUnit.doGetBrandCode("Z", pick, mUeiUserID, new DataApiUnit.DataApiCommonListener<BrandBean.CodeBean>() {
                @Override
                public void onSuccess(BrandBean.CodeBean bean) {
                    WLog.i(TAG, "码库 from API: " + bean.picks);
                    // 下载完成后  保存
                    FileUtil.createNewFile(getFileNameByCode(pick), bean.picks);
                    codeMap.put(pick, bean.picks);
                    updateMatchPickName(pick);
                    getKeys(bean.picks);
                }

                @Override
                public void onFail(int code, String msg) {
                    WLog.i(TAG, "onFail 1: " + msg);
                }
            });
        }
    }

    private void getKeys(String picks) {
        String ss = state;
        int flag = ACEService.ACEngineStart(Base64.decode(picks, Base64.DEFAULT), state);
        WLog.i(TAG, "getKeys: flag - " + flag + ", ss - " + state);

        getKeys();
    }

    private void getKeys() {
        List<AirConState> states = new ArrayList<>();
        List<AirConFunction> aFunctions = new ArrayList<>();
        List<AirConWidgetStatus> status = new ArrayList<>();
        int result = ACEService.ACGetKeys(states, status, aFunctions);
        if (result == ResultCode.SUCCESS) {
            WLog.i(TAG, "ACEService.ACGetKeys 执行成功！");
//            mKeyIdMap = AirConHelper.convertKeysToIds(aFunctions);
            mKeyIdSet = AirConHelper.convertKeysToIdSet(aFunctions);

            AirConHelper.convertStatusToShow(states, status, aFunctions, mAirShow);
            updateAll();
        } else {
            WLog.i(TAG, "ACEService.ACGetKeys 执行失败！");
        }
    }

    /**
     * 点击
     * 下一个码库
     */
    private void nextCodeClick() {
        mCheckIndex += 1;

        if (mCheckIndex >= mCodeList.size()) {
            mCheckIndex = mCodeList.size() - 1;
        }

        setCodeClickEnable();

        showCurrentCode();
    }

    /**
     * 点击
     * 上一个码库
     */
    private void prevCodeClick() {
        mCheckIndex -= 1;

        if (mCheckIndex <= 0) {
            mCheckIndex = 0;
        }

        setCodeClickEnable();

        showCurrentCode();
    }

    /**
     * 设置 下一个 上一个  按钮是否可点击
     */
    private void setCodeClickEnable() {
        mTextNext.setEnabled(mCheckIndex < mCodeList.size() - 1);
        mTextPrep.setEnabled(mCheckIndex > 0);
    }

    /**
     * 刷新全部
     */
    private void updateAll() {
        updateState();
        updateMode();
        updateTemp();
        updateSpeed();
        updateSwing();
    }

    private int lastSpeed = 0;

    /**
     * 刷新风速
     */
    private void updateSpeed() {
        try {
            mImageSpeed.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImageSpeed.getTag())));
        } catch (Exception e) {
            mImageSpeed.setEnabled(false);
        }

        int speed = mAirShow.speed.value;
        if (speed >= mSpeedResArr.length) {
            speed = mSpeedResArr.length - 1;
        } else if (speed <= 0) {
            speed = 0;
        }

        if (speed == lastSpeed) {
            return;
        } else {
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


    private int lastMode = -1;

    /**
     * 刷新模式
     */
    public void updateMode() {
        try {
            mImageMode.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImageMode.getTag())));
        } catch (Exception e) {
            mImageMode.setEnabled(false);
        }

        int m = mAirShow.mode.value;
        AnimatorSet setOut = new AnimatorSet();
        if (m == lastMode) {
            return;
        }

        // 退场
        switch (lastMode) {
            case 1:
                // 制冷
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 1f, 0.6f, 0f))
                        .with(ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 1f, 0.6f, 0f));
                break;
            case 5:
                // 制热
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.ROTATION, 0f, 360f));
                break;
            case 2:
                // 除湿
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, 0f, 100f));
                break;
            case 3:
                // 送风
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_X, 0f, 100f));
                break;
            case 0:
            default:
                // 自动
                setOut.play(ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 1f, 0f));
                break;
        }

        setOut.setDuration(CHANGE_MODE_DURATION).start();

        lastMode = m;

        // 进场
        switch (m) {
            case 1:
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
//                        ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_X, 0f, 0.4f, 1f).setDuration(CHANGE_MODE_DURATION).start();
//                        ObjectAnimator.ofFloat(mImageStateMode, View.SCALE_Y, 0f, 0.4f, 1f).setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case 5:
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
//                        ObjectAnimator.ofFloat(mImageStateMode, View.ROTATION, 0f, -360f).setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case 2:
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
//                        ObjectAnimator.ofFloat(mImageStateMode, View.TRANSLATION_Y, -100f, 0f).setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
            case 3:
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
            case 0:
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
//                        ObjectAnimator.ofFloat(mImageStateMode, View.ALPHA, 0f, 1f).setDuration(CHANGE_MODE_DURATION).start();
                    }
                }, CHANGE_MODE_DURATION);
                break;
        }
    }

    /**
     * 刷新状态 power
     */
    private void updateState() {
        try {
            mImagePower.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImagePower.getTag())));
        } catch (Exception e) {
            mImagePower.setEnabled(false);
        }


        mRelativeOn.setVisibility(mAirShow.power.value == 1 ? View.VISIBLE : View.INVISIBLE);
        mRelativeOff.setVisibility(mAirShow.power.value == 1 ? View.INVISIBLE : View.VISIBLE);

        if (!mState) {
            ObjectAnimator.ofFloat(mImageCircle, View.ALPHA, 1f, 0f, 1f, 0f, 1f, 0f, 1f).setDuration(2000).start();
        }
    }

    /**
     * 刷新温度
     */
    private void updateTemp() {
        try {
            mImageTempDown.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImageTempDown.getTag())));
            mImageTempUp.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImageTempUp.getTag())));
        } catch (Exception e) {
            mImageTempDown.setEnabled(false);
            mImageTempUp.setEnabled(false);
        }

        int f, s, u;
        u = mAirShow.unit.value;
        int temp = mAirShow.temp.value;
        if (temp >= 10) {
            f = temp % 100 / 10;
        } else {
            f = 0;
        }
        s = temp % 10;
        mImageTemFirst.setImageResource(mTemBumResArr[f]);
        mImageTemSecond.setImageResource(mTemBumResArr[s]);

        mImageStateTempUnit.setImageResource(u == 0 ? R.drawable.icon_tem_c : R.drawable.icon_tem_f);
    }

    /**
     * 刷新风向
     */
    private void updateSwing() {
        try {
            mImageSwingH.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImageSwingH.getTag())));
            mImageSwingV.setEnabled(mKeyIdSet.contains(Integer.parseInt((String) mImageSwingV.getTag())));
        } catch (Exception e) {
            mImageSwingH.setEnabled(false);
            mImageSwingV.setEnabled(false);
        }

        if (mAirShow.swing_v.value != 0 && mAirShow.swing_h.value != 0) {
            mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_a);
        } else if (mAirShow.swing_v.value != 0) {
            mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_v);
        } else if (mAirShow.swing_h.value != 0) {
            mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_h);
        } else {
            mImageStateSwing.setImageResource(R.drawable.icon_air_state_swing_n);
        }
    }

    private void sendCmd(String data) {
        WLog.i(TAG, "sendCmd: " + data);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", Preference.getPreferences().getCurrentGatewayID());
            object.put("devID", deviceID);
            object.put("clusterId", 0x0F01);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0061);
            object.put("commandType", 1);
            object.put("commandId", 0x8010);
            JSONArray array = new JSONArray();
            array.put(data);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数组转换成十六进制字符串
     */
    private String bytesToHexString(byte[] bArray) {
        StringBuilder byteSb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("10000190");
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            byteSb.append(bArray[i]).append(",");
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);
        }
        sb.insert(0, Integer.toHexString(sb.length() / 2)).insert(0, "0A00");
        return sb.toString().toUpperCase();
    }
}
