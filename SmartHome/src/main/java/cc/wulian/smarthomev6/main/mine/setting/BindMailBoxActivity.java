package cc.wulian.smarthomev6.main.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:绑定邮箱获取验证码
 */
public class BindMailBoxActivity extends BaseTitleActivity {
    private static final String SET_EMAIL = "SET_EMAIL";

    private EditText mailboxEditText;
    private Button mailboxButton;
    private SsoApiUnit ssoApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_mailbox, true);
        ssoApiUnit = new SsoApiUnit(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_BindEmail));
    }

    @Override
    protected void initView() {
        mailboxEditText = (EditText) findViewById(R.id.mailbox_editBox);
        mailboxButton = (Button) findViewById(R.id.confirm_mailbox);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(mailboxButton, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(mailboxButton, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        mailboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMailCode();
            }
        });
    }

    private void getMailCode(){
        String mailBox = mailboxEditText.getText().toString();
        if (StringUtil.isNullOrEmpty(mailBox)){
            ToastUtil.show(R.string.AccountSecurity_Email_Hint);
            return;
        }else if (!RegularTool.isLegalEmailAddress(mailBox)){
            ToastUtil.show(R.string.AccountSecurity_Email_Tips);
            return;
        }
        progressDialogManager.showDialog(SET_EMAIL, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doBindMail(mailBox, new SsoApiUnit.SsoApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(SET_EMAIL, 0);
                startActivityForResult(new Intent(BindMailBoxActivity.this, ConfirmMailBoxActivity.class).putExtra("email", mailboxEditText.getText().toString()), 1);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SET_EMAIL, 0);
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            finish();
        }
    }
}