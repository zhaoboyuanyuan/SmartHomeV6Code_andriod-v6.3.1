package cc.wulian.smarthomev6.main.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

/**
 * 作者: chao
 * 时间: 2017/7/3
 * 描述: 第三方登录绑定手机号设置密码
 * 联系方式: 805901025@qq.com
 */

public class ThirdLoginSetPwdActivity extends BaseTitleActivity {
    private static final String BIND_PHONE = "BIND_PHONE";

    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;

    private ClearEditText etPassword;
    private TextView tvBind;

    private SsoApiUnit ssoApiUnit;
    private ThirdPartyBean thirdPartyData;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_login_setpwd, true);
        ssoApiUnit = new SsoApiUnit(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_CreatPassword));
    }

    @Override
    protected void initView() {
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        pwdHintTextView = (TextView) findViewById(R.id.tv_pwd_hint);
        pwdHintImageView = (ImageView) findViewById(R.id.iv_pwd_hint);
        etPassword = (ClearEditText) findViewById(R.id.et_register_phone_number);
        tvBind = (TextView) findViewById(R.id.tv_register_button);
        tvBind.setText(getString(R.string.Sure));
    }

    @Override
    protected void initData() {
        //设置edittext   hint属性的文本的字体颜色
        SpannableString phoneNumber = new SpannableString(getString(R.string.GatewayChangePwd_NewPwd_Hint));
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        phoneNumber.setSpan(ass, 0, phoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPassword.setHint(new SpannableString(phoneNumber));

        thirdPartyData = (ThirdPartyBean) getIntent().getSerializableExtra("thirdPartyData");
        phone = getIntent().getStringExtra("phone");
    }

    @Override
    protected void initListeners() {
        tvBind.setOnClickListener(this);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
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
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() < 8) {
                    tvBind.setClickable(false);
                    tvBind.setBackgroundResource(R.drawable.shape_btn_negative_bg);
                } else {
                    tvBind.setClickable(true);
                    tvBind.setBackgroundResource(R.drawable.shape_btn_active_bg);
                }
            }
        });
        tvBind.setClickable(false);
    }


    private void setPwd() {
        String pwd = etPassword.getText().toString();
        int code = RegularTool.CheckPassword(pwd);

        if(code == WLPassWordStrengthIllegal){
            ToastUtil.show(R.string.Register_tips02);
        }else if(code == WLPassWordStrengthNoMatchRule){
            ToastUtil.show(R.string.Register_tips04);
        }else {
            progressDialogManager.showDialog(BIND_PHONE, ThirdLoginSetPwdActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doThirdRegister(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(), thirdPartyData.getPartnerId() + "", thirdPartyData.getAvatar(), thirdPartyData.getNick(), phone, "86", null, pwd, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
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
                    ToastUtil.show(msg);
                }
            });
        }
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
            case R.id.tv_register_button:
                setPwd();
                break;
        }
    }
}
