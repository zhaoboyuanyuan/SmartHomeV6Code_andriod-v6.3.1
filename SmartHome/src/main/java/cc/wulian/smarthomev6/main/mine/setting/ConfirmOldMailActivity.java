package cc.wulian.smarthomev6.main.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
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
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者: chao
 * 时间: 2017/7/10
 * 描述: 换绑邮箱，验证旧邮箱
 * 联系方式: 805901025@qq.com
 */

public class ConfirmOldMailActivity extends BaseTitleActivity {
    private static final String GET_VERIFY_CODE = "GET_VERIFY_CODE";
    private static final String VERIFY = "VERIFY";

    private TextView tvOldPhoneNumber;
    private EditText etVerification;
    private TextView tvGetVerification;
    private TextView tvConfirm;

    private SsoApiUnit ssoApiUnit;
    private TimeCount timeCount;
    private UserBean userBean;

    private ButtonSkinWrapper buttonSkinWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_mail, true);
        ssoApiUnit = new SsoApiUnit(this);
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_Identity_Verification));
    }

    @Override
    protected void initView() {
        tvOldPhoneNumber = (TextView) findViewById(R.id.tv_old_phone_number);
        etVerification = (EditText) findViewById(R.id.et_verification);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(tvConfirm);
    }

    @Override
    protected void initData() {
        userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
        tvOldPhoneNumber.setText(userBean.email);
    }

    @Override
    protected void initListeners() {
        tvGetVerification.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
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
        updateButtonState();
    }

    private void getVerification() {
        final String mail = userBean.email;
        if (StringUtil.isNullOrEmpty(mail)){
            ToastUtil.show(R.string.AccountSecurity_Email_Hint);
            return;
        }else if (!RegularTool.isLegalEmailAddress(mail)){
            ToastUtil.show(R.string.AccountSecurity_WrongEmail);
            return;
        }
        progressDialogManager.showDialog(GET_VERIFY_CODE, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doGetVerifyOldMailCode(mail, new SsoApiUnit.SsoApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(GET_VERIFY_CODE, 0);
                ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
                timeCount.start();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(GET_VERIFY_CODE, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void verifyPhone() {
        final String mail = userBean.email;
        if (StringUtil.isNullOrEmpty(mail)){
            ToastUtil.show(R.string.AccountSecurity_Email_Hint);
            return;
        }else if (!RegularTool.isLegalEmailAddress(mail)){
            ToastUtil.show(R.string.AccountSecurity_WrongEmail);
            return;
        }
        progressDialogManager.showDialog(VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doVerifyOldMail(mail, etVerification.getText().toString(), new SsoApiUnit.SsoApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(VERIFY, 0);
                startActivityForResult(new Intent(ConfirmOldMailActivity.this, ChangeMailActivity.class), 1);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(VERIFY, 0);
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
            case R.id.tv_confirm:
                verifyPhone();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            finish();
        }
    }

    private void updateButtonState() {
        if (etVerification.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }
}
