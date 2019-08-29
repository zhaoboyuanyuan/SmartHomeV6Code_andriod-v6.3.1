package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Packet;

import java.text.DecimalFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity.cameaHelper;

/**
 * Created by hxc on 2017/6/6.
 * func:小物摄像机sd卡存储界面
 */

public class CylincamRecordStorageActivity extends BaseTitleActivity implements View.OnClickListener, CameraHelper.Observer {
    private static final String FORMAT = "FORMAT";
    private TextView tvTotalStorage;
    private TextView tvUsedStorage;
    private String deviceID;
    private Button btnFormat;
    private WLDialog formatDialog;
    private boolean isFormat = false;
    private float total, free;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_record_storage, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Device_VideoStorage));
    }

    @Override
    protected void initView() {
        super.initView();
        tvTotalStorage = (TextView) findViewById(R.id.tv_total);
        tvUsedStorage = (TextView) findViewById(R.id.tv_used_storage);
        btnFormat = (Button) findViewById(R.id.btn_formatting);
    }

    @Override
    protected void initData() {
        super.initData();
        if (cameaHelper != null) {
            cameaHelper.attach(this);
        }
        free = getIntent().getFloatExtra("free", 0.1f);
        total = getIntent().getFloatExtra("total", 0.1f);
        DecimalFormat num = new DecimalFormat("##0.00");
        tvTotalStorage.setText(total + "G");
        tvUsedStorage.setText(num.format(total - free) + "G");
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnFormat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_formatting:
                formatDialog = DialogUtil.showCommonDialog(this,
                        getString(R.string.Format), getString(R.string.Format_Tip), getString(R.string.Sure), getString(R.string.Cancel),
                        new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View var1, String msg) {
                                isFormat = true;
                                IotSendOrder.sendSdFormat(cameaHelper.getmCamera());
                                formatDialog.dismiss();
                                progressDialogManager.showDialog(FORMAT, CylincamRecordStorageActivity.this, getResources().getString(R.string.Formatting), null, 30000);
                            }

                            @Override
                            public void onClickNegative(View var1) {
                                formatDialog.dismiss();
                            }
                        });
                formatDialog.show();
        }
    }

    @Override
    public void avIOCtrlOnLine() {

    }

    @Override
    public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (avIOCtrlMsgType) {
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_RESP://sd卡状态
                        WLog.i(TAG, "查询摄像机SD卡状态返回");
                        progressDialogManager.dimissDialog(FORMAT, 0);
//                        checkSdUpdateUi(IotUtil.byteArrayToMap(data));
                        tvTotalStorage.setText(total + "G");
                        tvUsedStorage.setText("0G");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SDCARD_FORMAT_RESP://格式化
                        WLog.i(TAG, "格式化成功");
                        int result = Packet.byteArrayToInt_Little(data, 0);
                        if (result == 0) {
                            IotSendOrder.findSdCodeStatus(cameaHelper.getmCamera());//格式化成功  然后去查询一下sd卡的状态
                            return;
                        }
                        ToastUtil.show(getString(R.string.Format_Failed));
                        progressDialogManager.dimissDialog(FORMAT, 0);
                        break;
                }
            }
        });
    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {

    }
}
