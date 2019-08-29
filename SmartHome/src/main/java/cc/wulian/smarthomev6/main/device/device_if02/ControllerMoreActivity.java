package cc.wulian.smarthomev6.main.device.device_if02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.custom.CustomMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.fan.FanMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.it_box.ITBoxMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.projector.ProjectorMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.stb.StbRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 遥控器更多界面
 */
public class ControllerMoreActivity extends BaseTitleActivity {
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";
    private TextView mTextName;
    private RelativeLayout rlEditMore;
    private String deviceID;
    private String blockName;
    private String blockType;
    private String blockId;
    private String codeLib;
    private WLDialog.Builder builder;
    private WLDialog renameDialog;
    private WLDialog deleteDialog;
    private DataApiUnit dataApiUnit;
    private String deviceName;
    private boolean isWidget;

    public static void start(Activity context, String deviceID, String blockName, String blockType, String blockId, String codeLib, boolean isWidget) {
        Intent intent = new Intent(context, ControllerMoreActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockId", blockId);
        intent.putExtra("codeLib", codeLib);
        intent.putExtra("isWidget", isWidget);
        context.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_controller_more, true);

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Mine_Setts);
    }

    @Override
    protected void initView() {
        super.initView();
        mTextName = (TextView) findViewById(R.id.item_device_more_rename_name);
        rlEditMore = (RelativeLayout) findViewById(R.id.item_device_more_edit);
        findViewById(R.id.item_device_more_delete).setOnClickListener(this);
        findViewById(R.id.item_device_more_rename).setOnClickListener(this);
        rlEditMore.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceID");
        blockName = getIntent().getStringExtra("blockName");
        blockType = getIntent().getStringExtra("blockType");
        blockId = getIntent().getStringExtra("blockId");
        codeLib = getIntent().getStringExtra("codeLib");
        isWidget = getIntent().getBooleanExtra("isWidget", false);

        dataApiUnit = new DataApiUnit(this);

        if (!TextUtils.isEmpty(blockName)) {
            mTextName.setText(blockName);
        }
        if (TextUtils.isEmpty(codeLib)) {
            rlEditMore.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_device_more_rename:
                showChangeNameDialog();
                break;
            case R.id.item_device_more_delete:
                showDeleteDialog();
                break;
            case R.id.item_device_more_edit:
                jumpEditController(blockType);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void jumpEditController(String blockType) {
        Intent intent = new Intent();
        switch (blockType) {
            case WifiIRManage.TYPE_TV:
                intent.setClass(this, TvRemoteMainActivity.class);
                break;
            case WifiIRManage.TYPE_FAN:
                intent.setClass(this, FanMainActivity.class);
                break;
            case WifiIRManage.TYPE_STB:
                intent.setClass(this, StbRemoteMainActivity.class);
                break;
            case WifiIRManage.TYPE_IT_BOX:
                intent.setClass(this, ITBoxMainActivity.class);
                break;
            case WifiIRManage.TYPE_PROJECTOR:
                intent.setClass(this, ProjectorMainActivity.class);
                break;
            case WifiIRManage.TYPE_AIR:
            case WifiIRManage.TYPE_CUSTOM:
                intent.setClass(this, CustomMainActivity.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//刷新
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", "MODE_LEARN");
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockId", blockId);
        intent.putExtra("isWidget", isWidget);
        startActivity(intent);
        finish();
    }


    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextText(blockName)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        deviceName = msg;
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }
                        changeName();
                        ProgressDialogManager.getDialogManager().showDialog(UPDATE, ControllerMoreActivity.this, null, null, 10000);

                    }

                    @Override
                    public void onClickNegative(View view) {
                        renameDialog.dismiss();
                    }
                });
        renameDialog = builder.create();
        if (!renameDialog.isShowing()) {
            renameDialog.show();
        }
    }


    private void showDeleteDialog() {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredtransponder_Airconditioner_Reconfirm)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        ProgressDialogManager.getDialogManager().showDialog(DELETE, ControllerMoreActivity.this, null, null, 10000);
                        delete();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        deleteDialog.dismiss();
                    }
                });
        deleteDialog = builder.create();
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }


    private void changeName() {
        dataApiUnit.doUpdateWifiIRController(deviceID, blockType, deviceName, blockId, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(UPDATE, 0);
                EventBus.getDefault().post(new DeviceReportEvent(MainApplication.getApplication().getDeviceCache().get(deviceID)));
                mTextName.setText(deviceName);
                renameDialog.dismiss();
                ToastUtil.show(getString(R.string.Change_Success));
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(UPDATE, 0);
                renameDialog.dismiss();
                ToastUtil.show(getString(R.string.Change_Fail));
            }
        });
    }

    private void delete() {
        dataApiUnit.doDeleteWifiIRController(deviceID, blockId, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(DELETE, 0);
                deleteDialog.dismiss();
                EventBus.getDefault().post(new DeviceReportEvent(MainApplication.getApplication().getDeviceCache().get(deviceID)));
                setResult(RESULT_OK, null);
                ControllerMoreActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(UPDATE, 0);
                deleteDialog.dismiss();
            }
        });
    }

}
