package cc.wulian.smarthomev6.main.login;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

/**
 * 作者: mamengchao
 * 时间: 2017/4/1 0001
 * 描述: 邮箱注册
 * 联系方式: 805901025@qq.com
 */

@Deprecated
public class MailRegisterActivity extends BaseTitleActivity {

    private static final String REGISTER_GET_VERIFY = "REGISTER_PG_KEY";
    private static final String REGISTER_PG_KEY = "REGISTER_PG_KEY";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private EditText etMail;
    private EditText etPassword;
    private EditText etVerification;
    private CheckBox cbReadAgreement;
    private TextView tvTermsUse;
    private TextView tvGetVerification;
    private TextView tvRegister;

    private SsoApiUnit ssoApiUnit;
    private TimeCount timeCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_register, true);
        ssoApiUnit = new SsoApiUnit(this);
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Login_Register));
    }

    @Override
    protected void initView() {
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        pwdHintTextView = (TextView) findViewById(R.id.tv_pwd_hint);
        pwdHintImageView = (ImageView) findViewById(R.id.iv_pwd_hint);
        etMail = (EditText) findViewById(R.id.et_register_mail);
        etPassword = (EditText) findViewById(R.id.et_set_password);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etVerification = (EditText) findViewById(R.id.et_verification);
        cbReadAgreement = (CheckBox) findViewById(R.id.cb_read_agreement);
        tvTermsUse = (TextView) findViewById(R.id.tv_terms_of_use);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        tvRegister = (TextView) findViewById(R.id.tv_register_button);
    }

    @Override
    protected void initData() {
        cbReadAgreement.setChecked(false);
        tvRegister.setEnabled(false);
        tvRegister.setBackground(getResources().getDrawable(R.drawable.shape_register_btn_no_bg));
    }

    @Override
    protected void initListeners() {
        cbReadAgreement.setOnClickListener(this);
        tvTermsUse.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int code = RegularTool.CheckPassword(s.toString());
                if (code == WLPassWordStrengthNone){
                    itemPwdHint.setVisibility(View.INVISIBLE);
                }else {
                    itemPwdHint.setVisibility(View.VISIBLE);
                    if (code == WLPassWordStrengthIllegal){
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    }else if (code == WLPassWordStrengthNoMatchRule){
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    }else if (code == WLPassWordStrengthHigh){
                        pwdHintTextView.setText(R.string.Register_tips13);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_04);
                    }else if (code == WLPassWordStrengthMiddle){
                        pwdHintTextView.setText(R.string.Register_tips12);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_03);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.cb_read_agreement:
                if (cbReadAgreement.isChecked()){
                    tvRegister.setBackground(getResources().getDrawable(R.drawable.shape_register_btn_yes_bg));
                    tvRegister.setEnabled(true);
                }else {
                    tvRegister.setBackground(getResources().getDrawable(R.drawable.shape_register_btn_no_bg));
                    tvRegister.setEnabled(false);
                }
                break;
            case R.id.tv_register_button:
                register();
                break;
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.tv_terms_of_use:
                startActivity(new Intent(this, DisclaimerActivity.class));
                break;
        }
    }

    private void register(){
        final String mail = etMail.getText().toString();
        String pwd = etPassword.getText().toString();
        String authcode = etVerification.getText().toString();
        int code = RegularTool.CheckPassword(pwd);

        if (TextUtils.isEmpty(mail) || !RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.show(R.string.AccountSecurity_WrongEmail);
            return;
        }else if (StringUtil.isNullOrEmpty(authcode)){
            ToastUtil.show(R.string.Forgot_Require_AuthCode);
            return;
        }else if (StringUtil.isNullOrEmpty(pwd)){
            ToastUtil.show(R.string.GatewayChangePwd_NewPwd_Hint);
            return;
        }else if (pwd.length() < 8) {
            ToastUtil.show(R.string.Register_tips03);
        }else if(code == WLPassWordStrengthIllegal){
            ToastUtil.show(R.string.Register_tips02);
        }else if(code == WLPassWordStrengthNoMatchRule){
            ToastUtil.show(R.string.Register_tips04);
        }else {
            progressDialogManager.showDialog(REGISTER_PG_KEY, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doRegisterMail(mail, pwd, authcode, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
                @Override
                public void onSuccess(RegisterPhoneBean bean) {
                    progressDialogManager.dimissDialog(REGISTER_PG_KEY, 0);
                    ToastUtil.show(R.string.Login_Success);
                    ApiConstant.setUserID(bean.uId);
                    setResult(RESULT_OK, getIntent().putExtra("USER_ID", mail));
                    finish();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(REGISTER_PG_KEY, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    private void getVerification(){
        String mail = etMail.getText().toString();
        if (TextUtils.isEmpty(mail) || !RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.show(R.string.AccountSecurity_WrongEmail);
            return;
        }
        progressDialogManager.showDialog(REGISTER_GET_VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doGetVerificationCode(mail, new SsoApiUnit.SsoApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(REGISTER_GET_VERIFY, 0);
                timeCount.start();
                ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(REGISTER_GET_VERIFY, 0);
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCount.cancel();
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvGetVerification.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg_grey);
            tvGetVerification.setClickable(false);
            tvGetVerification.setText(millisUntilFinished / 1000 + getString(R.string.Register_SMS_Wait_Second));
        }

        @Override
        public void onFinish() {
            if (tvGetVerification == null){
                return;
            }
            tvGetVerification.setText(R.string.Forgot_ReSend);
            tvGetVerification.setClickable(true);
            tvGetVerification.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg);
        }
    }
}
