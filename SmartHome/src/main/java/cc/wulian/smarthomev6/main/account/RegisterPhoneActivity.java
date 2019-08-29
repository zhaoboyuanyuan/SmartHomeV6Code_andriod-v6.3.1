package cc.wulian.smarthomev6.main.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseFullscreenActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.login.DisclaimerActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.customview.material.MaterialEditText;
import cc.wulian.smarthomev6.support.event.LoginSuccessEvent;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by 上海滩小马哥 on 2017/8/21.
 */

public class RegisterPhoneActivity extends BaseFullscreenActivity implements View.OnClickListener{

    private static final String GET_VERIFY = "GET_VERIFY";
    private View layout_root;
    private TextView tv_title,tv_read;
    private MaterialEditText et_account;
    private TextView tvGetVerification;
    private TextView tvTermsOfUse;
    private ImageView ivFinish;

    private SsoApiUnit ssoApiUnit;

    private ButtonSkinWrapper buttonSkinWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        ssoApiUnit = new SsoApiUnit(this);
    }

    @Override
    protected void initView() {
        layout_root = findViewById(R.id.layout_root);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_read = (TextView) findViewById(R.id.tv_read);
        et_account = (MaterialEditText) findViewById(R.id.et_account);
        tvGetVerification = (TextView) findViewById(R.id.tv_get_verification);
        tvTermsOfUse = (TextView) findViewById(R.id.tv_terms_of_use);
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
        Bitmap deleteEidtBitmap = skinManager.getBitmap(SkinResouceKey.BITMAP_BUTTON_EDIT_DELETE);
        if (deleteEidtBitmap != null) {
            et_account.setIconDelete(deleteEidtBitmap);
        }
        Integer textOtherColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_OTHER_TEXT);
        if (textOtherColor != null) {
            tv_title.setTextColor(textOtherColor);
            tv_read.setTextColor(textOtherColor);
            et_account.setTextColor(textOtherColor);
        }
        Integer textHintColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_TEXT);
        if (textHintColor != null) {
            et_account.setHintTextColor(textHintColor);
        }
        Integer textHintOtherColor = skinManager.getColor(SkinResouceKey.COLOR_HINT_OTHER_TEXT);
        if (textHintOtherColor != null) {
            et_account.setHintTextColor(textHintOtherColor);
            et_account.setUnderlineColor(textHintOtherColor);
        }
        Integer textHighlightColor = skinManager.getColor(SkinResouceKey.COLOR_LOGIN_HIGHLIGHT_TEXT);
        if (textHighlightColor != null) {
            et_account.setPrimaryColor(textHighlightColor);
            et_account.setFloatingLabelTextColor(textHighlightColor);
        }
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(tvGetVerification);
    }

    @Override
    protected void initListeners() {
        tvGetVerification.setOnClickListener(this);
        tvTermsOfUse.setOnClickListener(this);
        ivFinish.setOnClickListener(this);
        et_account.addTextChangedListener(new TextWatcher() {
            int beforeTextLength=0;
            int onTextLength=0;
            boolean isChanged = false;

            int location=0;//记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if(buffer.length()>0){
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if(s.charAt(i) == '-'){
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();

                onTextLength = s.length();
                buffer.append(s.toString());
                if(onTextLength == beforeTextLength || onTextLength <= 3 || isChanged){
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isChanged){
                    location = et_account.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if(buffer.charAt(index) == '-'){
                            buffer.deleteCharAt(index);
                        }else{
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if((index == 3 || index == 8)){
                            buffer.insert(index, '-');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if(konggeNumberC>konggeNumberB){
                        location+=(konggeNumberC-konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if(location>str.length()){
                        location = str.length();
                    }else if(location < 0){
                        location = 0;
                    }

                    et_account.setText(str);
                    Editable etable = et_account.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });

        updateButtonState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.tv_terms_of_use:
                startActivity(new Intent(RegisterPhoneActivity.this, DisclaimerActivity.class));
                break;
            case R.id.imageView_finish:
                EventBus.getDefault().post(new LoginSuccessEvent());
                finish();
                break;
            default:
                break;
        }
    }

    private void getVerification(){
        final String phone = et_account.getText().toString().replace("-","");
        if (StringUtil.isNullOrEmpty(phone)) {
            ToastUtil.show(R.string.Forgot_PhoneNumber_NotNull);
        } else if (!RegularTool.isLegalChinaPhoneNumber(phone)) {
            ToastUtil.show(R.string.Login_PhoneNumber_Error);
        } else {
            progressDialogManager.showDialog(GET_VERIFY, this, null, null, getResources().getInteger(R.integer.http_timeout));
            ssoApiUnit.doGetVerificationCode(phone, null, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    ToastUtil.show(R.string.Forgot_GetAreaCode_SuccessFul);
                    RegisterVerificationActivity.start(RegisterPhoneActivity.this, phone);
                    finish();
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(GET_VERIFY, 0);
                    ToastUtil.show(msg);
//                    if(code == 2000001){//验证手机号是否已经注册过
////                        showIsRegisterDialog();
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
