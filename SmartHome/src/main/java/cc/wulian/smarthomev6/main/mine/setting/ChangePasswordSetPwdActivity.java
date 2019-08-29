package cc.wulian.smarthomev6.main.mine.setting;

import android.content.Context;
import android.content.Intent;
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

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.ChangePasswordActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthHigh;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthIllegal;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthMiddle;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNoMatchRule;
import static cc.wulian.smarthomev6.support.tools.RegularTool.WLPassWordStrengthNone;

public class ChangePasswordSetPwdActivity extends BaseTitleActivity {

    private static final String CHANGE_PWD = "CHANGE_PWD";
    private LinearLayout itemPwdHint;
    private TextView pwdHintTextView;
    private ImageView pwdHintImageView;
    private EditText newPwdEditText;
    private Button confirmButton;
    private CheckBox cbNewPwd;

    private String account;
    private String newPwd;

    private ButtonSkinWrapper buttonSkinWrapper;

    public static void start(Context context, String account) {
        Intent intent = new Intent(context, ChangePasswordSetPwdActivity.class);
        intent.putExtra("account", account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_set_pwd, true);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.GatewayChangePwd_Tittle));
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
    }

    @Override
    protected void initData() {
        super.initData();
        account = getIntent().getStringExtra("account");
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
                if (code == WLPassWordStrengthNone) {
                    itemPwdHint.setVisibility(View.INVISIBLE);
                } else {
                    itemPwdHint.setVisibility(View.VISIBLE);
                    if (code == WLPassWordStrengthIllegal) {
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    } else if (code == WLPassWordStrengthNoMatchRule) {
                        pwdHintTextView.setText(R.string.Register_tips11);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_02);
                    } else if (code == WLPassWordStrengthHigh) {
                        pwdHintTextView.setText(R.string.Register_tips13);
                        pwdHintImageView.setImageResource(R.drawable.icon_three_04);
                    } else if (code == WLPassWordStrengthMiddle) {
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
                newPwd = newPwdEditText.getText().toString();
                int code = RegularTool.CheckPassword(newPwd);

                if (newPwd.length() < 8) {
                    ToastUtil.show(R.string.Register_tips03);
                } else if (code == WLPassWordStrengthIllegal) {
                    ToastUtil.show(R.string.Register_tips02);
                } else if (code == WLPassWordStrengthNoMatchRule) {
                    ToastUtil.show(R.string.Register_tips04);
                } else {
                    reset(newPwd);
                }
            }
        });

        updateButtonState();
    }

    private void reset(String password) {

        int code = RegularTool.CheckPassword(password);

        if (!(RegularTool.isLegalChinaPhoneNumber(account) || RegularTool.isLegalEmailAddress(account))) {
            ToastUtil.show(getString(R.string.AccountSecurity_tips));
        } else if (StringUtil.isNullOrEmpty(password)) {
            ToastUtil.show(getString(R.string.GatewayChangePwd_NewPwd_Hint));
        } else if (password.length() < 8) {
            ToastUtil.show(R.string.Register_tips03);
        } else if (code == WLPassWordStrengthIllegal) {
            ToastUtil.show(R.string.Register_tips02);
        } else if (code == WLPassWordStrengthNoMatchRule) {
            ToastUtil.show(R.string.Register_tips04);
        } else {
            progressDialogManager.showDialog(CHANGE_PWD, this, null, null, getResources().getInteger(R.integer.http_timeout));
            new SsoApiUnit(this).doUpdateAndLoginByAccount(account, null, password, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
                @Override
                public void onSuccess(RegisterPhoneBean bean) {
                    progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                    startActivity(new Intent(ChangePasswordSetPwdActivity.this, HomeActivity.class));
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(CHANGE_PWD, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    private void updateButtonState() {
        if (newPwdEditText.getText().length() == 0) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }
}
