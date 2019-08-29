package cc.wulian.smarthomev6.main.welcome;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.UserApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.TokenToMqttBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.core.socket.GatewaySearchUnit;
import cc.wulian.smarthomev6.support.tools.AssetsManager;
import cc.wulian.smarthomev6.support.tools.NFCTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VersionUtil;

/**
 * Created by syf on 2017/2/21.
 */

public class SplashActivity extends BaseActivity {

    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    private static final String PERMISSION_NFC = Manifest.permission.NFC;
    //    private static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private ProgressBar mProgressBar;
    public static ProgressDialog mProgressDialog;
    private ImageView iv_splash;
    private UserApiUnit userApiUnit;
    private SsoApiUnit ssoApiUnit;
    private Handler handler = new Handler();
    private Runnable startHomeTask = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }
    };
    private boolean isSupportNFC = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //若HomeActivity已经在运行，则直接跳转
        List<Activity> activityList = MainApplication.getApplication().getActivities();
        for (Activity activity : activityList) {
            if (TextUtils.equals(activity.getClass().getSimpleName(), HomeActivity.class.getSimpleName())) {
                if (activity.isDestroyed() || activity.isFinishing()) {
                    break;
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                    return;
                }
            }
        }
        setContentView(R.layout.activity_welcome);
        isSupportNFC = NFCTool.isSupport(this);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = SplashActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        iv_splash = (ImageView) findViewById(R.id.iv_splash);
        mProgressBar = (ProgressBar) findViewById(R.id.load_progress);
        mProgressDialog = new ProgressDialog(this);
//        showProgressDialog();
        showRandomSplashImage(iv_splash);
        checkPermission();
    }

    public static void showProgressDialog(String title, String msg){
        // 设置水平进度条
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIcon(R.drawable.ic_launcher);
        mProgressDialog.setTitle(title);

        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void showRandomSplashImage(ImageView iv_splash) {
//        int[] resArray = {
//                R.drawable.splash1,
//                R.drawable.splash2,
//                R.drawable.splash3
//        };
//        int length = resArray.length;
//        Random random = new Random();
//        int index = random.nextInt(length);
//        iv_splash.setImageResource(resArray[index]);
        if (LanguageUtil.isChina()) {
            iv_splash.setImageResource(R.drawable.splash_new);
        } else {
            iv_splash.setImageResource(R.drawable.splash_new_en);
        }
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, PERMISSION_READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || (isSupportNFC && ContextCompat.checkSelfPermission(this, PERMISSION_NFC) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    isSupportNFC ? new String[]{PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_PHONE_STATE, PERMISSION_NFC} : new String[]{PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE);
        } else {
            startApp();
        }
    }

    private void startApp() {
        mainApplication.startService();
        userApiUnit = new UserApiUnit(this);
        ssoApiUnit = new SsoApiUnit(this);
//        LocalInfo oldLocalInfo = mainApplication.getLocalInfo(true);
        LocalInfo localInfo = mainApplication.getLocalInfo(false);//刷新本机数据信息，检测语言等变化
//        if (TextUtils.isEmpty(ApiConstant.getTerminalId())
//                || !TextUtils.equals(MainApplication.getApplication().getLocalInfo().appLang, Preference.getPreferences().getLanguage())
//                || !TextUtils.equals(oldLocalInfo.appVersion, localInfo.appVersion)) {
//        checkTerminalId();
//        } else {
//            autoLogin();
//        }

        //FIXME 特殊版本修改特殊皮肤，过后改回
        int currentVersion = VersionUtil.getVersionCode(this);
        int oldVersion = preference.getCurrentVersion();
        if (currentVersion == 14 && currentVersion > oldVersion) {
            String currentSkin = preference.getCurrentSkin();
//            if ("008".equals(currentSkin) || "009".equals(currentSkin)) {//将之前圣诞皮肤和新年皮肤改回
//                preference.saveCurrentSkin(Preference.DEFAULT_SKIN_ID);
//            }
        }

        //复制默认html资源，有进度条提示
        AssetsManager.updateHtmlCommonAssets(getApplicationContext(), true, null);
        //复制默认皮肤资源、取消进度条提示
        AssetsManager.updateDefaultSkinAssets(getApplicationContext(), false, new AssetsManager.CopyTaskCallback() {
            @Override
            public void onFinish() {
                if (TextUtils.equals(Preference.getPreferences().getCurrentSkin(), Preference.DEFAULT_SKIN_ID)) {
                    SkinManager.unzipSkinPackage(Preference.DEFAULT_SKIN_ID, new SkinManager.ZipCallback() {
                        @Override
                        public void onFinish() {
                            checkTerminalId();
                        }

                        @Override
                        public void onFail() {
                            checkTerminalId();
                        }
                    });
                } else {
                    checkTerminalId();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hasPermission = true;
            if (grantResults != null) {
                for (int grantResult : grantResults) {
                    hasPermission = hasPermission & (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (hasPermission) {
                startApp();
            } else {
                // Permission Denied
                ToastUtil.show(R.string.the_system_will_exit);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        System.exit(0);
                    }
                }, 2000);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkTerminalId() {
        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType()) && preference.isLogin()) {
            autoGatewayLogin();
        } else {
            ssoApiUnit.doRegisterDevice(new SsoApiUnit.SsoApiCommonListener<RegisterDeviceBean>() {
                @Override
                public void onSuccess(RegisterDeviceBean bean) {
                    Preference.getPreferences().saveLanguage(MainApplication.getApplication().getLocalInfo().appLang);
                    autoLogin();
                }

                @Override
                public void onFail(int code, String msg) {
                    autoLogin();
                }
            });
        }
    }

    private GatewayBean gatewayBean = null;

    private void autoGatewayLogin() {
        handler.postDelayed(startHomeTask, 1000);
        GatewaySearchUnit gatewaySearchUnit = new GatewaySearchUnit();
        gatewaySearchUnit.setSocketTimeout(3000);
        gatewaySearchUnit.startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
            @Override
            public void result(List<GatewayBean> list) {
                final String gwID = preference.getCurrentGatewayID();
                final String gwPassword = preference.getGatewayPassword(gwID);
                for (GatewayBean bean : list) {
                    if (TextUtils.equals(gwID, bean.gwID)) {
                        gatewayBean = bean;
                        break;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gatewayBean != null) {
                            MQTTApiConfig.GW_SERVER_URL = gatewayBean.host + ":" + MQTTApiConfig.GW_SERVER_PORT;
                            preference.saveCurrentGatewayHost(MQTTApiConfig.GW_SERVER_URL);
                            MQTTApiConfig.gwID = gatewayBean.gwID;
                            MQTTApiConfig.gwPassword = gwPassword;
                            MQTTApiConfig.gwUserName = "a" + System.currentTimeMillis();
                            MQTTApiConfig.gwUserPassword = "b";//etGatewayPassword.getText().toString().trim();
                            MainApplication.getApplication().getMqttManager().connectGateway();
                        } else {
                            MainApplication.getApplication().logoutGateway();
                        }
                    }
                });
            }
        });

    }

    private void autoLogin() {
        handler.postDelayed(startHomeTask, 1000);
        if (Preference.ENTER_TYPE_ACCOUNT.equals(Preference.getPreferences().getUserEnterType()) && !TextUtils.isEmpty(ApiConstant.getAppToken())) {
            //账号登录且token不为空，则获取mqtt并连接
            getMQTTInfo();
        } else {

        }
    }

    private void getMQTTInfo() {
        ssoApiUnit.doTokenToMqtt(new SsoApiUnit.SsoApiCommonListener<TokenToMqttBean>() {
            @Override
            public void onSuccess(TokenToMqttBean bean) {
                if (bean != null && bean.state == 0) {
                    getUserInfo();
                    userApiUnit.doLoginSuccess(bean.mqttInfo);
                } else {
                    //处理账号在别的地方被登录的情况
                    mainApplication.logout(false);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                //处理MQTT信息获取不到的情况
                mainApplication.logout(false);
            }
        });
    }

    private void getUserInfo() {
        ssoApiUnit.doGetUserInfo(new SsoApiUnit.SsoApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(final UserBean bean) {
            }

            @Override
            public void onFail(int code, String msg) {
//                preference.saveIsLogin(false);
//                preference.saveCurrentGatewayID("");
//                preference.saveCurrentGatewayPassword("");
//                ((MainApplication) getApplication()).getDeviceCache().clear();
            }
        });
    }
}
