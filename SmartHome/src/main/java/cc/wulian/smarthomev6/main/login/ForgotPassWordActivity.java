package cc.wulian.smarthomev6.main.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
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
 * Created by mamengchao on 2017/3/7 0007.
 * Tips:忘记密码
 */

@Deprecated
public class ForgotPassWordActivity extends BaseTitleActivity {

    private static final String FORGOT_PASSWORD = "FORGOT_PASSWORD";
    private static final String GET_VERIFY = "GET_VERIFY";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private ClearEditText et_phone;
    private EditText et_verification;
    private TextView tv_verification;
    private ClearEditText et_password;
    private TextView bt_complete;
    private TimeCount timeCount;
    private SsoApiUnit ssoApiUnit;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password, true);
        timeCount = new TimeCount(60000, 1000);
        ssoApiUnit = new SsoApiUnit(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.GatewayList_ForgotPwd));
    }

    @Override
    protected void initView() {
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        pwdHintTextView = (TextView) findViewById(R.id.tv_pwd_hint);
        pwdHintImageView = (ImageView) findViewById(R.id.iv_pwd_hint);
        et_phone = (ClearEditText) findViewById(R.id.et_fp_phone_number);
        et_verification = (EditText) findViewById(R.id.et_fp_verification);
        tv_verification = (TextView) findViewById(R.id.tv_fp_get_verification);
        et_password = (ClearEditText) findViewById(R.id.et_fp_set_password);
        bt_complete = (TextView) findViewById(R.id.tv_fp_button);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tv_verification.setOnClickListener(this);
        bt_complete.setOnClickListener(this);
        et_phone.addTextChangedListener(new TextWatcher() {
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
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
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
        et_verification.addTextChangedListener(new TextWatcher() {
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
        updateButtonState();
    }

    private void getVerification() {
        final String account = et_phone.getText().toString();
        if (!(RegularTool.isLegalChinaPhoneNumber(account) || RegularTool.isLegalEmailAddress(account))) {
            ToastUtil.show(R.string.AccountSecurity_tips);
        } else {
            progressDialogManager.showDialog(GET_VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doGetChagnePwdVerificationCode(account, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
                    timeCount.start();
                    et_verification.setFocusable(true);
                    et_verification.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_verification, InputMethodManager.SHOW_FORCED);
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    if (code == 2000002){
                        showIsRegisterDialog();
                    }else {
                        ToastUtil.show(msg);
                    }
                }
            });
        }
    }

    private void resetPassWord() {
        String account = et_phone.getText().toString();
        final String pwd = et_password.getText().toString();
        String authCode = et_verification.getText().toString();
        int code = RegularTool.CheckPassword(pwd);

        if (!(RegularTool.isLegalChinaPhoneNumber(account) || RegularTool.isLegalEmailAddress(account))) {
            ToastUtil.show(R.string.AccountSecurity_tips);
        } else if (StringUtil.isNullOrEmpty(authCode)) {
            ToastUtil.show(R.string.Forgot_Require_AuthCode);
        } else if (StringUtil.isNullOrEmpty(pwd)) {
            ToastUtil.show(R.string.GatewayChangePwd_NewPwd_Hint);
        } else if (pwd.length() < 8) {
            ToastUtil.show(R.string.Register_tips03);
        } else if(code == WLPassWordStrengthIllegal){
            ToastUtil.show(R.string.Register_tips02);
        } else if(code == WLPassWordStrengthNoMatchRule){
            ToastUtil.show(R.string.Register_tips04);
        } else {
            progressDialogManager.showDialog(FORGOT_PASSWORD, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doChangePwd(account, pwd, authCode, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(FORGOT_PASSWORD, 0);
                    ToastUtil.show(R.string.Forgot_Reset_Successful);
                    finish();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(FORGOT_PASSWORD, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_fp_get_verification:
                getVerification();
                break;
            case R.id.tv_fp_button:
                resetPassWord();
                break;
            default:
                break;
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
            tv_verification.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg_grey);
            tv_verification.setClickable(false);
            tv_verification.setText(millisUntilFinished / 1000 + getString(R.string.Register_SMS_Wait_Second));
        }

        @Override
        public void onFinish() {
            if (tv_verification == null){
                return;
            }
            tv_verification.setText(R.string.Forgot_ReSend);
            tv_verification.setClickable(true);
            tv_verification.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg);
        }
    }

    private void showIsRegisterDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(this.getString(R.string.ForgotPassword_No_Register_Hint))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(this.getString(R.string.ForgotPassword_No_Register))
                .setPositiveButton(this.getResources().getString(R.string.ForgotPassword_Do_Register))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        dialog.dismiss();
                        startActivity(new Intent(ForgotPassWordActivity.this, RegisterActivity.class));
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

    private void updateButtonState() {
        if (et_phone.getText().length() == 0 || et_verification.getText().length() == 0
                || et_password.getText().length() == 0) {
            bt_complete.setBackgroundResource(R.drawable.shape_btn_negative_bg);
            bt_complete.setClickable(false);
        } else {
            bt_complete.setBackgroundResource(R.drawable.shape_btn_active_bg);
            bt_complete.setClickable(true);
        }
    }
}
