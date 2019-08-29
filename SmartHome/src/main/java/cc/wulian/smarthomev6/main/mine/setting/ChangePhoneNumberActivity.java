package cc.wulian.smarthomev6.main.mine.setting;

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

import com.alibaba.fastjson.JSON;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.customview.ClearEditText;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:更改手机号
 */
public class ChangePhoneNumberActivity extends BaseTitleActivity {

    private static final String CHANGE_PHONE = "CHANGE_PHONE";
    private TextView tvCountries;
    private ClearEditText etPhoneNumber;
    private EditText etVerification;
    private TextView tvGetVerification;
    private TextView tvRegister;

    private SsoApiUnit ssoApiUnit;
    private TimeCount timeCount;
    private UserBean userBean;

    private ButtonSkinWrapper buttonSkinWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number, true);
        ssoApiUnit = new SsoApiUnit(this);
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_PhoneNumber_Hint));
    }

    @Override
    protected void initView() {
        tvCountries = (TextView) findViewById(R.id.tv_countries);
        etPhoneNumber = (ClearEditText) findViewById(R.id.et_register_phone_number);
        etVerification = (EditText) findViewById(R.id.et_verification);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        tvRegister = (TextView) findViewById(R.id.tv_register_button);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(tvRegister);
    }

    @Override
    protected void initData() {
        userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
        //设置edittext   hint属性的文本的字体颜色
        SpannableString phoneNumber = new SpannableString(getString(R.string.Register_Phone_Hint));
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        phoneNumber.setSpan(ass, 0, phoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNumber.setHint(new SpannableString(phoneNumber));
    }

    @Override
    protected void initListeners() {
        tvCountries.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        etVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateButtonState();
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
                updateButtonState();
            }
        });
        updateButtonState();
    }

    private void getVerification() {
        final String phone = etPhoneNumber.getText().toString();
        if (StringUtil.isNullOrEmpty(phone)) {
            ToastUtil.show(R.string.Forgot_PhoneNumber_NotNull);
        } else if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
        } else {
            ssoApiUnit.doGetChagnePhoneVerificationCode(phone, null, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
                    timeCount.start();
                }

                @Override
                public void onFail(int code, String msg) {
                    ToastUtil.show(msg);
                }
            });
        }
    }

    private void changePhone() {
        final String phone = etPhoneNumber.getText().toString();
        final String authCode = etVerification.getText().toString();
        if (StringUtil.isNullOrEmpty(phone)) {
            ToastUtil.show(R.string.Forgot_PhoneNumber_NotNull);
        } else if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
        } else if (StringUtil.isNullOrEmpty(authCode)) {
            ToastUtil.show(R.string.Forgot_Require_AuthCode);
        } else {
            progressDialogManager.showDialog(CHANGE_PHONE, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doChangePhone(phone, null, authCode, new SsoApiUnit.SsoApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(CHANGE_PHONE, 0);
                    ToastUtil.show(R.string.AccountSecurity_PhoneNumber_Success);
                    userBean.phone = etPhoneNumber.getText().toString();
                    String jsonObject = JSON.toJSONString(userBean);
                    Preference.getPreferences().saveCurrentAccountInfo(jsonObject);
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(CHANGE_PHONE, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.tv_register_button:
                changePhone();
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
            if (tvGetVerification == null) {
                return;
            }
            tvGetVerification.setText(R.string.Forgot_ReSend);
            tvGetVerification.setClickable(true);
            tvGetVerification.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg);
        }
    }

    private void updateButtonState() {
        if (etVerification.getText().length() == 0 || etPhoneNumber.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }

}