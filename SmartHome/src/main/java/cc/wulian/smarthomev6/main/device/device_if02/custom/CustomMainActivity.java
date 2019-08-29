package cc.wulian.smarthomev6.main.device.device_if02.custom;

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
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.ControllerMoreActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBlocksBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.CustomKeyBean;
import cc.wulian.smarthomev6.main.device.device_if02.fan.ControllerViewHelper;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.customview.popupwindow.DeleteUeiCodePopupWindow;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditUeiCustomKeyPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.WifiIRSceneEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by huxc on 2018/10/11.
 * wifi红外自定义遥控器
 */

public class CustomMainActivity extends BaseTitleActivity {
    public static final String MODE_LEARN = "MODE_LEARN";
    public static final String MODE_CONTROL = "MODE_CONTROL";
    public static final String SAVE = "SAVE";
    public static final String REMOVE_KEY = "REMOVE_KEY";
    private String deviceId;
    private String mode;
    private String blockType;
    private String blockName;
    private String blockId;
    private String codeLib; //码库id 为空表示学习的码库
    private String currentTag, currentValue;
    private boolean isWidget;
    private boolean isSceneOrHouseKeeper;
    private long clickTime;

    private WLDialog.Builder builder;
    private WLDialog matchingDialog;
    private WLDialog matchingFailDialog;
    private WLDialog createCustomKeyDialog;
    private WLDialog renameCustomKeyDialog;
    private DeleteUeiCodePopupWindow deleteCodePopup;
    private EditUeiCustomKeyPopupWindow editCodePopup;
    private Device device;
    // 码库文件列表
    private LinkedHashMap<String, String> learnMap = new LinkedHashMap<>();
    private List<ControllerViewHelper.keyItem> list;
    private List<ControllerBlocksBean.blocksBean> blockList;
    private List<ControllerBlocksBean.keyBean> keys;

    private ControllerViewHelper controllerViewHelper;
    private RecyclerView customView;
    private GridLayoutManager gridLayoutManager;
    private CustomViewAdapter customViewAdapter;
    private DataApiUnit dataApiUnit;
    private Handler handler;
    private Runnable timeoutRunnable;


    public static void startForScene(Activity context, String deviceID, String blockType, String blockName, String blockId, String codeLib) {
        Intent intent = new Intent(context, CustomMainActivity.class);
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
        Intent intent = new Intent(context, CustomMainActivity.class);
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
        Intent intent = new Intent(context, CustomMainActivity.class);
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
        setContentView(R.layout.activity_custom_main, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceID");
        blockId = intent.getStringExtra("blockId");
        blockName = intent.getStringExtra("blockName");
        blockType = intent.getStringExtra("blockType");
        codeLib = intent.getStringExtra("codeLib");
        mode = intent.getStringExtra("mode");
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);
        isWidget = getIntent().getBooleanExtra("isWidget", false);
        setTitle();
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (TextUtils.equals(mode, MODE_CONTROL)) {
            if (isSceneOrHouseKeeper) {
                if (TextUtils.isEmpty(blockName)) {
                    setToolBarTitle(mainApplication.getResources().getString(R.string.Infraredrelay_Custom_Popuptitle));
                } else {
                    setToolBarTitle(blockName);
                }
            } else {
                if (TextUtils.isEmpty(blockName)) {
                    setToolBarTitleAndRightImg(mainApplication.getResources().getString(R.string.Infraredrelay_Custom_Popuptitle), R.drawable.icon_more);
                } else {
                    setToolBarTitleAndRightImg(blockName, R.drawable.icon_more);
                }
            }
        } else if (TextUtils.equals(mode, MODE_LEARN)) {
            if (TextUtils.isEmpty(blockName)) {
                setToolBarTitle(mainApplication.getResources().getString(R.string.Infraredrelay_Custom_Popuptitle));
            } else {
                setToolBarTitle(blockName);
            }
        }
    }

    @Override
    protected void initView() {
        customView = (RecyclerView) findViewById(R.id.custom_content);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        customViewAdapter = new CustomViewAdapter(this, new ArrayList<ControllerViewHelper.keyItem>());
        customView.setLayoutManager(gridLayoutManager);
        customView.setItemAnimator(new DefaultItemAnimator());
        customView.setAdapter(customViewAdapter);
        deleteCodePopup = new DeleteUeiCodePopupWindow(this);
        editCodePopup = new EditUeiCustomKeyPopupWindow(this);
        controllerViewHelper = new ControllerViewHelper();
    }

    @Override
    protected void initData() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                if (!isWidget) {
                    WifiIRActivity.start(this, deviceId, false);
                    finish();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.img_right:
                ControllerMoreActivity.start(CustomMainActivity.this, deviceId, blockName, blockType, blockId, codeLib, isWidget);
                break;
            default:
                break;
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
                        loadCustomKeys();
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
            controllerViewHelper.convertCustomKeyToItem(keys);
            if (isCustomRemote()) {
                for (ControllerBlocksBean.keyBean bean :
                        keys) {
                    learnMap.put(bean.keyId, bean.code);
                }
            }
        }
    }

    //加载自定义按键视图
    private void loadCustomKeys() {
        list = controllerViewHelper.getIF02CustomKeys();
        customViewAdapter.update(list);
    }

    private boolean isCustomRemote() {
        if (TextUtils.isEmpty(codeLib)) {
            return true;
        } else {
            return false;
        }
    }

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
                        if (WifiIRManage.isContainsKeyName(msg,list)) {
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
                        if (WifiIRManage.isContainsKeyName(msg,list)) {
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

    //修改按键码
    private void updateLearnCode(String keyId, final String code) {
        dataApiUnit.doChangeWifiIRCode(deviceId, keyId, code, blockId, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "updateLearnCode");
                learnMap.put(currentTag, currentValue);
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
                list.add(new ControllerViewHelper.keyItem(bean.keyId, bean.keyName, null));
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
                    for (ControllerViewHelper.keyItem item : list) {
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


    private void upDateCustomKey(String tag, String value) {
        if (list != null && list.size() != 0) {
            for (ControllerViewHelper.keyItem item : list) {
                if (TextUtils.equals(item.getTag(), tag)) {
                    item.setValue(value);
                    break;
                }
            }
            customViewAdapter.update(list);
        }
    }

    private class CustomViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_FOOTER = -1;
        private Context context;
        private List<ControllerViewHelper.keyItem> list;

        public CustomViewAdapter(Context context, List<ControllerViewHelper.keyItem> list) {
            this.list = list;
            this.context = context;
        }

        public void update(List<ControllerViewHelper.keyItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addData() {
            notifyItemInserted(list.size());
        }

        public void removeData(int position) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
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
            if (TextUtils.equals(mode, MODE_LEARN)) {
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
                        //新增
                        showCreateCustomKeyDialog();
                    }
                });
            } else {
                ((ItemView) holder).textView.setText(list.get(position).getKeyName());
                ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                //学习模式
                if (TextUtils.equals(mode, MODE_LEARN)) {
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
                            //学习
                            sendLearnCmd(list.get(position).getTag());
                        }
                    });
                    ((ItemView) holder).imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //编辑
                            editCodePopup.showParent(v, list.get(position).getTag());
                            return true;
                        }
                    });
                }
                //控制模式
                else if (TextUtils.equals(mode, MODE_CONTROL)) {
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
                        ((CustomViewAdapter.ItemView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //控制
                                sendControlCmd(list.get(position).getTag());
                            }
                        });
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            if (TextUtils.equals(mode, MODE_CONTROL)) {
                return list.size();
            } else if (TextUtils.equals(mode, MODE_LEARN)) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
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
                break;
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
