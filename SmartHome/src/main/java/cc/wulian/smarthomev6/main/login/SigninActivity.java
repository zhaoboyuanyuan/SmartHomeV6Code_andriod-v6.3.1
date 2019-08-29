package cc.wulian.smarthomev6.main.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.SocializeUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.account.ForgotAccountActivity;
import cc.wulian.smarthomev6.main.account.RegisterMailActivity;
import cc.wulian.smarthomev6.main.account.RegisterPhoneActivity;
import cc.wulian.smarthomev6.main.account.ThirdBindPhoneActivity;
import cc.wulian.smarthomev6.main.application.BaseFullscreenActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ThirdPartyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.customview.material.MaterialEditText;
import cc.wulian.smarthomev6.support.event.LoginPopDismissEvent;
import cc.wulian.smarthomev6.support.event.LoginSuccessEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class SigninActivity extends BaseFullscreenActivity implements View.OnClickListener, TextWatcher {

    private static final String LOGIN = "LOGIN";
    private static final int REGISTER = 0;
    private static final int REQUEST_CODE_GATEWAY_LOGIN = 1;
    private int errorNumber = 0;
    // 声明控件对象
    private View layout_root;
    private TextView tv_or;
    private View view_line0, view_line1;
    private ImageView iv_logo;
    private MaterialEditText et_name, et_pass;
    private Button mLoginButton;
    private TextView mLoginError;
    private TextView mRegister;
    private ImageView mFinish;
    private TextView tvGatewayLogin;
    private TextView tvLoginMode;
    private ImageView ivWechatLogin, ivQQLogin, ivWeiboLogin;
    private SsoApiUnit ssoApiUnit;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private String pwd;
    private String account;

    private final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    private final String WEIXIN_PACKAGE_NAME = "com.tencent.mm";
    private final String WEIBO_PACKAGE_NAME = "com.sina.weibo";

    private ButtonSkinWrapper buttonSkinWrapper;


    public static void start(Context context, String account) {
        Intent intent = new Intent(context, SigninActivity.class);
        intent.putExtra("ACCOUNT", account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ssoApiUnit = new SsoApiUnit(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent() != null) {
            account = getIntent().getStringExtra("ACCOUNT");
        }
        if (!TextUtils.isEmpty(account)) {
            et_name.setText(account);
        }
    }

    @Override
    protected void initView() {
        layout_root = findViewById(R.id.layout_root);
        tv_or = (TextView) findViewById(R.id.tv_or);
        view_line0 = findViewById(R.id.view_line0);
        view_line1 = findViewById(R.id.view_line1);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        et_name = (MaterialEditText) findViewById(R.id.username);
        et_pass = (MaterialEditText) findViewById(R.id.password);
        et_pass.setTypeface(Typeface.DEFAULT);
        et_pass.setTransformationMethod(new PasswordTransformationMethod());

        mLoginButton = (Button) findViewById(R.id.login);
        mRegister = (TextView) findViewById(R.id.register);
        mLoginError = (TextView) findViewById(R.id.login_error);
        tvGatewayLogin = (TextView) findViewById(R.id.tv_gateway_login);
        ivWechatLogin = (ImageView) findViewById(R.id.iv_wechat_login);
        ivQQLogin = (ImageView) findViewById(R.id.iv_qq_login);
        ivWeiboLogin = (ImageView) findViewById(R.id.iv_weibo_login);
        mFinish = (ImageView) findViewById(R.id.imageView_finish);
        tvLoginMode = (TextView) findViewById(R.id.login_mode);

        if (LanguageUtil.isChina()) {
            ivWechatLogin.setVisibility(View.VISIBLE);
            ivQQLogin.setVisibility(View.VISIBLE);
            ivWeiboLogin.setVisibility(View.VISIBLE);
        } else {
            ivWechatLogin.setVisibility(View.GONE);
            ivQQLogin.setVisibility(View.GONE);
            ivWeiboLogin.setVisibility(View.GONE);
        }
        et_name.setText(preference.getCurrentAccountID());
        requesFocus(preference.getCurrentAccountID());
    }

    @Override
    protected void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_root, SkinResouceKey.BITMAP_MAIN_BACKGROUND);
        skinManager.setImageViewDrawable(mFinish, SkinResouceKey.BITMAP_BUTTON_EXIT);
        skinManager.setImageViewDrawable(iv_logo, SkinResouceKey.BITMAP_LOGIN_LOGO);
        skinManager.setImageViewDrawable(ivWechatLogin, SkinResouceKey.BITMAP_BUTTON_THIRD_WECHAT);
        skinManager.setImageViewDrawable(ivQQLogin, SkinResouceKey.BITMAP_BUTTON_THIRD_QQ);
        skinManager.setImageViewDrawable(ivWeiboLogin, SkinResouceKey.BITMAP_BUTTON_THIRD_WEIBO);
        Bitmap deleteEidtBitmap = skinManager.getBitmap(SkinResouceKey.BITMAP_BUTTON_EDIT_DELETE);
        if (deleteEidtBitmap != null) {
            et_name.setIconDelete(deleteEidtBitmap);
            et_pass.setIconDelete(deleteEidtBitmap);
        }
        Integer textOtherColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_OTHER_TEXT);
        if (textOtherColor != null) {
            et_name.setTextColor(textOtherColor);
            et_pass.setTextColor(textOtherColor);
            mRegister.setTextColor(textOtherColor);
            mLoginError.setTextColor(textOtherColor);
            tvLoginMode.setTextColor(textOtherColor);
            tv_or.setTextColor(textOtherColor);
            view_line0.setBackgroundColor(textOtherColor);
            view_line1.setBackgroundColor(textOtherColor);
            tvGatewayLogin.setTextColor(textOtherColor);
        }
        Integer textHintColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_TEXT);
        if (textHintColor != null) {
            et_name.setHintTextColor(textHintColor);
            et_pass.setHintTextColor(textHintColor);
        }
        Integer textHintOtherColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_OTHER_TEXT);
        if (textHintOtherColor != null) {
            et_name.setHintTextColor(textHintOtherColor);
            et_pass.setHintTextColor(textHintOtherColor);
            et_name.setUnderlineColor(textHintOtherColor);
            et_pass.setUnderlineColor(textHintOtherColor);
        }
        Integer textHighlightColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_HIGHLIGHT_TEXT);
        if (textHighlightColor != null) {
            et_name.setPrimaryColor(textHighlightColor);
            et_pass.setPrimaryColor(textHighlightColor);
            et_name.setFloatingLabelTextColor(textHighlightColor);
            et_pass.setFloatingLabelTextColor(textHighlightColor);
        }
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(mLoginButton);
    }

    @Override
    protected void initListeners() {
        et_name.addTextChangedListener(this);
        et_pass.addTextChangedListener(this);
        mLoginButton.setOnClickListener(this);
        mLoginError.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        tvGatewayLogin.setOnClickListener(this);
        ivWechatLogin.setOnClickListener(this);
        ivQQLogin.setOnClickListener(this);
        ivWeiboLogin.setOnClickListener(this);
        mFinish.setOnClickListener(this);
        tvLoginMode.setOnClickListener(this);
        updateButtonState();
    }

    @Override
    protected void initData() {
        errorNumber = 0;
        if (getIntent() != null) {
            account = getIntent().getStringExtra("ACCOUNT");
        }
    }

    private void requesFocus(String value) {
        if (!TextUtils.isEmpty(value)) {
            et_name.setText(value);
//            et_name.setFocusableInTouchMode(false);
//            et_pass.setFocusable(true);
//            et_pass.setFocusableInTouchMode(true);
            et_pass.requestFocus();
        } else {
//            et_pass.setFocusableInTouchMode(false);
//            et_name.setFocusable(true);
//            et_name.setFocusableInTouchMode(true);
            et_name.requestFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void login() {
        final String id = et_name.getText().toString();
        pwd = et_pass.getText().toString();
        if (StringUtil.isNullOrEmpty(id)) {
            ToastUtil.show(R.string.Login_Require_Username);
            return;
        }
        if (StringUtil.isNullOrEmpty(pwd)) {
            ToastUtil.show(R.string.GatewayChangePwd_NewPwd_Hint);
            return;
        }
        if (!(RegularTool.isLegalChinaPhoneNumber(id) || RegularTool.isLegalEmailAddress(id))) {
            ToastUtil.show(R.string.Login_Failed);
            return;
        }
        progressDialogManager.showDialog(LOGIN, SigninActivity.this, getString(R.string.Logining), null, getResources().getInteger(R.integer.http_timeout));
        String terminalId = ApiConstant.getTerminalId();
        if (TextUtils.isEmpty(terminalId)) {
            ssoApiUnit.doRegisterDevice(new SsoApiUnit.SsoApiCommonListener<RegisterDeviceBean>() {
                @Override
                public void onSuccess(RegisterDeviceBean bean) {
                    Preference.getPreferences().saveLanguage(MainApplication.getApplication().getLocalInfo().appLang);
                    login(id, pwd);
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(LOGIN, 0);
                    ToastUtil.show(msg);
                }
            });
        } else {
            login(id, pwd);
        }
    }

    private void login(String id, final String pwd) {
        preference.saveCurrentAccountID(id);
        ssoApiUnit.doLogin(id, null, pwd, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {//TODO 支持中国大陆以外的登陆
            @Override
            public void onSuccess(RegisterPhoneBean bean) {
                preference.saveAutoLogin(true);
                preference.saveThirdPartyLogin(false);
                getUserInfo();
                if (RegularTool.isNeedResetPwd(pwd)) {
                    String token = ApiConstant.getAppToken();
                    mainApplication.logout(false);
                    ApiConstant.setAppToken(token);
                    HttpHeaders headers = OkGo.getInstance().getCommonHeaders();
                    if (headers == null) {
                        headers = new HttpHeaders();
                    }
                    headers.put("WL-TOKEN", token);
                    OkGo.getInstance().addCommonHeaders(headers);
                    ConfirmPasswordActivity.start(SigninActivity.this, pwd);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOGIN, 0);
                if (code == 2000005) {
                    if (errorNumber++ > 2) {
                        showFindPwdDialog();
                        return;
                    }
                } else if (code == 2000028) {
                    showAccountLockedDialog();
                    return;
                }
                ToastUtil.show(msg);
            }
        });
    }

    private void getUserInfo() {
        ssoApiUnit.doGetUserInfo(new SsoApiUnit.SsoApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(final UserBean bean) {
                progressDialogManager.dimissDialog(LOGIN, 0);
                EventBus.getDefault().post(new LoginSuccessEvent());
                EventBus.getDefault().post(new LoginPopDismissEvent());
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOGIN, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void showFindPwdDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(this.getString(R.string.Login_Find_pwd))
                .setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.Login_Find_Pwd))
                .setPositiveButton(this.getResources().getString(R.string.Login_Find_pwd))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        errorNumber = 0;
                        startActivity(new Intent(SigninActivity.this, ForgotAccountActivity.class));
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showAccountLockedDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(this.getString(R.string.account_Prompt))
                .setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.account_is_locked))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.login_error:
                startActivity(new Intent(SigninActivity.this, ForgotAccountActivity.class));
                break;
            case R.id.register:
                if (LanguageUtil.isChina()) {
                    startActivity(new Intent(this, RegisterPhoneActivity.class));
                } else {
                    startActivity(new Intent(this, RegisterMailActivity.class));
                }
                break;
            case R.id.tv_gateway_login:
                tvGatewayLogin.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvGatewayLogin.setEnabled(true);
                    }
                }, 2000);
                Intent intent = new Intent();
                intent.setClass(this, GatewayLoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE_GATEWAY_LOGIN);
                break;
            case R.id.iv_wechat_login:
                ivWechatLogin.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivWechatLogin.setEnabled(true);
                    }
                }, 2000);
                if (isThirdPaatyInstalled(WEIXIN_PACKAGE_NAME)) {
                    loginFromThirdParty(SHARE_MEDIA.WEIXIN);
                } else {
                    ToastUtil.show(String.format(this.getString(R.string.Third_Party_Not_Installed), this.getString(R.string.Login_Wechat_Login)));
                }
                break;
            case R.id.iv_qq_login:
                ivQQLogin.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivQQLogin.setEnabled(true);
                    }
                }, 2000);
                if (isThirdPaatyInstalled(QQ_PACKAGE_NAME)) {
                    loginFromThirdParty(SHARE_MEDIA.QQ);
                } else {
                    ToastUtil.show(String.format(this.getString(R.string.Third_Party_Not_Installed), this.getString(R.string.Login_QQ_Login)));
                }
                break;
            case R.id.iv_weibo_login:
                ivWeiboLogin.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivWeiboLogin.setEnabled(true);
                    }
                }, 2000);
                if (isThirdPaatyInstalled(WEIBO_PACKAGE_NAME)) {
                    loginFromThirdParty(SHARE_MEDIA.SINA);
                } else {
                    ToastUtil.show(String.format(String.format(this.getString(R.string.Third_Party_Not_Installed), this.getString(R.string.Login_WeiBo_Login))));

                }
                break;
            case R.id.imageView_finish:
                EventBus.getDefault().post(new LoginSuccessEvent());
                finish();
                break;
            case R.id.login_mode:
                startActivity(new Intent(SigninActivity.this, SigninSmsActivity.class));
//                overridePendingTransition(R.anim.slide_in_right, 0);
                finish();
                break;
            default:
                break;
        }
    }

    //通过包名判断是否安装第三方软件
    private boolean isThirdPaatyInstalled(String packageName) {
        final PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loginFromThirdParty(SHARE_MEDIA thirdPartyType) {
        if (!thirdPartyType.toString().equals(SHARE_MEDIA.GENERIC.toString())) {
            SnsPlatform platform = thirdPartyType.toSnsPlatform();
            //获取授权
            //每次先调用撤销授权，否则微信无法每次都弹出授权页面
            UMShareAPI.get(this).deleteOauth(this, platform.mPlatform, null);
            UMShareAPI.get(this).doOauthVerify(this, platform.mPlatform, authListener);
        }
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
            //获取授权成功后，获取个人信息
            UMShareAPI.get(SigninActivity.this).getPlatformInfo(SigninActivity.this, platform.toSnsPlatform().mPlatform, authInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
            ToastUtil.show(t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
        }
    };

    UMAuthListener authInfoListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            progressDialogManager.showDialog(LOGIN, SigninActivity.this, getString(R.string.Logining), null, getResources().getInteger(R.integer.http_timeout));
            final ThirdPartyBean thirdPartyData = getThirdPartyData(platform, data);
            preference.saveCurrentAccountID("");
            preference.saveThirdPartyType(thirdPartyData.getPartnerId());
            preference.saveThirdPartyUid(thirdPartyData.getOpenId());
            ssoApiUnit.doLoginTHIRD(thirdPartyData, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
                @Override
                public void onSuccess(RegisterPhoneBean accountBean) {
                    preference.saveAutoLogin(true);
                    preference.saveIsLogin(true);
                    preference.saveUserEnterType(Preference.ENTER_TYPE_ACCOUNT);
                    preference.saveThirdPartyLogin(true);
                    getUserInfo();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(LOGIN, 0);
                    if (code == 2000005) {
                        if (errorNumber++ > 2) {
                            showFindPwdDialog();
                            return;
                        }
                    } else if (code == 2000009) {
                        startActivityForResult(new Intent(SigninActivity.this, ThirdBindPhoneActivity.class).putExtra("THIRDPARTYDATA", thirdPartyData), 1);
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

        }
    };

    private ThirdPartyBean getThirdPartyData(SHARE_MEDIA platform, Map<String, String> data) {
        int thirdPartyType = 1;
        if (platform == SHARE_MEDIA.WEIXIN) {
            thirdPartyType = 1;
        } else if (platform == SHARE_MEDIA.QQ) {
            thirdPartyType = 2;
        } else if (platform == SHARE_MEDIA.SINA) {
            thirdPartyType = 3;
        }

        String openId = data.get("openid");
        if (TextUtils.isEmpty(openId)) {
            openId = data.get("uid");
        }

        String iconUrl = "";
        if (!StringUtil.isNullOrEmpty(data.get("iconurl"))) {
            iconUrl = data.get("iconurl");
        }

        String nick = data.get("screen_name");

        int gender = 0;
        String genderStr = data.get("gender");
        if (genderStr.equals("男")) {
            gender = 0;
        } else if (genderStr.equals("女")) {
            gender = 1;
        }

        String unionid = data.get("unionid");

        ThirdPartyBean thirdPartyData = new ThirdPartyBean(thirdPartyType, openId, unionid, iconUrl, nick, gender, true);
        return thirdPartyData;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GATEWAY_LOGIN) {
                EventBus.getDefault().post(new LoginSuccessEvent());
                finish();
            }
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        EventBus.getDefault().post(new LoginSuccessEvent());
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateButtonState();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void updateButtonState() {
        if (et_name.getText().length() == 0 || et_pass.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }
}
