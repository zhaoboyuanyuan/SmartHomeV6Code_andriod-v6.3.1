package cc.wulian.smarthomev6.main.account;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseFullscreenActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.customview.ClearEditText;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class ForgotAccountActivity extends BaseFullscreenActivity {

    private static final String GET_VERIFY = "GET_VERIFY";
    private View layout_root;
    private TextView tv_title;
    private ImageView iv_underline;
    private ClearEditText et_account;
    private TextView tvGetVerification;
    private ImageView ivFinish;

    private SsoApiUnit ssoApiUnit;

    private ButtonSkinWrapper buttonSkinWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_account);
        ssoApiUnit = new SsoApiUnit(this);
    }


    @Override
    protected void initView() {
        layout_root = findViewById(R.id.layout_root);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_underline = (ImageView) findViewById(R.id.iv_underline);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        et_account = (ClearEditText) findViewById(R.id.et_account);
        ivFinish = (ImageView) findViewById(R.id.imageView_finish);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_root, SkinResouceKey.BITMAP_MAIN_BACKGROUND);
        skinManager.setImageViewDrawable(ivFinish, SkinResouceKey.BITMAP_BUTTON_EXIT);
        Drawable deleteEidtDrawable = skinManager.getDrawable(SkinResouceKey.BITMAP_BUTTON_EDIT_DELETE);
        if (deleteEidtDrawable != null) {
            et_account.setDeleteImageDrawable(deleteEidtDrawable);
        }
        Integer textOtherColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_OTHER_TEXT);
        if (textOtherColor != null) {
            tv_title.setTextColor(textOtherColor);
            et_account.setTextColor(textOtherColor);
        }
        Integer textHintColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_TEXT);
        if (textHintColor != null) {
            et_account.setHintTextColor(textHintColor);
        }
        Integer textHintOtherColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_OTHER_TEXT);
        if (textHintOtherColor != null) {
            et_account.setHintTextColor(textHintOtherColor);
            iv_underline.setBackgroundColor(textHintOtherColor);
        }
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(tvGetVerification);
    }

    @Override
    protected void initListeners() {
        et_account.addTextChangedListener(new TextWatcher() {
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

        tvGetVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerification();
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

    private void getVerification() {
        final String account = et_account.getText().toString();
        if (!(RegularTool.isLegalChinaPhoneNumber(account) || RegularTool.isLegalEmailAddress(account))) {
            ToastUtil.show(getString(R.string.AccountSecurity_tips));
        } else {
            progressDialogManager.showDialog(GET_VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doGetChagnePwdVerificationCode(account, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    ToastUtil.show(getString(R.string.Forgot_GetAreaCode_SuccessFul));
                    ForgotVerificationActivity.start(ForgotAccountActivity.this, account);
                    finish();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    ToastUtil.show(msg);
//                    if (code == 2000002){
//                        showIsRegisterDialog();
//                    }else {
//                    }
                }
            });
        }
    }

    private void updateButtonState() {
        if (et_account.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }
}
