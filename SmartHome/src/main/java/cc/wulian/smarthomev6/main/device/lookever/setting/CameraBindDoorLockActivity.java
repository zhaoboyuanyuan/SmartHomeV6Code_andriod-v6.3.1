package cc.wulian.smarthomev6.main.device.lookever.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.DoorLockAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/6/12.
 * func:随便看绑定门锁界面
 */

public class CameraBindDoorLockActivity extends BaseTitleActivity implements View.OnClickListener {
    private ListView lvBindLock;
    private WLDialog tipDialog;
    private WLDialog unbindDialog;
    private TextView tvBindLock;
    private TextView tvLockStatus;
    private TextView tvGatewayId;
    private TextView tvGatewayInfo;
    private Button btnUnbindLock;
    private LinearLayout llBindLock;
    private LinearLayout llLockStatus;
    private Device lockDevice;
    private String lockId;
    private String cameraId;
    private String cameraType;

    private DeviceCache deviceCache;
    private DeviceApiUnit deviceApiUnit;
    private List<Device> list;
    private boolean hasBindLock = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookever_bind_doorlock, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Bind_Lock), R.drawable.icon_help);
    }

    @Override
    protected void initView() {
        super.initView();
        lvBindLock = (ListView) findViewById(R.id.lv_lock_list);
        tvBindLock = (TextView) findViewById(R.id.tvBindLock);
        tvLockStatus = (TextView) findViewById(R.id.tvLockStatus);
        tvGatewayId = (TextView) findViewById(R.id.tvGatewayId);
        btnUnbindLock = (Button) findViewById(R.id.btn_unbind_lock);
        llBindLock = (LinearLayout) findViewById(R.id.ll_bind_lock);
        tvGatewayInfo = (TextView) findViewById(R.id.tv_gateway_info);
        llLockStatus = (LinearLayout) findViewById(R.id.ll_lock_status);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnUnbindLock, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnUnbindLock, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvBindLock.setOnClickListener(this);
        tvLockStatus.setOnClickListener(this);
        tvGatewayId.setOnClickListener(this);
        btnUnbindLock.setOnClickListener(this);
        llBindLock.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        cameraId = getIntent().getStringExtra("cameraId");
        cameraType = getIntent().getStringExtra("cameraType");
        deviceCache = MainApplication.getApplication().getDeviceCache();
        deviceApiUnit = new DeviceApiUnit(this);
        queryLockList();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                tipDialog = DialogUtil.showTipsDialog(CameraBindDoorLockActivity.this, getString(R.string.Help_Info),
                        getString(R.string.Lookever_Open_Lock_Tip),
                        getString(R.string.Tip_I_Known), new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View var1, String msg) {
                                tipDialog.dismiss();
                            }

                            @Override
                            public void onClickNegative(View var1) {

                            }
                        });
                tipDialog.show();
                break;
            case R.id.btn_unbind_lock:
                showUnbindDialog();
                break;
        }
    }

    private void getAllLock() {
        list = new ArrayList<>();
        for (Device device : deviceCache.getDevices()) {
            if (device.type.equals("70") || device.type.equals("OP")
                    || device.type.equals("Bg") || device.type.equals("Bd")
                    || device.type.equals("OW") || device.type.equals("Bf")
                    || device.type.equals("Bq")) {
                list.add(device);
            }
        }
    }

    private void queryLockList() {
        deviceApiUnit.doGetBindLock(cameraId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
                    @Override
                    public void onSuccess(DeviceRelationListBean bean) {
                        if (bean.deviceRelation == null || bean.deviceRelation.isEmpty()) {
                            lvBindLock.setVisibility(View.VISIBLE);
                            llBindLock.setVisibility(View.GONE);
                            getAllLock();
                            if (list.size() == 0) {
                                ToastUtil.show(getString(R.string.No_Lock));
                            }
                            final DoorLockAdapter adapter = new DoorLockAdapter(CameraBindDoorLockActivity.this, list, cameraId);
                            adapter.setbindLockCallback(new BindLockCallback() {
                                @Override
                                public void bind(Device device) {
                                    DeviceApiUnit deviceApiUnit = new DeviceApiUnit(CameraBindDoorLockActivity.this);
                                    deviceApiUnit.doBindDoorLock(cameraId, cameraType, device.devID, device.type, device.gwID, "1", new DeviceApiUnit.DeviceApiCommonListener<ResponseBean>() {
                                        @Override
                                        public void onSuccess(ResponseBean bean) {
                                            Log.i("hxc", bean.toString());
                                            if (bean.isSuccess()) {
                                                ToastUtil.show(R.string.Bind_Success);
                                                queryLockList();
                                            }
                                        }

                                        @Override
                                        public void onFail(int code, String msg) {
                                            ToastUtil.show(msg);
                                        }
                                    });
                                }
                            });
                            lvBindLock.setAdapter(adapter);

                        } else {
                            lvBindLock.setVisibility(View.GONE);
                            llBindLock.setVisibility(View.VISIBLE);
                            lockId = bean.deviceRelation.get(0).targetDeviceid;
                            lockDevice = deviceCache.get(lockId);
                            if (lockDevice != null) {
                                tvGatewayId.setText(lockDevice.gwID);
                                if (lockDevice.isOnLine()) {
                                    tvLockStatus.setText(getString(R.string.Device_Online));
                                } else {
                                    tvLockStatus.setText(getString(R.string.Device_Offline));
                                }
                                tvBindLock.setText(DeviceInfoDictionary.getNameByTypeAndName(lockDevice.type, lockDevice.name));
                                tvGatewayInfo.setText(getString(R.string.Device_Lock_Binding_Currentgateway));
                            } else {
                                llLockStatus.setVisibility(View.GONE);
                                tvBindLock.setText(DeviceInfoDictionary.getDefaultNameByType(bean.deviceRelation.get(0).targetDevicetype));
                                tvGatewayId.setText(bean.deviceRelation.get(0).gatewayId);
                                tvGatewayInfo.setText(getString(R.string.Device_Lock_Binding_Noncurrentgateway));
                            }

                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.show(msg);
                    }
                }
        );
    }

    private void showUnbindDialog() {
        unbindDialog = DialogUtil.showCommonDialog(this,
                getString(R.string.Unbind_Lock), getString(R.string.Device_Lock_Setup_Unbundling), getString(R.string.Sure), getString(R.string.Cancel), new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        unbindDoorLock();
                        unbindDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        unbindDialog.dismiss();
                    }
                });
        unbindDialog.show();
    }

    private void unbindDoorLock() {
        deviceApiUnit.doUnBindDoorLock(cameraId, lockId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                llBindLock.setVisibility(View.GONE);
                ToastUtil.show(R.string.Device_UNBind_Success);
                queryLockList();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
