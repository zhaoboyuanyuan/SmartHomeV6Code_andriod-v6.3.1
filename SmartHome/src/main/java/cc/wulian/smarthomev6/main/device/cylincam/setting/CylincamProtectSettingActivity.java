package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.main.device.lookever.setting.LookEverProtectTimeActivity;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity.cameaHelper;

/**
 * Created by hxc on 2017/6/7.
 * func:企鹅机安全防护设置界面
 */

public class CylincamProtectSettingActivity extends BaseTitleActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, CameraHelper.Observer {
    public static final int REQUESTCODE_MOVE_TIME = 1;
    public static final int REQUESTCODE_MOVE_AREA = 2;
    public static final int REQUESTCODE_COVER_TIME = 3;
    public static final int REQUESTCODE_COVER_AREA = 4;
    private TextView tvProtectTime;
    private TextView tvProtectArea;
    private SeekBar sbMove;
    private Button btnStartProtect;
    private RelativeLayout rlProtectTime;
    private RelativeLayout rlProtectArea;

    private String deviceId;
    private String[] fences = null;
    private int progress;

    public static final String START_PROTECT = "START_PROTECT";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_protect_setting, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Safety_Fortification));
    }

    @Override
    protected void initView() {
        super.initView();
        tvProtectTime = (TextView) findViewById(R.id.tv_protect_time);
        tvProtectArea = (TextView) findViewById(R.id.tv_protect_area);
        sbMove = (SeekBar) findViewById(R.id.sb_move);
        btnStartProtect = (Button) findViewById(R.id.btn_start_protect);
        rlProtectTime = (RelativeLayout) findViewById(R.id.rl_protect_time);
        rlProtectArea = (RelativeLayout) findViewById(R.id.rl_protect_area);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnStartProtect, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnStartProtect, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        if (cameaHelper != null) {
            cameaHelper.attach(this);
        }
        fences = getIntent().getStringArrayExtra("fences");
        deviceId = getIntent().getStringExtra("deviceId");
        showSelectedSensitivity();
        updateView();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnStartProtect.setOnClickListener(this);
        sbMove.setOnSeekBarChangeListener(this);
        rlProtectTime.setOnClickListener(this);
        rlProtectArea.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_start_protect:
                getSelectedSensitivity();
                IotSendOrder.sendMotionDetection(cameaHelper.getmCamera(), IotUtil.assemblyMotion(fences, 1));
                ProgressDialogManager.getDialogManager().showDialog(START_PROTECT, this, null, null, 10000);
                break;
            case R.id.rl_protect_time:
                startActivityForResult(
                        (new Intent(this, LookEverProtectTimeActivity.class)).putExtra(
                                "time", fences[2]).putExtra("weekday",
                                fences[1]), REQUESTCODE_MOVE_TIME);
                break;
            case R.id.rl_protect_area:
                startActivityForResult(new Intent(this, CylincamProtectAreaActivity.class)
                        .putExtra("area", fences[3]).putExtra("type", REQUESTCODE_MOVE_AREA).putExtra("deviceId", deviceId), REQUESTCODE_MOVE_AREA);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_MOVE_TIME:
                if (resultCode == RESULT_OK) {
                    fences[1] = data.getStringExtra("weekday");
                    fences[2] = data.getStringExtra("time");
                    updateView();
                }
                break;
            case REQUESTCODE_MOVE_AREA:
                fences[3] = data.getStringExtra("area");
                break;
        }
    }

    private void updateView() {
        if (fences != null) {
            int length = fences[3].split(";").length;
            if (length <= 0) {
                tvProtectArea.setText(getResources().getString(R.string.Not_set));
            } else {
                tvProtectArea.setText(getResources().getString(R.string.Have_Set));
            }

            String weekday_move = CameraUtil.convertWeekday(this, fences[1]);
            String time_move = CameraUtil.convertTime(this, fences[2]);
            tvProtectTime.setText(weekday_move + " " + time_move);
        }
    }

    private void showSelectedSensitivity() {
        if (fences != null) {
            switch (fences[0]) {
                case "0":
                    sbMove.setProgress(20);
                    break;
                case "1":
                    sbMove.setProgress(40);
                    break;
                case "2":
                    sbMove.setProgress(60);
                    break;
                case "3":
                    sbMove.setProgress(80);
                    break;
                case "4":
                    sbMove.setProgress(100);
                    break;
                default:
                    break;

            }
        }
    }

    private void getSelectedSensitivity() {
        if (0 <= progress && progress <= 20) {
            fences[0] = "0";
        } else if (21 < progress && progress <= 40) {
            fences[0] = "1";
        } else if (41 < progress && progress <= 60) {
            fences[0] = "2";
        } else if (61 < progress && progress <= 80) {
            fences[0] = "3";
        } else if (81 < progress && progress <= 100) {
            fences[0] = "4";
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.sb_move) {
            progress = sbMove.getProgress();
        }
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
                        ProgressDialogManager.getDialogManager().dimissDialog(START_PROTECT, 0);
                        setResult(RESULT_OK, new Intent(CylincamProtectSettingActivity.this,
                                CylincamSafeProtectActivity.class).putExtra("fences", fences).putExtra("protectSwitch", 1));
                        CylincamProtectSettingActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {

    }
}
