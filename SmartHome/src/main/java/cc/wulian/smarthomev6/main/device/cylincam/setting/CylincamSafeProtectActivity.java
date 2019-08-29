package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ConfigUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity.cameaHelper;

/**
 * Created by hxc on 2017/9/14.
 * func:小物摄像机安全防护界面
 */

public class CylincamSafeProtectActivity extends BaseTitleActivity implements View.OnClickListener, CameraHelper.Observer {
    private ImageView ivSafeProtectIcon;
    private TextView tvSafeProtectTips1;
    private TextView tvSafeProtectTips2;
    private TextView tvProtectTime;
    private LinearLayout llSafeProtectTime;
    private Button btnResetProtect;
    private Button btnStopProtect;
    private Button btnProtect;

    private String deviceID;
    private String[] fences = null;
    private int protectSwitch;//安全防护开关 0-close,1-open
    private SharedPreferences sp;
    public static final int REQUESTCODE_SETTING = 1;
    public static final String STOP_PROTECT = "stop_protect";
    private WLDialog mStopDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_safe_protect, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Device_Category_Safety));
    }

    @Override
    protected void initView() {
        super.initView();
        ivSafeProtectIcon = (ImageView) findViewById(R.id.iv_safe_protect_icon);
        tvSafeProtectTips1 = (TextView) findViewById(R.id.tv_safe_protect_tips1);
        tvSafeProtectTips2 = (TextView) findViewById(R.id.tv_safe_protect_tips2);
        tvProtectTime = (TextView) findViewById(R.id.tv_protect_time);
        llSafeProtectTime = (LinearLayout) findViewById(R.id.ll_safe_protect_time);
        btnResetProtect = (Button) findViewById(R.id.btn_reset_protect);
        btnStopProtect = (Button) findViewById(R.id.btn_stop_protect);
        btnProtect = (Button) findViewById(R.id.btn_protect);
    }

    @Override
    protected void initData() {
        super.initData();
        if (cameaHelper != null) {
            cameaHelper.attach(this);
        }
        protectSwitch = getIntent().getIntExtra("protectSwitch", -1);
        fences = getIntent().getStringArrayExtra("fences");
        deviceID = getIntent().getStringExtra("deviceId");
        updateView();
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnProtect, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setBackground(btnStopProtect, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnProtect, SkinResouceKey.COLOR_BUTTON_TEXT);
        skinManager.setTextColor(btnStopProtect, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        ivSafeProtectIcon.setOnClickListener(this);
        tvSafeProtectTips1.setOnClickListener(this);
        tvSafeProtectTips2.setOnClickListener(this);
        tvProtectTime.setOnClickListener(this);
        llSafeProtectTime.setOnClickListener(this);
        btnResetProtect.setOnClickListener(this);
        btnProtect.setOnClickListener(this);
        btnStopProtect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_stop_protect:
                showStopConfirmDialog();
                break;
            case R.id.btn_reset_protect:
            case R.id.btn_protect:
                startActivityForResult((new Intent(this, CylincamProtectSettingActivity.class)
                        .putExtra("fences", fences)).putExtra("deviceId",deviceID), REQUESTCODE_SETTING);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sp = getSharedPreferences(ConfigUtil.SP_CONFIG, MODE_PRIVATE);
        switch (requestCode) {
            case REQUESTCODE_SETTING:
                if (resultCode == RESULT_OK) {
                    fences = data.getStringArrayExtra("fences");
                    protectSwitch = data.getIntExtra("protectSwitch", -1);
                    WLog.i("safeProtect", "----onActivityResult--->");
                    updateView();
                }
                break;

            default:
                break;
        }
    }


    private void updateView() {
        if (protectSwitch == 0) {
            tvSafeProtectTips1.setVisibility(View.VISIBLE);
            tvSafeProtectTips1.setText(getString(R.string.Safety_Protection_Title));
            tvSafeProtectTips2.setVisibility(View.VISIBLE);
            ivSafeProtectIcon.setImageResource(R.drawable.icon_safe_protect_normal);
            llSafeProtectTime.setVisibility(View.GONE);
            btnResetProtect.setVisibility(View.GONE);
            btnStopProtect.setVisibility(View.GONE);
            btnProtect.setVisibility(View.VISIBLE);
        } else {
            ivSafeProtectIcon.setImageResource(R.drawable.icon_safe_protect_set);
            tvSafeProtectTips1.setText(getResources().getString(R.string.Opened_Safety_Protection));
            tvSafeProtectTips1.setVisibility(View.VISIBLE);
            tvSafeProtectTips2.setVisibility(View.GONE);
            llSafeProtectTime.setVisibility(View.VISIBLE);
            btnProtect.setVisibility(View.GONE);
            btnResetProtect.setVisibility(View.VISIBLE);
            btnStopProtect.setVisibility(View.VISIBLE);
            String weekday_move = CameraUtil.convertWeekday(CylincamSafeProtectActivity.this, fences[1]);
            String time_move = CameraUtil.convertTime(this,fences[2]);
            tvProtectTime.setText(weekday_move + time_move);
        }

    }

    private void showStopConfirmDialog() {
        Resources rs = getResources();
        mStopDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Hint),
                rs.getString(R.string.Stop_Protection_Tip), rs.getString(R.string.Sure), rs.getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        mStopDialog.dismiss();
                        protectSwitch = 0;
                        if (cameaHelper.getmCamera() != null) {
                            IotSendOrder.sendMotionDetection(cameaHelper.getmCamera(), IotUtil.assemblyMotion(fences, protectSwitch));
                            WLog.i(TAG, "灵敏度为" + fences[0] + "防护星期为" + fences[1] + ",防护时间为" + fences[2] + ",使能开关为" + protectSwitch + ",设防区域为" + fences[3]);
                        }
                        ProgressDialogManager.getDialogManager().showDialog(STOP_PROTECT, CylincamSafeProtectActivity.this, null, null, 10000);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        mStopDialog.dismiss();
                    }
                });

        mStopDialog.show();
    }

    @Override
    public void avIOCtrlOnLine() {

    }

    @Override
    public void avIOCtrlDataSource(byte[] data, final int avIOCtrlMsgType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (avIOCtrlMsgType) {
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
                        WLog.i(TAG, "安全防护设置成功");
                        ProgressDialogManager.getDialogManager().dimissDialog(STOP_PROTECT, 0);
                        updateView();
                }
            }
        });
    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {

    }
}
