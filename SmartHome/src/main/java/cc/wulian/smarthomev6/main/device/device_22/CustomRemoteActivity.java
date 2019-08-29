package cc.wulian.smarthomev6.main.device.device_22;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Device22KeyCodeCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22DetailBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22KeyBean;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditUeiCustomKeyPopupWindow;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.event.DeviceControlEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_AC;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_CUSTOM;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_TV;

public class CustomRemoteActivity extends BaseTitleActivity {

    public static final String MODE_LEARN = "MODE_LEARN";
    public static final String MODE_CONTROL = "MODE_CONTROL";
    public static final String ADD_KEY = "ADD_KEY";
    public static final String REMOVE_KEY = "REMOVE_KEY";
    public static final String CHANGE_KEY = "CHANGE_KEY";
    public static final String SEARCH_KEY = "SEARCH_KEY";
    private String mode, deviceId, name, type, index;
    private boolean isSceneOrHouseKeeper;
    private Device device;
    private Device22DetailBean remoteBean;
    private Device22KeyBean.KeyData learnKeyData;
    private List<Device22KeyBean.KeyData> remotelist;

    private RecyclerView mRecyclerView;
    private Button acTipButton;

    private GridLayoutManager gridLayoutManager;
    private CustomViewAdapter customViewAdapter;
    private WLDialog.Builder builder;
    private WLDialog addKeyDialog;
    private WLDialog renameDialog;
    private WLDialog matchingDialog;
    private WLDialog acTipsDialog;
    private EditUeiCustomKeyPopupWindow editCodePopup;

    private static int MSG_DISMISS_DIALOG = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != matchingDialog) {
                if (matchingDialog.isShowing()) {
                    matchingDialog.dismiss();
                }
            }
        }
    };

    public static void startForScene(Activity activity, String deviceId, String name, String type, String index) {
        Intent intent = new Intent(activity, CustomRemoteActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("mode", MODE_CONTROL);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        intent.putExtra("index", index);
        intent.putExtra("isSceneOrHouseKeeper", true);
        activity.startActivityForResult(intent, 1);
    }

    public static void start(Context context, String deviceId, String name, String type, String index) {
        Intent intent = new Intent(context, CustomRemoteActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("mode", MODE_CONTROL);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        intent.putExtra("index", index);
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }

    public static void edit(Activity context, String deviceId, String name, String type, String index) {
        Intent intent = new Intent(context, CustomRemoteActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("mode", MODE_LEARN);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        intent.putExtra("index", index);
        context.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_remote, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    protected void initTitle() {
        deviceId = getIntent().getStringExtra("deviceId");
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        index = getIntent().getStringExtra("index");
        mode = getIntent().getStringExtra("mode");
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);

        setTittle();
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.custom_content);
        acTipButton = (Button) findViewById(R.id.btn_ac_tip);
        editCodePopup = new EditUeiCustomKeyPopupWindow(this);

        if (TextUtils.equals(mode, MODE_LEARN) && TextUtils.equals(type, TYPE_AC)) {
            if (!Preference.getPreferences().isShownInstructionForUeiAddDevice(deviceId)) {
                showACTipsDialog();
                Preference.getPreferences().showInstructionForUeiAddDevice(deviceId);
            }
            acTipButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void loadData() {
        remotelist = new ArrayList<>();
        remoteBean = MainApplication.getApplication().getDevice22KeyCodeCache().get(deviceId, index);
        if (remoteBean.hasKeyCodeCatche) {
            remotelist.addAll(remoteBean.data);
        } else {
            searchKeyCmd();
        }
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        customViewAdapter = new CustomViewAdapter(this, remotelist);
        mRecyclerView.setAdapter(customViewAdapter);
    }

    @Override
    protected void initListeners() {
        acTipButton.setOnClickListener(this);
        editCodePopup.setPopupClickListener(new EditUeiCustomKeyPopupWindow.OnPopupClickListener() {
            @Override
            public void onDelete(String tag) {
                for (Device22KeyBean.KeyData key : remotelist) {
                    if (TextUtils.equals(tag, key.key)) {
                        removeKeyCmd(key.key, key.code, key.name);
                        return;
                    }
                }
            }

            @Override
            public void onRename(String tag) {
                for (Device22KeyBean.KeyData key : remotelist) {
                    if (TextUtils.equals(tag, key.key)) {
                        showRenameDialog(key);
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.img_right:
                Device22RemoteMoreActivity.start(CustomRemoteActivity.this, deviceId, index, name, type);
                break;
            case R.id.btn_ac_tip:
                showACTipsDialog();
                break;
        }
    }

    private void setTittle() {
        if (TextUtils.equals(mode, MODE_CONTROL)) {
            if (isSceneOrHouseKeeper) {
                if (TextUtils.isEmpty(name)) {
                    //自定义遥控器
                    if (TextUtils.equals(type, TYPE_CUSTOM)) {
                        setToolBarTitle(mainApplication.getResources().getString(R.string.Infraredrelay_Custom_Popuptitle));
                    }
                    //空调遥控器
                    else if (TextUtils.equals(type, TYPE_AC)) {
                        setToolBarTitle(mainApplication.getResources().getString(R.string.Infraredtransponder_List_Airconditioner));
                    }
                } else {
                    setToolBarTitle(name);
                }
            } else {
                if (TextUtils.isEmpty(name)) {
                    //自定义遥控器
                    if (TextUtils.equals(type, TYPE_CUSTOM)) {
                        setToolBarTitleAndRightImg(mainApplication.getResources().getString(R.string.Infraredrelay_Custom_Popuptitle), R.drawable.icon_more);
                    }
                    //空调遥控器
                    else if (TextUtils.equals(type, TYPE_AC)) {
                        setToolBarTitleAndRightImg(mainApplication.getResources().getString(R.string.Infraredtransponder_List_Airconditioner), R.drawable.icon_more);
                    }
                } else {
                    setToolBarTitleAndRightImg(name, R.drawable.icon_more);
                }
            }
        } else if (TextUtils.equals(mode, MODE_LEARN)) {
            if (TextUtils.isEmpty(name)) {
                if (TextUtils.equals(type, TYPE_CUSTOM)) {
                    setToolBarTitleAndRightBtn(mainApplication.getResources().getString(R.string.Infraredrelay_Custom_Popuptitle), getResources().getString(R.string.Done));
                } else if (TextUtils.equals(type, TYPE_AC)) {
                    setToolBarTitleAndRightBtn(mainApplication.getResources().getString(R.string.Infraredtransponder_List_Airconditioner), getResources().getString(R.string.Done));
                }
            } else {
                setToolBarTitleAndRightBtn(name, getResources().getString(R.string.Done));
            }
        }
    }

    private void showACTipsDialog() {
        if (acTipsDialog == null) {
            builder = new WLDialog.Builder(this);
            builder.setContentView(R.layout.dialog_22_ac_remote_match_content)
                    .setTitle(R.string.InTrans_Airconditioner_Helpheadlines)
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(false)
                    .setPositiveButton(this.getResources().getString(R.string.Got))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            acTipsDialog.dismiss();
                        }

                        @Override
                        public void onClickNegative(View view) {
                        }
                    });
            acTipsDialog = builder.create();
        }
        if (!acTipsDialog.isShowing()) {
            acTipsDialog.show();
        }
    }

    private void showLearnDialog() {
        if (matchingDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_22_custom_remote_match_content, null);
            ImageView hintImageView = (ImageView) view.findViewById(R.id.hint_imageView);
            if (TextUtils.equals(device.type, "22")) {
                hintImageView.setImageResource(R.drawable.custom_remote_learn);
            } else if (TextUtils.equals(device.type, "24")) {
                hintImageView.setImageResource(R.drawable.custom_remote_learn_24);
            }
            builder = new WLDialog.Builder(this);
            builder.setContentView(view)
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(false)
                    .setCancelable(false)
                    .setPositiveButton(this.getResources().getString(R.string.Infraredrelay_Custom_Matching))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                        }

                        @Override
                        public void onClickNegative(View view) {
                        }
                    });
            matchingDialog = builder.create();
        }
        if (!matchingDialog.isShowing()) {
            matchingDialog.show();
            if (TextUtils.equals(device.type, "22")) {
                mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 1000 * 20);
            } else if (TextUtils.equals(device.type, "24")) {
                mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 1000 * 5);
            }
        }
    }

    private void showAddKeyDialog() {
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
                        if (remotelist.size() != 0) {
                            for (Device22KeyBean.KeyData keyData : remotelist) {
                                if (TextUtils.equals(keyData.name, "c_" + msg)) {
                                    ToastUtil.single(R.string.Cylincam_Name_Repeated);
                                    addKeyDialog.dismiss();
                                    return;
                                }
                            }
                        }
                        addKeyCmd("c_" + msg);
                        addKeyDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        addKeyDialog.dismiss();
                    }
                });
        addKeyDialog = builder.create();
        if (!addKeyDialog.isShowing()) {
            addKeyDialog.show();
        }
    }

    private void showRenameDialog(final Device22KeyBean.KeyData keyData) {
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
                        changeKeyCmd(keyData.key, keyData.code, "c_" + msg);
                        renameDialog.dismiss();
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

    //新增命令
    private void searchKeyCmd() {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject dataObject = new JSONObject();
            dataObject.put("index", index);
            array.put(dataObject);

            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
            jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceId);//设备id
            jsonMsgContent.put("operType", 4);//模式
            jsonMsgContent.put("mode", 3);
            jsonMsgContent.put("data", array);//模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogManager.showDialog(SEARCH_KEY, CustomRemoteActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    //新增命令
    private void addKeyCmd(String name) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject dataObject = new JSONObject();
            dataObject.put("index", index);
            dataObject.put("name", name);
            array.put(dataObject);

            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
            jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceId);//设备id
            jsonMsgContent.put("operType", 1);//模式
            jsonMsgContent.put("mode", 2);
            jsonMsgContent.put("data", array);//模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogManager.showDialog(ADD_KEY, CustomRemoteActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    //删除命令
    private void removeKeyCmd(String key, String code, String name) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject dataObject = new JSONObject();
            dataObject.put("key", key);
            dataObject.put("index", index);
            dataObject.put("code", code);
            dataObject.put("name", name);
            array.put(dataObject);

            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
            jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceId);//设备id
            jsonMsgContent.put("operType", 2);//模式
            jsonMsgContent.put("mode", 2);
            jsonMsgContent.put("data", array);//模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogManager.showDialog(REMOVE_KEY, CustomRemoteActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    //修改命令
    private void changeKeyCmd(String key, String code, String name) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            JSONObject dataObject = new JSONObject();
            dataObject.put("key", key);
            dataObject.put("index", index);
            dataObject.put("code", code);
            dataObject.put("name", name);
            array.put(dataObject);

            jsonMsgContent.put("cmd", "516");
            jsonMsgContent.put("appID", mainApplication.getLocalInfo().appID);
            jsonMsgContent.put("gwID", preference.getCurrentGatewayID());
            jsonMsgContent.put("devID", deviceId);//设备id
            jsonMsgContent.put("operType", 3);//模式
            jsonMsgContent.put("mode", 2);
            jsonMsgContent.put("data", array);//模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogManager.showDialog(CHANGE_KEY, CustomRemoteActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                jsonMsgContent.toString(), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    //学习命令
    private void learnKeyCmd(String code) {
        showLearnDialog();
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", device.gwID);
            object.put("devID", deviceId);
            object.put("clusterId", 0x0F01);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0061);
            object.put("commandType", 1);
            object.put("commandId", 0x8010);
            if (!TextUtils.isEmpty(code)) {
                org.json.JSONArray array = new org.json.JSONArray();
                array.put(code);
                object.put("parameter", array);
            }

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //控制命令
    private void controlKeyCmd(String param) {
        if (isSceneOrHouseKeeper) {
            EventBus.getDefault().post(new UeiSceneEvent(name, param));
            setResult(RESULT_OK);
            finish();
        } else {
            JSONObject object = new JSONObject();
            try {
                object.put("cmd", "501");
                object.put("gwID", device.gwID);
                object.put("devID", deviceId);
                object.put("cluster", 0x0F01);
                object.put("endpointNumber", 1);
                object.put("endpointType", 0x0061);
                object.put("commandType", 1);
                object.put("commandId", 0x8011);
                org.json.JSONArray array = new org.json.JSONArray();
                array.put(param);
                object.put("parameter", array);

                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CustomViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_FOOTER = -1;
        private Context context;
        private List<Device22KeyBean.KeyData> list;

        public CustomViewAdapter(Context context, List<Device22KeyBean.KeyData> list) {
            this.list = list;
            this.context = context;
        }

        public void update(List<Device22KeyBean.KeyData> list) {
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

        public List<Device22KeyBean.KeyData> getList() {
            return list;
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
                        showAddKeyDialog();
                    }
                });
            } else {
                ((ItemView) holder).textView.setText(list.get(position).name.substring(2));
                ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                //学习模式
                if (TextUtils.equals(mode, MODE_LEARN)) {
                    if (TextUtils.isEmpty(list.get(position).code)) {
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
                            learnKeyData = new Device22KeyBean.KeyData();
                            learnKeyData.key = list.get(position).key;
                            learnKeyData.index = list.get(position).index;
                            learnKeyData.name = list.get(position).name;
                            learnKeyData.code = list.get(position).code;
                            learnKeyCmd(learnKeyData.code);
                        }
                    });
                    ((ItemView) holder).imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            editCodePopup.showParent(v, list.get(position).key);
                            return true;
                        }
                    });
                }
                //控制模式
                else if (TextUtils.equals(mode, MODE_CONTROL)) {
                    if (TextUtils.isEmpty(list.get(position).code)) {
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
                                controlKeyCmd(list.get(position).code);
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

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            org.json.JSONArray endpoints = object.optJSONArray("endpoints");
            org.json.JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            org.json.JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            String attributeId = attributes.getJSONObject(0).getString("attributeId");
            //学习
            if (TextUtils.equals(attributeId, "32769")) {
                changeKeyCmd(learnKeyData.key, attributeValue, learnKeyData.name);
                learnKeyData = null;
                if (TextUtils.equals(device.type, "22")) {
                    mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG);
                    ToastUtil.single(R.string.Infraredrelay_Custom_Matchsuccessfully);
                }
            }
            //控制
            else if (TextUtils.equals(attributeId, "32770")) {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Device22ConfigEvent event) {
        WLog.d(TAG, "Device22ConfigEvent: " + event.data);
        if (!TextUtils.equals(event.deviceId, deviceId)) {
            return;
        }
        if (event.mode == 1) {
            try {
                JSONArray dataArray = new JSONArray(event.data);
                JSONObject object = dataArray.getJSONObject(0);
                String name = object.optString("name");
                String index = object.optString("index");
                String type = object.optString("type");
                if (TextUtils.equals(index, this.index)) {
                    this.name = name;
                    setTittle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.mode == 2) {
            try {
                JSONArray dataArray = new JSONArray(event.data);
                JSONObject object = dataArray.getJSONObject(0);
                String name = object.optString("name");
                String index = object.optString("index");
                String key = object.optString("key");
                String code = object.optString("code");

                if (TextUtils.equals(index, this.index)) {
                    if (event.operType == 1) {
                        progressDialogManager.dimissDialog(ADD_KEY, 0);
                        Device22KeyBean.KeyData keyData = new Device22KeyBean.KeyData();
                        keyData.index = index;
                        keyData.name = name;
                        keyData.code = code;
                        keyData.key = key;

                        boolean hasThisKey = false;
                        List<Device22KeyBean.KeyData> keys = customViewAdapter.getList();
                        if (keys != null) {
                            for (Device22KeyBean.KeyData eachKey : keys) {
                                if (TextUtils.equals(eachKey.key, key)) {
                                    hasThisKey = true;
                                    break;
                                }
                            }
                        }
                        if (!hasThisKey) {
                            remotelist.add(keyData);
                            customViewAdapter.addData();
                        }
                    } else if (event.operType == 2) {
                        progressDialogManager.dimissDialog(REMOVE_KEY, 0);
                        for (int i = 0; i < remotelist.size(); i++) {
                            if (TextUtils.equals(remotelist.get(i).key, key)) {
                                remotelist.remove(i);
                                customViewAdapter.removeData(i);
                            }
                        }
                    } else if (event.operType == 3) {
                        progressDialogManager.dimissDialog(CHANGE_KEY, 0);
                        for (int i = 0; i < remotelist.size(); i++) {
                            if (TextUtils.equals(remotelist.get(i).key, key)) {
                                remotelist.get(i).name = name;
                                remotelist.get(i).index = index;
                                remotelist.get(i).key = key;
                                remotelist.get(i).code = code;
                                customViewAdapter.update(remotelist);
                            }
                        }
                    } else if (event.operType == 4) {
                        remoteBean = MainApplication.getApplication().getDevice22KeyCodeCache().get(deviceId, index);
                        if (remoteBean != null && remoteBean.data != null) {
                            remotelist.addAll(remoteBean.data);
                        }
                        customViewAdapter.update(remotelist);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.mode == 3) {
            progressDialogManager.dimissDialog(SEARCH_KEY, 0);
            remoteBean = MainApplication.getApplication().getDevice22KeyCodeCache().get(deviceId, index);
            if (remoteBean != null && remoteBean.data != null) {
                remotelist = remoteBean.data;
                customViewAdapter.update(remotelist);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                dealData(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceControlReport(DeviceControlEvent event) {
        try {
            JSONObject object = new JSONObject(event.data);
            String devID = object.getString("devID");
            int retCode = object.getInt("retCode");
            if (TextUtils.equals(devID, deviceId)) {
                if (retCode == 5) {
                    ToastUtil.single(getResources().getString(R.string.Infraredtransponder_Learn_Full));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (TextUtils.equals(mode, MODE_CONTROL)) {
                finish();
            }
        }
    }
}
