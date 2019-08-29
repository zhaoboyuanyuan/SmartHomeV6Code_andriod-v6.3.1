package cc.wulian.smarthomev6.main.mine.platform.whiterobot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ControlPlatformApiUnit;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/12/19.
 * 小白机器人，取消授权界面
 */

public class WhiteRobotUnauthActivity extends BaseTitleActivity {
    private static final String GET_DATA = "GET_DATA";

    private Button btn_unauth;
    private ControlPlatformApiUnit controlPlatformApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiterobot_unauth, true);
        controlPlatformApiUnit = new ControlPlatformApiUnit(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Authorize_Login));
    }

    @Override
    protected void initView() {
        btn_unauth = (Button) findViewById(R.id.btn_unauth);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btn_unauth, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btn_unauth, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btn_unauth.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_unauth) {
            WLDialog.Builder builder = new WLDialog.Builder(this);
            builder.setMessage(getString(R.string.Cancel_Authorize_Tip))
                    .setCancelOnTouchOutSide(false)
                    .setNegativeButton(R.string.Cancel)
                    .setPositiveButton(R.string.Sure)
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View var1, String msg) {
                            unauth();
                        }

                        @Override
                        public void onClickNegative(View var1) {

                        }
                    })
                    .create()
                    .show();
        }
    }

    private void unauth() {
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        controlPlatformApiUnit.doDeleteAuthStatus(ApiConstant.getUserID(), ApiConstant.WHITEROBOT_CLIEND_ID, new ControlPlatformApiUnit.CommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                ToastUtil.show(msg);
            }
        });
    }
}
