package cc.wulian.smarthomev6.main.device.device_if02;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.DeviceIF02Adapter;
import cc.wulian.smarthomev6.main.device.device_if02.airconditioner.AirConditionerMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerListBean;
import cc.wulian.smarthomev6.main.device.device_if02.custom.CustomMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.fan.FanMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.it_box.ITBoxMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.projector.ProjectorMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.setting.WifiIRSettingActivity;
import cc.wulian.smarthomev6.main.device.device_if02.stb.StbRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.MessageListenerAdapter;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2018/6/13.
 * func：wifi红外转发设备列表界面
 * email: hxc242313@qq.com
 */

public class WifiIRActivity extends BaseTitleActivity {

    private String deviceID;
    private boolean isSceneOrHouseKeeper;
    private Device mDevice;
    private View mViewAdd;
    private RecyclerView mRecyclerView;
    private DeviceIF02Adapter mAdapter;
    private ImageView mImageEmpty;
    private TextView mTextEmpty;
    private RelativeLayout mRelativeOffline;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private DataApiUnit dataApiUnit;
    private List<ControllerListBean.ControllerBean> controllerList;


    public static void start(Context context, String deviceID, boolean isSceneOrHouseKeeper) {
        Intent intent = new Intent(context, WifiIRActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("isSceneOrHouseKeeper", isSceneOrHouseKeeper);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_wifi_ir, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);
        deviceID = getIntent().getStringExtra("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }

    @Override
    protected void initView() {
        super.initView();

        mRecyclerView = (RecyclerView) findViewById(R.id.device_if01_recycler);
        mImageEmpty = (ImageView) findViewById(R.id.device_if01_image_empty);
        mTextEmpty = (TextView) findViewById(R.id.device_if01_text_empty);
        mRelativeOffline = (RelativeLayout) findViewById(R.id.device_if01_relative_offline);
        mViewAdd = findViewById(R.id.device_if01_add);

        if (isSceneOrHouseKeeper) {
            mViewAdd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        mAdapter = new DeviceIF02Adapter(deviceID);
        dataApiUnit = new DataApiUnit(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });

        updateState();
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();

        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(mViewAdd, SkinResouceKey.BITMAP_BUTTON_BG_S);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWifiIRDeviceList();
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        mViewAdd.setOnClickListener(this);
        mRelativeOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAdapter.setOnItemClickListener(new DeviceIF02Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(ControllerListBean.ControllerBean bean) {
                switch (bean.blockType) {
                    case WifiIRManage.TYPE_AIR:
                        if (TextUtils.isEmpty(bean.codeLib)) {
                            if (isSceneOrHouseKeeper) {
                                CustomMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                        bean.blockType,
                                        bean.blockName,
                                        bean.blockId,
                                        bean.codeLib);
                            } else {
                                CustomMainActivity.start(WifiIRActivity.this, deviceID,
                                        bean.blockType,
                                        bean.blockName,
                                        bean.blockId,
                                        bean.codeLib,
                                        false);
                            }
                        } else {
                            if (isSceneOrHouseKeeper) {
                                AirConditionerMainActivity.startForScene(
                                        WifiIRActivity.this, deviceID,
                                        bean.blockType,
                                        bean.blockName,
                                        bean.blockId,
                                        bean.codeLib);
                            } else {
                                AirConditionerMainActivity.start(
                                        WifiIRActivity.this, deviceID,
                                        bean.blockType,
                                        bean.blockName,
                                        bean.blockId,
                                        bean.codeLib);
                            }
                        }
                        break;
                    case WifiIRManage.TYPE_TV:
                        if (isSceneOrHouseKeeper) {
                            TvRemoteMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib);
                        } else {
                            TvRemoteMainActivity.start(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib,
                                    false);
                        }
                        break;

                    case WifiIRManage.TYPE_FAN:
                        if (isSceneOrHouseKeeper) {
                            FanMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib);
                        } else {
                            FanMainActivity.start(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib,
                                    false);
                        }
                        break;

                    case WifiIRManage.TYPE_STB:
                        if (isSceneOrHouseKeeper) {
                            StbRemoteMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib);
                        } else {
                            StbRemoteMainActivity.start(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib,
                                    false);
                        }
                        break;
                    case WifiIRManage.TYPE_IT_BOX:
                        if (isSceneOrHouseKeeper) {
                            ITBoxMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib);
                        } else {
                            ITBoxMainActivity.start(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib,
                                    false);
                        }
                        break;
                    case WifiIRManage.TYPE_PROJECTOR:
                        if (isSceneOrHouseKeeper) {
                            ProjectorMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib);
                        } else {
                            ProjectorMainActivity.start(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib,
                                    false);
                        }
                        break;
                    case WifiIRManage.TYPE_CUSTOM:
                        if (isSceneOrHouseKeeper) {
                            CustomMainActivity.startForScene(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib);
                        } else {
                            CustomMainActivity.start(WifiIRActivity.this, deviceID,
                                    bean.blockType,
                                    bean.blockName,
                                    bean.blockId,
                                    bean.codeLib,
                                    false);
                        }
                        break;
                }
            }
        });
    }

    private void updateState() {
        mViewAdd.setEnabled(mDevice.isOnLine());
        mRelativeOffline.setVisibility(mDevice.isOnLine() ? View.GONE : View.VISIBLE);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                startActivityForResult(new Intent(this, WifiIRSettingActivity.class).putExtra("deviceId", deviceID), 1);
                break;
            case R.id.device_if01_add:
                if (!TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
                    showDialog();
                } else {
                    WifiIRCategoryListActivity.start(WifiIRActivity.this, deviceID);
                }
                break;
        }
    }


    private void getWifiIRDeviceList() {
        dataApiUnit.doGetWifiIRControllerList(deviceID, new DataApiUnit.DataApiCommonListener<ControllerListBean>() {
            @Override
            public void onSuccess(ControllerListBean bean) {
                controllerList = bean.blocks;
                updateControllerView();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });

    }

    private void showDialog() {
        builder = new WLDialog.Builder(WifiIRActivity.this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(R.string.Infraredtransponder_Adddevice_LANlogin)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new MessageListenerAdapter() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            updateState();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceInfoChangedEvent event) {
        if (event != null && TextUtils.equals(event.deviceInfoBean.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
        }
    }

    private void updateControllerView() {

        if (controllerList == null || controllerList.size() == 0) {
            mAdapter.clear();
            mRecyclerView.setVisibility(View.INVISIBLE);
            mImageEmpty.setVisibility(View.VISIBLE);
            mTextEmpty.setVisibility(View.VISIBLE);
            return;
        }

        if (!controllerList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mImageEmpty.setVisibility(View.INVISIBLE);
            mTextEmpty.setVisibility(View.INVISIBLE);
            mAdapter.setData(controllerList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && deviceID != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(event.device.type, event.device.name));
            }
        }
    }
}
