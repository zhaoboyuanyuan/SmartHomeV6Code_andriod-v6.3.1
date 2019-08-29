package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.customview.ClearEditText;
import cc.wulian.smarthomev6.support.event.GatewayPasswordChangedEvent;
import cc.wulian.smarthomev6.support.event.MQTTRegisterEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

/**
 * Created by mamengchao on 2017/2/28 0028.
 * Tips:网关/账号密码修改
 */

public class ChangePasswordActivity extends BaseTitleActivity {

    private static final String CHANGE_PWD = "CHANGE_PWD";
    public static final String CHANGE_USER_PWD = "CHANGE_USER_PWD";
    public static final String CHANGE_GATEWAY_PWD = "CHANGE_GATEWAY_PWD";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private ClearEditText oldPwdEditText;
    private TextView gatewayPwdHintTextView;
    private Button confirmButton;
    private CheckBox cbNewPwd;
    private EditText etNewPwd;

    private SsoApiUnit ssoApiUnit;
    private String type;
    private String newPwd;
    private GatewayInfoBean gatewayInfoBean;

    private ButtonSkinWrapper buttonSkinWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password, true);
        EventBus.getDefault().register(this);
        ssoApiUnit = new SsoApiUnit(this);
        preference.saveVerifyGatewayPassword("");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.GatewayChangePwd_Tittle));
    }

    @Override
    protected void initView() {
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        pwdHintTextView = (TextView) findViewById(R.id.tv_pwd_hint);
        pwdHintImageView = (ImageView) findViewById(R.id.iv_pwd_hint);
        gatewayPwdHintTextView = (TextView) findViewById(R.id.gw_pwd_hint);

        oldPwdEditText = (ClearEditText) findViewById(R.id.old_pwd_editText);

        etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        cbNewPwd = (CheckBox) findViewById(R.id.cb_new_pwd);
        SpannableString password = new SpannableString(getString(R.string.Register_tips01));
        AbsoluteSizeSpan passwordAss = new AbsoluteSizeSpan(13, true);
        password.setSpan(passwordAss, 0, password.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etNewPwd.setHint(new SpannableString(password));

        confirmButton = (Button) findViewById(R.id.confirm_pwd_button);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(confirmButton);
    }

    @Override
    protected void initData() {
        type = getIntent().getStringExtra("type");
        if (CHANGE_GATEWAY_PWD.equals(type)) {
            gatewayPwdHintTextView.setVisibility(View.VISIBLE);
            String gwId = preference.getCurrentGatewayID();
            if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
                gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
                if ("GW15".equals(gatewayInfoBean.gwType)) {
                    gatewayPwdHintTextView.setVisibility(View.INVISIBLE);
                    return;
                }
                gatewayPwdHintTextView.setText(getString(R.string.Register_tips15) + gwId + "，" + getString(R.string.Register_tips16));
            }
        } else {
            gatewayPwdHintTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initListeners() {
        cbNewPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                CharSequence charsequence = etNewPwd.getText();
                if (charsequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charsequence;
                    Selection.setSelection(spanText, charsequence.length());
                }
            }
        });
        oldPwdEditText.addTextChangedListener(new TextWatcher() {
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
        });
        etNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
                int code = RegularTool.CheckPassword(s.toString());
                if (code == WLPassWordStrengthNone) {
                    itemPwdHint.setVisibility(View.INVISIBLE);
                } else {
                    itemPwdHint.setVisibility(View.VISIBLE);
                    if (code == WLPassWordStrengthIllegal) {
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    } else if (code == WLPassWordStrengthNoMatchRule) {
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    } else if (code == WLPassWordStrengthHigh) {
                        pwdHintTextView.setText(R.string.Register_tips13);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_04);
                    } else if (code == WLPassWordStrengthMiddle) {
                        pwdHintTextView.setText(R.string.Register_tips12);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_03);
                    } else if (CHANGE_GATEWAY_PWD.equals(type) && TextUtils.equals(s.toString(), preference.getCurrentGatewayID().substring(preference.getCurrentGatewayID().length() - 6).toUpperCase())) {
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPwd = oldPwdEditText.getText().toString();
                newPwd = etNewPwd.getText().toString();
                int code = RegularTool.CheckPassword(newPwd);

//                if (CHANGE_GATEWAY_PWD.equals(type) && !StringUtil.equals(oldPwd, preference.getCurrentGatewayPassword())) {
//                    ToastUtil.show(R.string.Register_tips06);
//                } else
                if (newPwd.length() < 8) {
                    ToastUtil.show(R.string.Register_tips03);
                } else if (code == WLPassWordStrengthIllegal) {
                    ToastUtil.show(R.string.Register_tips02);
                } else if (code == WLPassWordStrengthNoMatchRule) {
                    ToastUtil.show(R.string.Register_tips04);
                } else if (CHANGE_GATEWAY_PWD.equals(type) && TextUtils.equals(newPwd, preference.getCurrentGatewayID().substring(preference.getCurrentGatewayID().length() - 6).toUpperCase())) {
                    ToastUtil.show(R.string.Register_tips14);
                } else if (CHANGE_GATEWAY_PWD.equals(type) && TextUtils.equals(oldPwd, newPwd)) {
                    ToastUtil.show(R.string.Register_tips07);
                } else {
                    progressDialogManager.showDialog(CHANGE_PWD, ChangePasswordActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
                    if (CHANGE_USER_PWD.equals(type)) {
                        ssoApiUnit.doChangePwdByOld(oldPwd, newPwd, new SsoApiUnit.SsoApiCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                preference.saveAutoLogin(true);
                                preference.saveIsLogin(true);
                                preference.saveUserEnterType(Preference.ENTER_TYPE_ACCOUNT);
                                preference.saveThirdPartyLogin(true);
                                getUserInfo();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                                ToastUtil.show(msg);
                            }
                        });
                    } else {
                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        preference.saveVerifyGatewayPassword(etNewPwd.getText().toString());
                        MainApplication.getApplication().setGwSelfLogin(true);
                        if (!TextUtils.isEmpty(currentGatewayId)) {
                            ((MainApplication) getApplication())
                                    .getMqttManager()
                                    .publishEncryptedMessage(
                                            MQTTCmdHelper.createChangeGatewayPassword(currentGatewayId, oldPwd, newPwd),
                                            MQTTManager.MODE_GATEWAY_FIRST);
                        }
                    }
                }
            }
        });
        updateButtonState();
    }

    private void getUserInfo() {
        ssoApiUnit.doGetUserInfo(new SsoApiUnit.SsoApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(final UserBean bean) {
                progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                startActivity(new Intent(ChangePasswordActivity.this, HomeActivity.class));
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                ToastUtil.show(msg);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEnterGatewayPasswordEvent(GatewayPasswordChangedEvent event) {
        progressDialogManager.dimissDialog(CHANGE_PWD, 0);
        String currentGatewayId = preference.getCurrentGatewayID();
        if ("0".equals(event.bean.data)) {
            if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
                ToastUtil.show(R.string.Forgot_Reset_Successful);
                preference.saveGatewayPassword(currentGatewayId, newPwd);
                setResult(RESULT_OK);
                finish();
            } else {
                String verifyGwPwd = preference.getVerifyGatewayPassword();
                if (!TextUtils.isEmpty(verifyGwPwd) && TextUtils.equals(MD5Util.encrypt(verifyGwPwd), event.bean.gwPwd)) {
                    ToastUtil.show(R.string.Forgot_Reset_Successful);
                    MQTTApiConfig.GW_SERVER_URL = preference.getCurrentGatewayHost();
                    MQTTApiConfig.gwID = event.bean.gwID;
                    MQTTApiConfig.gwPassword = verifyGwPwd;
                    MQTTApiConfig.gwUserName = "a" + System.currentTimeMillis();
                    MQTTApiConfig.gwUserPassword = "b";//etGatewayPassword.getText().toString().trim();
                    ((MainApplication) getApplication()).getMqttManager().connectGateway();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        } else {
            preference.saveGatewayPassword(currentGatewayId, "");
            ToastUtil.show(R.string.PersonalInfo_ChangeName_Fail);
        }
    }

    private void updateButtonState() {
        if (oldPwdEditText.getText().length() == 0 || etNewPwd.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MQTTRegisterEvent event) {
        if (event.state == MQTTRegisterEvent.STATE_REGISTER_SUCCESS) {
            preference.saveUserEnterType(Preference.ENTER_TYPE_GW);
            preference.saveGatewayPassword(Preference.getPreferences().getCurrentGatewayID(), newPwd);
            preference.saveCurrentGatewayState("1");
            preference.saveIsLogin(true);
            preference.saveGatewayRelationFlag("1");
            preference.saveThirdPartyLogin(false);
            preference.saveAutoLogin(false);
            setResult(RESULT_OK);
            finish();
        } else {
            String msg = event.msg;
            if (!TextUtils.isEmpty(msg)) {
                ToastUtil.single(msg);
                MainApplication.getApplication().getMqttManager().disconnectGateway();
            }
        }
    }
}
