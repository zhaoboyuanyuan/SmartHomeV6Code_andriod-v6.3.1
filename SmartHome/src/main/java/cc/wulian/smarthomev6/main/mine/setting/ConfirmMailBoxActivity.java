package cc.wulian.smarthomev6.main.mine.setting;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:绑定邮箱
 */
public class ConfirmMailBoxActivity extends BaseTitleActivity {
    private static final String SET_EMAIL = "SET_EMAIL";
    private static final String GET_EMAIL_CODE = "GET_EMAIL_CODE";

    private TextView again_textView;
    private TextView tv_email;
    private Button confirm_button;
    private EditText et_verification;
    private UserBean userBean;
    private TimeCount timeCount;

    private String email;
    private SsoApiUnit ssoApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssoApiUnit = new SsoApiUnit(this);
        setContentView(R.layout.activity_confirm_mailbox, true);
        timeCount = new TimeCount(60000, 1000);
        timeCount.start();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_VerifyEmail));
    }

    @Override
    protected void initView() {
        tv_email = (TextView) findViewById(R.id.tv_email);
        again_textView = (TextView) findViewById(R.id.again_textView);
        confirm_button = (Button) findViewById(R.id.confirm_button);
        et_verification = (EditText) findViewById(R.id.et_verification);
        again_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMailCode();
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindMailBox();
            }
        });
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(confirm_button, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(confirm_button, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
        email = getIntent().getStringExtra("email");
        tv_email.setText(email);
    }

    private void bindMailBox(){
        progressDialogManager.showDialog(SET_EMAIL, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doVerifyMail(email,et_verification.getText().toString(), new SsoApiUnit.SsoApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(SET_EMAIL, 0);
                userBean.email = email;
                String jsonObject = JSON.toJSONString(userBean);
                Preference.getPreferences().saveCurrentAccountInfo(jsonObject);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SET_EMAIL, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void getMailCode(){
        progressDialogManager.showDialog(GET_EMAIL_CODE, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doBindMail(email, new SsoApiUnit.SsoApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(GET_EMAIL_CODE, 0);
                timeCount.start();
                ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(GET_EMAIL_CODE, 0);
                ToastUtil.show(msg);
            }
        });
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
            again_textView.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg_grey);
            again_textView.setClickable(false);
            again_textView.setText(getString(R.string.Forgot_ReSend) + "(" + millisUntilFinished / 1000 + getString(R.string.Register_SMS_Wait_Second) + ")");
        }

        @Override
        public void onFinish() {
            if (again_textView == null){
                return;
            }
            again_textView.setText(R.string.Forgot_ReSend);
            again_textView.setClickable(true);
            again_textView.setBackgroundResource(R.drawable.shape_get_verification_code_btn_bg);
        }
    }
}