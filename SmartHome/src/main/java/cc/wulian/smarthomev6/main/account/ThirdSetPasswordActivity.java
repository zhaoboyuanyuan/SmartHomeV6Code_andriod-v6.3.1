package cc.wulian.smarthomev6.main.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseFullscreenActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ThirdPartyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

/**
 * Created by 上海滩小马哥 on 2017/8/30.
 *
 */

public class ThirdSetPasswordActivity extends BaseFullscreenActivity {

    private static final String REGISTER = "REGISTER";
    private View layout_root;
    private TextView tv_title, tv_tips;
    private ImageView iv_underline;
    private CheckBox cbPwdShow;
    private EditText etPwd;
    private TextView tvSure;
    private TextView tvPwdHint;
    private ImageView ivPwdHint;
    private LinearLayout itemPwdHint;
    private ImageView ivFinish;

    private SsoApiUnit ssoApiUnit;
    private String account;
    private ThirdPartyBean thirdPartyData;

    private ButtonSkinWrapper buttonSkinWrapper;


    public static void start(Context context, String account, ThirdPartyBean thirdPartyData) {
        Intent intent = new Intent(context, ThirdSetPasswordActivity.class);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra("THIRDPARTYDATA", thirdPartyData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_set_password);
        ssoApiUnit = new SsoApiUnit(this);
    }

    @Override
    protected void initView() {
        layout_root = findViewById(R.id.layout_root);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        iv_underline = (ImageView) findViewById(R.id.iv_underline);
        tvSure = (TextView) findViewById(R.id.tv_sure);
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        ivPwdHint = (ImageView) findViewById(R.id.iv_pwd_hint);
        tvPwdHint = (TextView) findViewById(R.id.tv_pwd_hint);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        cbPwdShow = (CheckBox) findViewById(R.id.cb_pwd_show);
        ivFinish = (ImageView) findViewById(R.id.imageView_finish);
    }

    @Override
    protected void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_root, SkinResouceKey.BITMAP_MAIN_BACKGROUND);
        skinManager.setImageViewDrawable(ivFinish, SkinResouceKey.BITMAP_BUTTON_EXIT);
        Integer textOtherColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_OTHER_TEXT);
        if (textOtherColor != null) {
            tv_title.setTextColor(textOtherColor);
            etPwd.setTextColor(textOtherColor);
        }
        Integer textHintColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_TEXT);
        if (textHintColor != null) {
            etPwd.setHintTextColor(textHintColor);
        }
        Integer textHintOtherColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_OTHER_TEXT);
        if (textHintOtherColor != null) {
            etPwd.setHintTextColor(textHintOtherColor);
            tv_tips.setTextColor(textHintOtherColor);
            tvPwdHint.setTextColor(textHintOtherColor);
            iv_underline.setBackgroundColor(textHintOtherColor);
        }
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(tvSure);
    }


    @Override
    protected void initData() {
        if (getIntent() != null) {
            account = getIntent().getStringExtra("ACCOUNT");
            thirdPartyData = (ThirdPartyBean) getIntent().getSerializableExtra("THIRDPARTYDATA");
        }
    }

    @Override
    protected void initListeners() {
        cbPwdShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                CharSequence charsequence = etPwd.getText();
                if (charsequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charsequence;
                    Selection.setSelection(spanText, charsequence.length());
                }
            }
        });

        etPwd.addTextChangedListener(new TextWatcher() {
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
                        tvPwdHint.setText(R.string.Register_tips11);
                        ivPwdHint.setImageResource(R.drawable.icon_three_02);
                    }else if (code == WLPassWordStrengthNoMatchRule){
                        tvPwdHint.setText(R.string.Register_tips11);
                        ivPwdHint.setImageResource(R.drawable.icon_three_02);
                    }else if (code == WLPassWordStrengthHigh){
                        tvPwdHint.setText(R.string.Register_tips13);
                        ivPwdHint.setImageResource(R.drawable.icon_three_04);
                    }else if (code == WLPassWordStrengthMiddle){
                        tvPwdHint.setText(R.string.Register_tips12);
                        ivPwdHint.setImageResource(R.drawable.icon_three_03);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAndLogin(etPwd.getText().toString());
            }
        });

        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateButtonState();
    }

    private void registerAndLogin(String password){
        int code = RegularTool.CheckPassword(password);

        if(code == WLPassWordStrengthIllegal){
            ToastUtil.show(R.string.Register_tips02);
        }else if(code == WLPassWordStrengthNoMatchRule){
            ToastUtil.show(R.string.Register_tips04);
        }else {
            progressDialogManager.showDialog(REGISTER, ThirdSetPasswordActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doThirdRegister(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(), thirdPartyData.getPartnerId() + "", thirdPartyData.getAvatar(), thirdPartyData.getNick(), account, "86", null, password, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
                @Override
                public void onSuccess(RegisterPhoneBean bean) {
                    ToastUtil.show(R.string.Bind_Success);
                    preference.saveAutoLogin(true);
                    preference.saveIsLogin(true);
                    preference.saveUserEnterType(Preference.ENTER_TYPE_ACCOUNT);
                    preference.saveThirdPartyLogin(true);
                    getUserInfo();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(REGISTER, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    private void getUserInfo() {
        ssoApiUnit.doGetUserInfo(new SsoApiUnit.SsoApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(final UserBean bean) {
                progressDialogManager.dimissDialog(REGISTER, 0);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(REGISTER, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void updateButtonState() {
        if (etPwd.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }

}
