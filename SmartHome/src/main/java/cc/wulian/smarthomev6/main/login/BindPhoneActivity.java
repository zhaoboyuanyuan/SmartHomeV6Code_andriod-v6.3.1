package cc.wulian.smarthomev6.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ThirdPartyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.customview.ClearEditText;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者: chao
 * 时间: 2017/7/3
 * 描述: 第三方登录绑定手机号
 * 联系方式: 805901025@qq.com
 */

@Deprecated
public class BindPhoneActivity extends BaseTitleActivity {
    private static final String GET_VERIFICATION = "GET_VERIFICATION";
    private static final String BIND_PHONE = "BIND_PHONE";
    private static final String UPDATE_PHONE = "UPDATE_PHONE";

    private TextView tvCountries;
    private ClearEditText etPhoneNumber;
    private EditText etVerification;
    private TextView tvGetVerification;
    private TextView tvBind;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private SsoApiUnit ssoApiUnit;
    private TimeCount timeCount;
    private ThirdPartyBean thirdPartyData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number, true);
        ssoApiUnit = new SsoApiUnit(this);
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Third_PartyLogin_PhoneNumber_Bunding));
    }

    @Override
    protected void initView() {
        tvCountries = (TextView) findViewById(R.id.tv_countries);
        etPhoneNumber = (ClearEditText) findViewById(R.id.et_register_phone_number);
        etVerification = (EditText) findViewById(R.id.et_verification);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        tvBind = (TextView) findViewById(R.id.tv_register_button);
        tvBind.setText(getString(R.string.Sure));
    }

    @Override
    protected void initData() {
        //设置edittext   hint属性的文本的字体颜色
        SpannableString phoneNumber = new SpannableString(getString(R.string.Register_Phone_Hint));
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        phoneNumber.setSpan(ass, 0, phoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNumber.setHint(new SpannableString(phoneNumber));

        thirdPartyData = (ThirdPartyBean) getIntent().getSerializableExtra("thirdPartyData");
    }

    @Override
    protected void initListeners() {
        tvCountries.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        tvBind.setOnClickListener(this);
        etVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!StringUtil.isNullOrEmpty(editable.toString()) &&
                        !StringUtil.isNullOrEmpty(etPhoneNumber.getText().toString())) {
                    tvBind.setClickable(true);
                    tvBind.setBackgroundResource(R.drawable.shape_btn_active_bg);
                } else {
                    tvBind.setClickable(false);
                    tvBind.setBackgroundResource(R.drawable.shape_btn_negative_bg);
                }
            }
        });
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!StringUtil.isNullOrEmpty(editable.toString()) &&
                        !StringUtil.isNullOrEmpty(etVerification.getText().toString())) {
                    tvBind.setClickable(true);
                    tvBind.setBackgroundResource(R.drawable.shape_btn_active_bg);
                } else {
                    tvBind.setClickable(false);
                    tvBind.setBackgroundResource(R.drawable.shape_btn_negative_bg);
                }
            }
        });
        tvBind.setClickable(false);
    }

    private void getVerification() {
        final String phone = etPhoneNumber.getText().toString();
        if (StringUtil.isNullOrEmpty(phone)) {
            ToastUtil.show(R.string.Forgot_PhoneNumber_NotNull);
        } else if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
        } else {
            progressDialogManager.showDialog(GET_VERIFICATION, BindPhoneActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doThirdGetPhoneCode(phone, "86", new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(GET_VERIFICATION, 0);
                    ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
                    timeCount.start();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(GET_VERIFICATION, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    private void bindPhone() {
        String phone = etPhoneNumber.getText().toString();
        String authcode = etVerification.getText().toString();

        if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
            return;
        }
        progressDialogManager.showDialog(BIND_PHONE, BindPhoneActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doThirdBind(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(), thirdPartyData.getPartnerId()+ "", phone, "86", null, authcode, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
            @Override
            public void onSuccess(RegisterPhoneBean bean) {
                progressDialogManager.dimissDialog(BIND_PHONE, 0);
                ToastUtil.show(R.string.Bind_Success);
                preference.saveAutoLogin(true);
                preference.saveIsLogin(true);
                preference.saveUserEnterType(Preference.ENTER_TYPE_ACCOUNT);
                preference.saveThirdPartyLogin(true);
                getUserInfo();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(BIND_PHONE, 0);
                if (code== 2000008){
                    showUpdateDialog();
                }else if (code== 2000002){
                    showRegisterDialog();
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    private void updatePhone() {
        progressDialogManager.showDialog(UPDATE_PHONE, BindPhoneActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doThirdUpdate(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(), thirdPartyData.getPartnerId()+ "", etPhoneNumber.getText().toString(), "86", null, etVerification.getText().toString(), new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
            @Override
            public void onSuccess(RegisterPhoneBean bean) {
                progressDialogManager.dimissDialog(UPDATE_PHONE, 0);
                ToastUtil.show(R.string.Bind_Success);
                preference.saveAutoLogin(true);
                preference.saveIsLogin(true);
                preference.saveUserEnterType(Preference.ENTER_TYPE_ACCOUNT);
                preference.saveThirdPartyLogin(true);
                getUserInfo();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(UPDATE_PHONE, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void getUserInfo() {
        ssoApiUnit.doGetUserInfo(new SsoApiUnit.SsoApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(final UserBean bean) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.tv_register_button:
                bindPhone();
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

    private void showUpdateDialog() {
        String msg =getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_Wechat);
        if (thirdPartyData.getPartnerId() == 1){
            msg = getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_Wechat);
        }else if (thirdPartyData.getPartnerId() == 2){
            msg = getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_QQ);
        }else if (thirdPartyData.getPartnerId() == 3){
            msg = getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_Weibo);
        }
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.Third_PartyLogin_PhoneNumber_Bunding_Permit))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        updatePhone();
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

    private void showRegisterDialog() {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setMessage(getString(R.string.Third_PartyLogin_PhoneNumber_Register))
                .setPositiveButton(getString(R.string.Login_Account_Register_Go))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        startActivityForResult(new Intent(BindPhoneActivity.this, ThirdLoginSetPwdActivity.class).putExtra("thirdPartyData", thirdPartyData).putExtra("phone", etPhoneNumber.getText().toString()), 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
