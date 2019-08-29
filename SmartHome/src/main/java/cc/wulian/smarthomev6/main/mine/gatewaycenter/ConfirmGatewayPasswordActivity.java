package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Context;
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
import cc.wulian.smarthomev6.main.login.GatewayLoginActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.GatewayPasswordChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

/**
 * Created by 上海滩小马哥 on 2017/8/9.
 */

public class ConfirmGatewayPasswordActivity extends BaseTitleActivity {
    private static final String CHANGE_PWD = "CHANGE_PWD";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private EditText oldPwdEditText, newPwdEditText;
    private TextView gatewayId;
    private Button confirmButton;
    private CheckBox cbNewPwd;

    private String oldPwd;
    private String newPwd;
    private GatewayInfoBean gatewayInfoBean;
    private ButtonSkinWrapper buttonSkinWrapper;

    public static void start(Context context, String oldPwd) {
        Intent intent = new Intent(context, ConfirmGatewayPasswordActivity.class);
        intent.putExtra("oldPwd", oldPwd);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_gateway_password, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarOnlyTitle(getString(R.string.Register_tips10));
//        setToolBarTitle(getString(R.string.Register_tips10));
    }

    @Override
    protected void initView() {
        super.initView();
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        pwdHintTextView = (TextView) findViewById(R.id.tv_pwd_hint);
        pwdHintImageView = (ImageView) findViewById(R.id.iv_pwd_hint);
        gatewayId = (TextView) findViewById(R.id.tv_id_hint);
        cbNewPwd = (CheckBox) findViewById(R.id.new_pwd_checkBox);

        oldPwdEditText = (EditText) findViewById(R.id.old_pwd_editText);
        newPwdEditText = (EditText) findViewById(R.id.new_pwd_editText);
        SpannableString password = new SpannableString(getString(R.string.Register_tips01));
        AbsoluteSizeSpan passwordAss = new AbsoluteSizeSpan(13, true);
        password.setSpan(passwordAss, 0, password.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        newPwdEditText.setHint(new SpannableString(password));

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
        super.initData();
        oldPwd = getIntent().getStringExtra("oldPwd");
        String gwId = preference.getCurrentGatewayID();

        if (!TextUtils.isEmpty(gwId)) {
            gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
            if ("GW15".equals(gatewayInfoBean.gwType)) {
                gatewayId.setVisibility(View.INVISIBLE);
            } else {
                gatewayId.setText(getString(R.string.Register_tips15) + gwId + "，" + getString(R.string.Register_tips16));
            }
        }
        if (TextUtils.isEmpty(oldPwd)) {
            oldPwdEditText.setEnabled(false);
            oldPwdEditText.setText(gwId.substring(gwId.length() - 6, gwId.length()).toUpperCase());
        } else {
            oldPwdEditText.setEnabled(false);
            oldPwdEditText.setText(oldPwd);
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        cbNewPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newPwdEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    newPwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                CharSequence charsequence = newPwdEditText.getText();
                if (charsequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charsequence;
                    Selection.setSelection(spanText, charsequence.length());
                }
            }
        });
        oldPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newPwdEditText.addTextChangedListener(new TextWatcher() {
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
                    } else if (TextUtils.equals(s.toString(), preference.getCurrentGatewayID().substring(preference.getCurrentGatewayID().length() - 6).toUpperCase())) {
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
            public void onClick(View v) {
                oldPwd = oldPwdEditText.getText().toString();
                newPwd = newPwdEditText.getText().toString();
                int code = RegularTool.CheckPassword(newPwd);

                if (newPwd.length() < 8) {
                    ToastUtil.show(R.string.Register_tips03);
                } else if (code == WLPassWordStrengthIllegal) {
                    ToastUtil.show(R.string.Register_tips02);
                } else if (code == WLPassWordStrengthNoMatchRule) {
                    ToastUtil.show(R.string.Register_tips04);
                } else if (TextUtils.equals(newPwd, preference.getCurrentGatewayID().substring(preference.getCurrentGatewayID().length() - 6).toUpperCase())) {
                    ToastUtil.show(R.string.Register_tips14);
                } else if (StringUtil.equals(newPwd, oldPwd)) {
                    ToastUtil.show(R.string.Register_tips07);
                } else {
                    progressDialogManager.showDialog(CHANGE_PWD, ConfirmGatewayPasswordActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
                    String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                    MainApplication.getApplication().setGwSelfLogin(true);
                    if (!TextUtils.isEmpty(currentGatewayId)) {
                        preference.saveVerifyGatewayPassword(newPwd);
                        ((MainApplication) getApplication())
                                .getMqttManager()
                                .publishEncryptedMessage(
                                        MQTTCmdHelper.createChangeGatewayPassword(currentGatewayId, oldPwd, newPwd),
                                        MQTTManager.MODE_GATEWAY_FIRST);
                    }
                }
            }
        });

        updateButtonState();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEnterGatewayPasswordEvent(GatewayPasswordChangedEvent event) {
        progressDialogManager.dimissDialog(CHANGE_PWD, 0);
        String currentGatewayId = preference.getCurrentGatewayID();
        if ("0".equals(event.bean.data)) {
            if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
                ToastUtil.show(R.string.Forgot_Reset_Successful);
                preference.saveGatewayPassword(currentGatewayId, newPwd);
//                ((MainApplication) getApplication()).getMqttManager().connectGatewayInCloud();
//                setResult(RESULT_OK);
//                finish();
                Intent intent = new Intent();
                intent.putExtra("gwId", preference.getCurrentGatewayID());
                setResult(RESULT_OK, intent);

                preference.saveGatewayPassword(preference.getCurrentGatewayID(), newPwd);
//                ((MainApplication) getApplication()).clearCurrentGateway();
                finish();
            } else {
                ToastUtil.show(R.string.Forgot_Reset_Successful);
                logoutGateway(preference.getCurrentGatewayID());
            }
        } else {
            preference.saveGatewayPassword(currentGatewayId, "");
            ToastUtil.show(R.string.PersonalInfo_ChangeName_Fail);
        }
    }

    //退出网关登录操作
    private void logoutGateway(final String gwID) {
        ((MainApplication) getApplication()).logoutGateway();

        preference.saveCurrentGatewayID(gwID);
        new DeviceApiUnit(ConfirmGatewayPasswordActivity.this).doSwitchGatewayId(gwID);
        Intent intent = new Intent(this, GatewayLoginActivity.class);
        intent.putExtra("gwID", gwID);
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

    private void updateButtonState() {
        if (oldPwdEditText.getText().length() == 0 || newPwdEditText.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }

}
