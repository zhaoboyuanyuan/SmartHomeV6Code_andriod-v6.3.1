package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.BaseFragment;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter.GateWayListAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListBeanAll;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenu;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static android.app.Activity.RESULT_OK;

/**
 * Created by syf on 2017/2/17.
 */

public class BindGatewayFragment extends BaseFragment {

    private static final String GET = "GET";
    private static final String VERIFY = "VERIFY";
    private static final String DELETE = "DELETE";
    private static final int BIND = 0;
    private static final int CONFIRM = 1;
    private Context context;

    private SwipeMenuListView gateWayListView;
    private List<DeviceBean> gateWayList;
    private List<DeviceBean> allGateway;
    private GatewayManager gatewayManager;
    private GateWayListAdapter gateWayListAdapter;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private DeviceApiUnit deviceApiUnit;
    private String bindGwId;
    private String currentPage;
    private String pageSize = "10";
    private int totalCount;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_bind_gateway;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        gateWayListAdapter = new GateWayListAdapter(getActivity(), null);
        gateWayListAdapter.setMenuCreator(creatLeftDeleteItem());
        gateWayListAdapter.setOnMenuItemClickListener(new SwipeMenuAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                showUnbindDialog(gateWayListAdapter.getItem(position).getDeviceId());
            }
        });
        gateWayListView.setAdapter(gateWayListAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void initView() {
        gateWayList = new ArrayList<>();
        allGateway = new ArrayList<>();
        gatewayManager = new GatewayManager();
        deviceApiUnit = new DeviceApiUnit(getActivity());
        gateWayListView = (SwipeMenuListView) rootView.findViewById(R.id.gateway_listView);
        gateWayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DeviceBean bean = gateWayList.get(position);
                if (!bean.getIsSelect()) {
                    if ("2".equals(bean.relationStatus)) {

                        //查询网关密码
                        final String password = preference.getGatewayPassword(bean.deviceId);
                        if (TextUtils.isEmpty(password)) {
                            showPasswordDialog(bean);
                        } else {
                            verifyPassword(bean.deviceId, password, bean);
                        }
                    } else {
                        ProgressDialogManager.getDialogManager().showDialog(VERIFY, mActivity, null, null, mActivity.getResources().getInteger(R.integer.http_timeout));
                        deviceApiUnit.doSwitchGatewayId(bean.deviceId, new DeviceApiUnit.DeviceApiCommonListener() {
                            @Override
                            public void onSuccess(Object object) {
                                ProgressDialogManager.getDialogManager().dimissDialog(VERIFY, 0);
                                switchGateway(bean, null, true);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ProgressDialogManager.getDialogManager().dimissDialog(VERIFY, 0);
//                                if (20121 == code) {
                                showPasswordDialog(bean);
//                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        getAllGateway(false);
    }

    /**
     * 创建左划删除item样式
     */
    private SwipeMenuCreator creatLeftDeleteItem() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu, int position) {
                SwipeMenuItem settingItem = new SwipeMenuItem(mActivity);
                settingItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                settingItem.setWidth(DisplayUtil.dip2Pix(mActivity, 90));
                settingItem.setTitle(R.string.AuthManager_Disbind);
                settingItem.setTitleSize(DisplayUtil.dip2Sp(mActivity, 5));
                settingItem.setTitleColor(mActivity.getResources().getColor(R.color.white));
                menu.addMenuItem(settingItem);
            }
        };
        return creator;
    }

    private void getAllGateway(final boolean isNewBind) {
        gateWayList.clear();
        allGateway.clear();
        ProgressDialogManager.getDialogManager().showDialog(GET, mActivity, null, null, mActivity.getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.getWifiDeviceList("1", pageSize, new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
            @Override
            public void onSuccess(DeviceListBeanAll bean) {
                parserAllDeviceList(bean);
                int allCount = Integer.parseInt(bean.totalCount);
                int size = Integer.parseInt(pageSize);
                //总共需要分页的次数
                final int times = allCount % size == 0 ? allCount / size : allCount / size + 1;
                if (times <= 1) {
                    updateView(isNewBind);
                } else {
                    //需要开启的线程总数
                    totalCount = times - 1;
                    createThreadPoolTask(totalCount, isNewBind);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET, 0);
                ToastUtil.show(msg);
            }
        });

    }

    private void parserAllDeviceList(DeviceListBeanAll bean) {
        for (DeviceBean deviceBean : bean.devices) {
            if (deviceBean.isGateway()) {
                deviceBean.setName((DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName())));
                if (!deviceBean.isShared()) {
                    gateWayList.add(deviceBean);
                }
                allGateway.add(deviceBean);
                gatewayManager.saveGatewayInfo(deviceBean);
            }

        }
    }

    private void createThreadPoolTask(int times, final boolean isNewBind) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < times; i++) {
            final int page = i + 2;//第二页开始
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "开启子线程：" + (page - 1));
                    try {
                        deviceApiUnit.getWifiDeviceList(String.valueOf(page), pageSize, new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
                            @Override
                            public void onSuccess(DeviceListBeanAll bean) {
                                --totalCount;
                                Log.i(TAG, "获取第" + bean.currentPage + "页数据，" + "剩余线程数：" + totalCount);
                                parserAllDeviceList(bean);
                                if (totalCount == 0) {
                                    updateView(isNewBind);
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                --totalCount;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        fixedThreadPool.shutdown();
    }

    private void updateView(boolean isNewBind) {
        selectCurrentGateway(allGateway);
        gateWayListAdapter.swapData(gateWayList);
        // 保存 绑定 的 网关的 数量
        if (allGateway.isEmpty()) {
            Preference.getPreferences().saveBindGatewayCount(0);
        } else {
            Preference.getPreferences().saveBindGatewayCount(allGateway.size());
        }
        List<String> gatewayIdList = new ArrayList<>();
        for (DeviceBean deviceBean : allGateway) {
            gatewayIdList.add(deviceBean.deviceId);
        }
        Preference.getPreferences().saveCurrentGatewayList(gatewayIdList);
        Log.i(TAG, "swapData:" + gateWayList.size());
        ProgressDialogManager.getDialogManager().dimissDialog(GET, 0);
        if (isNewBind) {
            for (DeviceBean deviceBean : allGateway) {
                if (TextUtils.equals(bindGwId, deviceBean.getDeviceId())) {
                    MainApplication.getApplication().setGwSelfLogin(true);
                    switchGateway(deviceBean, preference.getGatewayPassword(deviceBean.deviceId), false);
                    break;
                }
            }
        }
    }

    private void selectCurrentGateway(List<DeviceBean> list) {
        String currentGatewayId = preference.getCurrentGatewayID();
        if (list != null) {
            for (DeviceBean bean : list) {
                if (TextUtils.equals(currentGatewayId, bean.getDeviceId())) {
                    bean.setIsSelect(true);
                } else {
                    bean.setIsSelect(false);
                }
            }
        }
    }

    private void showPasswordDialog(final DeviceBean bean) {
        WLDialog.Builder builder = new WLDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_password, null);
        final EditText editText = (EditText) v.findViewById(R.id.et_gateway_password);
        final ImageView imageView = v.findViewById(R.id.iv_delete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        editText.setTypeface(Typeface.DEFAULT);
        editText.setTransformationMethod(new PasswordTransformationMethod());
        String title = String.format(getString(R.string.GatewayChangePwd_Input), bean.deviceId);
        builder.setTitle(title)
                .setCancelOnTouchOutSide(false)
                .setContentView(v)
                .setPositiveButton(context.getResources().getString(R.string.Sure))
                .setNegativeButton(context.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        final String password = editText.getText().toString().trim();
                        String currentGatewayId = bean.deviceId;
                        verifyPassword(currentGatewayId, password, bean);
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        WLDialog dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void verifyPassword(String gwId, final String pwd, final DeviceBean bean) {
        ProgressDialogManager.getDialogManager().showDialog(VERIFY, mActivity, null, null, mActivity.getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doVerifyGwPwdAndSaveGwId(gwId, pwd, null, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object result) {
                ProgressDialogManager.getDialogManager().dimissDialog(VERIFY, 0);
                switchGateway(bean, pwd, true);
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(VERIFY, 0);
                showPasswordDialog(bean);
            }
        });
    }

    //切换网关。只有在云账号登录下才有这个功能。
    private void switchGateway(final DeviceBean bean, String password, boolean isFinish) {
        //清空当前网关信息
        MainApplication.getApplication().clearCurrentGateway();
        //保存网关密码
        preference.saveGatewayPassword(bean.deviceId, password);
        preference.saveCurrentGatewayID(bean.deviceId);
        deviceApiUnit.doSwitchGatewayId(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        selectCurrentGateway(gateWayList);
        gateWayListAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        MainApplication.getApplication().getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        EventBus.getDefault().post(new DeviceReportEvent(null));
        requestAllInfo(bean.deviceId);
        if (isFinish) {
            getActivity().finish();
        }
//        if ("1".equals(bean.getState()) && bean.passwordStatus == 1) {
        if ("1".equals(bean.getState())
                && !TextUtils.isEmpty(password)
                && (RegularTool.isNeedResetPwd(password) || TextUtils.equals(password, bean.deviceId.substring(bean.deviceId.length() - 6).toUpperCase()))) {
            Log.i("hxc", "22222: ");
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, ConfirmGatewayPasswordActivity.class);
                    intent.putExtra("oldPwd", preference.getGatewayPassword(bean.deviceId));
                    startActivityForResult(intent, CONFIRM);
                }
            });
        }
    }

    //根据http接口先存储网关的基本信息，后面mqtt接口的信息刷新为详细
    private void saveCurrentGatewayInfo(DeviceBean bean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gwID", bean.deviceId);
        jsonObject.put("gwVer", bean.version);
        jsonObject.put("gwName", bean.getName());
        jsonObject.put("gwType", bean.getType());
        jsonObject.put("hostFlag", bean.getHostFlag());
        preference.saveCurrentGatewayInfo(jsonObject.toJSONString());
    }

    private void requestAllInfo(String gatewayId) {
        getPushType(gatewayId);
        MQTTManager mqttManager = MainApplication.getApplication().getMqttManager();
        LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllDevices(gatewayId, localInfo.appID),
                mqttManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllRooms(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllScenes(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGatewayInfo(gatewayId, 0, localInfo.appID, null),
                MQTTManager.MODE_CLOUD_ONLY);
    }

    private void getPushType(String gwId) {
        deviceApiUnit.doGetIsPush(gwId, new DeviceApiUnit.DeviceApiCommonListener<DeviceIsPushBean>() {
            @Override
            public void onSuccess(DeviceIsPushBean bean) {
                if (StringUtil.equals(bean.isPush, "0")) {
                    preference.saveAlarmPush(false);
                } else if (StringUtil.equals(bean.isPush, "1")) {
                    preference.saveAlarmPush(true);
                }
            }

            @Override
            public void onFail(int code, String msg) {
            }
        });
    }

    private void showUnbindDialog(final String id) {
        builder = new WLDialog.Builder(mActivity);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(getString(R.string.gateway_deletetips))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        unbind(id);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 解除绑定
     */
    private void unbind(final String deviceId) {
        ProgressDialogManager.getDialogManager().showDialog(DELETE, mActivity, null, null, mActivity.getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doUnBindDevice(deviceId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(DELETE, 0);
                if (TextUtils.equals(preference.getCurrentGatewayID(), deviceId)) {
                    MainApplication.getApplication().clearCurrentGateway();
                }
                getAllGateway(false);
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(DELETE, 0);
                ToastUtil.show(msg);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        //网关上线处理
        if (event.bean != null && !TextUtils.isEmpty(event.bean.gwID) && gateWayList != null && gateWayListAdapter != null) {
            for (DeviceBean bean : gateWayList) {
                if (TextUtils.equals(bean.deviceId, event.bean.gwID)) {
                    //下线：15 上线：01
                    if ("15".equals(event.bean.cmd)) {
                        bean.setState("0");
                    } else if ("01".equals(event.bean.cmd)) {
                        bean.setState("1");
                    }
                    gateWayListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BIND) {
                bindGwId = data.getStringExtra("gwId");
                getAllGateway(true);
            } else if (requestCode == CONFIRM) {
//                getActivity().finish();
                bindGwId = data.getStringExtra("gwId");
                getAllGateway(true);
            }
        }
    }
}
