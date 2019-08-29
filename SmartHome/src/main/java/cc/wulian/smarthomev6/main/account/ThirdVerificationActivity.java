package cc.wulian.smarthomev6.main.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by 上海滩小马哥 on 2017/8/30.
 */

public class ThirdVerificationActivity extends BaseFullscreenActivity {

    private static final String VERIFY = "VERIFY";
    private static final String GET_VERIFY = "GET_VERIFY";
    private static final String UPDATE_PHONE = "UPDATE_PHONE";
    private View layout_root;
    private TextView tv_title;
    private EditText etVerication;
    private TextView tvSendAgain;
    private ImageView ivFinish;
    private LinearLayout pwdShowLayout;

    private SsoApiUnit ssoApiUnit;
    private TimeCount timeCount;
    private String account;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private ThirdPartyBean thirdPartyData;

    private Integer textHintOtherColor;

    public static void start(Context context, String account, ThirdPartyBean thirdPartyData) {
        Intent intent = new Intent(context, ThirdVerificationActivity.class);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra("THIRDPARTYDATA", thirdPartyData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_verification);

        ssoApiUnit = new SsoApiUnit(this);
    }

    @Override
    protected void initView() {
        layout_root = findViewById(R.id.layout_root);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tvSendAgain = (TextView) findViewById(R.id.tv_send_again);
        etVerication = (EditText) findViewById(R.id.et_verication);
        ivFinish = (ImageView) findViewById(R.id.imageView_finish);
        pwdShowLayout = (LinearLayout) findViewById(R.id.password_show_linearLayout);

    }

    @Override
    protected void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_root, SkinResouceKey.BITMAP_MAIN_BACKGROUND);
        skinManager.setImageViewDrawable(ivFinish, SkinResouceKey.BITMAP_BUTTON_EXIT);
        Integer textOtherColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_OTHER_TEXT);
        if (textOtherColor != null) {
            tv_title.setTextColor(textOtherColor);

            //生成密码短杠的图片
            float radius = DisplayUtil.dip2Pix(getApplicationContext(), 2);
            float[] outerR = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
            RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setColor(textOtherColor);
            shapeDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            shapeDrawable.getPaint().setStrokeWidth(0);
            for (int i = 1; i <= 6; i++) {
                TextView textView = (TextView) pwdShowLayout.findViewWithTag("tv_verification_" + i);
                textView.setTextColor(textOtherColor);

                ImageView imageView = (ImageView) pwdShowLayout.findViewWithTag("iv_verification_" + i);
                imageView.setBackground(shapeDrawable);
            }

        }
        textHintOtherColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_OTHER_TEXT);
        if (textHintOtherColor != null) {
            tvSendAgain.setTextColor(textHintOtherColor);
        }
    }


    @Override
    protected void initData() {
        if (getIntent() != null) {
            account = getIntent().getStringExtra("ACCOUNT");
            thirdPartyData = (ThirdPartyBean) getIntent().getSerializableExtra("THIRDPARTYDATA");
        }
        timeCount = new TimeCount(60000, 1000);
        timeCount.start();
    }

    @Override
    protected void initListeners() {
        etVerication.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    updateView(0, "");
                } else if (count == 6) {
                    updateAllView(s.toString());
                } else {
                    updateView(s.length(), s.toString().substring(s.length() - 1, s.length()));
                }

                if (s.length() == 6) {
                    bindPhone(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvSendAgain.setOnClickListener(new View.OnClickListener() {
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
    }

    private void updateView(int num, String text) {
        for (int i = 0; i < 6; i++) {
            if ((i + 1) == num) {
                TextView textView = (TextView) pwdShowLayout.findViewWithTag("tv_verification_" + num);
                ImageView imageView = (ImageView) pwdShowLayout.findViewWithTag("iv_verification_" + num);
                textView.setText(text);
                imageView.setVisibility(View.INVISIBLE);
            } else if ((i + 1) > num) {
                TextView textView = (TextView) pwdShowLayout.findViewWithTag("tv_verification_" + (i + 1));
                ImageView imageView = (ImageView) pwdShowLayout.findViewWithTag("iv_verification_" + (i + 1));
                textView.setText("");
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateAllView(String text) {
        for (int i = 0; i < 6; i++) {
            char s = text.charAt(i);
            TextView textView = (TextView) pwdShowLayout.findViewWithTag("tv_verification_" + (i + 1));
            ImageView imageView = (ImageView) pwdShowLayout.findViewWithTag("iv_verification_" + (i + 1));
            textView.setText(String.valueOf(s));
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    private void getVerification() {
        if (TextUtils.isEmpty(account)) {
            return;
        }
        progressDialogManager.showDialog(GET_VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doThirdGetPhoneCode(account, null, new SsoApiUnit.SsoApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                timeCount.start();
                progressDialogManager.dimissDialog(GET_VERIFY, 0);
                ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(GET_VERIFY, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void bindPhone(String authCode) {
        if (!RegularTool.isLegalChinaPhoneNumber(account)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
            return;
        }
        progressDialogManager.showDialog(VERIFY, ThirdVerificationActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doThirdBind(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(),thirdPartyData.getPartnerId() + "", account, "86", null, authCode, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
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
                progressDialogManager.dimissDialog(VERIFY, 0);
                if (code == 2000008) {
                    showUpdateDialog();
                } else if (code == 2000002) {
                    showRegisterDialog();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    private void updatePhone() {
        progressDialogManager.showDialog(UPDATE_PHONE, ThirdVerificationActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doThirdUpdate(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(), thirdPartyData.getPartnerId() + "", account, "86", null, etVerication.getText().toString(), new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
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

    private void showUpdateDialog() {
        String msg = getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_Wechat);
        if (thirdPartyData.getPartnerId() == 1) {
            msg = getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_Wechat);
        } else if (thirdPartyData.getPartnerId() == 2) {
            msg = getString(R.string.Third_PartyLogin_PhoneNumber_Rebunding_QQ);
        } else if (thirdPartyData.getPartnerId() == 3) {
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
                        startActivityForResult(new Intent(ThirdVerificationActivity.this, ThirdSetPasswordActivity.class).putExtra("THIRDPARTYDATA", thirdPartyData).putExtra("ACCOUNT", account), 1);
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
            if (textHintOtherColor == null) {
                tvSendAgain.setTextColor(getResources().getColor(R.color.v6_text_secondary));
            } else {
                tvSendAgain.setTextColor(textHintOtherColor);
            }
            tvSendAgain.setClickable(false);
            tvSendAgain.setText(String.format(getString(R.string.login_tips7), millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            if (tvSendAgain == null){
                return;
            }
            tvSendAgain.setText(R.string.Forgot_ReSend);
            tvSendAgain.setClickable(true);
            tvSendAgain.setTextColor(getResources().getColor(R.color.v6_green));
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
