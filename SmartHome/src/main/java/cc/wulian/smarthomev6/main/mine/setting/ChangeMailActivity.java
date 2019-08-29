package cc.wulian.smarthomev6.main.mine.setting;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 作者: chao
 * 时间: 2017/7/10
 * 描述: 换绑邮箱
 * 联系方式: 805901025@qq.com
 */

public class ChangeMailActivity extends BaseTitleActivity {
    private static final String CHANGE_MAIL = "CHANGE_MAIL";

    private ClearEditText etMAil;
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
        setContentView(R.layout.activity_change_mail, true);
        ssoApiUnit = new SsoApiUnit(this);
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_ChangedEmail));
    }

    @Override
    protected void initView() {
        etMAil = (ClearEditText) findViewById(R.id.et_register_phone_number);
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
    }

    @Override
    protected void initListeners() {
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
        etMAil.addTextChangedListener(new TextWatcher() {
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
        final String mail = etMAil.getText().toString();
        if (StringUtil.isNullOrEmpty(mail)){
            ToastUtil.show(R.string.AccountSecurity_Email_Hint);
        }else if (!RegularTool.isLegalEmailAddress(mail)){
            ToastUtil.show(R.string.AccountSecurity_WrongEmail);
            return;
        }else {
            ssoApiUnit.doBindMail(mail, new SsoApiUnit.SsoApiCommonListener<Object>() {
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

    private void changeMail() {
        final String mail = etMAil.getText().toString();
        final String authCode = etVerification.getText().toString();
        if (TextUtils.isEmpty(mail) || !RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.show(R.string.AccountSecurity_WrongEmail);
            return;
        }else if (StringUtil.isNullOrEmpty(authCode)){
            ToastUtil.show(R.string.Forgot_Require_AuthCode);
            return;
        } else {
            progressDialogManager.showDialog(CHANGE_MAIL, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doVerifyMail(mail, authCode, new SsoApiUnit.SsoApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(CHANGE_MAIL, 0);
                    ToastUtil.show(R.string.AccountSecurity_PhoneNumber_Success);
                    userBean.email = etMAil.getText().toString();
                    String jsonObject = JSON.toJSONString(userBean);
                    Preference.getPreferences().saveCurrentAccountInfo(jsonObject);
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(CHANGE_MAIL, 0);
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
                changeMail();
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

    private void updateButtonState() {
        if (etVerification.getText().length() == 0 || etMAil.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }
}
