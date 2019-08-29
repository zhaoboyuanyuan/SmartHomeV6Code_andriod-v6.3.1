package cc.wulian.smarthomev6.main.device;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.adapter.DeviceListAdapter;
import cc.wulian.smarthomev6.main.device.lookever.CameraLiveStreamActivity;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListBeanAll;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LiveStreamInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.customview.EmptyGWBindPop;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshView;
import cc.wulian.smarthomev6.support.customview.TopStateView;
import cc.wulian.smarthomev6.support.customview.popupwindow.MoreMenuPopupWindow;
import cc.wulian.smarthomev6.support.customview.popupwindow.SearchPopuWindow;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.InputMethodUtils;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.R.id.ll_search;


/**
 * Created by Administrator on 2017/1/15
 */

public class DeviceFragment extends WLFragment implements View.OnClickListener, View.OnTouchListener {

    private static final String QUERY = "query";
    private static final String VIRTUAL_GW_ID = "000000000000";

    private static List<String> defaultCategory;
    private LinearLayout llAllPartition;
    private LinearLayout llAllCategory;
    private ListView lvDevice;
    private SwipeRefreshView mSwipe;
    private DeviceListAdapter deviceListAdapter;
    private LinearLayout llAllType;
    private MoreMenuPopupWindow areaPopupWindow;
    private MoreMenuPopupWindow categoryPWindow;
    private MoreMenuPopupWindow addPopupWindow;
    private SearchPopuWindow searchPopuWindow;
    private ImageView ivPartition;
    private ImageView ivCategory;
    private TextView emptyTextView;
    private TextView differentiateByArea;
    private TextView differentiateByCate;
    private TextView searchEmptyTextView;
    //顶部状态文字
    private TopStateView view_topstate;
    private String currentCategory;
    private String areaCode = null;
    private String categoryCode = null;
    private String currentPage = "1";
    private String forbiddenDevice;

    private DeviceApiUnit deviceApiUnit;
    private DataApiUnit dataApiUnit;
    private Handler handler = new Handler(Looper.getMainLooper());

    private List<MoreMenuPopupWindow.MenuItem> partitionList = new ArrayList<>();
    private List<Device> deviceList = new ArrayList<>();
    private long lastRefreshTime;
    private AreaManager areaManager;
    private Button btnBatchControl;

    @Override
    public int layoutResID() {
        return R.layout.fragment_device;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        defaultCategory = new ArrayList<>();
        defaultCategory.add(getString(R.string.Device_AllCategory));
        defaultCategory.add(getString(R.string.Adddevice_Smartdoor_Lock));
        defaultCategory.add(getString(R.string.Adddevice_Camera));
        defaultCategory.add(getString(R.string.Adddevice_Switchgear));
        defaultCategory.add(getString(R.string.Adddevice_Socket));
        defaultCategory.add(getString(R.string.Adddevice_Security_Equipment));
        defaultCategory.add(getString(R.string.Adddevice_Environmental_Monitoring));
        defaultCategory.add(getString(R.string.Adddevice_Curtain));
        defaultCategory.add(getString(R.string.Adddevice_Remote_Control));
        defaultCategory.add(getString(R.string.Adddevice_Relay));
        defaultCategory.add(getString(R.string.Adddevice_Controller));
        defaultCategory.add(getString(R.string.AddDevice_Music));
        defaultCategory.add(getString(R.string.Adddevice_Healthy));
        defaultCategory.add(getString(R.string.Adddevice_HA));
        currentCategory = defaultCategory.get(0);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
    }

    @Override
    public void initView(View v) {
        deviceApiUnit = new DeviceApiUnit(getActivity());
        view_topstate = (TopStateView) v.findViewById(R.id.view_topstate);
        llAllType = (LinearLayout) v.findViewById(R.id.ll_all_type);
        llAllPartition = (LinearLayout) v.findViewById(R.id.ll_all_partition);
        ivPartition = (ImageView) v.findViewById(R.id.iv_device_all_partition);
        ivCategory = (ImageView) v.findViewById(R.id.iv_device_all_category);
        llAllCategory = (LinearLayout) v.findViewById(R.id.ll_all_category);
        lvDevice = (ListView) v.findViewById(R.id.lv_device);
        mSwipe = (SwipeRefreshView) v.findViewById(R.id.fragment_device_swipe);
        emptyTextView = (TextView) v.findViewById(R.id.view_is_empty);
        searchEmptyTextView = (TextView) v.findViewById(R.id.search_is_empty);
        differentiateByArea = (TextView) v.findViewById(R.id.tv_differentiate_by_area);
        differentiateByCate = (TextView) v.findViewById(R.id.tv_differentiate_by_cate);
        btnBatchControl = v.findViewById(R.id.btn_quick_control);

        mSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        initDefaultMenuItem();
        initListener();
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(llAllType, SkinResouceKey.BITMAP_NAV_BOTTOM_BACKGROUND);
        if (searchPopuWindow != null) {
            searchPopuWindow.updateSkin();
        }
    }

    protected void initData() {
        setmEtSearchAndmRelRight(R.drawable.icon_add, getString(R.string.Device_SearchDevice));
        setLeftImageAndEvent(R.drawable.icon_partition, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(preference.getCurrentGatewayID())) {
                    showPopWindow();
                    return;
                }
                startActivity(new Intent(getActivity(), AreaActivity.class));
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.Device_SearchDevice));
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        searchPopuWindow = new SearchPopuWindow(mActivity);
        llSearch.setVisibility(View.VISIBLE);

        deviceList.clear();
        deviceList.addAll(((MainApplication) getActivity().getApplication()).getDeviceCache().getDevices());
        deviceListAdapter = new DeviceListAdapter(mActivity, deviceList);
        lvDevice.setAdapter(deviceListAdapter);
        lvDevice.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    if (!TextUtils.isEmpty(categoryCode) && isSupportBatchControl(categoryCode)) {
                        btnBatchControl.setVisibility(View.VISIBLE);
                    } else {
                        btnBatchControl.setVisibility(View.GONE);
                    }
                } else {
                    if (!TextUtils.isEmpty(categoryCode) && isSupportBatchControl(categoryCode)) {
                        btnBatchControl.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        lvDevice.setTextFilterEnabled(true);

        updateListView();
        updateGatewayStateView();
//        getDeviceInfoHttp();
        getRoomList();

        loadData();
    }

    private void showAreaMenuItem(View view) {
        partitionList.clear();
        if (areaManager == null) {
            areaManager = new AreaManager(getContext());
        }
        List<RoomInfo> roomInfos = areaManager.loadDiskRoom();
        MoreMenuPopupWindow.MenuItem allItem = new MoreMenuPopupWindow.MenuItem(mActivity) {
            @Override
            public void initSystemState(TextView titleTextView) {
                titleTextView.setVisibility(View.VISIBLE);
                if (areaCode == null) {
                    titleTextView.setTextColor(mActivity.getResources().getColor(R.color.v6_text_green_light));
                } else {
                    titleTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
                }
                titleTextView.setText(R.string.Device_All);
            }

            @Override
            public void doSomething() {
                WLog.i(TAG, "全部分区: 全部");
                areaCode = null;
                loadData();
                differentiateByArea.setText(getResources().getString(R.string.Device_AllArea));
                areaPopupWindow.dismiss();
            }
        };
        partitionList.add(allItem);

        MoreMenuPopupWindow.MenuItem defaultItem = new MoreMenuPopupWindow.MenuItem(mActivity) {
            @Override
            public void initSystemState(TextView titleTextView) {
                titleTextView.setVisibility(View.VISIBLE);
                if (TextUtils.equals(areaCode, "")) {
                    titleTextView.setTextColor(mActivity.getResources().getColor(R.color.v6_text_green_light));
                } else {
                    titleTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
                }
                titleTextView.setText(R.string.Device_NoneArea);
            }

            @Override
            public void doSomething() {
                WLog.i(TAG, "全部分区: 未分区");
                areaCode = "";
                loadData();
                differentiateByArea.setText(getResources().getString(R.string.Device_NoneArea));
                areaPopupWindow.dismiss();
            }
        };
        partitionList.add(defaultItem);

        for (final RoomInfo info : roomInfos) {
            MoreMenuPopupWindow.MenuItem menuItem = new MoreMenuPopupWindow.MenuItem(mActivity) {
                @Override
                public void initSystemState(TextView titleTextView) {
                    titleTextView.setVisibility(View.VISIBLE);
                    if (TextUtils.equals(areaCode, info.getRoomID())) {
                        titleTextView.setTextColor(mActivity.getResources().getColor(R.color.v6_text_green_light));
                    } else {
                        titleTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
                    }
                    titleTextView.setText(info.getName());
                }

                @Override
                public void doSomething() {
                    WLog.i(TAG, "分区: " + info.getName());
                    areaCode = info.getRoomID();
                    loadData();
                    differentiateByArea.setText(info.getName());
                    areaPopupWindow.dismiss();
                }
            };
            partitionList.add(menuItem);
        }
        areaPopupWindow = new MoreMenuPopupWindow(mActivity);
        areaPopupWindow.setMenuItems(partitionList);
        areaPopupWindow.showParentScroll(view);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // ivPartition.setImageResource(R.drawable.icon_boult);
                ObjectAnimator.ofFloat(ivPartition, "rotationX", 180f, 0f).setDuration(700).start();
            }
        });
    }

    private void showCategoryMenuItem(View view) {
        List<MoreMenuPopupWindow.MenuItem> categoryList = new ArrayList<>();
        for (final String cateGory : defaultCategory) {
            final MoreMenuPopupWindow.MenuItem menuItem = new MoreMenuPopupWindow.MenuItem(mActivity) {
                @Override
                public void initSystemState(TextView titleTextView) {
                    titleTextView.setVisibility(View.VISIBLE);
                    if (StringUtil.equals(currentCategory, cateGory)) {
                        titleTextView.setTextColor(mActivity.getResources().getColor(R.color.v6_text_green_light));
                    } else {
                        titleTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
                    }
                    titleTextView.setText(cateGory);
                }

                @Override
                public void doSomething() {
                    WLog.i(TAG, "全部类别: " + cateGory);
                    if (defaultCategory.indexOf(cateGory) == 0) {
                        categoryCode = null;
                    } else {
                        categoryCode = String.valueOf(defaultCategory.indexOf(cateGory));
                    }
                    loadData();
                    if (deviceList != null && deviceList.size() > 0) {
                        isSupportBatchControl(categoryCode);
                    } else {
                        btnBatchControl.setVisibility(View.GONE);
                    }
                    differentiateByCate.setText(cateGory);
                    currentCategory = cateGory;
                    categoryPWindow.dismiss();
                }
            };
            categoryList.add(menuItem);
        }

        categoryPWindow = new MoreMenuPopupWindow(mActivity);
        categoryPWindow.setMenuItems(categoryList);
        categoryPWindow.showParentScroll(view);
//        categoryPWindow.showParent(view);
        categoryPWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // ivCategory.setImageResource(R.drawable.icon_boult);
                ObjectAnimator.ofFloat(ivCategory, "rotationX", 180f, 0f).setDuration(700).start();
            }
        });
    }

    private void initDefaultMenuItem() {
        List<MoreMenuPopupWindow.MenuItem> addList = new ArrayList<>();
        addList.clear();
        for (int i = 0; i < 2; i++) {
            MoreMenuPopupWindow.MenuItem addEnuItem = null;
            if (i == 0) {
                addEnuItem = new MoreMenuPopupWindow.MenuItem(mActivity, true) {
                    @Override
                    public void initSystemState(TextView titleTextView) {

                    }

                    @Override
                    public void initSystemState(ImageView titleImageView, TextView titleTextView) {
                        titleImageView.setVisibility(View.VISIBLE);
                        titleTextView.setVisibility(View.VISIBLE);
                        titleImageView.setImageResource(R.drawable.icon_manager_area);
                        titleTextView.setText(R.string.AddDevice_AddDevice);
                    }

                    @Override
                    public void doSomething() {
                        addPopupWindow.dismiss();
                        if (TextUtils.equals(preference.getCurrentGatewayID(), VIRTUAL_GW_ID)) {
                            ToastUtil.show(getResources().getString(R.string.Experience_Gateway_06));
                        } else {
                            startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                        }
                    }
                };
            } else {
                addEnuItem = new MoreMenuPopupWindow.MenuItem(mActivity, true) {

                    @Override
                    public void initSystemState(TextView titleTextView) {

                    }

                    @Override
                    public void initSystemState(ImageView titleImageView, TextView titleTextView) {
                        titleImageView.setVisibility(View.VISIBLE);
                        titleTextView.setVisibility(View.VISIBLE);
                        titleImageView.setImageResource(R.drawable.icon_add_device);
                        titleTextView.setText(R.string.Device_ManagerArea);
                    }

                    @Override
                    public void doSomething() {
                        addPopupWindow.dismiss();
                        if (TextUtils.isEmpty(preference.getCurrentGatewayID())) {
                            showPopWindow();
                            return;
                        }
                        startActivity(new Intent(getActivity(), AreaActivity.class));
                    }
                };
            }
            addList.add(addEnuItem);
        }
        addPopupWindow = new MoreMenuPopupWindow(mActivity);
        addPopupWindow.setMenuItems(addList);
    }

    public void initListener() {
        btnBatchControl.setOnClickListener(this);
        llAllPartition.setOnClickListener(this);
        llAllCategory.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        getRightRel().setOnClickListener(this);
        getSearchContent().setOnClickListener(this);
        mLinSub.setOnTouchListener(this);
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = deviceListAdapter.getItem(position);
                forbiddenDevice = MainApplication.getApplication().forbiddenDevice;
                DeviceForbiddenBean deviceForbiddenBean = null;
                if (!TextUtils.isEmpty(forbiddenDevice)) {
                    deviceForbiddenBean = JSON.parseObject(forbiddenDevice, DeviceForbiddenBean.class);
                    if (deviceForbiddenBean != null && (deviceForbiddenBean.getStatus() == 0 && deviceForbiddenBean.getType() == 1)) {
                        if (TextUtils.equals(preference.getCurrentGatewayID(), deviceForbiddenBean.getGwID())) {
                            for (String devId : deviceForbiddenBean.getDevIDs()
                                    ) {
                                if (TextUtils.equals(devId, device.devID)) {
                                    DialogUtil.showDeviceForbiddenDialog(getActivity(), getString(R.string.device_device_Disable));
                                    return;
                                }
                            }
                        }
                    }
                }
                if (DeviceInfoDictionary.supportMe(device)) {
                    if (TextUtils.equals("OX", device.type)) {
                        showDeleteOXDevice(device);
                    } else {
                        DeviceInfoDictionary.showDetail(getActivity(), device);
                    }
                } else {
                    showDeleteNoSupportDevice(device);
                }
            }
        });
        mSwipe.setOnRefreshListener(new SwipeRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (System.currentTimeMillis() - lastRefreshTime > 10000) {
                    // 在这里请求设备列表
                    getRoomList();
                    getWifiDeviceList("1");
                    getEquesList();
                    WLog.i("icamProcess", "refresh获取设备列表");
                    getDeviceInfoMqtt();
                    lastRefreshTime = System.currentTimeMillis();
                }
                mSwipe.setRefreshing(false);
            }
        });

        mSwipe.setOnLoadListener(new SwipeRefreshView.OnLoadListener() {
            @Override
            public void onLoad() {
                Log.i(TAG, "onLoad: " + currentPage);
                getWifiDeviceList(Integer.parseInt(currentPage) + 1 + "");
                mSwipe.setLoading(false);
            }
        });
    }

    // 删除不支持的设备

    private WLDialog.Builder deleteBuilder;
    private WLDialog deleteDialog;
    // 所删除设备的  id
    private String deleteDeviceId;
    private final static String DELETE_SUPPORT_PROGRESS = "Delete_No_Support";

    /**
     * 删除不支持的设备
     */
    private void showDeleteNoSupportDevice(final Device device) {
        deleteBuilder = new WLDialog.Builder(getContext());
        deleteBuilder.setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.Device_Delete))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        deleteDeviceId = device.devID;
                        ProgressDialogManager
                                .getDialogManager()
                                .showDialog(DELETE_SUPPORT_PROGRESS, getContext(), null, new CustomProgressDialog.OnDialogDismissListener() {
                                    @Override
                                    public void onDismiss(CustomProgressDialog var1, int var2) {
                                        if (var2 != 0) {
                                            ToastUtil.single(R.string.Home_Scene_DeleteScene_Failed);
                                        }
                                    }
                                }, getResources().getInteger(R.integer.http_timeout));
                        MainApplication.getApplication().getMqttManager()
                                .publishEncryptedMessage(
                                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(), deleteDeviceId, 3, null, null),
                                        MQTTManager.MODE_GATEWAY_FIRST);
                        deleteDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        deleteDialog.dismiss();
                    }
                });
        deleteDialog = deleteBuilder.create();
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    /**
     * 删除OX
     */
    private void showDeleteOXDevice(final Device device) {
        deleteBuilder = new WLDialog.Builder(getContext());
        deleteBuilder.setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.device_OX_Device_exception))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Delete))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        deleteDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        deleteDeviceId = device.devID;
                        ProgressDialogManager
                                .getDialogManager()
                                .showDialog(DELETE_SUPPORT_PROGRESS, getContext(), null, new CustomProgressDialog.OnDialogDismissListener() {
                                    @Override
                                    public void onDismiss(CustomProgressDialog var1, int var2) {
                                        if (var2 != 0) {
                                            ToastUtil.single(R.string.Home_Scene_DeleteScene_Failed);
                                        }
                                    }
                                }, getResources().getInteger(R.integer.http_timeout));
                        MainApplication.getApplication().getMqttManager()
                                .publishEncryptedMessage(
                                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(), deleteDeviceId, 3, null, null),
                                        MQTTManager.MODE_GATEWAY_FIRST);
                        deleteDialog.dismiss();
                    }
                });
        deleteDialog = deleteBuilder.create();
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    private void showPopWindow() {
        final EmptyGWBindPop popupWindow = new EmptyGWBindPop(getContext());
        popupWindow.setOnPopClickListener(new EmptyGWBindPop.onPopClickListener() {
            @Override
            public void virtualGateway() {
                bindVirtualGateway();
            }

            @Override

            public void bindGateway() {
                getActivity().startActivity(new Intent(getContext(), GatewayListActivity.class));
            }

            @Override
            public void cancel() {

            }
        });
        popupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.backgroundAlpha((Activity) getActivity(), 1f);
            }
        });
    }

    private void bindVirtualGateway() {
        deviceApiUnit = new DeviceApiUnit(getActivity());
        final String id = VIRTUAL_GW_ID;
        final String password = "Wulian123";
        deviceApiUnit.doBindDevice(id, password, "GW99", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                deviceApiUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                    @Override
                    public void onSuccess(List<DeviceBean> deviceBeanList) {
                        for (DeviceBean deviceBean : deviceBeanList) {
                            if (deviceBean.deviceId.equalsIgnoreCase(VIRTUAL_GW_ID)) {
                                ToastUtil.show(getResources().getString(R.string.Experience_Gateway_05));
                                switchGateway(deviceBean, password);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.show(msg);
                    }
                });

            }

            @Override
            public void onFail(int code, String msg) {
                if (code == 20128) {
                    ToastUtil.show(R.string.Experience_Gateway_04);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    //切换网关。只有在云账号登录下才有这个功能。
    private void switchGateway(DeviceBean bean, String password) {
        //清空当前网关信息
        MainApplication.getApplication().clearCurrentGateway();
        //保存网关密码
        preference.saveGatewayPassword(bean.deviceId, password);
        preference.saveCurrentGatewayID(bean.deviceId);
        deviceApiUnit.doSwitchGatewayId(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        MainApplication.getApplication().getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        EventBus.getDefault().post(new DeviceReportEvent(null));
        requestAllInfo(bean.deviceId);
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

    private void updateListView() {
        if (deviceList.isEmpty()) {
            lvDevice.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            searchEmptyTextView.setVisibility(View.GONE);
        } else {
            lvDevice.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            searchEmptyTextView.setVisibility(View.GONE);
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_all_partition:
                // ivPartition.setImageResource(R.drawable.icon_boult1);
                // 动画
                ObjectAnimator.ofFloat(ivPartition, "rotationX", 0f, 180f).setDuration(700).start();
                showAreaMenuItem(llAllType);
                break;
            case R.id.ll_all_category:
                // ivCategory.setImageResource(R.drawable.icon_boult1);
                // 动画
                ObjectAnimator.ofFloat(ivCategory, "rotationX", 0f, 180f).setDuration(700).start();
                showCategoryMenuItem(llAllType);
                break;
            case R.id.rel_right:
//                addPopupWindow.show(getRightRel());
////                setInputMethed(view);
                if (TextUtils.equals(preference.getCurrentGatewayID(), VIRTUAL_GW_ID)) {
                    ToastUtil.show(getResources().getString(R.string.Experience_Gateway_06));
                } else {
                    startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                }
                break;
            case ll_search:
                List<Device> list = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
                searchPopuWindow.setDeviceListData(list);
                searchPopuWindow.show(view);
                break;
            case R.id.btn_quick_control:
//                ToastUtil.show(categoryCode);
                sendBatchActionCmd();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            switch (view.getId()) {
                case R.id.hl_base_fragment_lin_sub:
                    setInputMethed(view);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    //插座开关照明安防类设备批量操作
    private void sendBatchActionCmd() {
        JSONObject object = new JSONObject();
        object.put("cmd", "526");
        object.put("gwID", preference.getCurrentGatewayID());
        object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
        object.put("operType", "0");

        JSONArray devArray = new JSONArray();
        for (Device device :
                deviceList) {
            if (DeviceInfoDictionary.isSupportBatch(device.type)) {
                JSONObject devObj = new JSONObject();
                devObj.put("devID", device.devID);
                devArray.add(devObj);
            }

        }
        object.put("devIdArray", devArray);

        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        ToastUtil.show(R.string.device_device_sent);
    }


    private void setInputMethed(View view) {
        mLinSub.setFocusable(true);
        mLinSub.setFocusableInTouchMode(true);
        mLinSub.requestFocus();
        InputMethodUtils.hide(getActivity(), view); //强制隐藏键盘
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomReport(GetRoomListEvent event) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomReport(RoomInfoEvent event) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event != null && event.device != null) {
            if (TextUtils.equals(deleteDeviceId, event.device.devID)
                    && event.device.mode == 3) {
                deleteDeviceId = null;
                ProgressDialogManager.getDialogManager().dimissDialog(DELETE_SUPPORT_PROGRESS, 0);
            }
        }
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        if (event.bean != null) {
            if ("15".equals(event.bean.cmd)) {
//                getDeviceInfoHttp();
            }
        } else {
            resetArea();
            resetCategory();
            btnBatchControl.setVisibility(View.GONE);
        }
        updateGatewayStateView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChangedEvent(DeviceInfoChangedEvent event) {
        if (event != null) {
            if (TextUtils.equals(deleteDeviceId, event.deviceInfoBean.devID)
                    && event.deviceInfoBean.mode == 3) {
                deleteDeviceId = null;
                ProgressDialogManager.getDialogManager().dimissDialog(DELETE_SUPPORT_PROGRESS, 0);
            }
        }
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && deviceListAdapter != null) {
            loadData();
            if (deviceList.isEmpty()) {
                getWifiDeviceList("1");
                WLog.i("icamProcess", "onHiddenChanged获取设备列表");
            }
        }
    }

    private void resetArea() {
        areaCode = null;
        differentiateByArea.setText(getResources().getString(R.string.Device_AllArea));
    }

    private void resetCategory() {
        categoryCode = null;
        differentiateByCate.setText(defaultCategory.get(0));
        currentCategory = defaultCategory.get(0);
        differentiateByArea.setText(getResources().getString(R.string.Device_AllArea));
    }


    /**
     * 获取分区列表
     */
    private void getRoomList() {
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createGetAllRooms(
                                Preference.getPreferences().getCurrentGatewayID()),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void getDeviceInfoMqtt() {
        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
        if (!TextUtils.isEmpty(currentGatewayId)) {
            ((MainApplication) getActivity().getApplication())
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createGetAllDevices(currentGatewayId, MQTTManager.appID),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    //获取移康猫眼列表
    private void getEquesList() {
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesLogin(getActivity(), ApiConstant.EQUES_URL, ApiConstant.getUserID(), ApiConstant.EQUES_APPKEY);
    }

    private void getWifiDeviceList(String pageNum) {
        if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_GW)) {
            return;
        }
        //获取wifi设备
        deviceApiUnit.getWifiDeviceList(pageNum, "10", new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
            @Override
            public void onSuccess(DeviceListBeanAll bean) {
                if (bean == null) {
                    return;
                }
                currentPage = bean.currentPage;
                List<DeviceBean> deviceBeanList = new ArrayList<>();
                for (DeviceBean deviceBean : bean.devices) {
                    //排除移康猫眼，移康猫眼设备列表使用sdk获取
                    if (TextUtils.isEmpty(deviceBean.getType())
                            || !(deviceBean.getType().startsWith("GW") ||
                            TextUtils.equals(deviceBean.getType(), "CMICY1"))) {
                        deviceBeanList.add(deviceBean);
                    }
                }
                if (deviceBeanList.size() > 0) {
                    final DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
//                    deviceCache.clearWifiDevices();
                    for (DeviceBean device : deviceBeanList) {
                        Device wifiDevice = new Device(device);
                        deviceCache.clearWifiDevice(device.deviceId);
                        deviceCache.add(wifiDevice);
                        HomeWidgetManager.saveNewWifiWidget(wifiDevice);
                    }
                    EventBus.getDefault().post(new DeviceReportEvent(null));
                }
            }


            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //根据当前网关状态改变顶部view
    private void updateGatewayStateView() {
//        if (preference.isLogin()) {
//            final String currentGatewayId = preference.getCurrentGatewayID();
//            final String currentGatewayState = preference.getCurrentGatewayState();
//            if (TextUtils.isEmpty(currentGatewayId)) {
//                view_topstate.setVisibility(View.VISIBLE);
//                view_topstate.setContent(getString(R.string.Gateway_Unbind));
//            } else if (!"1".equals(currentGatewayState)) {
//                view_topstate.setVisibility(View.VISIBLE);
//                view_topstate.setContent(getString(R.string.Gateway_Offline));
//            } else {
//                view_topstate.setVisibility(View.GONE);
//            }
//        } else {
        view_topstate.setVisibility(View.GONE);
//        }
    }

    private void loadData() {
        deviceList.clear();
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
        List<Device> currentlist = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
        if (areaCode == null) {
            if (categoryCode == null) {
                deviceList = currentlist;
            } else {
                for (Device device : currentlist) {
                    if (StringUtil.equals(String.valueOf(DeviceInfoDictionary.getCategoryByType(device.type)), categoryCode)) {
                        deviceList.add(device);
                    }
                }
            }
        } else {
            if (categoryCode == null) {
                for (Device device : currentlist) {
                    if (TextUtils.equals(areaCode, "")) {
                        if (TextUtils.isEmpty(device.roomID)) {
                            deviceList.add(device);
                        }
                    } else {
                        if (StringUtil.equals(device.roomID, areaCode)) {
                            deviceList.add(device);
                        }
                    }
                }
            } else {
                for (Device device : currentlist) {
                    if (TextUtils.equals(areaCode, "")) {
                        if (StringUtil.equals(String.valueOf(DeviceInfoDictionary.getCategoryByType(device.type)), categoryCode) &&
                                TextUtils.isEmpty(device.roomID)) {
                            deviceList.add(device);
                        }
                    } else {
                        if (StringUtil.equals(String.valueOf(DeviceInfoDictionary.getCategoryByType(device.type)), categoryCode) &&
                                StringUtil.equals(device.roomID, areaCode)) {
                            deviceList.add(device);
                        }
                    }

                }
            }
        }
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
        deviceListAdapter.sort(deviceList);
        deviceListAdapter.swapData(deviceList);
        searchPopuWindow.refereshDeviceListData(deviceList);
        updateListView();
//            }
//        }.execute();
    }


    private boolean isSupportBatchControl(String categoryCode) {
        boolean flag = false;
        if (!TextUtils.isEmpty(categoryCode)) {
            switch (categoryCode) {
                case "3"://开关照明
                case "4"://插座
                    flag = true;
                    btnBatchControl.setVisibility(View.VISIBLE);
                    btnBatchControl.setText(getString(R.string.recommendScene_12));
                    break;
                case "5"://安防设备
                    flag = true;
                    btnBatchControl.setVisibility(View.VISIBLE);
                    btnBatchControl.setText(getString(R.string.recommendScene_14));
                    break;
                default:
                    flag = false;
                    btnBatchControl.setVisibility(View.GONE);
                    break;
            }
        } else {
            btnBatchControl.setVisibility(View.GONE);
        }

        return flag;
    }

    /**
     * 获取爱看直播流信息
     *
     * @param device
     */
    private void getLiveStreamInfo(final Device device) {
        dataApiUnit = new DataApiUnit(getActivity());
        dataApiUnit.doGetLiveStreamData(device.devID, new DataApiUnit.DataApiCommonListener<LiveStreamInfoBean>() {
            @Override
            public void onSuccess(LiveStreamInfoBean bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(QUERY, 0);
                if (bean.Open == 1) {
                    CameraLiveStreamActivity.start(getActivity(), device);
                } else {
                    DeviceInfoDictionary.showDetail(getActivity(), device);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(QUERY, 0);
                DeviceInfoDictionary.showDetail(getActivity(), device);
            }
        });
    }

}
