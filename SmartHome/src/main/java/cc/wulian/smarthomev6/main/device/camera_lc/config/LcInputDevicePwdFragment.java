package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lechange.opensdk.media.DeviceInitInfo;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.camera_lc.business.Business;
import cc.wulian.smarthomev6.main.device.camera_lc.business.util.TaskPoolHelper;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class LcInputDevicePwdFragment extends WLFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private final static String KEY_INIT = "INIT";
    private final int startPolling = 0x10;
    private final int successOnline = 0x11;
    private final int asynWaitOnlineTimeOut = 0x12;
    private final int successAddDevice = 0x13;
    // private final int successOffline = 0x14;
    // private final int addDeviceTimeOut = 0x15;
    // private final int failedAddDevice = 0x16;
    // private final int successOnlineEx = 0x17;
    private final int deviceInitSuccess = 0x18;
    private final int deviceInitFailed = 0x19;
    private final int deviceInitByIPFailed = 0x1A;
    private final int deviceSearchSuccess = 0x1B;
    private final int deviceSearchFailed = 0x1C;
    private final int INITMODE_UNICAST = 0;
    private final int INITMODE_MULTICAST = 1;
    private int curInitMode = INITMODE_MULTICAST;
    private boolean mIsOffline = true;
    private boolean mIsDeviceSearched = false; //设备初始化标志，保证设备初始化接口只调用一次
    private boolean mIsDeviceInitSuccess = false;
    private LinearLayout llImgTips;
    private TextView tvTips;
    private TextWatcher passwordWatcher;
    private Button btnNextStep;
    private ImageView ivClearPwd;
    private CheckBox cbPwdShow;
    private EditText etWifiPwd;
    private LcConfigWifiModel configData;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private Handler mHandler;
    private String key = "";
    private LcConfigSuccessFragment successFragment;
    private LcConfigFailFragment failFragment;
    private LinearLayout llTips;
    private TextView tvTips1;
    private ButtonSkinWrapper buttonSkinWrapper;

    public static LcInputDevicePwdFragment newInstance(LcConfigWifiModel configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        LcInputDevicePwdFragment inputDevicePwdFragment = new LcInputDevicePwdFragment();
        inputDevicePwdFragment.setArguments(bundle);
        return inputDevicePwdFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_lc_input_device_pwd;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
        Bundle bundle = getArguments();
        configData = (LcConfigWifiModel) bundle.getSerializable("configData");
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        buttonSkinWrapper = new ButtonSkinWrapper(btnNextStep);
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.AddDevice_AddDevice));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initView(View view) {
        llImgTips = view.findViewById(R.id.ll_img_tips);
        llTips = view.findViewById(R.id.ll_tips);
        tvTips1 = view.findViewById(R.id.tv_tip1);
        btnNextStep = view.findViewById(R.id.btn_next_step);
        ivClearPwd = view.findViewById(R.id.iv_clear_pwd);
        cbPwdShow = view.findViewById(R.id.cb_wifi_pwd_show);
        etWifiPwd = view.findViewById(R.id.et_wifi_pwd);
        etWifiPwd.setTypeface(Typeface.DEFAULT);
        etWifiPwd.setTransformationMethod(new PasswordTransformationMethod());
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String s = StringUtil.deleteEmoji(source.toString());
                if (TextUtils.equals(s, source)) {
                    return null;
                }
                return s;
            }
        };
        etWifiPwd.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(32)});
    }

    @Override
    public void initListener() {
        super.initListener();
        initWatcher();
        etWifiPwd.addTextChangedListener(passwordWatcher);
        cbPwdShow.setOnCheckedChangeListener(this);
        btnNextStep.setOnClickListener(this);
        ivClearPwd.setOnClickListener(this);
        updateButtonState();
    }

    @Override
    protected void initData() {
        super.initData();
        llImgTips.setVisibility(configData.getmStatus() == 1 ? View.VISIBLE : View.GONE);
        llTips.setVisibility(configData.getmStatus() == 1 ? View.VISIBLE : View.GONE);
        tvTips1.setText(configData.getmStatus() == 1 ? R.string.add_deviceLC_password_8_32 : R.string.add_deviceLC_password_4);
        initHandler();
    }

    private void initWatcher() {

        passwordWatcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void updateButtonState() {
        if (etWifiPwd.getText().length() < 8) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }


    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(LogUtil.LC_TAG, "msg.what" + msg.what);
                switch (msg.what) {
                    case deviceSearchSuccess:
                        Log.d(LogUtil.LC_TAG, "deviceSearchSuccess");
                        initDevice(((DeviceInitInfo) msg.obj).mMac, ((DeviceInitInfo) msg.obj).mIp, curInitMode);
                        break;
                    case deviceSearchFailed:
                        Log.d(LogUtil.LC_TAG, "deviceSearchFailed:" + (String) msg.obj);
                        break;
                    case deviceInitSuccess:
                        Log.d(LogUtil.LC_TAG, "deviceInitSuccess");
                        mIsDeviceInitSuccess = true;
                        Log.d(LogUtil.LC_TAG, "开始绑定: ");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ProgressDialogManager.getDialogManager().dimissDialog(KEY_INIT, 0);
                                bindDevice();
                            }
                        }, 5 * 1000);
                        break;
                    case deviceInitFailed:
                        Log.d(LogUtil.LC_TAG, "deviceInitFailed: " + (String) msg.obj);
                        //组播失败后走单播
                        curInitMode = INITMODE_UNICAST;

                        searchDevice();
                        break;
                    case deviceInitByIPFailed:
                        Log.d(LogUtil.LC_TAG, "deviceInitByIPFailed: " + (String) msg.obj);
                        curInitMode = INITMODE_MULTICAST;
                        break;
                    case startPolling:
//                        checkOnline();
                        break;
                    case asynWaitOnlineTimeOut:
                        Log.d(LogUtil.LC_TAG, "checkIsOnlineTimeOut");
                        break;
                    case successOnline:
                        Log.d(LogUtil.LC_TAG, "successOnline");
                        if (!mIsDeviceSearched) {
                            mIsDeviceSearched = true;
                            searchDevice();
                        } else if (mIsDeviceInitSuccess) {//设备初始化成功且在线，则绑定
//                            unBindDeviceInfo();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void initDevice(final String mMac, String mIp, int initMode) {
        //1.使用组播进行初始化（initMode=INITMODE_MULTICAST）,走else流程
        //2.组播失败后再使用单播（initMode=INITMODE_UNICAST），此时直接使用组播时输入的秘钥进行初始化
        if (initMode == INITMODE_UNICAST) {
            Business.getInstance().initDeviceByIP(mMac, mIp, key, new Handler() {
                public void handleMessage(Message msg) {
                    String message = (String) msg.obj;
                    if (msg.what == 0) {
                        mHandler.obtainMessage(deviceInitSuccess, message).sendToTarget();
                    } else {
                        mHandler.obtainMessage(deviceInitByIPFailed, message).sendToTarget();
                    }
                }
            });
        } else {
            final String deviceId = configData.getDeviceId();
            final int status = configData.getmStatus();
            //not support init
            if (status == 0 && !Business.getInstance().isOversea) {
                key = "";
                mHandler.obtainMessage(deviceInitSuccess, "inner, go bind without key").sendToTarget();
            } else {
                if (status == 0 || status == 2) {
                    if (Business.getInstance().isOversea) {

                        checkPwdValidity(deviceId, key, mHandler);
                    } else {
                        mHandler.obtainMessage(deviceInitSuccess, "Inner, go bind with key").sendToTarget();
                    }
                } else if (status == 1) {
                    Business.getInstance().initDevice(mMac, key, new Handler() {
                        public void handleMessage(Message msg) {
                            String message = (String) msg.obj;
                            if (msg.what == 0) {
                                mHandler.obtainMessage(deviceInitSuccess, message).sendToTarget();
                            } else {
                                mHandler.obtainMessage(deviceInitFailed, message).sendToTarget();
                            }
                        }

                        ;
                    });
                }
            }
        }
    }

    private void searchDevice() {
        Business.getInstance().searchDevice(configData.getDeviceId(), 15000, new Handler() {
            public void handleMessage(final Message msg) {
                if (msg.what < 0) {
                    if (msg.what == -2)
                        mHandler.obtainMessage(deviceSearchFailed, "device not found").sendToTarget();
                    else
                        mHandler.obtainMessage(deviceSearchFailed, "StartSearchDevices failed").sendToTarget();
                    return;
                }

                mHandler.obtainMessage(deviceSearchSuccess, msg.obj).sendToTarget();
            }
        });
    }

    private void checkPwdValidity(final String deviceId, final String key, final Handler handler) {
        TaskPoolHelper.addTask(new TaskPoolHelper.RunnableTask("real") {
            @Override
            public void run() {
                if (0 == Business.getInstance().checkPwdValidity(deviceId, key)) {
                    handler.obtainMessage(deviceInitSuccess, "checkPwdValidity success").sendToTarget();
                } else {
                    handler.obtainMessage(deviceInitByIPFailed, "checkPwdValidity failed").sendToTarget();
                }
            }
        });
    }

    private void bindDevice() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(getActivity());
        deviceApiUnit.doBindDevice(configData.getDeviceId(), etWifiPwd.getText().toString(), configData.getDeviceType(), new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                Log.i(LogUtil.LC_TAG, "绑定乐橙摄像机成功 ");
                successFragment = LcConfigSuccessFragment.newInstance(configData);
                ft = manager.beginTransaction();
                ft.replace(android.R.id.content, successFragment);
                ft.addToBackStack(null);
                ft.commit();
            }

            @Override
            public void onFail(int code, String msg) {
                Log.i(LogUtil.LC_TAG, "绑定乐橙摄像机失败 ");
                failFragment = LcConfigFailFragment.newInstance(configData);
                ft = manager.beginTransaction();
                ft.replace(android.R.id.content, failFragment);
                ft.addToBackStack(null);
                ft.commit();
            }

        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_wifi_pwd_show) {
            if (isChecked) {
                etWifiPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etWifiPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            CharSequence charsequence = etWifiPwd.getText();
            if (charsequence instanceof Spannable) {
                Spannable spanText = (Spannable) charsequence;
                Selection.setSelection(spanText, charsequence.length());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear_pwd:
                etWifiPwd.setText("");
                break;
            case R.id.btn_next_step:
                if (RegularTool.isIllegal(etWifiPwd.getText().toString()) || !RegularTool.isLcPwdRule(etWifiPwd.getText().toString())) {
                    ToastUtil.show(R.string.add_deviceLC_password_3);
                } else {
                    ProgressDialogManager.getDialogManager().showDialog(KEY_INIT, getActivity(), null, null, 10 * 1000);
                    key = etWifiPwd.getText().toString();
                    initDevice(configData.getmMac(), configData.getmIp(), curInitMode);
                }
                break;
        }
    }
}
