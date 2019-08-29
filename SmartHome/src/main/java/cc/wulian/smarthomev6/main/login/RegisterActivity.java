package cc.wulian.smarthomev6.main.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import cc.wulian.smarthomev6.support.customview.ClearEditText;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

/**
 * Created by syf on 2017/2/20.
 */

@Deprecated
public class RegisterActivity extends BaseTitleActivity {

    private static final String REGISTER_PG_KEY = "REGISTER_PG_KEY";
    private static final String GET_VERIFY = "GET_VERIFY";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private TextView tvCountries;
    private ClearEditText etPhoneNumber;
    private TextView tvGetVerification;
    private ClearEditText etPassword;
    private EditText etVerification;
    private CheckBox cbReadAgreement;
    private TextView tvTermsUse;
    private TextView tvRegister;
    private SsoApiUnit ssoApiUnit;
    private TimeCount timeCount;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register, true);
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
        tvCountries = (TextView) findViewById(R.id.tv_countries);
        etPhoneNumber = (ClearEditText) findViewById(R.id.et_register_phone_number);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        etPassword = (ClearEditText) findViewById(R.id.et_set_password);
        etPassword.setTypeface(Typeface.DEFAULT);
        etPassword.setTransformationMethod(new HideReturnsTransformationMethod());
        etVerification = (EditText) findViewById(R.id.et_verification);
        cbReadAgreement = (CheckBox) findViewById(R.id.cb_read_agreement);
        tvTermsUse = (TextView) findViewById(R.id.tv_terms_of_use);
        tvRegister = (TextView) findViewById(R.id.tv_register_button);
    }

    @Override
    protected void initData() {
        //设置edittext   hint属性的文本的字体颜色
        SpannableString phoneNumber = new SpannableString(getString(R.string.Register_Phone_Hint));
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        phoneNumber.setSpan(ass, 0, phoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNumber.setHint(new SpannableString(phoneNumber));
        SpannableString password = new SpannableString(getString(R.string.Register_tips01));
        AbsoluteSizeSpan passwordAss = new AbsoluteSizeSpan(15, true);
        password.setSpan(passwordAss, 0, password.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPassword.setHint(new SpannableString(password));
        SpannableString authcode = new SpannableString(getString(R.string.Forgot_Require_AuthCode));
        AbsoluteSizeSpan authcodeAss = new AbsoluteSizeSpan(15, true);
        authcode.setSpan(authcodeAss, 0, authcode.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etVerification.setHint(new SpannableString(authcode));
        cbReadAgreement.setChecked(false);
        tvRegister.setEnabled(false);
        tvRegister.setBackground(getResources().getDrawable(R.drawable.shape_register_btn_no_bg));
    }

    @Override
    protected void initListeners() {
        tvCountries.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        cbReadAgreement.setOnClickListener(this);
        tvTermsUse.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
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
            case R.id.tv_countries:
                break;
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.cb_read_agreement:
                if (cbReadAgreement.isChecked()) {
                    tvRegister.setBackground(getResources().getDrawable(R.drawable.shape_register_btn_yes_bg));
                    tvRegister.setEnabled(true);
                } else {
                    tvRegister.setBackground(getResources().getDrawable(R.drawable.shape_register_btn_no_bg));
                    tvRegister.setEnabled(false);
                }
                break;
            case R.id.tv_terms_of_use:
                startActivity(new Intent(this, DisclaimerActivity.class));
                break;
            case R.id.tv_register_button:
                register();
                break;
        }
    }

    private void getVerification() {
        final String phone = etPhoneNumber.getText().toString();
        if (StringUtil.isNullOrEmpty(phone)) {
            ToastUtil.show(R.string.Forgot_PhoneNumber_NotNull);
        } else if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
        } else {
            progressDialogManager.showDialog(GET_VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doGetVerificationCode(phone, null, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
                    timeCount.start();
                    etVerification.setFocusable(true);
                    etVerification.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etVerification, InputMethodManager.SHOW_FORCED);
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    if(code == 2000001){//验证手机号是否已经注册过
                        showIsRegisterDialog();
                    }else {
                        ToastUtil.show(msg);
                    }
                }
            });
        }
    }

    private void register() {
        final String phone = etPhoneNumber.getText().toString();
        final String pwd = etPassword.getText().toString();
        final String authCode = etVerification.getText().toString();
        int code = RegularTool.CheckPassword(pwd);

        if (StringUtil.isNullOrEmpty(phone)) {
            ToastUtil.show(R.string.Forgot_PhoneNumber_NotNull);
        }else if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
        }else if (StringUtil.isNullOrEmpty(authCode)) {
            ToastUtil.show(R.string.Forgot_Require_AuthCode);
        }else if (StringUtil.isNullOrEmpty(pwd)) {
            ToastUtil.show(R.string.GatewayChangePwd_NewPwd_Hint);
        }else if (pwd.length() < 8) {
            ToastUtil.show(R.string.Register_tips03);
        }else if(code == WLPassWordStrengthIllegal){
            ToastUtil.show(R.string.Register_tips02);
        }else if(code == WLPassWordStrengthNoMatchRule){
            ToastUtil.show(R.string.Register_tips04);
        }else {
            progressDialogManager.showDialog(REGISTER_PG_KEY, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doRegisterPhone(phone, null, pwd, authCode, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
                @Override
                public void onSuccess(RegisterPhoneBean bean) {
                    ApiConstant.setUserID(bean.uId);
                    ToastUtil.show(R.string.Login_Success);
                    progressDialogManager.dimissDialog(REGISTER_PG_KEY, 0);
                    setResult(RESULT_OK, getIntent().putExtra("USER_ID", phone));
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

    private void showIsRegisterDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(this.getString(R.string.http_error_20105))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(this.getString(R.string.Register_DO_Login_Hint))
                .setPositiveButton(this.getResources().getString(R.string.Register_DO_Login))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        dialog.dismiss();
                        finish();
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
}
