package cc.wulian.smarthomev6.main.mine.setting;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wulian.sec.wuliansec.WuLianEncrypt;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.entity.UserPushInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.setts.MessageSettingsActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.MQTTEvent;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VersionUtil;
import cc.wulian.smarthomev6.support.utils.ViewUtils;

/**
 * Created by syf on 2017/2/15
 */
public class SettingActivity extends BaseTitleActivity {

    private static final String LOGOUT = "FORGOT_PASSWORD";
    private static final String CLEAR_CACHE = "CLEAR CACHE";
    private static final int REQUEST_CODE_RESET_PWD = 1;
    private static final int REQUEST_CODE_SET_PUSHTIME = 2;
    private RelativeLayout itemSettingSecurity;
    private RelativeLayout itemSettingAlarmVoice;
    private RelativeLayout itemSettingSkin;
    private RelativeLayout itemSettingCache;
    private RelativeLayout itemSettingVersion;
    private RelativeLayout itemSettingUserPush;
    private RelativeLayout itemSettingPushTime;
    private RelativeLayout itemSettingPushManage;
    private RelativeLayout itemSceneSetting;
    private ToggleButton itemSettingAlarmShake;
    private ToggleButton itemSettingAlarmUser;
    private LinearLayout mLinearSettings;
    private Button itemSettingLogout;
    private TextView tv_settingVer;
    private TextView tv_pushTime;

    private SsoApiUnit ssoApiUnit;
    private DeviceApiUnit mDeviceApi;
    private Dialog mDialog;
    private Handler mHandler = new Handler();
    private String pushTime;

    private int mLinearSettsMaxHeight;

    private int showVersionInfoCount = 0;
    private Runnable clearVersionInfoCountTask = new Runnable() {
        @Override
        public void run() {
            showVersionInfoCount = 0;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_setting, true);
        ssoApiUnit = new SsoApiUnit(this);
        mDeviceApi = new DeviceApiUnit(this);

        if (TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            getUserPush();
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Mine_Setts));
    }

    @Override
    protected void initView() {
        itemSettingSecurity = (RelativeLayout) findViewById(R.id.item_setting_security);
        itemSettingAlarmVoice = (RelativeLayout) findViewById(R.id.item_setting_alarm_voice);
        itemSettingSkin = (RelativeLayout) findViewById(R.id.item_setting_skin);
        itemSettingCache = (RelativeLayout) findViewById(R.id.item_setting_cache);
        itemSettingVersion = (RelativeLayout) findViewById(R.id.item_setting_version);
        itemSettingUserPush = (RelativeLayout) findViewById(R.id.item_setting_user_push);
        itemSettingPushTime = (RelativeLayout) findViewById(R.id.item_setting_push_time);
        itemSettingPushManage = (RelativeLayout) findViewById(R.id.item_setting_push_manage);
        itemSceneSetting = (RelativeLayout) findViewById(R.id.item_scene_setting);
        itemSettingAlarmShake = (ToggleButton) findViewById(R.id.item_setting_alarm_shake);
        itemSettingAlarmUser = (ToggleButton) findViewById(R.id.item_setting_alarm_user);
        tv_settingVer = (TextView) findViewById(R.id.tv_setting_version);
        tv_pushTime = (TextView) findViewById(R.id.tv_setting_push_time);
        itemSettingLogout = (Button) findViewById(R.id.item_setting_logout);

        mLinearSettings = (LinearLayout) findViewById(R.id.setting_linear);
        mLinearSettsMaxHeight = ViewUtils.getTargetHeight(mLinearSettings);

        if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_GW)) {
            itemSettingSecurity.setVisibility(View.GONE);
            itemSettingUserPush.setVisibility(View.GONE);
            itemSettingPushTime.setVisibility(View.GONE);
            itemSettingPushManage.setVisibility(View.GONE);
            itemSettingSkin.setVisibility(View.GONE);
        }

        itemSettingAlarmShake.setChecked(preference.getAlarmShake());
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
//        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
//        skinManager.setTextColor(itemSettingLogout, SkinResouceKey.COLOR_NAV);
    }

    @Override
    protected void initData() {
        final String sysVersion = "V" + VersionUtil.getVersionName(this);
        tv_settingVer.setText(sysVersion);
//        String language = MainApplication.getApplication().getLocalInfo().appLang;
//        if (!TextUtils.isEmpty(language)) {//语言环境过滤，被过滤的语言不显示报警语音选项
//            switch (language) {
//                case "ja":
//                    itemSettingAlarmVoice.setVisibility(View.GONE);
//                    break;
//            }
//        }
        if (LanguageUtil.isAllChina() || LanguageUtil.isEnglish()) {
            itemSettingAlarmVoice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListeners() {
        itemSettingSecurity.setOnClickListener(this);
        itemSettingAlarmVoice.setOnClickListener(this);
        itemSettingSkin.setOnClickListener(this);
        itemSettingCache.setOnClickListener(this);
        itemSettingVersion.setOnClickListener(this);
        itemSettingPushTime.setOnClickListener(this);
        itemSettingPushManage.setOnClickListener(this);
        itemSceneSetting.setOnClickListener(this);
        itemSettingLogout.setOnClickListener(this);
        itemSettingAlarmShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    preference.saveAlarmShake(true);
                } else {
                    preference.saveAlarmShake(false);
                }
            }
        });
        itemSettingAlarmUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setUserPush(isChecked);
            }
        });
    }

    private void getUserPush() {
        mDeviceApi.doQueryDevicePushSetts(null, "1", null, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                UserPushInfo info = (UserPushInfo) bean;
                if (info.userPushInfo == null || info.userPushInfo.isEmpty()) {
                    showSettsLinear(true);
                } else {
                    String timeValue = info.userPushInfo.get(0).time;
                    if (TextUtils.isEmpty(timeValue)) {
                        pushTime = "00:00,23:59";
                        tv_pushTime.setText(getText(R.string.mine_Setting_pushtime_from) + "00:00" + getText(R.string.mine_Setting_pushtime_to) + "23:59");
                    } else {
                        pushTime = timeValue;
                        String startTime = timeValue.split(",")[0];
                        String endTime = timeValue.split(",")[1];
                        tv_pushTime.setText(getText(R.string.mine_Setting_pushtime_from) + startTime + getText(R.string.mine_Setting_pushtime_to) + endTime);
                    }

                    showSettsLinear(info.userPushInfo.get(0).pushFlag == 1);
                }
            }

            @Override
            public void onFail(int code, String msg) {
//                ToastUtil.single(msg);
            }
        });
    }

    private void setUserPush(final boolean isPush) {
//        showSettsLinear(isPush);
        progressDialogManager.showDialog(TAG, this, null, new CustomProgressDialog.OnDialogDismissListener() {
            @Override
            public void onDismiss(CustomProgressDialog var1, int var2) {
                if (var2 != 0) {
//                    showSettsLinear(!isPush);
                }
            }
        }, getResources().getInteger(R.integer.http_timeout));
        mDeviceApi.doSaveUserPushSetts(null, null, isPush ? "1" : "0", "1", "0", null, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                showSettsLinear(isPush);
                progressDialogManager.dimissDialog(TAG, 0);
            }

            @Override
            public void onFail(int code, String msg) {
//                ToastUtil.single(msg);
                progressDialogManager.dimissDialog(TAG, 0);
//                showSettsLinear(!isPush);
            }
        });
    }

    private void showSettsLinear(boolean isPush) {
        itemSettingAlarmUser.setChecked(isPush);
        mLinearSettings.setVisibility(isPush ? View.VISIBLE : View.GONE);
//        ViewWrapper wrapper = new ViewWrapper(mLinearSettings);
//        if (isPush) {
//            mLinearSettings.setVisibility(View.VISIBLE);
//            ObjectAnimator animator = ObjectAnimator.ofInt(wrapper, "height", mLinearSettsMaxHeight).setDuration(700);
//            animator.setInterpolator(new LinearOutSlowInInterpolator());
//            animator.start();
//        } else {
//            ObjectAnimator animator = ObjectAnimator.ofInt(wrapper, "height", 0).setDuration(700);
//            animator.setInterpolator(new LinearOutSlowInInterpolator());
//            animator.start();
//            /*mLinearSettings.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mLinearSettings.setVisibility(View.GONE);
//                }
//            }, 700);*/
//        }
    }

    private static class ViewWrapper {
        private View mViewTarget;

        public ViewWrapper(View viewTarget) {
            mViewTarget = viewTarget;
        }

        public void setHeight(int height) {
            mViewTarget.getLayoutParams().height = height;
            mViewTarget.requestLayout();
        }

        public int getHeight() {
            return mViewTarget.getLayoutParams().height;
        }
    }

    private void logout() {
        if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
            progressDialogManager.showDialog(LOGOUT, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doLogout(
                    new SsoApiUnit.SsoApiCommonListener<Object>() {

                        @Override
                        public void onSuccess(Object bean) {
                            progressDialogManager.dimissDialog(LOGOUT, 0);
                            ToastUtil.show(R.string.Setting_Logout_Success);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            progressDialogManager.dimissDialog(LOGOUT, 0);
                            ToastUtil.show(msg);
                        }
                    }
            );
        } else if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
            ((MainApplication) getApplication()).logoutGateway();
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayDisconnect(MQTTEvent event) {
        if (event.state == MQTTEvent.STATE_CONNECT_DISCONNECT && event.tag == MQTTManager.TAG_GATEWAY) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_setting_security:
                startActivityForResult(new Intent(this, AccountSecurityActivity.class), REQUEST_CODE_RESET_PWD);
                break;
            case R.id.item_setting_alarm_voice:
                startActivity(new Intent(this, AlarmVoiceActivity.class));
                break;
            case R.id.item_setting_skin:
                startActivity(new Intent(this, SkinActivity.class));
                break;
            case R.id.item_setting_cache:
//                mDialog = DialogUtil.showCommonDialog(SettingActivity.this, true,
//                        getString(R.string.Setting_Clear_Cache),
//                        getString(R.string.Confirm_Clear_Cache),
//                        getString(R.string.Sure),
//                        getString(R.string.Cancel),
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mDialog.dismiss();
                progressDialogManager.showDialog(CLEAR_CACHE, SettingActivity.this, null, null, 60000);
                new Thread() {
                    @Override
                    public void run() {
                        final String msg = getString(R.string.Setting_Clear_Cache) + clearCache(FileUtil.getVideoPath(),
                                FileUtil.getMscPath(),
                                FileUtil.getTempDirectoryPath(),
                                FileUtil.getUserDirectoryPath()//,
//                                FileUtil.getHtmlResourcesPath()
                        );
                        MainApplication.getApplication().getDeviceCache().clearDatabaseCache(null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialogManager.dimissDialog(CLEAR_CACHE, 0);
                                ToastUtil.show(msg);
                            }
                        });
                    }
                }.start();
//                            }
//                        },
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mDialog.dismiss();
//                            }
//                        });
                break;

            case R.id.item_setting_version:
                mHandler.removeCallbacks(clearVersionInfoCountTask);
                showVersionInfoCount += 1;
                mHandler.postDelayed(clearVersionInfoCountTask, 1000);
                if (showVersionInfoCount >= 5) {
                    LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
                    ToastUtil.show(
                            "Channel: " + localInfo.market + "\n" + "V6Server: " + ApiConstant.BASE_URL + "\n"
                                    + "WuLianEncrypt so ver: " + WuLianEncrypt.getSoVersion());
                    showVersionInfoCount = 0;
                }
                break;
            case R.id.item_setting_logout:
                logout();
                break;
            case R.id.item_setting_push_time:
                startActivityForResult(new Intent(this, PushTimeActivity.class).putExtra("TIME", pushTime), REQUEST_CODE_SET_PUSHTIME);
                break;
            case R.id.item_setting_push_manage:
//                startActivityForResult(new Intent(this, PushTimeActivity.class).putExtra("TIME", pushTime), REQUEST_CODE_SET_PUSHTIME);
                startActivity(new Intent(SettingActivity.this, MessageSettingsActivity.class));
                break;
            case R.id.item_scene_setting:
                startActivity(new Intent(SettingActivity.this, SceneSettingActivity.class));
                break;
            default:
                break;
        }
    }

    private String clearCache(String... dirs) {
        int fileSize = 0;
        for (String dir : dirs) {
            fileSize += deleteFile(new File(dir));
        }
        ImageLoader.getInstance().clearDiskCache();
        DecimalFormat df = new DecimalFormat("#.##");
        double unit = 1024;
        double temp = fileSize / unit;
        String size = "0";
        if (temp > 1024) {
            temp = temp / 1024;
            size = df.format(temp) + "M";
            if (temp > 1024) {
                temp = temp / 1024;
                size = df.format(temp) + "G";
            }
        } else {
            size = df.format(temp) + "KB";
        }
        return size;
    }

    private int deleteFile(File dirs) {
        int fileSize = 0;
        if (dirs != null && dirs.exists()) {
            File[] files = dirs.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    long length = files[i].length();
                    if (files[i].delete()) {
                        fileSize += length;
                    }
                } else {
                    fileSize += deleteFile(files[i]);
                }
            }
        }
        return fileSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESET_PWD) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
        if (requestCode == REQUEST_CODE_SET_PUSHTIME) {
            if (resultCode == RESULT_OK) {
                String timeValue = data.getStringExtra("TIME");
                pushTime = timeValue;
                String startTime = timeValue.split(",")[0];
                String endTime = timeValue.split(",")[1];
                tv_pushTime.setText(getText(R.string.mine_Setting_pushtime_from) + startTime + getText(R.string.mine_Setting_pushtime_to) + endTime);
            }
        }
    }
}
