package cc.wulian.smarthomev6.main.mine;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class FeedbackActivity extends BaseTitleActivity {

    private TextView mTextCount;
    private EditText mEditMsg, mEditEmail;
    private Button mButtonSubmit;
    private ButtonSkinWrapper buttonSkinWrapper;
    private String mMsg, mEmail;

    private boolean isSending = false;

    @Override
    protected void updateSkin() {
        super.updateSkin();
        buttonSkinWrapper = new ButtonSkinWrapper(mButtonSubmit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(R.string.Feedback_Theme);
    }

    @Override
    protected void initView() {
        mTextCount = (TextView) findViewById(R.id.feedback_text_count);
        mEditMsg = (EditText) findViewById(R.id.feedback_edit_msg);
        mEditEmail = (EditText) findViewById(R.id.feedback_edit_email);
        mButtonSubmit = (Button) findViewById(R.id.feedback_button_submit);
    }

    @Override
    protected void initListeners() {
        mEditMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 300 && s.length() > 0) {
                    mTextCount.setText((300 - s.length()) + "");
                    mMsg = mEditMsg.getText().toString().trim();
                    buttonSkinWrapper.setActive(true);
                } else {
                    buttonSkinWrapper.setActive(false);
                    if (s.length() == 0) {
                        mTextCount.setText(300 + "");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSending) {
                    return;
                }
                isSending = true;

                mEmail = mEditEmail.getText().toString().trim();
                if (TextUtils.isEmpty(mEmail)) {
                    mEmail = null;
                }

                if (!NetworkUtil.isConnected(FeedbackActivity.this)) {
                    ToastUtil.single(R.string.Feedback_NetworkUnconnected);
                    return;
                }

                new DataApiUnit(FeedbackActivity.this).doSendFeedback(mMsg, mEmail, null, new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        ToastUtil.single(R.string.Feedback_PutinSuccessfully);
                        finish();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                        isSending = false;
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        KeyboardUtil.hideKeyboard(this, mEditMsg);
    }
}
