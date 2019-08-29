package cc.wulian.smarthomev6.main.device.device_if02.it_box;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.tv.TvViewHelper;
import cc.wulian.smarthomev6.main.device.device_if02.ControllerMoreActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBlocksBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.CustomKeyBean;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.customview.RemoteControlView;
import cc.wulian.smarthomev6.support.customview.popupwindow.DeleteUeiCodePopupWindow;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditUeiCustomKeyPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.WifiIRSceneEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.support.customview.RemoteControlView.AREA_BOTTOM;
import static cc.wulian.smarthomev6.support.customview.RemoteControlView.AREA_CENTER;
import static cc.wulian.smarthomev6.support.customview.RemoteControlView.AREA_LEFT;
import static cc.wulian.smarthomev6.support.customview.RemoteControlView.AREA_RIGHT;
import static cc.wulian.smarthomev6.support.customview.RemoteControlView.AREA_TOP;


/**
 * 互联网盒子遥控主界面（控制和学习）
 */
public class ITBoxMainActivity extends BaseTitleActivity {

    private FrameLayout contentLayout;
    private RemoteControlView tvCenterControlView;
    private CheckBox btnKeyboard, btnCustomboard;
    private View layoutMainView, layoutKeyboard, layoutCustomView;
    private RecyclerView customContent;
    private GridLayoutManager gridLayoutManager;
    private CustomViewAdapter customViewAdapter;
    private WLDialog.Builder builder;
    private WLDialog matchingDialog;
    private WLDialog matchingFailDialog;
    private WLDialog createCustomKeyDialog;
    private WLDialog renameCustomKeyDialog;
    private DeleteUeiCodePopupWindow deleteCodePopup;
    private EditUeiCustomKeyPopupWindow editCodePopup;


    private String deviceId;
    private String mode;
    private String blockType;
    private String blockName;
    private String blockId;
    private String codeLib; //码库id 为空表示学习的码库
    private boolean isSceneOrHouseKeeper;
    private boolean isWidget;
    private Device device;
    // 码库文件列表
    private LinkedHashMap<String, String> learnMap = new LinkedHashMap<>();
    private List<TvViewHelper.TvItem> list;
    private List<ControllerBlocksBean.blocksBean> blockList;
    private List<ControllerBlocksBean.keyBean> keys;
    private String currentTag, currentValue;
    private TvViewHelper tvViewHelper;
    private DataApiUnit dataApiUnit;
    private Handler handler;
    private Runnable timeoutRunnable;
    private long clickTime;

    public static void startForScene(Activity context, String deviceID, String blockType, String blockName, String blockId, String codeLib) {
        Intent intent = new Intent(context, ITBoxMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", WifiIRManage.MODE_CONTROL);
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockId", blockId);
        intent.putExtra("codeLib", codeLib);
        intent.putExtra("isSceneOrHouseKeeper", true);
        context.startActivityForResult(intent, 1);
    }

    public static void start(Context context, String deviceID, String blockType, String blockName, String blockId, String codeLib, boolean isWidget) {
        Intent intent = new Intent(context, ITBoxMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", WifiIRManage.MODE_CONTROL);
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockId", blockId);
        intent.putExtra("codeLib", codeLib);
        intent.putExtra("isSceneOrHouseKeeper", false);
        intent.putExtra("isWidget", isWidget);
        context.startActivity(intent);
    }

    public static void learn(Context context, String deviceID, String blockType, String blockName, String blockId) {
        Intent intent = new Intent(context, ITBoxMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", WifiIRManage.MODE_LEARN);
        intent.putExtra("blockType", blockType);
        intent.putExtra("blockName", blockName);
        intent.putExtra("blockId", blockId);
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_if01_it_box_main, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void initView() {
        contentLayout = (FrameLayout) findViewById(R.id.fl_content);
        layoutKeyboard = findViewById(R.id.layout_keyboard);
        layoutMainView = findViewById(R.id.layout_main_view);
        layoutCustomView = findViewById(R.id.layout_custom);
        btnKeyboard = (CheckBox) findViewById(R.id.btn_keyboard);
        btnCustomboard = (CheckBox) findViewById(R.id.btn_customboard);
        tvCenterControlView = (RemoteControlView) findViewById(R.id.center_control);
        customContent = (RecyclerView) findViewById(R.id.custom_content);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        customContent.setLayoutManager(gridLayoutManager);
        customContent.setItemAnimator(new DefaultItemAnimator());
        customViewAdapter = new CustomViewAdapter(this, null);
        customContent.setAdapter(customViewAdapter);
        deleteCodePopup = new DeleteUeiCodePopupWindow(this);
        editCodePopup = new EditUeiCustomKeyPopupWindow(this);

        tvViewHelper = new TvViewHelper();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceID");
        blockId = intent.getStringExtra("blockId");
        blockName = intent.getStringExtra("blockName");
        blockType = intent.getStringExtra("blockType");
        codeLib = intent.getStringExtra("codeLib");
        mode = intent.getStringExtra("mode");
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);
        isWidget = getIntent().getBooleanExtra("isWidget", false);
        dataApiUnit = new DataApiUnit(this);
        handler = new Handler();
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setTitle();
        getSupportKeys();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (matchingDialog != null) {
                    matchingDialog.dismiss();
                    matchingDialog = null;
                    WLog.i(TAG, "15s后超时,关闭学习状态");
                    setLearningStatus(WifiIRManage.STOP_LEARN);
                    showMatchingFailDialog();
                }
            }
        };

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        registerKeyEvent(contentLayout);
        btnKeyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnCustomboard.setChecked(false);
                    layoutMainView.setVisibility(View.GONE);
                    layoutKeyboard.setVisibility(View.VISIBLE);
                    layoutCustomView.setVisibility(View.GONE);
                    btnKeyboard.setBackgroundResource(R.drawable.selector_tv_remote_cancle);
                } else {
                    layoutMainView.setVisibility(View.VISIBLE);
                    layoutKeyboard.setVisibility(View.GONE);
                    layoutCustomView.setVisibility(View.GONE);
                    btnKeyboard.setBackgroundResource(R.drawable.selector_tv_remote_num);
                }
            }
        });
        btnCustomboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnKeyboard.setChecked(false);
                    layoutMainView.setVisibility(View.GONE);
                    layoutKeyboard.setVisibility(View.GONE);
                    layoutCustomView.setVisibility(View.VISIBLE);
                    btnCustomboard.setBackgroundResource(R.drawable.selector_tv_remote_cancle);
                } else {
                    layoutMainView.setVisibility(View.VISIBLE);
                    layoutKeyboard.setVisibility(View.GONE);
                    layoutCustomView.setVisibility(View.GONE);
                    btnCustomboard.setBackgroundResource(R.drawable.selector_tv_remote_custom);
                }
            }
        });
        tvCenterControlView.setOnClickListener(new RemoteControlView.OnClickListener() {
            @Override
            public void onClick(RemoteControlView.AreaItem item) {
                if (TextUtils.equals(mode, WifiIRManage.MODE_CONTROL)) {
                    if (item.isEnable()) {
                        sendControlCmd(item.getTag());
                    } else {
                        ToastUtil.single(R.string.Infraredtransponder_No_Infraredcode);
                    }

                } else if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
                    sendLearnCmd(item.getTag());
                    WLog.i(TAG, "sendLearnCmd: ");
                }
            }
        });
        tvCenterControlView.setOnLongClickListener(new RemoteControlView.OnLongClickListener() {
            @Override
            public void onLongClick(String tag) {
                if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
                    deleteCodePopup.showParent(tvCenterControlView, tag);
                }
            }
        });
        deleteCodePopup.setPopupClickListener(new DeleteUeiCodePopupWindow.OnPopupClickListener() {
            @Override
            public void onDelete(String tag) {
                currentTag = tag;
                deleteLearnCode(tag, false);
            }
        });
        editCodePopup.setPopupClickListener(new EditUeiCustomKeyPopupWindow.OnPopupClickListener() {
            @Override
            public void onDelete(String tag) {
                currentTag = tag;
                deleteLearnCode(tag, true);
            }

            @Override
            public void onRename(String tag) {
                currentTag = tag;
                showRenameCustomKeyDialog(tag);
            }
        });
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (TextUtils.equals(mode, WifiIRManage.MODE_CONTROL)) {
            if (isSceneOrHouseKeeper) {
                if (TextUtils.isEmpty(blockName)) {
                    if (TextUtils.equals(blockType, WifiIRManage.TYPE_TV)) {
                        setToolBarTitle(mainApplication.getResources().getString(R.string.Infraredrelay_Addremote_Television));
                    }
                } else {
                    setToolBarTitle(blockName);
                }
            } else {
                if (TextUtils.isEmpty(blockName)) {
                    if (TextUtils.equals(blockType, WifiIRManage.TYPE_TV)) {
                        setToolBarTitleAndRightImg(mainApplication.getResources().getString(R.string.Infraredrelay_Addremote_Television), R.drawable.icon_more);
                    }
                } else {
                    setToolBarTitleAndRightImg(blockName, R.drawable.icon_more);
                }
            }
        } else if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
            if (TextUtils.isEmpty(blockName)) {
                if (TextUtils.equals(blockType, WifiIRManage.TYPE_TV)) {
                    setToolBarTitle(mainApplication.getResources().getString(R.string.Infraredrelay_Addremote_Television));
                }
            } else {
                setToolBarTitle(blockName);
            }
        }
    }


    private void getSupportKeys() {
        dataApiUnit.doGetWifiIRCodeList(deviceId, blockId, new DataApiUnit.DataApiCommonListener<ControllerBlocksBean>() {
            @Override
            public void onSuccess(ControllerBlocksBean bean) {
                blockList = bean.blocks;
                for (ControllerBlocksBean.blocksBean blocksBean : blockList
                        ) {
                    if (blocksBean.blockId.equals(blockId)) {
                        keys = blocksBean.keys;
                        loadAllKeys();
                        loadAllViews();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //加载所有支持按键
    public void loadAllKeys() {
        if (keys != null && keys.size() != 0) {
            tvViewHelper.convertKeyToItem(keys);
            if (isCustomRemote()) {
                for (ControllerBlocksBean.keyBean bean :
                        keys) {
                    learnMap.put(bean.keyId, bean.code);
                }
            }
        }
    }

    //加载遥控器布局
    public void loadAllViews() {
        //加载固定键盘
        if (TextUtils.equals(mode, WifiIRManage.MODE_CONTROL)) {
            tvCenterControlView.setMode(RemoteControlView.MODE_CONTROL);
            updateControlView(contentLayout);
        } else if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
            tvCenterControlView.setMode(RemoteControlView.MODE_LEARN);
            updateLearnView(contentLayout);
        }
        //加载中心圆盘
        upDateCenterKeys();
        //加载多余按键
        loadCustomKeys();
    }

    //加载按键视图---控制模式
    private void updateControlView(View view) {
        if (view != null) {
            if (view.getTag() != null) {
                if (tvViewHelper.getKeyByTag(view.getTag().toString()) != null) {
                    switch (view.getTag().toString()) {
                        case "power":
                        case "boot":
                        case "menu":
                        case "signal":
                        case "mute":
                        case "back":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_" + view.getTag()));
                            break;
                        case "vol+":
                        case "ch+":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("selector_tv_remote_c_up"));
                            break;
                        case "vol-":
                        case "ch-":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("selector_tv_remote_c_down"));
                            break;
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_common"));
                            break;

                    }
                } else {
                    switch (view.getTag().toString()) {
                        case "power":
                        case "boot":
                        case "menu":
                        case "signal":
                        case "mute":
                        case "back":
                            view.setBackgroundResource(getResource("tv_remote_unenable_" + view.getTag()));
                            break;
                        case "vol+":
                        case "ch+":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("tv_remote_unenable_up"));
                            break;
                        case "vol-":
                        case "ch-":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("tv_remote_unenable_down"));
                            break;
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                            view.setBackgroundResource(getResource("tv_remote_unenable_common"));
                            break;

                    }
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            if (vg.getChildCount() > 0) {
                for (int i = 0; i < vg.getChildCount(); i++) {
                    updateControlView(vg.getChildAt(i));
                }
            }
        }
    }

    //加载按键视图---学习模式
    private void updateLearnView(View view) {
        if (view != null) {
            if (view.getTag() != null) {
                if (tvViewHelper.getKeyByTag(view.getTag().toString()) != null) {
                    switch (view.getTag().toString()) {
                        case "power":
                        case "boot":
                        case "menu":
                        case "signal":
                        case "mute":
                        case "back":
                            view.setBackgroundResource(getResource("selector_tv_remote_l_" + view.getTag()));
                            break;
                        case "vol+":
                        case "ch+":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("selector_tv_remote_l_up"));
                            break;
                        case "vol-":
                        case "ch-":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("selector_tv_remote_l_down"));
                            break;
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                            view.setBackgroundResource(getResource("selector_tv_remote_l_common"));
                            break;
                    }
                } else {
                    switch (view.getTag().toString()) {
                        case "power":
                        case "boot":
                        case "menu":
                        case "signal":
                        case "mute":
                        case "back":
                            view.setBackgroundResource(getResource("tv_remote_learn_" + view.getTag()));
                            break;
                        case "vol+":
                        case "ch+":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("tv_remote_learn_up"));
                            break;
                        case "vol-":
                        case "ch-":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("tv_remote_learn_down"));
                            break;
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                            view.setBackgroundResource(getResource("tv_remote_learn_common"));
                            break;
                    }
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            if (vg.getChildCount() > 0) {
                for (int i = 0; i < vg.getChildCount(); i++) {
                    updateLearnView(vg.getChildAt(i));
                }
            }
        }
    }

    //加载圆盘自定义view（上下左右确定键）
    private void upDateCenterKeys() {
        tvCenterControlView.updateViews(tvViewHelper.getKeyByTag(AREA_LEFT) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_TOP) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_RIGHT) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_BOTTOM) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_CENTER) == null ? false : true);
    }

    //加载自定义按键视图
    private void loadCustomKeys() {
        list = tvViewHelper.getIF02CustomKeys();
        customViewAdapter.update(list);
    }

    //新建按键
    private void showCreateCustomKeyDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(R.string.Infraredrelay_Custom_Buttonname)
                .setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setEditTextHint(R.string.Infraredrelay_Custom_Enterbuttonname)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (WifiIRManage.containsKeyName(msg,list)) {
                            ToastUtil.single(R.string.Cylincam_Name_Repeated);
                        } else {
                            createCustomKey(msg);
                        }
                        createCustomKeyDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        createCustomKeyDialog.dismiss();
                    }
                });
        createCustomKeyDialog = builder.create();
        if (!createCustomKeyDialog.isShowing()) {
            createCustomKeyDialog.show();
        }
    }

    //重命名按键
    private void showRenameCustomKeyDialog(final String tag) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(R.string.Infraredrelay_Custom_Buttonname)
                .setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setEditTextHint(R.string.Infraredrelay_Custom_Enterbuttonname)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (WifiIRManage.containsKeyName(msg,list)) {
                            ToastUtil.single(R.string.Cylincam_Name_Repeated);
                        } else {
                            renameCustomKey(tag, msg);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        renameCustomKeyDialog.dismiss();
                    }
                });
        renameCustomKeyDialog = builder.create();
        if (!renameCustomKeyDialog.isShowing()) {
            renameCustomKeyDialog.show();
        }
    }

    //更新单个按键视图
    private void upDateOneKey(View view, String tag, boolean enable) {
        if (view != null) {
            if (view.getTag() != null && TextUtils.equals((String) view.getTag(), tag)) {
                if (enable) {
                    switch (view.getTag().toString()) {
                        case "power":
                        case "boot":
                        case "menu":
                        case "signal":
                        case "mute":
                        case "back":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_" + view.getTag()));
                            break;
                        case "vol+":
                        case "ch+":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("selector_tv_remote_c_up"));
                            break;
                        case "vol-":
                        case "ch-":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("selector_tv_remote_c_down"));
                            break;
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_common"));
                            break;

                    }
                } else {
                    switch (view.getTag().toString()) {
                        case "power":
                        case "boot":
                        case "menu":
                        case "signal":
                        case "mute":
                        case "back":
                            view.setBackgroundResource(getResource("tv_remote_learn_" + view.getTag()));
                            break;
                        case "vol+":
                        case "ch+":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("tv_remote_learn_up"));
                            break;
                        case "vol-":
                        case "ch-":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("tv_remote_learn_down"));
                            break;
                        case "0":
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                        case "8":
                        case "9":
                            view.setBackgroundResource(getResource("tv_remote_learn_common"));
                            break;
                    }
                }
                return;
            }
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                if (vg.getChildCount() > 0) {
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        upDateOneKey(vg.getChildAt(i), tag, enable);
                    }
                }
            }
        }
    }

    //更新自定义按键视图
    private void upDateCustomKey(String tag, String value) {
        if (list != null && list.size() != 0) {
            for (TvViewHelper.TvItem item : list) {
                if (TextUtils.equals(item.getTag(), tag)) {
                    item.setValue(value);
                    break;
                }
            }
            customViewAdapter.update(list);
        }
    }

    public int getResource(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    private boolean isCustomRemote() {
        if (TextUtils.isEmpty(codeLib)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                if (isCustomRemote() && !isWidget) {
                    WifiIRActivity.start(this, deviceId, false);
                    finish();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.img_right:
                ControllerMoreActivity.start(this, deviceId, blockName, blockType, blockId, codeLib, isWidget);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    //递归添加事件
    private void registerKeyEvent(View view) {
        if (view != null) {
            if (view.getTag() != null) {
                if (TextUtils.equals(mode, WifiIRManage.MODE_CONTROL)) {
                    view.setOnClickListener(controlClickListener);
                } else if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
                    view.setOnClickListener(learnClickListener);
                    view.setOnLongClickListener(deleteClickListener);
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                if (vg.getChildCount() > 0) {
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        registerKeyEvent(vg.getChildAt(i));
                    }
                }
            }
        }
    }

    private View.OnClickListener controlClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                if (tvViewHelper.getKeyByTag(v.getTag().toString()) != null) {
                    sendControlCmd(v.getTag().toString());
                } else {
                    ToastUtil.single(R.string.Infraredtransponder_No_Infraredcode);
                }
            }
        }
    };

    private View.OnClickListener learnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                sendLearnCmd(v.getTag().toString());
                WLog.i(TAG, "sendLearnCmd: ");
            }
        }
    };

    private View.OnLongClickListener deleteClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v.getTag() != null) {
                if (learnMap.containsKey((String) v.getTag())) {
                    deleteCodePopup.showParent(v, v.getTag().toString());
                }
            }
            return true;
        }
    };


    //发送控制命令
    public void sendControlCmd(String tag) {
        String codeType = null;
        if (TextUtils.isEmpty(codeLib)) {
            codeType = "1";//学习的码库
        } else {
            codeType = "0";//下载的码库
        }
        VibratorUtil.holdSpeakVibration();
        if (isSceneOrHouseKeeper) {
            EventBus.getDefault().post(new WifiIRSceneEvent("controlDevice", deviceId, codeType, blockId, tag, blockName));
            setResult(RESULT_OK);
            finish();
        } else {
            //点击间隔0.5s防止数据错误
            long currentTime = (new Date()).getTime();
            if (currentTime - clickTime > 500) {
                WLog.i(TAG, "time interval: " + (currentTime - clickTime));
                clickTime = currentTime;
                dataApiUnit.doControlWifiIR(deviceId, codeType, blockId, tag, null, new DataApiUnit.DataApiCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }

        }

    }

    //发送学习命令
    public void sendLearnCmd(String tag) {
        setLearningStatus(WifiIRManage.START_LEARN);
        showMatchingDialog();
        handler.postDelayed(timeoutRunnable, 15 * 1000);
        currentTag = tag;
    }

    //开启、结束学习
    private void setLearningStatus(final String status) {
        dataApiUnit.setWifiIRLearningStatus(deviceId, status, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                if (WifiIRManage.START_LEARN.equals(status)) {
                    WLog.i(TAG, "开启学习");
                } else {
                    WLog.i(TAG, "结束学习");
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //修改按键码
    private void updateLearnCode(String keyId, final String code) {
        dataApiUnit.doChangeWifiIRCode(deviceId, keyId, code, blockId, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "updateLearnCode");
                learnMap.put(currentTag, currentValue);
                upDateOneKey(contentLayout, currentTag, true);
                tvCenterControlView.updateView(currentTag, true);
                upDateCustomKey(currentTag, currentValue);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //新增按键码
    private void createCustomKey(String keyName) {
        dataApiUnit.doAddWifiIRCode(deviceId, blockId, keyName, new DataApiUnit.DataApiCommonListener<CustomKeyBean>() {
            @Override
            public void onSuccess(CustomKeyBean bean) {
                WLog.i(TAG, "createCustomKey");
                list.add(new TvViewHelper.TvItem(bean.keyId, bean.keyName, null));
                learnMap.put(bean.keyId, "");
                customViewAdapter.update(list);
                customViewAdapter.addData();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //修改按键名称
    private void renameCustomKey(final String keyId, final String keyName) {
        dataApiUnit.doUpdateWifiIRCodeName(deviceId, keyId, keyName, blockId, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "renameCustomKey");
                if (list != null && list.size() != 0) {
                    for (TvViewHelper.TvItem item : list) {
                        if (TextUtils.equals(item.getTag(), keyId)) {
                            item.setKeyName(keyName);
                            learnMap.put(keyId, keyName);
                        }
                    }
                }
                customViewAdapter.update(list);
                renameCustomKeyDialog.dismiss();
            }

            @Override
            public void onFail(int code, String msg) {
                renameCustomKeyDialog.dismiss();
            }
        });
    }

    //删除按键码
    private void deleteLearnCode(final String keyId, final boolean isCustom) {
        dataApiUnit.doDeleteWifiIRCode(deviceId, blockId, keyId, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "deleteLearnCode");
                if (isCustom) {
                    if (list != null && list.size() != 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (TextUtils.equals(list.get(i).getTag(), keyId)) {
                                list.remove(i);
                                customViewAdapter.removeData(i);
                                learnMap.remove(keyId);
                                break;
                            }
                        }
                        customViewAdapter.update(list);
                    }
                } else {
                    learnMap.remove(currentTag);
                    upDateOneKey(contentLayout, currentTag, false);
                    tvCenterControlView.updateView(currentTag, false);
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void showMatchingDialog() {
        if (matchingDialog == null) {
            matchingDialog = DialogUtil.showIF02LearningDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    matchingDialog.dismiss();
                    matchingDialog = null;
                    setLearningStatus(WifiIRManage.STOP_LEARN);
                    handler.removeCallbacks(timeoutRunnable);
                }
            });
        }
        if (!matchingDialog.isShowing()) {
            matchingDialog.show();
        }
    }

    private void showMatchingFailDialog() {
        if (matchingFailDialog == null) {
            builder = new WLDialog.Builder(this);
            builder.setTitle(R.string.Infraredrelay_Custom_Matchfailed)
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(false)
                    .setMessage(R.string.Infraredrelay_Custom_Matchfailed_Prompt)
                    .setPositiveButton(this.getResources().getString(R.string.Sure))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            matchingFailDialog.dismiss();
                        }

                        @Override
                        public void onClickNegative(View view) {
                        }
                    });
            matchingFailDialog = builder.create();
        }
        if (!matchingFailDialog.isShowing()) {
            matchingFailDialog.show();
        }
    }

    private class CustomViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_FOOTER = -1;
        private Context context;
        private List<TvViewHelper.TvItem> list;

        public CustomViewAdapter(Context context, List<TvViewHelper.TvItem> list) {
            this.list = list;
            this.context = context;
        }

        public void update(List<TvViewHelper.TvItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addData() {
            notifyItemInserted(list.size());
        }

        public void removeData(int position) {
            notifyItemRemoved(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            if (viewType == TYPE_FOOTER) {
                AddView viewholder = new AddView(layoutInflater.inflate(R.layout.item_tv_remote_custom_add, parent, false));
                return viewholder;
            }

            ItemView viewholder = new ItemView(layoutInflater.inflate(R.layout.item_tv_remote_custom, parent, false));
            return viewholder;
        }

        @Override
        public int getItemViewType(int position) {
            if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
                if (position == getItemCount() - 1) {
                    return TYPE_FOOTER;
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (getItemViewType(position) == TYPE_FOOTER) {
                ((AddView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCreateCustomKeyDialog();
                    }
                });
            } else {
                if (isCustomRemote()) {
                    ((ItemView) holder).textView.setText(list.get(position).getKeyName());
                } else {
                    ((ItemView) holder).textView.setText(list.get(position).getTag());
                }
                ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                //学习模式
                if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
                    if (TextUtils.isEmpty(list.get(position).getValue())) {
                        ((ItemView) holder).imageView.setImageResource(R.drawable.tv_remote_custom_bg_learn);
                        ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                    } else {
                        ((ItemView) holder).imageView.setImageResource(R.drawable.selector_tv_remote_custom_bg);
                        ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                    }
                    ((ItemView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendLearnCmd(list.get(position).getTag());
                            WLog.i(TAG, "sendLearnCmd: ");
                        }
                    });
                    ((ItemView) holder).imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            editCodePopup.showParent(v, list.get(position).getTag());
                            return true;
                        }
                    });
                }
                //控制模式
                else if (TextUtils.equals(mode, WifiIRManage.MODE_CONTROL)) {
                    if (isCustomRemote()) {
                        if (TextUtils.isEmpty(list.get(position).getValue())) {
                            ((ItemView) holder).imageView.setImageResource(R.drawable.tv_remote_custom_bg_unenable);
                            ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ((ItemView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtil.single(R.string.Infraredtransponder_No_Infraredcode);
                                }
                            });
                        } else {
                            ((ItemView) holder).imageView.setImageResource(R.drawable.selector_tv_remote_custom_bg);
                            ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ((ItemView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendControlCmd(list.get(position).getTag());
                                }
                            });
                        }
                    } else {
                        ((ItemView) holder).imageView.setImageResource(R.drawable.selector_tv_remote_custom_bg);
                        ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                        ((ItemView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendControlCmd(list.get(position).getTag());
                            }
                        });
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            if (TextUtils.equals(mode, WifiIRManage.MODE_CONTROL)) {
                return list.size();
            } else if (TextUtils.equals(mode, WifiIRManage.MODE_LEARN)) {
                return list.size() + 1;
            }
            return list.size();
        }

        class ItemView extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView textView;

            public ItemView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_bg);
                textView = (TextView) itemView.findViewById(R.id.item_text);
            }
        }

        class AddView extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public AddView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.button);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute
                            attribute) {
                        if (cluster.clusterId == 0x0F01) {
                            switch (attribute.attributeId) {
                                case 0x8001:
                                    parserLearnData(attribute.attributeValue);
                                    break;
                                case 0x8002:
                                    parserControlData(attribute.attributeValue);
                                    break;
                            }
                        }
                    }
                });
            }
        }
    }


    private void parserControlData(String attribute) {
        switch (attribute) {
            case WifiIRManage.MODE_CONTROL_SUCCESS:
                WLog.i(TAG, "IR_SUCCESS");
                break;
            case WifiIRManage.MODE_CONTROL_FAIL:
                WLog.i(TAG, "DATA_ERROR");
            case WifiIRManage.MALLOC_FAIL2_6:
                WLog.i(TAG, "MALLOC_FAIL2_6");
                break;
            case WifiIRManage.CHECK_FAIL:
                WLog.i(TAG, "CHECK_FAIL");
                break;
        }
    }

    private void parserLearnData(String attribute) {
        switch (attribute) {
            case WifiIRManage.IR_START:
                WLog.i(TAG, "IR_START");
                break;
            case WifiIRManage.IR_STOP:
                WLog.i(TAG, "IR_STOP");
                break;
            case WifiIRManage.IR_TIMEROUT:
                if (matchingDialog != null) {
                    matchingDialog.dismiss();
                    matchingDialog = null;
                }
                showMatchingFailDialog();
                WLog.i(TAG, "IR_TIMEROUT");
                break;
            default:
                WLog.i(TAG, "学到的码：" + attribute);
                handler.removeCallbacks(timeoutRunnable);
                //刷新界面
                if (matchingDialog != null && matchingDialog.isShowing()) {
                    matchingDialog.dismiss();
                    matchingDialog = null;
                }
                currentValue = attribute;
                updateLearnCode(currentTag, currentValue);
                break;
        }
    }
}
