package cc.wulian.smarthomev6.main.device.penguin.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.callback.IcamMsgEventHandler;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/6/6.
 * func:企鹅机录像存储界面
 */

public class PenguinRecordStorageActivity extends BaseTitleActivity implements IcamMsgEventHandler {
    private static final String FORMAT = "FORMAT";
    private TextView tvTotalStorage;
    private TextView tvUsedStorage;
    private ICamDeviceBean iCamDeviceBean;
    private String deviceID;
    private String uniqueDeviceId;
    private String sipDomain;
    private Button btnFormat;
    private boolean isFormatOk;
    private WLDialog formatDialog;
    private boolean isFormat = false;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    IPCMsgController.MsgQueryStorageStatus(uniqueDeviceId, sipDomain);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penguin_record_storage, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        deviceID = iCamDeviceBean.did;
        uniqueDeviceId = iCamDeviceBean.uniqueDeviceId;
        sipDomain = iCamDeviceBean.sdomain;
        IPCMsgController.MsgQueryStorageStatus(uniqueDeviceId, sipDomain);
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
                                IPCMsgController.MsgConfigLocalStorageDeviceFormat(uniqueDeviceId, sipDomain);
                                isFormat = true;
                                formatDialog.dismiss();
                                progressDialogManager.showDialog(FORMAT, PenguinRecordStorageActivity.this, getResources().getString(R.string.Formatting), null);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            ToastUtil.show(R.string.Setting_Fail);
            switch (event.getApiType()) {
                case QUERY_STORAGE_STATUS:
                    ToastUtil.show(getString(R.string.Config_Query_Device_Fail));
                    break;
                case CONFIG_LOCAL_STORAGE_DEVICE_FORMAT:
                    ToastUtil.show(getString(R.string.Format_Failed));
                    break;
            }
        } else {
            switch (event.getApiType()) {
                case QUERY_STORAGE_STATUS:
                    Pattern p = Pattern
                            .compile("<storage.*totalsize=\"(\\d+)\"\\s+.*freesize=\"(\\d+)\".*/?>(</storage>)?");
                    Matcher matcher = p.matcher(event.getMessage());
                    if (matcher.find()) {
                        try {
                            String totalSize = matcher.group(1).trim();
                            String freeSize = matcher.group(2).trim();

                            long totalSizeNum = Long.parseLong(totalSize) * 1024;
                            long freeSizeNum = Long.parseLong(freeSize) * 1024;
                            tvTotalStorage.setText(CameraUtil.convertFileSize(totalSizeNum));
                            tvUsedStorage.setText(CameraUtil.convertFileSize(totalSizeNum - freeSizeNum));
                            if (CameraUtil.convertFileSize(freeSizeNum).equals(CameraUtil.convertFileSize(totalSizeNum))) {
                                isFormatOk = true;
                                myHandler.removeMessages(0);
                                if (isFormat) {
                                    ToastUtil.show(R.string.Format_Success);
                                }
                                progressDialogManager.dimissDialog(FORMAT, 0);
                            } else {
                                myHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CONFIG_LOCAL_STORAGE_DEVICE_FORMAT:// 格式化
                    if (CameraUtil.getParamFromXml(event.getMessage(), "status").equalsIgnoreCase(
                            "ok")) {
                        myHandler.sendEmptyMessage(0);
                    } else {
                        ToastUtil.show(R.string.Format_Failed);
                    }

                    break;
            }
        }
    }
}
