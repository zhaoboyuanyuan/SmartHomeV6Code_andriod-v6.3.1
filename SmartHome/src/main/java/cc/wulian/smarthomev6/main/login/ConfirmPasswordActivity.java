package cc.wulian.smarthomev6.main.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

public class ConfirmPasswordActivity extends BaseTitleActivity{

    private static final String CHANGE_PWD = "CHANGE_PWD";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private EditText newPwdEditText;
    private Button confirmButton;
    private CheckBox cbNewPwd;

    private String oldPwd;
    private ButtonSkinWrapper buttonSkinWrapper;

    public static void start(Context context, String oldPwd) {
        Intent intent = new Intent(context, ConfirmPasswordActivity.class);
        intent.putExtra("oldPwd", oldPwd);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarOnlyTitle(getString(R.string.Register_tips09));
    }

    @Override
    protected void initView() {
        super.initView();
        itemPwdHint = (LinearLayout) findViewById(R.id.item_pwd_hint);
        pwdHintTextView = (TextView) findViewById(R.id.tv_pwd_hint);
        pwdHintImageView = (ImageView) findViewById(R.id.iv_pwd_hint);
        cbNewPwd = (CheckBox) findViewById(R.id.new_pwd_checkBox);

        newPwdEditText = (EditText) findViewById(R.id.new_pwd_editText);
        SpannableString password = new SpannableString(getString(R.string.Register_tips01));
        AbsoluteSizeSpan passwordAss = new AbsoluteSizeSpan(13, true);
        password.setSpan(passwordAss, 0, password.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        newPwdEditText.setHint(new SpannableString(password));

        confirmButton = (Button) findViewById(R.id.confirm_pwd_button);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(confirmButton);
        updateButtonState();
    }

    @Override
    protected void initData() {
        super.initData();
        oldPwd = getIntent().getStringExtra("oldPwd");
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        cbNewPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newPwdEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    newPwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                CharSequence charsequence = newPwdEditText.getText();
                if (charsequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charsequence;
                    Selection.setSelection(spanText, charsequence.length());
                }
            }
        });
        newPwdEditText.addTextChangedListener(new TextWatcher() {
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
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd = newPwdEditText.getText().toString();
                int code = RegularTool.CheckPassword(newPwd);

                if (newPwd.length() < 8) {
                    ToastUtil.show(R.string.Register_tips03);
                } else if(code == WLPassWordStrengthIllegal){
                    ToastUtil.show(R.string.Register_tips02);
                } else if(code == WLPassWordStrengthNoMatchRule){
                    ToastUtil.show(R.string.Register_tips04);
                } else if (StringUtil.equals(newPwd, oldPwd)) {
                    ToastUtil.show(R.string.Register_tips07);
                }else {
                    progressDialogManager.showDialog(CHANGE_PWD, ConfirmPasswordActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
                    SsoApiUnit ssoApiUnit = new SsoApiUnit(ConfirmPasswordActivity.this);
                    ssoApiUnit.doChangePwdByOld(oldPwd, newPwd, new SsoApiUnit.SsoApiCommonListener<Object>() {

                        @Override
                        public void onSuccess(Object bean) {
                            progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                            ToastUtil.show(R.string.AccountSecurity_ChangePassword_Hint);
                            MainApplication.getApplication().logout(false);
                            startActivity(new Intent(ConfirmPasswordActivity.this, SigninActivity.class));
                            finish();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                            ToastUtil.show(msg);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void updateButtonState() {
        if (newPwdEditText.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }
}
